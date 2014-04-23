package CityPopulization.world.civilian.event;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import java.util.ArrayList;
import simplelibrary.config2.Config;
public class EventLoad extends Event{
    private ResourceList resources;
    boolean complete;
    public EventLoad(ResourceList resources){
        this.resources = resources;
    }
    @Override
    public boolean isComplete(){
        return complete;
    }
    @Override
    public void start(Civilian worker){}
    @Override
    public void work(Civilian worker){
        ResourceList list = new ResourceList().addAll(resources).removeAll(worker.resources);
        ArrayList<Resource> lst = list.listResources();
        complete = lst.isEmpty();
        if(!complete){
            worker.resources.add(lst.get(0), 1);
            worker.homePlot.world.getPlot(Math.round(worker.x), Math.round(worker.y), Math.round(worker.z)).readyResources.remove(lst.get(0), 1);
        }
    }
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "load");
        config.set("resources", resources.save());
        config.set("complete", complete);
        return config;
    }
    @Override
    public boolean validate(){
        return resources!=null;
    }
}
