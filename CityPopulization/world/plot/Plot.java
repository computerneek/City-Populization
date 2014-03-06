package CityPopulization.world.plot;
import CityPopulization.render.Side;
import CityPopulization.world.World;
import CityPopulization.world.aircraft.Aircraft;
import CityPopulization.world.aircraft.Terminal;
import CityPopulization.world.player.Player;
import java.util.ArrayList;
import java.util.Random;
public class Plot{
    private ArrayList<Aircraft> inboundAircraft = new ArrayList<>();
    public final int x;
    public final int y;
    public final int z;
    public final Random rand;
    private PlotType type;
    private int level;
    private Player owner;
    public final World world;
    private ArrayList<Player> playerVisibilities = new ArrayList<>();
    public boolean shouldRenderTopFace;
    public boolean shouldRenderLeftFace;
    public boolean shouldRenderRightFace;
    public boolean shouldRenderFrontFace;
    public boolean shouldRenderBackFace;
    private int frameBoost;
    public Side front = Side.FRONT;
    public Terminal terminal;
    public Plot(World world, int x, int y, int z){
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        int anum = x*y*z+x*y+x-2*y*z+47*x*z-2785*y*z+225*x*y*z;
        anum = anum*anum+anum/2+anum*anum*anum-46*anum+anum;
        rand = new Random(world.seed*anum);
        this.frameBoost = Math.abs(rand.nextInt());
        if(z>0){
            setType(PlotType.Air);
        }else if(z==0){
            setType(PlotType.Grass);
        }else{
            setType(PlotType.Stone);
        }
        terminal = new Terminal(this);
    }
    public Plot setType(PlotType type){
        this.type = type;
        this.level = 1;
        world.schedulePlotUpdate(this);
        for(int i = -1; i<2; i++){
            for(int j = -1; j<2; j++){
                for(int k = -1; k<2; k++){
                    Plot plot = world.getPlot(x+i, y+j, z+k);
                    if(plot!=null){
                        plot.onNeighborPlotChange();
                    }
                }
            }
        }
        return this;
    }
    public PlotType getType(){
        return type;
    }
    public int getLevel(){
        return level;
    }
    public Plot setLevel(int i){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public void setTimeSinceLastBreakage(int get){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public void setBreakages(int i){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public void setNextBreakageTime(int i){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public Plot setOwner(Player player){
        owner = player;
        for(int i = -1; i<2; i++){
            for(int j = -1; j<2; j++){
                for(int k = -1; k<2; k++){
                    world.generatePlot(x+i, y+j, z+k).updateVisibility();
                }
            }
        }
        return this;
    }
    public Plot setFront(Side front){
        this.front = front;
        return this;
    }
    public Aircraft addInboundAircraft(Aircraft aircraft){
        if(getType()==PlotType.AirportEntrance){
            inboundAircraft.add(aircraft);
            return aircraft;
        }
        return null;
    }
    private void updateVisibility(){
        playerVisibilities.clear();
        PLAYER:for(Player player : world.listPlayers()){
            for(int i = -1; i<2; i++){
                for(int j = -1; j<2; j++){
                    for(int k = -1; k<2; k++){
                        Plot plot = world.getPlot(x+i, y+j, z+k);
                        if(plot!=null&&plot.owner==player){
                            playerVisibilities.add(player);
                            continue PLAYER;
                        }
                    }
                }
            }
        }
    }
    public void update(){
        if(getType()==PlotType.AirportEntrance){
            world.schedulePlotUpdate(this, 20);
            doAirportUpdate();
        }
    }
    private void doAirportUpdate(){
        if(!inboundAircraft.isEmpty()){
            attemptToLandAircraft(inboundAircraft.remove(0));
        }
    }
    private void attemptToLandAircraft(Aircraft aircraft){
        ArrayList<Plot> terminals = new ArrayList<>();
        findTerminals(terminals);
        for(Plot plot : terminals){
            Terminal terminal = plot.terminal;
            if(terminal.occupied==0){
                terminal.attemptToLandAircraft(aircraft);
                if(terminal.occupied>0){
                    return;
                }
            }
        }
        inboundAircraft.add(aircraft);
    }
    private void onNeighborPlotChange(){
        shouldRenderTopFace = world.getPlot(x, y, z+1)==null||!world.getPlot(x, y, z+1).getType().isOpaque();
        shouldRenderLeftFace = world.getPlot(x-1, y, z)==null||!world.getPlot(x-1, y, z).getType().isOpaque();
        shouldRenderRightFace = world.getPlot(x+1, y, z)==null||!world.getPlot(x+1, y, z).getType().isOpaque();
        shouldRenderFrontFace = world.getPlot(x, y-1, z)==null||!world.getPlot(x, y-1, z).getType().isOpaque();
        shouldRenderBackFace = world.getPlot(x, y+1, z)==null||!world.getPlot(x, y+1, z).getType().isOpaque();
    }
    public void render(Player localPlayer){
        if(!playerVisibilities.contains(localPlayer)){
            return;
        }
        type.render(this);
    }
    public int getFrameNumber(){
        return frameBoost+world.age;
    }
    public Plot getLeftPlot(){
        return front.left().getPlot(world, x, y, z);
    }
    public Plot getRightPlot(){
        return front.right().getPlot(world, x, y, z);
    }
    public Plot getFrontPlot(){
        return front.getPlot(world, x, y, z);
    }
    public Plot getBackPlot(){
        return front.reverse().getPlot(world, x, y, z);
    }
    private void findTerminals(ArrayList<Plot> terminals){
        findTerminal(terminals, x+1, y, z);
        findTerminal(terminals, x-1, y, z);
        findTerminal(terminals, x, y+1, z);
        findTerminal(terminals, x, y-1, z);
        findTerminal(terminals, x, y, z+1);
        findTerminal(terminals, x, y, z-1);
    }
    private void findTerminal(ArrayList<Plot> terminals, int x, int y, int z){
        Plot plot = world.getPlot(x, y, z);
        if(plot==null){
            return;
        }
        if(plot.getType()==PlotType.AirportTerminal&&!terminals.contains(plot)&&plot.getOwner()==getOwner()){
            terminals.add(plot);
            plot.findTerminals(terminals);
        }
    }
    public Player getOwner(){
        return owner;
    }
    public Side getFront(){
        return front;
    }
}
