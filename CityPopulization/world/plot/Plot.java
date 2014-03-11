package CityPopulization.world.plot;
import CityPopulization.menu.MenuIngame;
import CityPopulization.render.Side;
import CityPopulization.world.World;
import CityPopulization.world.aircraft.Aircraft;
import CityPopulization.world.aircraft.Terminal;
import CityPopulization.world.aircraft.passenger.AircraftPassenger;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.civilian.Path;
import CityPopulization.world.civilian.Worker;
import CityPopulization.world.civilian.WorkerTask;
import CityPopulization.world.civilian.WorkerTaskSegment;
import CityPopulization.world.civilian.event.EventSequence;
import CityPopulization.world.player.Player;
import CityPopulization.world.resource.ResourceList;
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
    public Player owner;
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
    public ArrayList<Civilian> civilians = new ArrayList<>();
    public ArrayList<Civilian> civiliansPresent = new ArrayList<>();
    public ArrayList<Worker> workers = new ArrayList<>();
    public ArrayList<Worker> workersPresent = new ArrayList<>();
    int timeSinceLastCivilianOperation = 0;
    int timeSinceLastWorkerOperation = 0;
    public WorkerTask task;
    public ResourceList resources = new ResourceList();
    public ResourceList readyResources = new ResourceList();
    public ResourceList inboundResources = new ResourceList();
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
        this.level = 0;
        world.clearPlotUpdates(this);
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
        onPlotChange();
        return this;
    }
    public PlotType getType(){
        return type;
    }
    public int getLevel(){
        return level;
    }
    public Plot setLevel(int level){
        this.level = level;
        world.clearPlotUpdates(this);
        world.schedulePlotUpdate(this);
        onPlotChange();
        return this;
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
        onPlotChange();
        return this;
    }
    public Plot setFront(Side front){
        this.front = front;
        onPlotChange();
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
            world.schedulePlotUpdate(this);
            doAirportUpdate();
        }
        if(!civiliansPresent.isEmpty()){
            world.schedulePlotUpdate(this);
            civilianUpdate();
        }
        if(!workersPresent.isEmpty()){
            world.schedulePlotUpdate(this);
            workerUpdate();
        }
        if(task==null&&!inboundResources.listResources().isEmpty()){
            resources.addAll(inboundResources);
            inboundResources = new ResourceList();
        }
    }
    private void doAirportUpdate(){
        if(world.age%20==0&&!inboundAircraft.isEmpty()){
            attemptToLandAircraft(inboundAircraft.remove(0));
        }
        ArrayList<Plot> terminals = new ArrayList<>();
        findTerminals(terminals);
        for(Plot plot : terminals){
            plot.terminal.update(terminal);
        }
        terminal.update(terminal);
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
        shouldRenderFrontFace = world.getPlot(x, y+1, z)==null||!world.getPlot(x, y+1, z).getType().isOpaque();
        shouldRenderBackFace = world.getPlot(x, y-1, z)==null||!world.getPlot(x, y-1, z).getType().isOpaque();
        
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
    public void addPassenger(AircraftPassenger passenger){
        Civilian civilian = passenger.createCivilian();
        civilian.homePlot = this;
        civilian.x = x;
        civilian.y = y;
        civilian.z = z;
        if(civilian instanceof Worker){
            workers.add((Worker)civilian);
            workersPresent.add((Worker)civilian);
        }else{
            civilians.add(civilian);
            civiliansPresent.add(civilian);
        }
        world.schedulePlotUpdate(this);
    }
    private void civilianUpdate(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void workerUpdate(){
        timeSinceLastWorkerOperation++;
        if(timeSinceLastWorkerOperation>=20){
            doWorkerUpdate();
        }
    }
    private void doWorkerUpdate(){
        ArrayList<WorkerTask> tasks = new ArrayList<>();
        findPotentialTasks(tasks);
        for(WorkerTask potentialTask : tasks){
            if(potentialTask.isFull()||potentialTask.getCurrentSegment().isFull()){
                continue;
            }
            Worker worker = workersPresent.get(0);
            WorkerTaskSegment segment = potentialTask.getCurrentSegment();
            EventSequence sequence = segment.generateEventSequence(worker, this);
            if(sequence!=null){
                worker.assign(sequence);
                world.civilians.add(worker);
                workersPresent.remove(worker);
                timeSinceLastWorkerOperation = 0;
                break;
            }
        }
    }
    private void findPotentialTasks(ArrayList<WorkerTask> tasks){
        if(owner==null||!owner.getWorkerTaskManager().hasTasks()){
            return;
        }
        Path.findPotentialTasks(tasks, this);
    }
    public ArrayList<Side> getPathableSides(){
        ArrayList<Side> sides = new ArrayList<>();
        for(Side side : type.getPathableSides()){
            switch(side){
                case FRONT:
                    sides.add(front);
                    break;
                case LEFT:
                    sides.add(front.left());
                    break;
                case RIGHT:
                    sides.add(front.right());
                    break;
                case BACK:
                    sides.add(front.reverse());
                    break;
                default:
                    sides.add(side);
            }
        }
        return sides;
    }
    public Side getDirectionToPlot(Plot plot){
        for(Side side : Side.values()){
            if(plot==side.getPlot(world, x, y, z)){
                return side;
            }
        }
        return null;
    }
    public ArrayList<Side> getTravelableSides(){
        ArrayList<Side> sides = getPathableSides();
        ArrayList<Side> val = new ArrayList<>();
        for(Side side : sides){
            Plot plot = side.getPlot(world, x, y, z);
            if(plot==null){
                continue;
            }
            if(plot.getPathableSides().contains(side.reverse())){
                val.add(side);
            }
        }
        return val;
    }
    public void readyResources(ResourceList resources){
        readyResources.addAll(resources);
        this.resources.removeAll(resources);
    }
    private MenuIngame menu;
    public void select(MenuIngame menu){
        this.menu = menu;
    }
    public void unselect(){
        menu = null;
    }
    private void onPlotChange(){
        if(menu!=null){
            menu.onPlotUpdate();
        }
    }
}
