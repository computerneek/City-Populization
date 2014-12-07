package CityPopulization.world.civilian.event;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import java.util.ArrayList;
import simplelibrary.config2.Config;
public class EventUnloadAndDelete extends Event{
    boolean complete;
    private Civilian worker;
    @Override
    public boolean isComplete(){
        return complete;
    }
    @Override
    public void start(Civilian worker){
        this.worker = worker;
    }
    @Override
    public void work(Civilian worker){
        ResourceList list = new ResourceList().addAll(worker.resources);
        ArrayList<Resource> lst = list.listResources();
        complete = lst.isEmpty();
        if(!complete){
            worker.resources.remove(lst.get(0), 1);
        }
    }
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "unload");
        config.set("complete", complete);
        return config;
    }
    @Override
    public boolean validate(){
        return true;
    }
}