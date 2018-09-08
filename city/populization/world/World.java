package city.populization.world;
import city.populization.render.Sound;
import city.populization.world.plot.PlotOwner;
import city.populization.world.plot.PlotPos;
import city.populization.world.plot.entity.PlotEntity;
import city.populization.core.Client;
import city.populization.menu.Viewport;
import city.populization.world.civilian.Civilian;
import city.populization.world.plot.Plot;
import city.populization.world.plot.entity.PlotEntityHouse;
import city.populization.world.plot.type.PlotResource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import simplelibrary.Queue;
import simplelibrary.net.packet.PacketString;
public class World {
    private long seed;
    public WorldData data;
    private Template template;
    public HashMap<ZoneCoords, Zone> zones = new HashMap<>();
    public HashMap<PlotPos, PlotEntity> entities = new HashMap<>();
    public Queue<PlotPos> requestedUpdates = new Queue<>();
    public Set<Civilian> civilians = new HashSet<>();
    private long time;
    private boolean running = true;
    public Viewport viewport = new Viewport(0.5f, 0.5f, 0);
    HashMap<String, Player> players = new HashMap<>();
    public ArrayList<Player> playerList = new ArrayList<>();
    public static final Sound timerClick = Sound.get("/world/timer/click.wav");
    public static final Sound timerDone = Sound.get("/world/timer/done.wav");
    public long getTime() {
        return time;
    }
    public boolean isRunning() {
        return running;
    }
    public void tick() {
        time++;
        for(Player p : players.values()){
            p.updateTime(time);
        }
        if(time>0){
            if(!requestedUpdates.isEmpty()){
                Queue<PlotPos> q = requestedUpdates;
                requestedUpdates = new Queue<>();
                for(PlotPos p : q){
                    getPlot(p).onUpdate(this, p);
                }
            }
            for(Civilian c : new HashSet<>(civilians)){
                c.tick();
            }
            for(PlotEntity e : entities.values()){
                e.tick();
            }
        }
    }
    public void setTime(long time) {
        this.time = time;
    }
    public void save() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO Save the world!
    }
    public void setData(WorldData d) {
        data = d;
        this.seed = data.seed;
        this.template = data.template;
    }
    public long getSeed() {
        return seed;
    }
    public void setPlot(PlotPos plotPos, Plot type) {
        setPlot(plotPos, type, null, Direction.SOUTH);
    }
    public void setPlot(PlotPos plotPos, Plot type, PlotOwner owner){
        setPlot(plotPos, type, owner, Direction.SOUTH);
    }
    public void setPlot(PlotPos plotPos, Plot type, Direction heading){
        setPlot(plotPos, type, null, heading);
    }
    public void setPlot(PlotPos plotPos, Plot type, int level){
        setPlot(plotPos, type, null, Direction.SOUTH, level);
    }
    public void setPlot(PlotPos plotPos, Plot type, PlotOwner owner, Direction heading){
        setPlot(plotPos, type, owner, heading, 0);
    }
    public void setPlot(PlotPos plotPos, Plot type, PlotOwner owner, int level){
        setPlot(plotPos, type, owner, Direction.SOUTH, level);
    }
    public void setPlot(PlotPos plotPos, Plot type, Direction heading, int level){
        setPlot(plotPos, type, null, heading, level);
    }
    public void setPlot(PlotPos plotPos, Plot type, PlotOwner owner, Direction heading, int level){
        getZone(ZoneCoords.fromPlot(plotPos)).setPlot(plotPos, type, owner, heading, level);
    }
    private Zone getZone(ZoneCoords fromPlot) {
        Zone zone = zones.get(fromPlot);
        return zone==null?makeZone(fromPlot):zone;
    }
    private Zone makeZone(ZoneCoords fromPlot){
        synchronized(zones){
            //TODO attempt to load the zone before generating a new one
            return (template==null?new Zone(this, fromPlot):template.generateZone(this, fromPlot));
        }
    }
    public Plot getPlot(PlotPos plotPos) {
        return getZone(ZoneCoords.fromPlot(plotPos)).getPlot(plotPos);
    }
    public PlotOwner getOwner(PlotPos pos){
        return getZone(ZoneCoords.fromPlot(pos)).getOwner(pos);
    }
    public Direction getHeading(PlotPos p){
        return getZone(ZoneCoords.fromPlot(p)).getHeading(p);
    }
    public int getLevel(PlotPos p){
        return getZone(ZoneCoords.fromPlot(p)).getLevel(p);
    }
    public Player[] getVisibilities(PlotPos p){
        return getZone(ZoneCoords.fromPlot(p)).getVisibilities(p);
    }
    public void setVisibilities(PlotPos p, Player[] vis){
        getZone(ZoneCoords.fromPlot(p)).setVisibilities(p, vis);
    }
    public WorldLoader getWorldLoader() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO get the variable-stored world loader
    }
    public Viewport getViewport() {
        return viewport;
    }
    public synchronized void addPlayer(Client c) {
        if(!c.isAuthorized()){
            addClient(c);
            return;
        }
        Random rand = new Random();
        Player player = new Player(this, c, 30+6*players.size());
        players.put(c.getUsername(), player);
        playerList.add(player);
        spawnStartingLocation(player);
    }
    public synchronized void addClient(Client c) {
        if(c.isAuthorized()&&players.containsKey(c.getUsername())){
            players.get(c.getUsername()).restoreConnection(c);
            informReconnect(players.get(c.getUsername()));
        }else if(players.containsKey(c.getUsername())){
            c.connection.send(new PacketString("Cannot overtake existing player without valid login!"), "disconnect.reason");
            c.disconnect();
        }else{
            Random rand = new Random();
            Player player = new Player(this, c, 30+6*players.size());
            players.put(c.getUsername(), player);
            playerList.add(player);
            player.setSpectator(true);
            addSpectator(player);
        }
    }
    private void spawnStartingLocation(Player player) {
        PlotPos pos = null;
        while(pos==null){
            pos = player.getCenter();
            while(!canSeeSky(pos)){
                pos = pos.shift(Direction.UP, 1);
            }
            while(!(getPlot(pos) instanceof PlotResource)){
                pos = pos.shift(Direction.DOWN, 1);
            }
            if(isNearVisible(pos)){
                pos = null;
                player.refreshStartingPosition();
            }
        }
        int seedAdjust = 0;
        while(!template.generateStartingPosition(this, pos, player, new Random(seed+playerList.indexOf(player)+seedAdjust))){
            seedAdjust++;
        }
    }
    private void addSpectator(Player player) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO Add a spectator player to the world
    }
    private void informReconnect(Player get) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO inform other players that the selected player has reconnected
    }
    private boolean canSeeSky(PlotPos pos) {
        for(int i = 0; i<100; i++){
            if(getPlot(pos.shift(Direction.UP, i+1))!=Plot.Air){
                return false;
            }
        }
        return true;
    }
    private boolean isNearVisible(PlotPos pos) {
        for(int x = pos.x-10; x<pos.x+11; x++){
            for(int y = pos.y-10; y<pos.y+11; y++){
                for(int z = pos.z-10; z<pos.z+11; z++){
                    PlotPos p = new PlotPos(x, y, z);
                    if(getOwner(p)!=null||getVisibilities(p).length>0){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public synchronized void render(){
        GL11.glLoadIdentity();
        Viewport v = getViewport();
        GL11.glTranslated(-v.x, -v.y, -4);
        GL11.glScalef(1, 1, 0.25f);
        GL11.glTranslated(0, 0, -v.z);
        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        synchronized(zones){
            for(Zone z : zones.values()){
                z.render(v.z);
            }
        }
//        for(Aircraft aircraft : this.aircraft){
//            aircraft.render(localPlayer);
//        }
//        for(Civilian civilian : civilians){
//            civilian.render(localPlayer);
//        }
    }
    public void passOnHome(PlotPos home, Civilian owner) {
        ArrayList<Civilian> occupants = getOccupants(home);
        PlotOwner plotOwner = getOwner(home);
        occupants.remove(owner);
        if(occupants.isEmpty()){
            setOwner(home, owner.owner);
        }else if(owner==plotOwner){
            setOwner(home, occupants.get(0));
        }
    }
    public void addOccupant(PlotPos plot, Civilian occupant) {
        ArrayList<Civilian> occupants = getOccupants(plot);
        occupants.add(occupant);
    }
    private void setOwner(PlotPos home, PlotOwner owner) {
        getZone(ZoneCoords.fromPlot(home)).setOwner(home, owner);
    }
    private ArrayList<Civilian> getOccupants(PlotPos home) {
        PlotEntity entity = entities.get(home);
        if(entity!=null&&entity instanceof PlotEntityHouse){
            return ((PlotEntityHouse)entity).getOccupants();
        }else{
            return null;
        }
    }
    public void pause() {
        running=!running;
    }
}
