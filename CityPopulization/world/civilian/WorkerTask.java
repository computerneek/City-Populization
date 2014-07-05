package CityPopulization.world.civilian;
import CityPopulization.Core;
import CityPopulization.world.player.Player;
import CityPopulization.world.plot.Plot;
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
    public WorkerTask(){
        if(Core.world!=null&&!Core.world.localPlayer.sandbox){
            segments.add(new WorkerTaskSegment().setType("Resource Collection"));
        }
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
        for(WorkerTaskSegment segment : segments){
            if(!segment.isFull()){
                return false;
            }
        }
        getCurrentSegment();
        return true;
    }
    public void prepare(){
        owner.cash-=cash;
        if(!Core.world.localPlayer.sandbox){
            segments.add(new WorkerTaskSegment().setType("Resource Returns").setResources(revenue));
        }
        for(WorkerTaskSegment segment : segments){
            segment.setParentTask(this);
        }
    }
    public WorkerTaskSegment getCurrentSegment(){
        for(WorkerTaskSegment segment : segments){
            if(!segment.isComplete()){
                return segment;
            }
        }
        targetPlot.task = null;
        return null;
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
        WorkerTask task = new WorkerTask();
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
            seg.task = task;
        }
        task.cash = get.get("cash");
        if(get.hasProperty("resx")){
            task.plotRestriction = Core.loadingWorld.generatePlot((int)get.get("resx"), (int)get.get("resy"), (int)get.get("resz"));
        }
        return task;
    }
    public void check(){
        getCurrentSegment();
    }
}
