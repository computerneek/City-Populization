package city.populization.world;
import city.populization.world.plot.PlotOwner;
import city.populization.world.plot.PlotPos;
import city.populization.connection.PacketPlot;
import city.populization.connection.PacketPlotPos;
import city.populization.core.Client;
import city.populization.world.civilian.FamilyTree;
import city.populization.world.plot.Plot;
import java.util.ArrayList;
import java.util.Random;
import simplelibrary.net.packet.Packet;
import simplelibrary.net.packet.PacketLong;
public class Player extends PlotOwner{
    private final Random rand;
    private int startX;
    private int startY;
    private int startZ;
    private int startingPosRange;
    private Client client;
    private final World world;
    protected long cash;
    private final String name;
    private final FamilyTree familyTree;
    private ArrayList<PlotPos> townHalls = new ArrayList<>();
    public FamilyTree getFamilyTree() {
        return familyTree;
    }
    public Player(World world, Client c, int maxRange) {
        familyTree = new FamilyTree(world, this);
        rand = new Random();
        this.startingPosRange = maxRange;
        refreshStartingPosition();
        this.client = c;
        this.world = world;
        if(c!=null){
            this.name = c.getUsername();
        }else{
            this.name = "Y";
        }
    }
    public Player(World world) {
        this(world, null, 5);
        startX = 0;
        startY = 0;
        startZ = 0;
    }
    public void setSpectator(boolean b) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO Set spectator state
    }
    public void restoreConnection(Client c) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO Restore lost client connection
    }
    public PlotPos getCenter() {
        return new PlotPos(startX, startY, startZ);
    }
    public void refreshStartingPosition() {
        startingPosRange+=5;
        startX = rand.nextInt(startingPosRange)-startingPosRange/2;
        startY = rand.nextInt(startingPosRange)-startingPosRange/2;
        startZ = 0;
    }
    public void setStartingZ(int z) {
        startZ = z;
    }
    public void setCash(long cash) {
        this.cash = cash;
        send(new PacketLong(cash), "world.player.local.cash");
    }
    public void notifyDisappear(PlotPos pos) {
        send(new PacketPlotPos(shiftPos(pos)), "world.plot.change");
    }
    public void notifyAppear(PlotPos pos) {
        notifyPlotChange(pos);
    }
    private PlotPos shiftPos(PlotPos pos){
        return new PlotPos(pos.x-startX, pos.y-startY, pos.z-startZ);
    }
    public void notifyPlotChange(PlotPos pos) {
        send(new PacketPlot(world, pos, shiftPos(pos), this), "world.plot.change");
    }
    @Override
    public String getName() {
        return name;
    }
    public void updateTime(long time) {
        send(new PacketLong(time), "world.time");
    }
    private void send(Packet packet, String... channels) {
        if(client!=null&&client.isConnected()){
            client.connection.send(packet, channels);
        }
    }
    public boolean isPlayer(){
        return true;
    }
    public World getWorld() {
        return world;
    }
    public boolean canPumpOil() {
        for(PlotPos p : townHalls){
            if(canPumpOilFromHall(p)) return true;
        }
        return false;
    }
    private boolean canPumpOilFromHall(PlotPos p){
        throw new UnsupportedOperationException("Not supported yet."); //TODO Determine if the player has oil pumping equipment and thence can pump oil
    }
    public int countTownHalls() {
        return townHalls.size();
    }
    public void onPlotChange(PlotPos pos, Plot type, PlotOwner owner) {
        if(owner==this&&type==Plot.TownHall&&!townHalls.contains(pos)){
            townHalls.add(pos);
        }else{
            townHalls.remove(pos);
        }
    }
}
