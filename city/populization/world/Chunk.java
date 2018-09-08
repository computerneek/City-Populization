package city.populization.world;
import city.populization.menu.MenuWorld;
import city.populization.render.ResourceLocation;
import city.populization.world.civilian.Civilian;
import city.populization.world.plot.PlotOwner;
import city.populization.world.plot.PlotPos;
import city.populization.world.plot.Plot;
import city.populization.world.plot.entity.PlotEntity;
import city.populization.world.plot.type.PlotEntitied;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;

import static city.populization.world.ChunkCoords.EXPONENT;
public class Chunk implements Iterable<PlotPos>{
    private int[] types = new int[512];
    private PlotOwner[] owners = new PlotOwner[512];
    private Direction[] headings = new Direction[512];
    private int[] levels = new int[512];
    private Player[][] visibilities = new Player[512][0];
    public final Zone zone;
    public final ChunkCoords coords;
    private boolean changed = true;
    private int lastZ;
    private int renderList;
    private boolean hasList;
    private boolean purgeRenderList;
    public Chunk(Zone zone, ChunkCoords coords) {
        this.zone = zone;
        this.coords = coords;
        zone.setChunk(coords, this);
    }
    /**
     * Gets the index in the types array for the specified coordinates inside the chunk
     */
    private synchronized int getIndex(int x, int y, int z){
        return x+y*8+z*64;
    }
    private PlotPos getPos(int index){
        int[] minCoords = coords.getMinCoords();
        return new PlotPos(index%(1<<EXPONENT)+minCoords[0], (index/(1<<EXPONENT))%(1<<EXPONENT)+minCoords[1], (index/(1<<(EXPONENT*2)))+minCoords[2]);
    }
    private int getIndex(PlotPos p){
        return getIndexFromPlot(p.x, p.y, p.z);
    }
    private int getIndexFromPlot(int x, int y, int z){
        int[] coords = this.coords.getMinCoords();
        return getIndex(x-coords[0], y-coords[1], z-coords[2]);
    }
    public long getChunkSeed() {
        return zone.getZoneSeed()+new Random(coords.x*10000+coords.y*100+coords.z).nextLong();
    }
    public void flagChanged() {
        changed = true;
        zone.flagChanged();
        purgeRenderList();
    }
    public Plot getPlot(PlotPos p) {
        return Plot.getTypes().get(types[getIndex(p)]);
    }
    public int getLevel(PlotPos p){
        return levels[getIndex(p)];
    }
    public PlotOwner getOwner(PlotPos p){
        return owners[getIndex(p)];
    }
    public Direction getHeading(PlotPos p){
        return headings[getIndex(p)];
    }
    public Player[] getVisibilities(PlotPos p){
        return visibilities[getIndex(p)];
    }
    public void setVisibilities(PlotPos p, Player[] vis){
        visibilities[getIndex(p)] = vis;
    }
    public void setPlot(PlotPos p, Plot plot, PlotOwner owner, Direction heading, int level) {
        int index = getIndex(p);
        boolean changed = types[index]!=plot.index||headings[index]!=heading||levels[index]!=level;
        if(changed){
            zone.world.entities.remove(p);
        }
        PlotOwner last = owners[index];
        types[index] = plot.index;
        owners[index] = owner;
        headings[index] = heading;
        levels[index] = level;
        if(zone.world.data!=null){//If the data is not null, this is the serverside world.
            for(Player player : getVisibilities(p)){
                player.notifyPlotChange(p);
            }
            if(last!=owner){
                if(owner instanceof Player){
                    addVisibility(p, (Player)owner);
                }
                if(last!=null&&last instanceof Player){
                    removeVisibility(p, (Player)last);
                }
            }
            if(plot instanceof PlotEntitied){
                PlotEntity e = ((PlotEntitied)plot).createNewEntity();
                e.world = zone.world;
                e.pos = p;
                e.rand = new Random(getChunkSeed()+new Random(p.x*100000+p.y*1000+p.z*10).nextLong());
                e.construct();
                zone.world.entities.put(p, e);
            }
        }else{//If the data is null, this is the clientside world, so we need to purge the render list
            purgeRenderList = true;
        }
    }
    public void setOwner(PlotPos p, PlotOwner owner) {
        int index = getIndex(p);
        PlotOwner last = owners[index];
        owners[index] = owner;
        if(zone.world.data!=null){//If the data is not null, this is the serverside world.
            for(Player player : getVisibilities(p)){
                player.notifyPlotChange(p);
            }
            if(last!=owner){
                if(owner instanceof Player){
                    addVisibility(p, (Player)owner);
                }
                if(last!=null&&last instanceof Player){
                    removeVisibility(p, (Player)last);
                }
            }
        }else{//If the data is null, this is the clientside world, so we need to purge the render list
            purgeRenderList = true;
        }
    }
    private void addVisibility(PlotPos p, Player owner){
        for(int x = p.x-2; x<=p.x+2; x++){
            for(int y = p.y-2; y<=p.y+2; y++){
                Z:for(int z = p.z-2; z<=p.z+2; z++){
                    PlotPos p2 = new PlotPos(x, y, z);
                    Player[] vis = zone.world.getVisibilities(p2);
                    for(Player v : vis){
                        if(v==owner){
                            continue Z;
                        }
                    }
                    Player[] newVis = new Player[vis.length+1];
                    System.arraycopy(vis, 0, newVis, 0, vis.length);
                    newVis[vis.length] = owner;
                    zone.world.setVisibilities(p2, newVis);
                    owner.notifyAppear(p2);
                }
            }
        }
    }
    private void removeVisibility(PlotPos p, Player owner) {
        World world = zone.world;
        if(world.players.isEmpty()){
            return;
        }
        for(int x = p.x-2; x<=p.x+2; x++){
            for(int y = p.y-2; y<=p.y+2; y++){
                for(int z = p.z-2; z<=p.z+2; z++){
                    boolean owned = false;
                    for(Player p2 : zone.world.getVisibilities(new PlotPos(x, y, z))){
                        if(p2==owner){
                            //This will only be called if the plot is visible and needs to disappear
                            updateVisibility(world, x, y, z);
                            break;
                        }
                    }
                }
            }
        }
    }
    private void updateVisibility(World world, int X, int Y, int Z){
        Set<Player> players = new HashSet<>();
        PlotOwner p = null;
        PlotPos pos = new PlotPos(X, Y, Z);
        for(int x = X-2; x<=X+2; x++){
            for(int y = Y-2; y<=Y+2; y++){
                for(int z = Z-2; z<=Z+2; z++){
                    p = world.getOwner(new PlotPos(x, y, z));
                    if(p!=null&&p instanceof Player){
                        players.add((Player) p);
                    }
                }
            }
        }
        Set<Player> p2 = new HashSet<>(players);
        for(Player player : world.getVisibilities(pos)){
            if(!players.contains(player)){
                player.notifyDisappear(pos);
            }else{
                p2.remove(player);
            }
        }
        for(Player player : p2){
            player.notifyAppear(pos);
        }
        world.setVisibilities(pos, players.toArray(new Player[players.size()]));
    }
    @Override
    public Iterator<PlotPos> iterator() {
        return new Iterator<PlotPos>() {
            private int next = 0;
            @Override
            public boolean hasNext() {
                return next<types.length;
            }
            @Override
            public PlotPos next() {
                PlotPos c = getPos(next);
                next++;
                return c;
            }
        };
    }
    public synchronized void render(int displayZ) {
        if(purgeRenderList){
            purgeRenderList();
            purgeRenderList = false;
        }
        if(displayZ!=lastZ){
            if((displayZ<coords.z<<EXPONENT&&lastZ<coords.z<<EXPONENT)||
               (displayZ>(coords.z+1)<<EXPONENT&&lastZ>(coords.z+1<<EXPONENT))){
                //The change stayed above or below us, so no change here!
            }else{
                purgeRenderList();
            }
            lastZ = displayZ;
        }
        if(hasRenderList()){
            renderList();
        }else{
            createRenderList();
        }
    }
    private synchronized void purgeRenderList() {
        if(hasList){
            GL11.glDeleteLists(renderList, 1);
            hasList = false;
        }
    }
    private synchronized boolean hasRenderList() {
        return hasList&&renderList>0;
    }
    private synchronized void renderList() {
        GL11.glCallList(renderList);
    }
    private synchronized void createRenderList() {
        //IF ONLY THE STINKING RENDER LISTS WOULD WORK
//        renderList = GL11.glGenLists(1);
//        if(renderList==0){
//            throw new OutOfMemoryError("No more room for render lists!");
//        }
//        ImageStash.instance.bindTexture(0);//Ensure that texture binds are included in the list
//        GL11.glNewList(GL11.GL_COMPILE_AND_EXECUTE, renderList);
        int[] minCoords = coords.getMinCoords();
        GL11.glPushMatrix();
        GL11.glTranslatef(minCoords[0], minCoords[1], minCoords[2]);
        for(int x = 0; x<8; x++){
            for(int y = 0; y<8; y++){
                for(int z = 0; z<8; z++){
                    int index = getIndex(x, y, z);
                    Plot p = Plot.getTypes().get(types[index]);
                    if(p.model!=null){
                        GL11.glPushMatrix();
                        PlotPos pos = getPos(index);
                        if(pos.z>lastZ){
                            continue;
                        }
                        switch(p.model.getFront(this, pos, headings[index])){
                            case NORTH:
                                GL11.glTranslatef(x+1, y+1, z);
                                GL11.glRotatef(180, 0, 0, 1);
                                break;
                            case SOUTH:
                                GL11.glTranslatef(x, y, z);
                                break;
                            case EAST:
                                GL11.glTranslatef(x, y+1, z);
                                GL11.glRotatef(-90, 0, 0, 1);
                                break;
                            case WEST:
                                GL11.glTranslatef(x+1, y, z);
                                GL11.glRotatef(90, 0, 0, 1);
                                break;
                        }
                        p.model.render(this, pos);
                        GL11.glPopMatrix();
                    }
                }
            }
        }
        GL11.glPopMatrix();
//        GL11.glEndList();
//        hasList = true;
    }
}
