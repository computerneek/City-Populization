package CityPopulization.world.civilian.event;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.civilian.WorkerTaskSegment;
import CityPopulization.world.civilian.WorkerTaskSegmentSet;
import simplelibrary.config2.Config;
public class EventSatisfy extends Event {
    private WorkerTaskSegment segment;
    public EventSatisfy(WorkerTaskSegment segment){
        this.segment = segment;
    }
    boolean started = false;
    @Override
    public boolean isComplete(){
        return started;
    }
    @Override
    public void start(Civilian worker){
        segment.workersSatisfied++;
        segment.task.check();
        started = true;
    }
    @Override
    public void work(Civilian worker){}
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "satisfy");
        config.set("started", started);
        config.set("x", segment.task.targetPlot.x);
        config.set("y", segment.task.targetPlot.y);
        config.set("z", segment.task.targetPlot.z);
        if(segment.task.segments.contains(segment)){
            config.set("index", segment.task.segments.indexOf(segment));
        }else{
            WorkerTaskSegment seg = null;
            for(WorkerTaskSegment seg2 : segment.task.segments){
                if(seg2 instanceof WorkerTaskSegmentSet&&((WorkerTaskSegmentSet)seg2).segments.contains(segment)){
                    seg = seg2;
                    break;
                }
            }
            config.set("index1", segment.task.segments.indexOf(seg));
            config.set("index2", ((WorkerTaskSegmentSet)seg).segments.indexOf(segment));
        }
        return config;
    }
    @Override
    public boolean validate(){
        return segment!=null;
    }
}
