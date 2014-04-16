package CityPopulization.world.civilian;
import CityPopulization.world.player.Player;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import java.util.ArrayList;
import simplelibrary.config2.Config;
public class WorkerTask{
    public Plot targetPlot;
    public Plot altPlot;
    public boolean started;
    public Player owner;
    public ResourceList cost;
    public ResourceList revenue;
    public ArrayList<WorkerTaskSegment> segments = new ArrayList<>();
    public int cash;
    private Plot plotRestriction;
    public WorkerTask(){
        segments.add(new WorkerTaskSegment().setType("Resource Collection"));
    }
    public WorkerTask setPlot(Plot plot){
        this.targetPlot = plot;
        this.altPlot = plot;
        return this;
    }
    public WorkerTask setAltPlot(Plot plot){
        this.altPlot = plot;
        return this;
    }
    public WorkerTask setCost(ResourceList cost){
        this.cost = new ResourceList().addAll(cost).add(Resource.Tools, 1);
        return this;
    }
    public WorkerTask setRevenue(ResourceList revenue){
        this.revenue = new ResourceList().addAll(revenue).multiply(targetPlot.world.difficulty.incomeModifier).add(Resource.Tools, 1);
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
        getCurrentSegment();
        return true;
    }
    public void prepare(){
        owner.cash-=cash;
        segments.add(new WorkerTaskSegment().setType("Resource Returns").setResources(revenue));
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
        targetPlot.task = null;
        owner.getWorkerTaskManager().removeTask(this);
        return null;
    }
    public WorkerTask setCash(int cash){
        this.cash = cash;
        return this;
    }
    public WorkerTask restrict(Plot plot){
        plotRestriction = plot;
        return this;
    }
    public boolean canReceiveFrom(Plot plot){
        return plotRestriction==null||plotRestriction==plot;
    }
    public Config save(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
