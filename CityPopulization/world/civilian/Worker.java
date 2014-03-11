package CityPopulization.world.civilian;
import CityPopulization.world.civilian.event.Event;
import CityPopulization.world.civilian.event.EventSequence;
import CityPopulization.world.resource.ResourceList;
public class Worker extends Civilian{
    private EventSequence eventSequence;
    public ResourceList resources = new ResourceList();
    private Event currentEvent;
    {
        speed*=2;
    }
    public void assign(EventSequence sequence){
        this.eventSequence = sequence;
    }
    @Override
    public void update(){
        if(currentEvent!=null){
            currentEvent.work(this);
        }
        if(currentEvent==null||currentEvent.isComplete()&&path==null&&dest==null){
            currentEvent = eventSequence==null?null:eventSequence.nextEvent();
            if(eventSequence==null||eventSequence.isComplete()){
                eventSequence = null;
            }
            if(currentEvent!=null){
                currentEvent.start(this);
            }
        }
        super.update();
    }
    @Override
    public void updateOnHomePlot(){
        if(eventSequence!=null||currentEvent!=null){
            return;
        }
        super.updateOnHomePlot();
    }
}
