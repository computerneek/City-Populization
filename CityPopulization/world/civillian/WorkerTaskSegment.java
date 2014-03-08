package CityPopulization.world.civillian;
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
    private ResourceList resourcesToHarvest;
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
        if(true){
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        switch(type){
            case "Resource Collection":
                if(resourcesToHarvest.listResources().size()>0){
                    requiredWorkers++;
                }
        }
        return sequence;
    }
    private int getRequiredWorkers(){
        return requiredWorkers;
    }
    public void setParentTask(WorkerTask task){
        this.task = task;
        switch(type){
            case "Resource Collection":
                this.resourcesToHarvest = new ResourceList().addAll(task.cost);
        }
    }
    public boolean isComplete(){
        return workersSatisfied>=getRequiredWorkers();
    }
}
