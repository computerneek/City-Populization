package CityPopulization.world.story;
import CityPopulization.Core;
import CityPopulization.world.World;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.resource.Resource;
public class GoalHarvest extends Goal {
    private final Resource resource;
    private final int quantity;
    private final boolean additional;
    private int progress;
    private int initial;
    public GoalHarvest(Resource resource, int quantity, boolean additional){
        this.resource=resource;
        this.quantity=quantity;
        this.additional=additional;
        if(additional){
            int count = 0;
            for(Plot plot : Core.world.localPlayer.resourceStructures){
                count+=plot.resources.get(resource);
            }
            initial = count;
        }
    }
    @Override
    public void update(World world){
        int count = 0;
        for(Plot plot : world.localPlayer.resourceStructures){
            count+=plot.resources.get(resource);
        }
        progress = count;
    }
    @Override
    public String getText(){
        return "Harvest "+quantity+" "+resource.name()+" ("+(progress>=quantity?"Complete":quantity-progress+" left")+")";
    }
    @Override
    public boolean isComplete(){
        return progress-initial>=quantity;
    }
}
