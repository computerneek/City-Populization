package CityPopulization.world.civilian;
import CityPopulization.render.Side;
import CityPopulization.world.civilian.event.EventLoad;
import CityPopulization.world.civilian.event.EventPath;
import CityPopulization.world.civilian.event.EventPlotSet;
import CityPopulization.world.civilian.event.EventSatisfy;
import CityPopulization.world.civilian.event.EventSequence;
import CityPopulization.world.civilian.event.EventUnload;
import CityPopulization.world.civilian.event.EventWait;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
public class WorkerTaskSegment {
    private String type;
    private Object[] data;
    private ArrayList<Worker> workers = new ArrayList<>();
    private WorkerTask task;
    public int workersSatisfied;
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
    public WorkerTaskSegment setResources(ResourceList resources){
        this.resources = resources;
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
            sequence.add(new EventSatisfy(this));
            sequence.add(new EventPath(Path.findPath(task.targetPlot, home)));
            if(sequence.validate()){
                resources.removeAll(loaded);
                plot.readyResources(loaded);
            }else{
                return null;
            }
        }else if(type.equals("Plot Type")){
            sequence.add(new EventPath(Path.findPath(home, task.targetPlot)));
            sequence.add(new EventWait(100));
            sequence.add(new EventPlotSet(task.targetPlot, (PlotType)data[0], (Integer)data[1], (Side)data[2], task.owner));
            sequence.add(new EventSatisfy(this));
            sequence.add(new EventPath(Path.findPath(task.targetPlot, home)));
            if(!sequence.validate()){
                return null;
            }
        }else if(type.equals("Resource Returns")){
            Plot plot = findWarehouse();
            if(plot==null){
                plot = findAirport();
                if(plot==null){
                    return null;
                }
            }
            ResourceList loaded = getResourcesToOffload(plot);
            sequence.add(new EventPath(Path.findPath(home, task.targetPlot)));
            sequence.add(new EventLoad(loaded));
            sequence.add(new EventSatisfy(this));
            sequence.add(new EventPath(Path.findPath(task.targetPlot, plot)));
            sequence.add(new EventUnload());
            sequence.add(new EventPath(Path.findPath(plot, home)));
            if(sequence.validate()){
                resources.removeAll(loaded);
            }else{
                return null;
            }
        }else{
            throw new AssertionError(type);
        }
        switch(type){
            case "Resource Collection":
            case "Resource Returns":
                if(resources.listResources().size()>0){
                    requiredWorkers++;
                }
        }
        task.started = true;
        workers.add(worker);
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
    private Plot findWarehouse(){
        ArrayList<Plot> plots = Path.findWarehouse(task.targetPlot);
        if(plots.isEmpty()){
            return null;
        }
        HashMap<Integer, ArrayList<Plot>> map = new HashMap<>();
        ArrayList<Integer> ints = new ArrayList<>();
        for(Plot plot : plots){
            ResourceList lst = new ResourceList().addAll(plot.resources).addAll(plot.readyResources);
            int count = lst.count();
            int canHarvest = task.owner.getResourcesPerWarehouse()*plot.getLevel();
            if(!ints.contains(canHarvest)){
                ints.add(canHarvest);
                map.put(canHarvest, new ArrayList<Plot>());
            }
            map.get(canHarvest).add(plot);
        }
        Collections.sort(ints);
        return map.get(ints.get(ints.size()-1)).get(0);
    }
    private Plot findAirport(){
        return Path.findAirportEntrance(task.targetPlot);
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
    private ResourceList getResourcesToOffload(Plot plot){
        ResourceList list = new ResourceList();
        int maxResources = Math.min(task.owner.race.getWorkerResourceCapacity(), plot.getType()==PlotType.Warehouse?(task.owner.getResourcesPerWarehouse()*(plot.getLevel()+1)-plot.resources.count()):10);
        list.addAll(resources);
        while(list.count()>maxResources){
            list.remove(list.listResources().get(0), 1);
        }
        return list;
    }
}
