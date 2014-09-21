package CityPopulization.world.civilian;
import CityPopulization.render.Side;
import CityPopulization.world.civilian.event.EventLoad;
import CityPopulization.world.civilian.event.EventPath;
import CityPopulization.world.civilian.event.EventPlotSet;
import CityPopulization.world.civilian.event.EventSatisfy;
import CityPopulization.world.civilian.event.EventSequence;
import CityPopulization.world.civilian.event.EventTrainWorker;
import CityPopulization.world.civilian.event.EventUnload;
import CityPopulization.world.civilian.event.EventUnloadAndDelete;
import CityPopulization.world.civilian.event.EventWait;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.resource.ResourceList;
import java.util.ArrayList;
import simplelibrary.config2.Config;
public class WorkerTaskSegmentSet extends WorkerTaskSegment{
    public String type;
    private Object[] data;
    private int workers = 0;
    public WorkerTask task;
    public int workersSatisfied;
    private int requiredWorkers = 1;
    private ResourceList resources;
    private Plot targetPlot;
    ArrayList<WorkerTaskSegment> segments = new ArrayList<>();
    public WorkerTaskSegmentSet add(WorkerTaskSegment seg){
        segments.add(seg);
        return this;
    }
    public boolean isFull(){
        for(WorkerTaskSegment seg : segments){
            if(!seg.isFull()){
                return false;
            }
        }
        return true;
    }
    public EventSequence generateEventSequence(Civilian worker, Plot home){
        for(WorkerTaskSegment seg : segments){
            if(!seg.isFull()){
                EventSequence seq = seg.generateEventSequence(worker, home);
                if(seq!=null){
                    return seq;
                }
            }
        }
        return null;
    }
    public void setParentTask(WorkerTask task){
        this.task = task;
        for(WorkerTaskSegment seg : segments){
            seg.setParentTask(task);
        }
    }
    public boolean isComplete(){
        for(WorkerTaskSegment seg : segments){
            if(!seg.isComplete()){
                return false;
            }
        }
        return true;
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("set", true);
        for(int i = 0; i<segments.size(); i++){
            config.set(i+"", segments.get(i).save());
        }
        return config;
    }
    public static WorkerTaskSegmentSet load(Config get){
        WorkerTaskSegmentSet set = new WorkerTaskSegmentSet();
        for(int i = 0; get.hasProperty(i+""); i++){
            set.add(WorkerTaskSegment.load((Config)get.get(i+"")));
        }
        return set;
    }
}
