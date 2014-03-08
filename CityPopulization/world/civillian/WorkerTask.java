package CityPopulization.world.civillian;
import CityPopulization.world.player.Player;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.resource.ResourceList;
import java.util.ArrayList;
public class WorkerTask{
    public Plot targetPlot;
    public boolean started;
    public Player owner;
    ResourceList cost;
    private ResourceList revenue;
    private ArrayList<WorkerTaskSegment> segments = new ArrayList<>();
    public WorkerTask(){
        segments.add(new WorkerTaskSegment().setType("Resource Collection"));
    }
    public WorkerTask setPlot(Plot plot){
        this.targetPlot = plot;
        return this;
    }
    public WorkerTask setCost(ResourceList cost){
        this.cost = cost;
        return this;
    }
    public WorkerTask setRevenue(ResourceList revenue){
        this.revenue = revenue;
        return this;
    }
    public WorkerTask addSegment(WorkerTaskSegment segment){
        segments.add(segment);
        return this;
    }
    public WorkerTask setOwner(Player owner){
        this.owner = owner;
        return this;
    }
    public boolean isFull(){
        for(WorkerTaskSegment segment : segments){
            if(!segment.isFull()){
                return false;
            }
        }
        return true;
    }
    public void prepare(){
        segments.add(new WorkerTaskSegment().setType("Resource Returns"));
        for(WorkerTaskSegment segment : segments){
            segment.setParentTask(this);
        }
    }
    public WorkerTaskSegment getCurrentSegment(){
        for(WorkerTaskSegment segment : segments){
            if(!segment.isComplete()){
                return segment;
            }
        }
        return null;
    }
}
