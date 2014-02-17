package CityPopulization.world.plot;
import CityPopulization.world.World;
import CityPopulization.world.player.Player;
import CityPopulization.world.aircraft.Aircraft;
import java.util.ArrayList;
import java.util.Random;
public class Plot{
    private ArrayList<Aircraft> inboundAircraft = new ArrayList<>();
    private int x;
    private int y;
    private int z;
    public final Random rand;
    private PlotType type;
    private int level;
    private Player owner;
    private World world;
    private ArrayList<Player> playerVisibilities = new ArrayList<>();
    private boolean shouldRenderTopFace;
    private boolean shouldRenderLeftFace;
    private boolean shouldRenderRightFace;
    private boolean shouldRenderFrontFace;
    private boolean shouldRenderBackFace;
    public Plot(World world, int x, int y, int z){
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        int anum = x*y*z+x*y+x-2*y*z+47*x*z-2785*y*z+225*x*y*z;
        rand = new Random(anum*anum+anum/2+anum*anum*anum-46*anum+anum);
        if(z>0){
            setType(PlotType.Air);
        }else if(z==0){
            setType(PlotType.Grass);
        }else if(z<0){
            setType(PlotType.Stone);
        }
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
    public void setOwner(Player player){
        owner = player;
        for(int i = -1; i<2; i++){
            for(int j = -1; j<2; j++){
                for(int k = -1; k<2; k++){
                    world.generatePlot(x+i, y+j, z+k).updateVisibility();
                }
            }
        }
    }
    public void addInboundAircraft(Aircraft aircraft){
        if(getType()==PlotType.AirportEntrance){
            inboundAircraft.add(aircraft);
        }
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
        attemptToLandAircraft(inboundAircraft.remove(0));
    }
    private void attemptToLandAircraft(Aircraft aircraft){
        ArrayList<Plot> terminals = new ArrayList<>();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void onNeighborPlotChange(){
        shouldRenderTopFace = world.getPlot(x, y, z+1)==null||!world.getPlot(x, y, z+1).getType().isOpaque();
        shouldRenderLeftFace = world.getPlot(x-1, y, z)==null||!world.getPlot(x-1, y, z).getType().isOpaque();
        shouldRenderRightFace = world.getPlot(x+1, y, z)==null||!world.getPlot(x+1, y, z).getType().isOpaque();
        shouldRenderFrontFace = world.getPlot(x, y+1, z)==null||!world.getPlot(x, y+1, z).getType().isOpaque();
        shouldRenderBackFace = world.getPlot(x, y-1, z)==null||!world.getPlot(x, y-1, z).getType().isOpaque();
    }
    public void render(Player localPlayer){
        if(!playerVisibilities.contains(localPlayer)){
            return;
        }
        type.render(this);
    }
}