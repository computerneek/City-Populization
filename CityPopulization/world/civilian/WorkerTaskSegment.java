package CityPopulization.world.civilian;
import CityPopulization.world.civilian.event.EventLoad;
import CityPopulization.world.civilian.event.EventPath;
import CityPopulization.world.civilian.event.EventSequence;
import CityPopulization.world.civilian.event.EventUnload;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import java.util.ArrayList;
public class WorkerTaskSegment {
    private String type;
    private Object[] data;
    private ArrayList<Worker> workers = new ArrayList<>();
    private WorkerTask task;
    private int workersSatisfied;
    private int requiredWorkers = 1;
    private ResourceList resources;
    public WorkerTaskSegment setType(String type){
        this.type = type;
        return this;
    }
    public WorkerTaskSegment setData(Object... data){
        this.data = data;
        return this;
    }
    public boolean isFull(){
        return workers.size()>=getRequiredWorkers();
    }
    public EventSequence generateEventSequence(Worker worker, Plot home){
        EventSequence sequence = new EventSequence();
        if(type.equals("Resource Collection")){
            Plot plot = findResourcePlot();
            if(plot==null){
                return null;
            }
            ResourceList loaded = getResourcesToLoad(plot);
            sequence.add(new EventPath(Path.findPath(home, plot)));
            sequence.add(new EventLoad(loaded));
            sequence.add(new EventPath(Path.findPath(plot, task.targetPlot)));
            sequence.add(new EventUnload());
            sequence.add(new EventPath(Path.findPath(task.targetPlot, home)));
            if(sequence.validate()){
                resources.removeAll(loaded);
                plot.readyResources(resources);
            }else{
                return null;
            }
        }
        switch(type){
            case "Resource Collection":
                if(resources.listResources().size()>0){
                    requiredWorkers++;
                }
        }
        task.started = true;
        return sequence;
    }
    private int getRequiredWorkers(){
        return requiredWorkers;
    }
    public void setParentTask(WorkerTask task){
        this.task = task;
        switch(type){
            case "Resource Collection":
                this.resources = new ResourceList().addAll(task.cost);
        }
    }
    public boolean isComplete(){
        return workersSatisfied>=getRequiredWorkers();
    }
    private Plot findResourcePlot(){
        return Path.findResourcePlot(task.targetPlot, resources);
    }
    private ResourceList getResourcesToLoad(Plot plot){
        ResourceList list = new ResourceList();
        int resources = 0;
        int maxResources = task.owner.race.getWorkerResourceCapacity();
        for(Resource resource : this.resources.listResources()){
            int inPlot = plot.resources.get(resource);
            int canLoad = Math.min(inPlot, this.resources.get(resource));
            int toLoad = Math.min(canLoad, maxResources-resources);
            if(toLoad>0){
                list.add(resource, toLoad);
                resources+=toLoad;
            }
        }
        return list;
    }
}
