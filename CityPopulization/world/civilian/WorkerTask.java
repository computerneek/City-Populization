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
import simplelibrary.config2.Config;
public class WorkerTask{
    public Plot targetPlot;
    public Plot altPlot;
    public boolean started;
    public Player owner;
    public ResourceList cost = new ResourceList();
    public ResourceList revenue = new ResourceList();
    public ArrayList<WorkerTaskSegment> segments = new ArrayList<>();
    public int cash;
    private Plot plotRestriction;
    public ArrayList<Civilian> workersAssigned = new ArrayList<>();
    public WorkerTask(){}
    public void assign(Civilian worker){
        worker.assign(this);
        workersAssigned.add(worker);
    }
    public boolean couldUseMoreCivilians(Plot plot){
        if(!(this instanceof CivilianTask)){
            return false;
        }
        for(WorkerTaskSegment seg : getConcurrentSegments(null)){
            if(seg.couldUseCivilian(workersAssigned)==null&&seg.couldUseMoreCivilians(plot)){
                return true;
            }
        }
        return false;
    }
    public boolean couldUseMoreWorkers(Plot plot){
        for(WorkerTaskSegment seg : getConcurrentSegments(null)){
            if(seg.couldUseWorker(workersAssigned)==null&&seg.couldUseMoreWorkers(plot)){
                return true;
            }
        }
        return false;
    }
    public WorkerTask setPlot(Plot plot){
        this.targetPlot = plot;
        this.altPlot = plot;
        return this;
    }
    public WorkerTask setAltPlot(Plot plot){
        this.altPlot = plot;
        return this;
    }
    public WorkerTask setCost(ResourceList cost){
        this.cost = new ResourceList().addAll(cost).add(Resource.Tools, 1);
        return this;
    }
    public WorkerTask setRevenue(ResourceList revenue){
        this.revenue = new ResourceList().addAll(revenue).add(Resource.Tools, 1);
        return this;
    }
    public WorkerTask addSegment(WorkerTaskSegment segment){
        segments.add(segment);
        return this;
    }
    public WorkerTask setOwner(Player owner){
        this.owner = owner;
        return this;
    }
    public boolean isFull(){
        return !couldUseMoreCivilians(null)&&!couldUseMoreWorkers(null);
    }
    public WorkerTask configure(){
        if(!Core.world.localPlayer.sandbox){
            segments.add(0, new WorkerTaskSegment.ResourceCollection(cost, targetPlot));
            segments.add(new WorkerTaskSegment.ResourceReturns(revenue, targetPlot));
        }
        return prepare();
    }
    public WorkerTask start(){
        owner.cash-=cash;
        for(WorkerTaskSegment s : getConcurrentSegments(null)){
            s.authorizeWork();
        }
        return this;
    }
    public WorkerTask prepare(){
        for(WorkerTaskSegment segment : segments){
            segment.setParentTask(this);
        }
        return this;
    }
    public WorkerTask setCash(int cash){
        this.cash = cash;
        return this;
    }
    public WorkerTask restrict(Plot plot){
        plotRestriction = plot;
        return this;
    }
    public boolean canReceiveFrom(Plot plot){
        return plotRestriction==null||plotRestriction==plot;
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("civilian", this instanceof CivilianTask);
        config.set("x", targetPlot.x);
        config.set("y", targetPlot.y);
        config.set("z", targetPlot.z);
        if(altPlot!=null){
            config.set("altx", altPlot.x);
            config.set("alty", altPlot.y);
            config.set("altz", altPlot.z);
        }
        config.set("started", started);
        config.set("owner", owner.world.otherPlayers.indexOf(owner));
        config.set("cost", cost.save());
        config.set("revenue", revenue.save());
        Config two = Config.newConfig();
        two.set("count", segments.size());
        for(int i = 0; i<segments.size(); i++){
            two.set(i+"", segments.get(i).save());
        }
        config.set("segments", two);
        config.set("cash", cash);
        if(plotRestriction!=null){
            config.set("resx", plotRestriction.x);
            config.set("resy", plotRestriction.y);
            config.set("resz", plotRestriction.z);
        }
        return config;
    }
    public static WorkerTask load(Config get){
        WorkerTask task;
        if(get.hasProperty("civilian")&&(boolean)get.get("civilian")){
            task = new CivilianTask();
        }else{
            task = new WorkerTask();
        }
        task.segments.clear();
        task.targetPlot = Core.loadingWorld.generatePlot((int)get.get("x"), (int)get.get("y"), (int)get.get("z"));
        if(get.hasProperty("altx")){
            task.altPlot = Core.loadingWorld.generatePlot((int)get.get("altx"), (int)get.get("alty"), (int)get.get("altz"));
        }
        task.started = get.get("started");
        int which = get.get("owner");
        task.owner = which==-1?Core.loadingWorld.localPlayer:Core.loadingWorld.otherPlayers.get(which);
        task.cost = ResourceList.load((Config)get.get("cost"));
        task.revenue = ResourceList.load((Config)get.get("revenue"));
        Config two = get.get("segments");
        for(int i = 0; i<(int)two.get("count"); i++){
            WorkerTaskSegment seg = WorkerTaskSegment.load((Config)two.get(i+""));
            task.segments.add(seg);
            seg.setParentTask(task);
        }
        task.cash = get.get("cash");
        if(get.hasProperty("resx")){
            task.plotRestriction = Core.loadingWorld.generatePlot((int)get.get("resx"), (int)get.get("resy"), (int)get.get("resz"));
        }
        return task;
    }
    public void check(Plot plot){
        if(isComplete()){
            plot.task = null;
        }
    }
    private void assignCivilian(Civilian worker){
        for(WorkerTaskSegment seg : getConcurrentSegments(null)){
            if(seg.couldUseCivilian(worker)){
                seg.useCivilian(worker);
                return;
            }
        }
    }
    private void assignWorker(Civilian worker){
        for(WorkerTaskSegment seg : getConcurrentSegments(null)){
            if(seg.couldUseWorker(worker)){
                seg.useWorker(worker);
                return;
            }
        }
    }
    private void attemptFindSubtask(Civilian worker){
        if(worker.worker){
            assignWorker(worker);
        }else{
            assignCivilian(worker);
        }
    }
    private ArrayList<WorkerTaskSegment> getConcurrentSegments(WorkerTaskSegment afterCompleted){
        for(WorkerTaskSegment seg : segments){
            if(!seg.isComplete()&&afterCompleted!=seg){
                return seg.getConcurrentSegments(afterCompleted);
            }
        }
        return new ArrayList<>();
    }
    public void assignOrDismiss(Civilian worker){
        attemptFindSubtask(worker);
        if(worker.subtask==null){
            worker.task = null;
            workersAssigned.remove(worker);
        }
    }
    public Subtask readSubtask(Config c){
        int index = c.get("index", -1);
        if(index<0||WorkerTaskSegment.allSegments.size()<=index){
            return null;
        }
        Subtask s = new Subtask(WorkerTaskSegment.allSegments.get(index));
        s.toPlot = c.get("toPlot");
        s.currentTask = loadTask(c.get("currentTask", Config.newConfig()));
        c = c.get("tasks");
        for(int i = 0; c.hasProperty(i+""); i++){
            s.tasks.add(loadTask(c.get(i+"", Config.newConfig())));
        }
        if(s.seg!=null){
            s.seg.onChildLoaded(s);
        }
        return s;
    }
    private Object loadTask(Config c){
        if(c.hasProperty("type")){
            switch((String)c.get("type")){
                case "PLOT":
                    return targetPlot.world.getPlot(c.get("x", 0), c.get("y", 0), c.get("z", 0));
                case "RESOURCES":
                    return ResourceList.load(c.get("data", Config.newConfig()));
                case "PLOT_TYPE":
                    return new Object[]{
                        PlotType.valueOf(c.get("plottype", "AIR")),
                        c.get("level", 0),
                        Side.valueOf(c.get("front", "FRONT")),
                        c.hasProperty("owner")
                            ?(c.get("owner", -1)<0
                                ?targetPlot.world.localPlayer
                                :targetPlot.world.otherPlayers.get(c.get("owner", -1)))
                            :null
                    };
                case "STRING":
                    return c.get("val", "");
                case "INTEGER":
                    return c.get("val", 1);
                default:
                    throw new AssertionError(c.get("type"));
            }
        }else{
            return null;
        }
    }
    public void update(Civilian worker){
        for(WorkerTaskSegment seg : getConcurrentSegments(null)){
            seg.authorizeWork();
        }
        targetPlot.world.schedulePlotUpdate(targetPlot);
        if(worker.subtask!=null){
            worker.subtask.update(worker);
        }else{
            attemptFindSubtask(worker);
            if(worker.subtask!=null){
                worker.subtask.update(worker);
            }else if(!(worker.worker)||!attemptFutureAssignWorker(worker)){
                worker.task = null;
                workersAssigned.remove(worker);
            }
        }
    }
    private boolean attemptFutureAssignWorker(Civilian worker){
        for(WorkerTaskSegment seg : getConcurrentSegments(null)){
            if(seg.couldUseWorker(worker)){
                seg.useWorker(worker);
                return true;
            }
            if(seg.isFull()){
                for(WorkerTaskSegment seg2 : getConcurrentSegments(seg)){
                    if(seg2.couldUseWorker(worker)){
                        seg2.useWorker(worker);
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean isComplete(){
        return getConcurrentSegments(null).isEmpty();
    }
    public static class Subtask{
        private final WorkerTaskSegment seg;
        private Object currentTask;
        private boolean toPlot = true;
        ArrayList<Object> tasks = new ArrayList<>();
        public Subtask(WorkerTaskSegment seg){
            this.seg = seg;
        }
        public Config save(){
            Config c = Config.newConfig();
            c.set("toPlot", toPlot);
            Config c2 = Config.newConfig();
            c.set("tasks", c2);
            for(int i = 0; i<tasks.size(); i++){
                Object o = tasks.get(i);
                c2.set(i+"", saveTask(o));
            }
            c.set("currentTask", saveTask(currentTask));
            c.set("index", WorkerTaskSegment.allSegments.indexOf(seg));
            return c;
        }
        private Config saveTask(Object o){
            Config val = Config.newConfig();
            if(o==null){
                return val;
            }else if(o instanceof Plot){
                Plot plot = (Plot)o;
                val.set("type", "PLOT");
                val.set("x", plot.x);
                val.set("y", plot.y);
                val.set("z", plot.z);
            }else if(o instanceof ResourceList){
                val.set("type", "RESOURCES");
                val.set("data", ((ResourceList)o).save());
            }else if(o instanceof Object[]){
                Object[] objs = (Object[])o;
                PlotType type = (PlotType)objs[0];
                int level = (int)objs[1];
                Side front = (Side)objs[2];
                PlayerHuman owner = (PlayerHuman)objs[3];
                val.set("type", "PLOT_TYPE");
                val.set("plottype", type.name());
                val.set("level", level);
                val.set("front", front.name());
                if(owner!=null){
                    val.set("owner", owner.world.otherPlayers.indexOf(owner));
                }
                throw new AssertionError(o.getClass().getName());
            }else if(o instanceof String){
                val.set("type", "STRING");
                val.set("val", (String)o);
            }else if(o instanceof Integer){
                val.set("type", "INTEGER");
                val.set("val", (Integer)o);
            }
            return val;
        }
        private void update(Civilian worker){
            if(!seg.workAllowed){
                return;
            }
            if(currentTask==null&&tasks.isEmpty()){
                ResourceList list = new ResourceList().addAll(worker.resources);
                ArrayList<Resource> lst = list.listResources();
                if(lst.isEmpty()){
                    worker.subtask = null;
                    seg.complete(this);
                    return;
                }
                if(toPlot){
                    worker.homePlot.world.getPlot(Math.round(worker.x), Math.round(worker.y), Math.round(worker.z)).inboundResources.add(lst.get(0), 1);
                }
                worker.resources.remove(lst.get(0), 1);
            }else if(currentTask==null){
                currentTask = tasks.remove(0);
            }
            if(currentTask!=null){
                if(currentTask instanceof Plot){
                    if(worker.path==null){
                        Plot p = (Plot)currentTask;
                        if(worker.getCurrentPlot()==p){
                            currentTask = null;
                            return;
                        }
                        worker.path = Path.findPath(worker.getCurrentPlot(), p, worker.worker);
                        if(worker.path==null){
                            seg.fail(this, new ArrayList<>(tasks));
                            worker.subtask = null;
                        }
                    }
                }else if(currentTask instanceof ResourceList){
                    ArrayList<Resource> lst = ((ResourceList)currentTask).listResources();
                    if(lst.isEmpty()){
                        currentTask = null;
                        return;
                    }
                    worker.resources.add(lst.get(0), 1);
                    worker.getCurrentPlot().readyResources.remove(lst.get(0), 1);
                    ((ResourceList)currentTask).remove(lst.get(0), 1);
                }else if(currentTask instanceof Integer){
                    currentTask = (int)currentTask - 1;
                    if((int)currentTask<1){
                        currentTask = null;
                    }
                }else if(currentTask instanceof Object[]){
                    Object[] objs = (Object[])currentTask;
                    PlotType type = (PlotType)objs[0];
                    int level = (int)objs[1];
                    Side front = (Side)objs[2];
                    PlayerHuman owner = (PlayerHuman)objs[3];
                    Plot p = worker.getCurrentPlot();
                    p.setType(type);
                    p.setLevel(level);
                    p.setFront(front);
                    p.setOwner(owner);
                    currentTask = null;
                }else if(currentTask instanceof String){
                    worker.upgradeToWorker();
                    currentTask = null;
                }
            }
        }
        public Subtask goTo(Plot p){
            tasks.add(p);
            return this;
        }
        public Subtask pickup(ResourceList loaded){
            tasks.add(loaded);
            return this;
        }
        public Subtask plotType(PlotType type, int level, Side front, Player owner){
            tasks.add(100);
            tasks.add(new Object[]{type, level, front, owner});
            return this;
        }
        public Subtask setTrain(){
            tasks.add(500);
            tasks.add("TRAIN");
            return this;
        }
        public Subtask setToPlot(boolean b){
            this.toPlot = b;
            return this;
        }
    }
}
