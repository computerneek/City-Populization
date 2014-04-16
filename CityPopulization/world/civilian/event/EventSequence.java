package CityPopulization.world.civilian.event;
import java.util.ArrayList;
import simplelibrary.config2.Config;
public class EventSequence {
    public ArrayList<Event> events = new ArrayList<>();
    public void add(Event event){
        events.add(event);
    }
    public boolean validate(){
        for(Event event : events){
            if(event==null){
                return false;
            }
        }
        return true;
    }
    public Event nextEvent(){
        return events.remove(0);
    }
    public boolean isComplete(){
        return events.isEmpty();
    }
    public Config save(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
