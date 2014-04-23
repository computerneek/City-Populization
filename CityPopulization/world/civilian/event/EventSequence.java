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
            if(event==null||!event.validate()){
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
        Config config = Config.newConfig();
        config.set("count", events.size());
        for(int i = 0; i<events.size(); i++){
            config.set(i+"", events.get(i).save());
        }
        return config;
    }
    public static EventSequence load(Config config){
        EventSequence seq = new EventSequence();
        for(int i = 0; i<(int)config.get("count"); i++){
            seq.events.add(Event.load((Config)config.get(i+"")));
        }
        return seq;
    }
}
