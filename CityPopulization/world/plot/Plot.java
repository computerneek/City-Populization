package CityPopulization.world.plot;
import CityPopulization.Core;
import CityPopulization.menu.MenuIngame;
import CityPopulization.render.Side;
import CityPopulization.world.World;
import CityPopulization.world.aircraft.Aircraft;
import CityPopulization.world.aircraft.Terminal;
import CityPopulization.world.aircraft.passenger.AircraftPassenger;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.civilian.CivilianTask;
import CityPopulization.world.civilian.Path;
import CityPopulization.world.civilian.Worker;
import CityPopulization.world.civilian.WorkerTask;
import CityPopulization.world.civilian.WorkerTaskSegment;
import CityPopulization.world.civilian.event.EventSequence;
import CityPopulization.world.player.Player;
import CityPopulization.world.player.Race;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import org.lwjgl.opengl.GL11;
import simplelibrary.config2.Config;
public class Plot{
    public final int x;
    public final int y;
    public final int z;
    public final Random rand;
    public PlotType type;
    public int level;
    public Player owner;
    public final World world;
    public ArrayList<Player> playerVisibilities = new ArrayList<>();
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
    public int timeSinceLastCivilianOperation = 0;
    public int timeSinceLastWorkerOperation = 0;
    public WorkerTask task;
    public ResourceList resources = new ResourceList();
    public ResourceList readyResources = new ResourceList();
    public ResourceList inboundResources = new ResourceList();
    public ArrayList<Aircraft> inboundAircraft = new ArrayList<>();
    public int coming;
    public int fallProgress = 0;
    public int lastTaskTimeWorker = -1;
    public ArrayList<WorkerTask> lastTasksWorker = new ArrayList<>();
    public int lastTaskTimeCivilian = -1;
    public ArrayList<WorkerTask> lastTasksCivilian = new ArrayList<>();
    private int civilianTime;
    public static final int VISION_DISTANCE = 2;
    private boolean taskWorkerFailed;
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
        if(this.type==PlotType.Warehouse&&type!=this.type){
            resources.addAll(inboundResources);
            coming = Math.max(0, coming-inboundResources.count());
            inboundResources = new ResourceList();
            if(task!=null){
                task = new WorkerTask().setOwner(owner).setCash(0).setPlot(this).setCost(new ResourceList()).setRevenue(resources);
                task.prepare();
                task.cost.remove(Resource.Tools, 1);
                task.revenue.remove(Resource.Tools, 1);
                task.segments.remove(0);
                world.schedulePlotUpdate(this, 20);
                resources = new ResourceList();
            }else{
                task.revenue.addAll(resources);
                resources = new ResourceList();
            }
        }else if((this.type==PlotType.House||getSkyscraperPlots().length>0)&&type!=this.type){
            for(Worker worker : workers){
                worker.timer = 0;
            }
            for(Civilian civil : civilians){
                civil.timer = 0;
            }
        }
        this.type = type;
        this.level = 0;
        if(Core.world==world){
            world.clearPlotUpdates(this);
            world.schedulePlotUpdate(this);
            for(int i = -VISION_DISTANCE; i<=VISION_DISTANCE; i++){
                for(int j = -VISION_DISTANCE; j<=VISION_DISTANCE; j++){
                    for(int k = -VISION_DISTANCE; k<=VISION_DISTANCE; k++){
                        Plot plot = world.getPlot(x+i, y+j, z+k);
                        if(plot!=null){
                            plot.onNeighborPlotChange();
                        }
                    }
                }
            }
            onPlotChange();
        }
        if(owner!=null){
            owner.resourceStructures.remove(this);
            if(type==PlotType.Warehouse){
                owner.resourceStructures.add(this);
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
    public Plot setLevel(int level){
        this.level = level;
        if(Core.world==world){
            world.clearPlotUpdates(this);
            world.schedulePlotUpdate(this);
            onPlotChange();
        }
        return this;
    }
    public Plot setOwner(Player player){
        if(owner!=null){
            owner.resourceStructures.remove(this);
        }
        owner = player;
        if(Core.world==world){
            for(int i = -VISION_DISTANCE; i<=VISION_DISTANCE; i++){
                for(int j = -VISION_DISTANCE; j<=VISION_DISTANCE; j++){
                    for(int k = -VISION_DISTANCE; k<=VISION_DISTANCE; k++){
                        world.generatePlot(x+i, y+j, z+k).updateVisibility();
                    }
                }
            }
            onPlotChange();
        }
        if(type==PlotType.Warehouse){
            owner.resourceStructures.add(this);
        }
        return this;
    }
    public Plot setFront(Side front){
        this.front = front;
        onPlotChange();
        return this;
    }
    public Aircraft addInboundAircraft(Aircraft aircraft){
        inboundAircraft.add(aircraft);
        return aircraft;
    }
    public void updateVisibility(){
        ArrayList<Player> visibilityClone = new ArrayList<Player>(playerVisibilities);
        playerVisibilities.clear();
        PLAYER:for(Player player : world.listPlayers()){
            for(int i = -VISION_DISTANCE; i<=VISION_DISTANCE; i++){
                for(int j = -VISION_DISTANCE; j<=VISION_DISTANCE; j++){
                    for(int k = -VISION_DISTANCE; k<=VISION_DISTANCE; k++){
                        Plot plot = world.getPlot(x+i, y+j, z+k);
                        if(plot!=null&&plot.owner==player){
                            playerVisibilities.add(player);
                            continue PLAYER;
                        }
                    }
                }
            }
        }
        Plot plot;
        shouldRenderTopFace = (plot=world.getPlot(x, y, z+1))==null||!plot.getType().isOpaque()||plot.fallProgress>0||fallProgress>0;
        shouldRenderLeftFace = (plot=world.getPlot(x-1, y, z))==null||!plot.getType().isOpaque()||plot.fallProgress>0||fallProgress>0;
        shouldRenderRightFace = (plot=world.getPlot(x+1, y, z))==null||!plot.getType().isOpaque()||plot.fallProgress>0||fallProgress>0;
        shouldRenderFrontFace = (plot=world.getPlot(x, y+1, z))==null||!plot.getType().isOpaque()||plot.fallProgress>0||fallProgress>0;
        shouldRenderBackFace = (plot=world.getPlot(x, y-1, z))==null||!plot.getType().isOpaque()||plot.fallProgress>0||fallProgress>0;
        for(Player player : visibilityClone){
            if(!playerVisibilities.contains(player)){
                player.plotDisappear(x-player.offsetX, y-player.offsetY, z);
            }
        }
        for(Player player : playerVisibilities){
            if(!playerVisibilities.contains(player)){
                player.plotAppear(x-player.offsetX, y-player.offsetY, z);
            }
        }
    }
    public void update(){
        if(type.falls()&&world.getPlot(x, y, z-1)!=null&&!world.getPlot(x, y, z-1).type.supports()){
            boolean go = true;
            PlotType atype = world.getPlot(x, y, z-1).type;
            if(type==PlotType.OilDeposit){
                if(atype==PlotType.Sand||atype==PlotType.OilDeposit){
                    go = false;
                }
            }
            if(go){
                fallProgress++;
                world.schedulePlotUpdate(this);
                onNeighborPlotChange();
                if(type==PlotType.Sand&&atype==PlotType.OilDeposit){
                    world.getPlot(x, y, z-1).fallProgress = -fallProgress;
                }else if(fallProgress==1){
                    world.getPlot(x, y, z-1).demolish();
                }
                if(fallProgress>=100){
                    if(type==PlotType.Sand&&atype==PlotType.OilDeposit){
                        Player anowner = world.getPlot(x, y, z-1).owner;
                        world.getPlot(x, y, z-1).setType(PlotType.Sand).setOwner(owner).fallProgress=0;
                        setType(PlotType.OilDeposit).setOwner(anowner);
                    }else{
                        world.getPlot(x, y, z-1).setType(type.getFallenType()).setOwner(owner);
                        setType(PlotType.Air);
                        setOwner(null);
                    }
                    fallProgress=0;
                }
            }
        }
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
        if(getType()!=PlotType.AirportEntrance&&task==null&&!inboundResources.listResources().isEmpty()){
            resources.addAll(inboundResources);
            coming = Math.max(0, coming-inboundResources.count());
            inboundResources = new ResourceList();
        }
        if(owner!=null&&getType()==PlotType.Warehouse){
            world.schedulePlotUpdate(this);
            if(task==null&&resources.count()>(level+1)*owner.getResourcesPerWarehouse()){
                ResourceList lst = resources.split(resources.count()-(level+1)*owner.getResourcesPerWarehouse());
                task = new WorkerTask().setOwner(owner).setCash(0).setPlot(this).setCost(new ResourceList()).setRevenue(lst);
                task.prepare();
                task.cost.remove(Resource.Tools, 1);
                task.revenue.remove(Resource.Tools, 1);
                task.segments.remove(0);
            }
        }else if(owner!=null&&type!=PlotType.AirportEntrance&&task==null&&resources.count()>0){
            task = new WorkerTask().setOwner(owner).setCash(0).setPlot(this).setCost(new ResourceList()).setRevenue(resources);
            task.prepare();
            task.cost.remove(Resource.Tools, 1);
            task.revenue.remove(Resource.Tools, 1);
            task.segments.remove(0);
            world.schedulePlotUpdate(this, 20);
            resources = new ResourceList();
        }
    }
    public Plot[] getSkyscraperPlots(){
        Plot plot = this;
        while(plot.type==PlotType.SkyscraperFloor){
            plot = world.generateAndGetPlot(x, y, plot.z-1);
        }
        if(plot.type!=PlotType.SkyscraperBase){
            return new Plot[0];
        }
        ArrayList<Plot> plots = new ArrayList<>();
        do{
            plots.add(plot);
            plot = world.generateAndGetPlot(x, y, plot.z+1);
        }while(plot.type==PlotType.SkyscraperFloor);
        return plots.toArray(new Plot[plots.size()]);
    }
    public boolean canAddSkyscraperFloor(){
        Plot[] plots = getSkyscraperPlots();
        return plots.length>0&&plots.length<10&&world.getPlot(x, y, z+plots.length).type==PlotType.Air;
    }
    private void doAirportUpdate(){
        if(world.age%20==0&&!inboundAircraft.isEmpty()){
            attemptToLandAircraft(inboundAircraft.remove(0));
        }
        ArrayList<Plot> terminals = new ArrayList<>();
        findTerminals(terminals);
        ResourceList fuel = new ResourceList(Resource.Fuel, Math.max(Math.min(inboundResources.get(Resource.Fuel), 500-terminal.fuel-resources.get(Resource.Fuel)), 0));
        resources.addAll(fuel);
        inboundResources.removeAll(fuel);
        for(Plot plot : terminals){
            plot.terminal.update(terminal);
        }
        terminal.update(terminal);
        terminal.schedule.update(this);
        if(owner!=null&&task==null&&resources.count()>0&&!Path.findWarehouse(this, true).isEmpty()){
            boolean OK = false;
            for(Plot plot : Path.findWarehouse(this, true)){
                if(plot.task==null&&plot.resources.count()<(plot.level+1)*owner.getResourcesPerWarehouse()&&plot.coming==0){
                    OK = true;
                }
            }
            if(!OK){
                return;
            }
            fuel = new ResourceList(Resource.Fuel, resources.get(Resource.Fuel));
            fuel = fuel.split(500-terminal.fuel);
            resources.removeAll(fuel);
            if(resources.count()>0){
                task = new WorkerTask().setOwner(owner).setCash(0).setPlot(this).setCost(new ResourceList()).setRevenue(resources);
                task.prepare();
                task.cost.remove(Resource.Tools, 1);
                task.revenue.remove(Resource.Tools, 1);
                task.segments.remove(0);
                resources = new ResourceList();
            }
            resources.addAll(fuel);
        }else if(owner!=null&&task==null&&terminal.fuel+resources.get(Resource.Fuel)<500&&!Path.findWarehouse(this, true).isEmpty()){
            int available = 0;
            for(Plot plot : Path.findWarehouse(this, true)){
                available+=plot.resources.get(Resource.Fuel);
            }
            if(available==0){
                return;
            }
            int count = Math.min(Math.max(1, available/10), 500-terminal.fuel-resources.get(Resource.Fuel));
            task = new WorkerTask().setOwner(owner).setCash(0).setPlot(this).setCost(new ResourceList(Resource.Fuel, count)).setRevenue(new ResourceList());
            task.prepare();
            task.cost.remove(Resource.Tools, 1);
            task.revenue.remove(Resource.Tools, 1);
            task.segments.remove(1);
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
        Plot plot;
        shouldRenderTopFace = (plot=world.getPlot(x, y, z+1))==null||!plot.getType().isOpaque()||plot.fallProgress>0||fallProgress>0;
        shouldRenderLeftFace = (plot=world.getPlot(x-1, y, z))==null||!plot.getType().isOpaque()||plot.fallProgress>0||fallProgress>0;
        shouldRenderRightFace = (plot=world.getPlot(x+1, y, z))==null||!plot.getType().isOpaque()||plot.fallProgress>0||fallProgress>0;
        shouldRenderFrontFace = (plot=world.getPlot(x, y+1, z))==null||!plot.getType().isOpaque()||plot.fallProgress>0||fallProgress>0;
        shouldRenderBackFace = (plot=world.getPlot(x, y-1, z))==null||!plot.getType().isOpaque()||plot.fallProgress>0||fallProgress>0;
        world.schedulePlotUpdate(this);
    }
    public void render(Player localPlayer){
        if(!playerVisibilities.contains(localPlayer)&&(fallProgress==0||!world.getPlot(x, y, z-1).playerVisibilities.contains(localPlayer))){
            return;
        }
        if(fallProgress>0){
            GL11.glTranslatef(0, 0, -fallProgress/100f);
        }
        type.render(this);
        if(fallProgress>0){
            GL11.glTranslatef(0, 0, fallProgress/100f);
        }
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
        timeSinceLastCivilianOperation++;
        if(world.age%20==0){
            owner.cash+=civiliansPresent.size();
        }
        civilianTime++;
        if(civilianTime>=20){
            doCivilianUpdate();
        }
    }
    private void doCivilianUpdate(){
        if(owner==null){
            return;
        }
        if(getType()!=PlotType.AirportEntrance&&civilians.size()>getMaximumCivilianCapacity()&&!civiliansPresent.isEmpty()){
            civiliansPresent.get(0).timer = 0;
        }
        owner.cash+=civiliansPresent.size();
        civilianTime = 0;
        if(getType()!=PlotType.House&&getSkyscraperPlots().length==0){
            Civilian civilian = civiliansPresent.get(0);
            if(civilian.timer>0){
                Plot house = findEmptyHouse();
                if(house!=null){
                    Path path = Path.findPath(this, house, false);
                    if(path==null){
                        return;
                    }
                    civilian.homePlot = house;
                    house.civilians.add(civilian);
                    civilian.path = path;
                    world.civilians.add(civilian);
                    world.onCivilianAdded(civilian);
                    timeSinceLastCivilianOperation = 0;
                    civilians.remove(civilian);
                    civiliansPresent.remove(civilian);
                }
            }else{
                Plot airport = Path.findAirportEntrance(this, false);
                if(airport!=null){
                    Path path = Path.findPath(this, airport, false);
                    if(path!=null){
                        civilian.path = path;
                        civilian.homePlot = airport;
                        world.civilians.add(civilian);
                        world.onCivilianAdded(civilian);
                        airport.civilians.add(civilian);
                        civilians.remove(civilian);
                        civiliansPresent.remove(civilian);
                    }
                }
            }
        }else{
            for(Iterator<Civilian> it=civilians.iterator(); it.hasNext();){
                Civilian worker=it.next();
                if(shouldCivilianBeUnstable()){
                    worker.timer--;
                }
                if(worker.timer<=0){
                    Plot airport = Path.findAirportEntrance(this, false);
                    if(airport!=null){
                        Path path = Path.findPath(this, airport, false);
                        if(path!=null){
                            worker.path = path;
                            worker.homePlot = airport;
                            it.remove();
                            civiliansPresent.remove(worker);
                            world.civilians.add(worker);
                            world.onCivilianAdded(worker);
                            airport.civilians.add(worker);
                            it.remove();
                            civiliansPresent.remove(worker);
                        }
                    }
                }
            }
            ArrayList<WorkerTask> tasks = new ArrayList<>();
            ArrayList<Plot> plots = findPotentialTasks(tasks, false);
            ArrayList<Civilian> workersAvailable = new ArrayList<>();
            for(Civilian worker : civiliansPresent){
                if(worker.timer>0){
                    workersAvailable.add(worker);
                }
            }
            if(workersAvailable.isEmpty()){
                return;
            }
            Civilian worker = workersAvailable.get(new Random().nextInt(workersAvailable.size()));
            boolean taskSent = false;
            WorkerTask task = this.task;
            if(type==PlotType.SkyscraperFloor){
                task = getSkyscraperPlots()[0].task;
            }
            if(task!=null&&task instanceof CivilianTask&&!(task.isFull()||task.getCurrentSegment().isFull()||!task.canReceiveFrom(this))){
                WorkerTaskSegment segment = task.getCurrentSegment();
                EventSequence sequence = segment.generateEventSequence(worker, this);
                if(sequence!=null){
                    worker.assign(sequence);
                    world.civilians.add(worker);
                    world.onCivilianAdded(worker);
                    civiliansPresent.remove(worker);
                    timeSinceLastCivilianOperation = 0;
                    taskSent = true;
                }
            }
        }
    }
    private void workerUpdate(){
        timeSinceLastWorkerOperation++;
        if(world.age%20==0){
            owner.cash-=workersPresent.size();
        }
        if(timeSinceLastWorkerOperation>=20){
            doWorkerUpdate();
        }
    }
    private void doWorkerUpdate(){
        if(owner==null){
            return;
        }
        if(getType()!=PlotType.AirportEntrance&&workers.size()+civilians.size()>getMaximumCivilianCapacity()&&!workersPresent.isEmpty()){
            workersPresent.get(0).timer = 0;
        }
        if(getType()!=PlotType.House&&getSkyscraperPlots().length==0){
            for(Worker worker : workers){
                worker.timer--;
            }
            if(getType()!=PlotType.AirportEntrance){
                Worker worker = workersPresent.get(0);
                if(worker.timer>0){
                    Plot house = findEmptyHouse();
                    if(house!=null){
                        Path path = Path.findPath(this, house, false);
                        if(path==null){
                            return;
                        }
                        worker.homePlot = house;
                        house.workers.add(worker);
                        worker.path = path;
                        world.civilians.add(worker);
                        world.onCivilianAdded(worker);
                        timeSinceLastCivilianOperation = 0;
                    }
                }else{
                    Plot airport = Path.findAirportEntrance(this, false);
                    if(airport!=null){
                        Path path = Path.findPath(this, airport, true);
                        if(path!=null){
                            worker.path = path;
                            worker.homePlot = airport;
                            world.civilians.add(worker);
                            world.onCivilianAdded(worker);
                            airport.workers.add(worker);
                        }
                    }
                }
            }
        }else{
            for(Worker worker : workersPresent){
                if(worker.timer<=0){
                    Plot airport = Path.findAirportEntrance(this, true);
                    if(airport!=null){
                        Path path = Path.findPath(this, airport, true);
                        if(path!=null){
                            worker.path = path;
                            worker.homePlot = airport;
                            while(workers.remove(worker));
                            workersPresent.remove(worker);
                            world.civilians.add(worker);
                            world.onCivilianAdded(worker);
                            airport.workers.add(worker);
                            break;
                        }
                    }
                }
            }
        }
        ArrayList<WorkerTask> tasks = new ArrayList<>();
        ArrayList<Plot> plots = findPotentialTasks(tasks, true);
        if(lastTaskTimeWorker==world.age&&taskWorkerFailed){
            taskWorkerFailed = false;
            return;
        }
        ArrayList<Worker> workersAvailable = new ArrayList<>();
        Worker worker = null;
        for(Worker aworker : workersPresent){
            if(aworker.timer>0){
                worker = aworker;
                break;
            }
        }
        if(worker==null){
            return;
        }
        boolean taskSent = false;
        for(WorkerTask potentialTask : tasks){
            if(potentialTask.getCurrentSegment()==null||((potentialTask instanceof CivilianTask)&&potentialTask.getCurrentSegment().type.equals("Train Worker"))||potentialTask.isFull()||potentialTask.getCurrentSegment().isFull()||!potentialTask.canReceiveFrom(this)){
                continue;
            }
            WorkerTaskSegment segment = potentialTask.getCurrentSegment();
            EventSequence sequence = segment.generateEventSequence(worker, this);
            if(sequence!=null){
                worker.assign(sequence);
                world.civilians.add(worker);
                world.onCivilianAdded(worker);
                workersPresent.remove(worker);
                timeSinceLastWorkerOperation = 0;
                taskSent = true;
                break;
            }
        }
        if(!taskSent){
            for(Plot plot : plots){
                plot.taskWorkerFailed = true;
            }
            taskWorkerFailed = false;
        }
    }
    private ArrayList<Plot> findPotentialTasks(ArrayList<WorkerTask> tasks, boolean isWorker){
        if(owner==null){
            return new ArrayList<>();
        }
        if(isWorker&&lastTaskTimeWorker==world.age){
            tasks.addAll(lastTasksWorker);
            return new ArrayList<>();
        }else if(!isWorker&&lastTaskTimeCivilian==world.age){
            tasks.addAll(lastTasksCivilian);
            return new ArrayList<>();
        }
        return Path.findPotentialTasks(tasks, this, isWorker);
    }
    public ArrayList<Side> getPathableSides(boolean isWorker){
        ArrayList<Side> sides = new ArrayList<>();
        for(Side side : type.getPathableSides(isWorker)){
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
    public ArrayList<Side> getTravelableSides(boolean isWorker){
        ArrayList<Side> sides = getPathableSides(isWorker);
        ArrayList<Side> val = new ArrayList<>();
        for(Side side : sides){
            Plot plot = side.getPlot(world, x, y, z);
            if(plot==null){
                continue;
            }
            if(plot.getPathableSides(isWorker).contains(side.reverse())){
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
        if(getType()!=PlotType.House&&getSkyscraperPlots().length==0){
            while(!civiliansPresent.isEmpty()){
                Civilian civilian = civiliansPresent.remove(0);
                while(civilians.remove(civilian));
                if(civilian.timer>0){
                    Plot house = findEmptyHouse();
                    if(house!=null){
                        Path path = Path.findPath(this, house, false);
                        if(path==null){
                            return;
                        }
                        civilian.homePlot = house;
                        house.civilians.add(civilian);
                        civilian.path = path;
                        world.civilians.add(civilian);
                        world.onCivilianAdded(civilian);
                        timeSinceLastCivilianOperation = 0;
                    }
                }else{
                    Plot airport = Path.findAirportEntrance(this, true);
                    if(airport!=null){
                        Path path = Path.findPath(this, airport, true);
                        if(path!=null){
                            civilian.path = path;
                            civilian.homePlot = airport;
                            world.civilians.add(civilian);
                            world.onCivilianAdded(civilian);
                            airport.civilians.add(civilian);
                            break;
                        }
                    }
                }
            }
        }
    }
    public boolean canUpgrade(Race race){
        return level+1<type.getMaximumLevel()&&(type.getCost(level+1, race)!=null||world.localPlayer.sandbox);
    }
    private Plot findEmptyHouse(){
        return Path.findHouseWithSpace(this, false);
    }
    public int getMaximumCivilianCapacity(){
        return (int)Math.round(Math.max((level+1)*(level+1)*world.difficulty.homeOccupantModifier, 1));
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("x", x);
        config.set("y", y);
        config.set("z", z);
        config.set("type", type.name());
        if(level!=0){
            config.set("level", level);
        }
        if(owner!=null){
            config.set("owner", world.otherPlayers.indexOf(owner));
        }
        config.set("frameBoost", frameBoost);
        config.set("front", front.name());
        config.set("terminal", terminal.save());
        if(!civiliansPresent.isEmpty()){
            Config two = Config.newConfig();
            two.set("count", civiliansPresent.size());
            for(int i = 0; i<civiliansPresent.size(); i++){
                two.set(i+"", civiliansPresent.get(i).save());
            }
            config.set("civilians", two);
            config.set("timeCivilian", timeSinceLastCivilianOperation);
        }
        if(!workersPresent.isEmpty()){
            Config two = Config.newConfig();
            two.set("count", workersPresent.size());
            for(int i = 0; i<workersPresent.size(); i++){
                two.set(i+"", workersPresent.get(i).save());
            }
            config.set("workers", two);
            config.set("timeWorker", timeSinceLastWorkerOperation);
        }
        if(task!=null){
            config.set("task", task.save());
        }
        if(resources.count()!=0){
            config.set("resources", resources.save());
        }
        if(readyResources.count()!=0){
            config.set("readyResources", readyResources.save());
        }
        if(inboundResources.count()!=0){
            config.set("inboundResources", inboundResources.save());
        }
        if(!inboundAircraft.isEmpty()){
            Config two = Config.newConfig();
            two.set("count", inboundAircraft.size());
            for(int i = 0; i<inboundAircraft.size(); i++){
                two.set(i+"", inboundAircraft.get(i).save());
            }
            config.set("aircraft", two);
        }
        config.set("coming", coming);
        return config;
    }
    private void demolish(){
        setType(PlotType.Air);
    }
    public void load(Config get){
        setType(PlotType.valueOf((String)get.get("type")));
        if(get.hasProperty("level")){
            setLevel((int)get.get("level"));
        }
        int whichOwner = get.hasProperty("owner")?(int)get.get("owner"):-2;
        if(whichOwner==-1){
            setOwner(world.localPlayer);
        }else if(whichOwner>=0){
            setOwner(world.otherPlayers.get(whichOwner));
        }
        frameBoost = get.get("frameBoost");
        front = Side.valueOf((String)get.get("front"));
        if(get.hasProperty("civilians")){
            Config two = get.get("civilians");
            for(int i = 0; i<(int)two.get("count"); i++){
                Civilian civilian = Civilian.load((Config)two.get(i+""));
                civiliansPresent.add(civilian);
                civilians.add(civilian);
            }
            timeSinceLastCivilianOperation = get.get("timeCivilian");
        }
        if(get.hasProperty("workers")){
            Config two = get.get("workers");
            for(int i = 0; i<(int)two.get("count"); i++){
                Worker worker = (Worker)Civilian.load((Config)two.get(i+""));
                workersPresent.add(worker);
                workers.add(worker);
            }
            timeSinceLastWorkerOperation = get.get("timeWorker");
        }
        if(get.hasProperty("task")){
            task = WorkerTask.load((Config)get.get("task"));
        }
        if(get.hasProperty("resources")){
            resources = ResourceList.load((Config)get.get("resources"));
        }
        if(get.hasProperty("readyResources")){
            readyResources = ResourceList.load((Config)get.get("readyResources"));
        }
        if(get.hasProperty("inboundResources")){
            inboundResources = ResourceList.load((Config)get.get("inboundResources"));
        }
        if(get.hasProperty("aircraft")){
            Config two = get.get("aircraft");
            for(int i = 0; i<(int)two.get("count"); i++){
                inboundAircraft.add(Aircraft.load((Config)two.get(i+"")));
            }
        }
        coming = get.get("coming");
    }
    public void eraseAndLoad(Config cnfg){
        civilians.clear();
        civiliansPresent.clear();
        workers.clear();
        workersPresent.clear();
        inboundAircraft.clear();
        owner = null;
        timeSinceLastCivilianOperation = 0;
        timeSinceLastWorkerOperation = 0;
        task = null;
        resources = new ResourceList();
        readyResources = new ResourceList();
        coming = 0;
        load(cnfg);
    }
    private boolean shouldCivilianBeUnstable(){
        double stability = 0.9;
        double stabilityModifier = world.difficulty.stabilityModifier;
        stability = Math.pow(stability, 1/stabilityModifier);
        return rand.nextFloat()>stability;
    }
}
