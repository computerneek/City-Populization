package CityPopulization.world.civilian;
import CityPopulization.Core;
import CityPopulization.render.Side;
import CityPopulization.world.player.Player;
import CityPopulization.world.player.PlayerHuman;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import simplelibrary.config2.Config;
public abstract class WorkerTaskSegment {
    public final String type;
    boolean workAllowed;
    public WorkerTask task;
    public static final String RESOURCE_COLLECTION = "Resource Collection";
    public static final String RESOURCE_RETURNS = "Resource Return";
    public static final String PLOT_TYPE = "Plot Type";
    public static final String TRAIN_WORKER = "Train Worker";
    public static final String CONCURRENT = "Concurrent";
    public static final String SEQUENTIAL = "Sequential";
    static final ArrayList<WorkerTaskSegment> allSegments = new ArrayList<>();
    private WorkerTaskSegment(String type){
        this.type = type;
    }
    public boolean isFull(){
        return !couldUseCivilian((Civilian)null)&&!couldUseWorker((Civilian)null);
    }
/**    public EventSequence generateEventSequence(Civilian worker, Plot home){
//        EventSequence sequence = new EventSequence();
//        if(type.equals("Resource Collection")){
//        }else if(type.equals("Plot Type")){
//        }else if(type.equals("Resource Returns")){
//            Plot plot = findWarehouse(worker instanceof Worker);
//            if(plot==null){
//                return null;
//            }
//            ResourceList loaded = getResourcesToOffload(plot);
//            sequence.add(new EventPath(Path.findPath(home, task.altPlot, worker instanceof Worker)));
//            sequence.add(new EventLoad(loaded));
//            sequence.add(new EventSatisfy(this));
//            sequence.add(new EventPath(Path.findPath(task.altPlot, plot, worker instanceof Worker)));
//            sequence.add(new EventUnload());
//            sequence.add(new EventPath(Path.findPath(plot, home, worker instanceof Worker)));
//            if(sequence.validate()&&loaded.count()>0){
//                resources.removeAll(loaded);
//                plot.coming+=loaded.count();
//            }else if(resources.count()>0){
//                return null;
//            }else{
//                requiredWorkers--;
//                return null;
//            }
//        }else if(type.equals("Train Worker")){
//            sequence.add(new EventPath(Path.findPath(home, task.altPlot, worker instanceof Worker)));
//            sequence.add(new EventWait(100));
//            sequence.add(new EventTrainWorker());
//            sequence.add(new EventSatisfy(this));
//            sequence.add(new EventPath(Path.findPath(task.altPlot, home, worker instanceof Worker)));
//            if(!sequence.validate()){
//                return null;
//            }
//        }else{
//            throw new AssertionError(type);
//        }
//        switch(type){
//            case "Resource Collection":
//            case "Resource Returns":
//                if(resources.count()>0){
//                    requiredWorkers++;
//                }
//        }
//        task.started = true;
//        workers++;
//        return sequence;
    }*/
    public void setParentTask(WorkerTask task){
        this.task = task;
    }
    public abstract void write(Config config);
    private static synchronized void addSegment(WorkerTaskSegment segment){
        if(allSegments.indexOf(segment)>=0){
            return;
        }
        for(int i = 0; i<allSegments.size(); i++){
            if(allSegments.get(i)==null){
                allSegments.set(i, segment);
                return;
            }
        }
        allSegments.add(segment);
    }
    private static synchronized void removeSegment(WorkerTaskSegment segment){
        int index = allSegments.indexOf(segment);
        if(index>=0){
            allSegments.set(index, null);
        }
    }
    protected Plot findResourcePlot(ResourceList resources, boolean isWorker){
        return Path.findResourcePlot(task.targetPlot, resources, isWorker);
    }
    protected Plot findWarehouse(boolean isWorker){
        for(Plot plot : Path.findWarehouse(task.targetPlot, isWorker)){
            ResourceList lst = new ResourceList().addAll(plot.resources).addAll(plot.readyResources);
            int count = lst.count()+plot.coming;
            int canHarvest = task.owner.getResourcesPerWarehouse()*(plot.getLevel()+1)-count;
            if(canHarvest>0){
                return plot;
            }
        }
        return null;
    }
    protected Plot findAirport(boolean isWorker){
        return Path.findAirportEntrance(task.targetPlot, isWorker);
    }
    protected Plot findWorkshop(boolean isWorker){
        return Path.findWorkshop(task.targetPlot, isWorker);
    }
    protected ResourceList getResourcesToLoad(ResourceList resources, Plot plot){
        ResourceList list = new ResourceList();
        int count = 0;
        int maxResources = task.owner.race.getWorkerResourceCapacity(plot.world);
        for(Resource resource : resources.listResources()){
            int inPlot = plot.resources.get(resource);
            int canLoad = Math.min(inPlot, resources.get(resource));
            int toLoad = Math.min(canLoad, maxResources-count);
            if(toLoad>0){
                list.add(resource, toLoad);
                count+=toLoad;
            }
        }
        return list;
    }
    protected ResourceList getResourcesToOffload(ResourceList resources, Plot plot){
        ResourceList list = new ResourceList();
        int maxResources = Math.max(0, Math.min(task.owner.race.getWorkerResourceCapacity(plot.world), plot.getType()==CityPopulization.world.plot.PlotType.Warehouse?(task.owner.getResourcesPerWarehouse()*(plot.getLevel()+1)-plot.resources.count()-plot.coming):10));
        list.addAll(resources);
        while(list.count()>maxResources){
            list.remove(list.listResources().get(0), 1);
        }
        return list;
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", type);
        write(config);
        return config;
    }
    public static WorkerTaskSegment load(Config get){
        switch((String)get.get("type")){
            case RESOURCE_COLLECTION:
                return ResourceCollection.read(get);
            case RESOURCE_RETURNS:
                return ResourceReturns.read(get);
            case PLOT_TYPE:
                return PlotType.read(get);
            case TRAIN_WORKER:
                return TrainWorker.read(get);
            case CONCURRENT:
                return Concurrent.read(get);
            case SEQUENTIAL:
                return Sequential.read(get);
            default:
                throw new AssertionError((String)get.get("type"));
        }
    }
    public abstract boolean isComplete();
    public abstract boolean couldUseMoreCivilians(Plot plot);
    public abstract Civilian couldUseCivilian(ArrayList<Civilian> workersAssigned);
    public abstract boolean couldUseMoreWorkers(Plot plot);
    public abstract Civilian couldUseWorker(ArrayList<Civilian> workersAssigned);
    public abstract boolean couldUseCivilian(Civilian worker);
    public abstract boolean couldUseWorker(Civilian worker);
    public ArrayList<WorkerTaskSegment> getConcurrentSegments(WorkerTaskSegment afterCompleted){
        return isComplete()?new ArrayList<WorkerTaskSegment>():new ArrayList<>(Arrays.asList(this));
    }
    public abstract void useCivilian(Civilian worker);
    public abstract void useWorker(Civilian worker);
    public void authorizeWork(){
        workAllowed = true;
        addSegment(this);
    }
    public abstract void fail(WorkerTask.Subtask subtask, ArrayList<Object> commandsLeft);
    public abstract void complete(WorkerTask.Subtask s);
    public abstract void onChildLoaded(WorkerTask.Subtask s);
    public static class Sequential extends WorkerTaskSegment{
        private static WorkerTaskSegment read(Config get){
            Sequential s = new Sequential();
            for(int i = 0; get.hasProperty(i+""); i++){
                s.addSegment(load(get.get(i+"", Config.newConfig())));
            }
            return s;
        }
        private ArrayList<WorkerTaskSegment> subtasks = new ArrayList<>();
        public Sequential(){
            super(SEQUENTIAL);
        }
        public void addSegment(WorkerTaskSegment segment){
            subtasks.add(segment);
        }
        @Override
        public void complete(WorkerTask.Subtask s){}
        @Override
        public Civilian couldUseCivilian(ArrayList<Civilian> workersAssigned){
            return null;
        }
        @Override
        public boolean couldUseCivilian(Civilian worker){
            return false;
        }
        @Override
        public boolean couldUseMoreCivilians(Plot plot){
            return false;
        }
        @Override
        public boolean couldUseMoreWorkers(Plot plot){
            return false;
        }
        @Override
        public Civilian couldUseWorker(ArrayList<Civilian> workersAssigned){
            return null;
        }
        @Override
        public boolean couldUseWorker(Civilian worker){
            return false;
        }
        @Override
        public void fail(WorkerTask.Subtask subtask, ArrayList<Object> commandsLeft){}
        @Override
        public boolean isComplete(){
            return getConcurrentSegments(null).isEmpty();
        }
        @Override
        public void onChildLoaded(WorkerTask.Subtask s){}
        @Override
        public void useCivilian(Civilian worker){}
        @Override
        public void useWorker(Civilian worker){}
        @Override
        public void write(Config config){
            for(int i = 0; i<subtasks.size(); i++){
                config.set(i+"", subtasks.get(i).save());
            }
        }
        @Override
        public ArrayList<WorkerTaskSegment> getConcurrentSegments(WorkerTaskSegment afterCompleted){
            for(WorkerTaskSegment seg : subtasks){
                if(!seg.isComplete()&&seg!=afterCompleted){
                    return seg.getConcurrentSegments(afterCompleted);
                }
            }
            return new ArrayList<>();
        }
        @Override
        public void setParentTask(WorkerTask task){
            super.setParentTask(task);
            for(WorkerTaskSegment s : subtasks){
                s.setParentTask(task);
            }
        }
    }
    public static class Concurrent extends WorkerTaskSegment{
        private static WorkerTaskSegment read(Config get){
            Concurrent s = new Concurrent();
            for(int i = 0; get.hasProperty(i+""); i++){
                s.addSegment(load(get.get(i+"", Config.newConfig())));
            }
            return s;
        }
        private ArrayList<WorkerTaskSegment> subtasks = new ArrayList<>();
        public Concurrent(){
            super(CONCURRENT);
        }
        public void addSegment(WorkerTaskSegment segment){
            subtasks.add(segment);
        }
        @Override
        public void complete(WorkerTask.Subtask s){}
        @Override
        public Civilian couldUseCivilian(ArrayList<Civilian> workersAssigned){
            return null;
        }
        @Override
        public boolean couldUseCivilian(Civilian worker){
            return false;
        }
        @Override
        public boolean couldUseMoreCivilians(Plot plot){
            return false;
        }
        @Override
        public boolean couldUseMoreWorkers(Plot plot){
            return false;
        }
        @Override
        public Civilian couldUseWorker(ArrayList<Civilian> workersAssigned){
            return null;
        }
        @Override
        public boolean couldUseWorker(Civilian worker){
            return false;
        }
        @Override
        public void fail(WorkerTask.Subtask subtask, ArrayList<Object> commandsLeft){}
        @Override
        public boolean isComplete(){
            return getConcurrentSegments(null).isEmpty();
        }
        @Override
        public void onChildLoaded(WorkerTask.Subtask s){}
        @Override
        public void useCivilian(Civilian worker){}
        @Override
        public void useWorker(Civilian worker){}
        @Override
        public void write(Config config){
            for(int i = 0; i<subtasks.size(); i++){
                config.set(i+"", subtasks.get(i).save());
            }
        }
        @Override
        public ArrayList<WorkerTaskSegment> getConcurrentSegments(WorkerTaskSegment afterCompleted){
            ArrayList<WorkerTaskSegment> val = new ArrayList<>();
            for(WorkerTaskSegment seg : subtasks){
                if(seg!=afterCompleted){
                    val.addAll(seg.getConcurrentSegments(afterCompleted));
                }
            }
            return val;
        }
        @Override
        public void setParentTask(WorkerTask task){
            super.setParentTask(task);
            for(WorkerTaskSegment s : subtasks){
                s.setParentTask(task);
            }
        }
    }
    public static class PlotType extends WorkerTaskSegment{
        private static WorkerTaskSegment read(Config get){
            Side front = Side.valueOf(get.get("front", "FRONT"));
            int level = get.get("level", 0);
            Player owner = null;
            if(get.hasProperty("owner")){
                owner = get.get("owner", -1)<0?Core.loadingWorld.getLocalPlayer():Core.loadingWorld.otherPlayers.get(get.get("owner", -1));
            }
            CityPopulization.world.plot.PlotType type = CityPopulization.world.plot.PlotType.valueOf(get.get("ptype", "AIR"));
            Plot p = Core.loadingWorld.findPlot(get.get("x", 0), get.get("y", 0), get.get("z", 0));
            boolean complete = get.get("complete", false);
            return new PlotType(p, type, level, front, owner, complete);
        }
        private final Side front;
        private final int level;
        private final Player owner;
        private final CityPopulization.world.plot.PlotType type;
        private final Plot plot;
        private boolean ordered;
        private boolean complete;
        public PlotType(Plot plot, CityPopulization.world.plot.PlotType type, int level, Side front, Player owner){
            this(plot, type, level, front, owner, false);
        }
        PlotType(Plot plot, CityPopulization.world.plot.PlotType type, int level, Side front, Player owner, boolean complete){
            super(PLOT_TYPE);
            this.type = type;
            this.level = level;
            this.front = front;
            this.owner = owner;
            this.plot = plot;
            this.complete = complete;
        }
        @Override
        public void complete(WorkerTask.Subtask s){}
        @Override
        public Civilian couldUseCivilian(ArrayList<Civilian> workersAssigned){
            return null;
        }
        @Override
        public boolean couldUseCivilian(Civilian worker){
            return false;
        }
        @Override
        public boolean couldUseMoreCivilians(Plot plot){
            return false;
        }
        @Override
        public boolean couldUseMoreWorkers(Plot plot){
            return couldUseWorker(new ArrayList<Civilian>(plot.workersPresent))!=null;
        }
        @Override
        public Civilian couldUseWorker(ArrayList<Civilian> workersAssigned){
            for(Civilian c : workersAssigned){
                if(c.worker&&couldUseWorker(c)){
                    return c;
                }
            }
            return null;
        }
        @Override
        public boolean couldUseWorker(Civilian worker){
            if(worker==null){
                return !ordered&&!isComplete();
            }
            return !ordered&&!isComplete()&&worker.subtask==null&&Path.findPath(worker.getCurrentPlot(), plot, worker.worker)!=null;
        }
        @Override
        public void fail(WorkerTask.Subtask subtask, ArrayList<Object> commandsLeft){
            ordered = false;
        }
        @Override
        public boolean isComplete(){
            if(complete) return true;
            boolean val = plot.type==type&&plot.level==level&&plot.getFront()==front&&plot.owner==owner;
            if(val){
                removeSegment(this);
                complete = true;
            }
            return val;
        }
        @Override
        public void onChildLoaded(WorkerTask.Subtask s){
            ordered = true;
        }
        @Override
        public void useCivilian(Civilian worker){}
        @Override
        public void useWorker(Civilian worker){
            worker.subtask = new WorkerTask.Subtask(this).goTo(plot).plotType(type, level, front, owner);
            ordered = true;
        }
        @Override
        public void write(Config config){
            config.set("front", front.name());
            config.set("level", level);
            if(owner!=null){
                config.set("owner", owner.world.otherPlayers.indexOf(owner));
            }
            config.set("ptype", type.name());
            config.set("x", plot.x);
            config.set("y", plot.y);
            config.set("z", plot.z);
            config.set("complete", complete);
        }
    }
    public static class ResourceCollection extends WorkerTaskSegment{
        private static WorkerTaskSegment read(Config get){
            ResourceList cost = ResourceList.load(get.get("cost", Config.newConfig()));
            ResourceList left = ResourceList.load(get.get("left", Config.newConfig()));
            Plot target = Core.loadingWorld.findPlot(get.get("x", 0), get.get("y", 0), get.get("z", 0));
            ResourceCollection r = new ResourceCollection(cost, target);
            r.left.clear().addAll(left);
            return r;
        }
        private final ResourceList cost;
        private ArrayList<WorkerTask.Subtask> pending = new ArrayList<>();
        private final Plot target;
        private final ResourceList left;
        public ResourceCollection(ResourceList cost, Plot targetPlot){
            super(RESOURCE_COLLECTION);
            this.cost = cost;
            this.left = new ResourceList().addAll(cost);
            this.target = targetPlot;
        }
        @Override
        public void complete(WorkerTask.Subtask s){
            pending.remove(s);
        }
        @Override
        public Civilian couldUseCivilian(ArrayList<Civilian> workersAssigned){
            for(Civilian c : workersAssigned){
                if(couldUseCivilian(c)){
                    return c;
                }
            }
            return null;
        }
        @Override
        public boolean couldUseCivilian(Civilian worker){
            return couldUseWorker(worker);
        }
        @Override
        public boolean couldUseMoreCivilians(Plot plot){
            return couldUseCivilian(plot.civiliansPresent)!=null;
        }
        @Override
        public boolean couldUseMoreWorkers(Plot plot){
            return couldUseWorker(new ArrayList<Civilian>(plot.workersPresent))!=null;
        }
        @Override
        public Civilian couldUseWorker(ArrayList<Civilian> workersAssigned){
            for(Civilian c : workersAssigned){
                if(couldUseWorker(c)){
                    return c;
                }
            }
            return null;
        }
        @Override
        public boolean couldUseWorker(Civilian worker){
            if(worker!=null&&worker.subtask!=null){
                return false;
            }
            if(worker==null){
                return left.count()>0;
            }
            Path path = Path.findPath(worker.getCurrentPlot(), target, worker.worker);
            return path!=null&&findResourcePlot(left, worker.worker)!=null&&!left.isEmpty();
        }
        @Override
        public void fail(WorkerTask.Subtask subtask, ArrayList<Object> commandsLeft){
            pending.remove(subtask);
            for(Object obj : commandsLeft){
                if(obj instanceof ResourceList){
                    cost.addAll((ResourceList)obj);
                }
            }
        }
        @Override
        public boolean isComplete(){
            if(workAllowed&&task.owner!=null&&task.owner.sandbox){
                return true;
            }
            boolean val = left.count()<1&&pending.isEmpty();
            if(val){
                removeSegment(this);
            }
            return val;
        }
        @Override
        public void onChildLoaded(WorkerTask.Subtask s){
            pending.add(s);
        }
        @Override
        public void useCivilian(Civilian worker){
            useWorker(worker);
        }
        @Override
        public void useWorker(Civilian worker){
            Plot p = findResourcePlot(left, worker.worker);
            if(p==null){
                return;
            }
            ResourceList loaded = getResourcesToLoad(left, p);
            worker.subtask = new WorkerTask.Subtask(this).goTo(p).pickup(loaded).goTo(target).setToPlot(false);
            left.removeAll(loaded);
            p.readyResources(loaded);
            pending.add(worker.subtask);
        }
        @Override
        public void write(Config config){
            config.set("cost", cost.save());
            config.set("left", left.save());
            config.set("x", target.x);
            config.set("y", target.y);
            config.set("z", target.z);
        }
        @Override
        public boolean isFull(){
            return left.isEmpty();
        }
    }
    public static class ResourceReturns extends WorkerTaskSegment{
        private static WorkerTaskSegment read(Config get){
            Plot p = Core.loadingWorld.findPlot(get.get("x", 0), get.get("y", 0), get.get("z", 0));
            ResourceList r = ResourceList.load(get.get("revenue", Config.newConfig()));
            return new ResourceReturns(r, p);
        }
        private final ResourceList revenue;
        private ArrayList<WorkerTask.Subtask> subtasks = new ArrayList<>();
        private final Plot target;
        public ResourceReturns(ResourceList revenue, Plot targetPlot){
            super(RESOURCE_RETURNS);
            this.revenue = revenue;
            this.target = targetPlot;
        }
        @Override
        public void complete(WorkerTask.Subtask s){
            subtasks.remove(s);
        }
        @Override
        public Civilian couldUseCivilian(ArrayList<Civilian> workersAssigned){
            for(Civilian c : workersAssigned){
                if(couldUseCivilian(c)){
                    return c;
                }
            }
            return null;
        }
        @Override
        public boolean couldUseCivilian(Civilian worker){
            return couldUseWorker(worker);
        }
        @Override
        public boolean couldUseMoreCivilians(Plot plot){
            return couldUseCivilian(plot.civiliansPresent)!=null;
        }
        @Override
        public boolean couldUseMoreWorkers(Plot plot){
            return couldUseWorker(new ArrayList<Civilian>(plot.workersPresent))!=null;
        }
        @Override
        public Civilian couldUseWorker(ArrayList<Civilian> workersAssigned){
            for(Civilian c : workersAssigned){
                if(couldUseWorker(c)){
                    return c;
                }
            }
            return null;
        }
        @Override
        public boolean couldUseWorker(Civilian worker){
            if(isComplete()||(worker!=null&&worker.subtask!=null)){
                return false;
            }
            Plot p;
            if(worker==null){
                p = findWarehouse(true);
            }else{
                p = findWarehouse(worker.worker);
            }
            return p!=null&&new ResourceList().addAll(p.resources).addAll(p.readyResources).count()+p.coming<task.owner.getResourcesPerWarehouse()*(p.getLevel()+1)&&!revenue.isEmpty();
        }
        @Override
        public void fail(WorkerTask.Subtask subtask, ArrayList<Object> commandsLeft){
            subtasks.remove(subtask);
            for(Object obj : commandsLeft){
                if(obj instanceof ResourceList){
                    revenue.addAll((ResourceList)obj);
                }
            }
        }
        @Override
        public boolean isComplete(){
            if(workAllowed&&task.owner!=null&&task.owner.sandbox){
                return true;
            }
            boolean val = revenue.count()<1;
            if(val){
                for(WorkerTask.Subtask s : subtasks){
                    for(Object o : s.tasks){
                        if(o instanceof ResourceList){
                            val = false;
                        }
                    }
                }
            }
            if(val){
                removeSegment(this);
            }
            return val;
        }
        @Override
        public void onChildLoaded(WorkerTask.Subtask s){
            subtasks.add(s);
        }
        @Override
        public void useCivilian(Civilian worker){
            useWorker(worker);
        }
        @Override
        public void useWorker(Civilian worker){
            Plot plot = findWarehouse(worker.worker);
            if(plot==null){
                return;
            }
            ResourceList loaded = getResourcesToOffload(revenue, plot);
            worker.subtask = new WorkerTask.Subtask(this).goTo(target).pickup(loaded).goTo(plot);
            revenue.removeAll(loaded);
            plot.coming += loaded.count();
            subtasks.add(worker.subtask);
        }
        @Override
        public void write(Config config){
            config.set("revenue", revenue.save());
            config.set("x", target.x);
            config.set("y", target.y);
            config.set("z", target.z);
        }
        @Override
        public boolean isFull(){
            return revenue.isEmpty();
        }
    }
    public static class TrainWorker extends WorkerTaskSegment{
        private static WorkerTaskSegment read(Config get){
            return new TrainWorker(Core.loadingWorld.findPlot(get.get("x", 0), get.get("y", 0), get.get("z", 0)), get.get("count", 0));
        }
        private final Plot plot;
        private int quantity;
        private ArrayList<WorkerTask.Subtask> subtasks = new ArrayList<>();
        public TrainWorker(Plot plot, int quantity){
            super(TRAIN_WORKER);
            this.plot = plot;
            this.quantity = quantity;
        }
        @Override
        public void complete(WorkerTask.Subtask s){
            subtasks.remove(s);
        }
        @Override
        public Civilian couldUseCivilian(ArrayList<Civilian> workersAssigned){
            for(Civilian c : workersAssigned){
                if(!c.worker&&couldUseCivilian(c)){
                    return c;
                }
            }
            return null;
        }
        @Override
        public boolean couldUseCivilian(Civilian worker){
            return quantity>0&&(worker==null?true:Path.findPath(worker.getCurrentPlot(), plot, false)!=null);
        }
        @Override
        public boolean couldUseMoreCivilians(Plot plot){
            return couldUseCivilian(plot.civiliansPresent)!=null;
        }
        @Override
        public boolean couldUseMoreWorkers(Plot plot){
            return false;
        }
        @Override
        public Civilian couldUseWorker(ArrayList<Civilian> workersAssigned){
            return null;
        }
        @Override
        public boolean couldUseWorker(Civilian worker){
            return false;
        }
        @Override
        public void fail(WorkerTask.Subtask subtask, ArrayList<Object> commandsLeft){
            subtasks.remove(subtask);
            for(Object o : commandsLeft){
                if(o instanceof String){
                    quantity++;
                }
            }
        }
        @Override
        public boolean isComplete(){
            return quantity<1&&subtasks.isEmpty();
        }
        @Override
        public void onChildLoaded(WorkerTask.Subtask s){
            subtasks.add(s);
        }
        @Override
        public void useCivilian(Civilian worker){
            worker.subtask = new WorkerTask.Subtask(this).goTo(plot).setTrain();
            subtasks.add(worker.subtask);
            quantity--;
        }
        @Override
        public void useWorker(Civilian worker){}
        @Override
        public void write(Config config){
            config.set("x", plot.x);
            config.set("y", plot.y);
            config.set("z", plot.z);
            config.set("count", quantity);
        }
    }
}
