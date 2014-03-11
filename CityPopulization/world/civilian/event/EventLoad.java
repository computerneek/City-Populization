package CityPopulization.world.civilian.event;
import CityPopulization.world.civilian.Worker;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import java.util.ArrayList;
public class EventLoad implements Event{
    private ResourceList resources;
    private Worker worker;
    private boolean complete;
    public EventLoad(ResourceList resources){
        this.resources = resources;
    }
    @Override
    public boolean isComplete(){
        return complete;
    }
    @Override
    public void start(Worker worker){
        this.worker = worker;
    }
    @Override
    public void work(Worker worker){
        ResourceList list = new ResourceList().addAll(resources).removeAll(worker.resources);
        ArrayList<Resource> lst = list.listResources();
        complete = lst.isEmpty();
        if(!complete){
            worker.resources.add(lst.get(0), 1);
            worker.homePlot.world.getPlot(Math.round(worker.x), Math.round(worker.y), Math.round(worker.z)).readyResources.remove(lst.get(0), 1);
        }
    }
}
