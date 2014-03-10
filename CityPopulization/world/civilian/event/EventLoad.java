package CityPopulization.world.civilian.event;
import CityPopulization.world.civilian.Worker;
import CityPopulization.world.resource.ResourceList;
public class EventLoad implements Event{
    private ResourceList resources;
    public EventLoad(ResourceList resources){
        this.resources = resources;
    }
    @Override
    public boolean isComplete(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void start(Worker worker){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public void work(Worker aThis){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
