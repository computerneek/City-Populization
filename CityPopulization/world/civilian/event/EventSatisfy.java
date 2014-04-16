package CityPopulization.world.civilian.event;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.civilian.WorkerTaskSegment;
public class EventSatisfy implements Event {
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
        started = true;
    }
    @Override
    public void work(Civilian worker){}
}
