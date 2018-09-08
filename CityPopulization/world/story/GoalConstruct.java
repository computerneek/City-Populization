package CityPopulization.world.story;
import CityPopulization.Core;
import CityPopulization.world.World;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import java.util.HashMap;
public class GoalConstruct extends Goal {
    private final PlotType type;
    private final int level;
    private final int count;
    private final boolean additional;
    private int progress;
    private int initial;
    public GoalConstruct(PlotType type, int level, int count, boolean additional){
        this.type=type;
        this.level=level;
        this.count=count;
        this.additional=additional;
        if(additional){
            int num = 0;
            HashMap<Integer, HashMap<Integer, HashMap<Integer, Plot>>> plots = Core.world.plots;
            for(HashMap<Integer, HashMap<Integer, Plot>> plots2 : plots.values()){
                for(HashMap<Integer, Plot> plots3 : plots2.values()){
                    for(Plot plot : plots3.values()){
                        if(plot.type==type&&plot.level==level){
                            num++;
                        }
                    }
                }
            }
            initial = num;
        }
    }
    @Override
    public void update(World world){
        int count=0;
        HashMap<Integer, HashMap<Integer, HashMap<Integer, Plot>>> plots = Core.world.plots;
        for(HashMap<Integer, HashMap<Integer, Plot>> plots2 : plots.values()){
            for(HashMap<Integer, Plot> plots3 : plots2.values()){
                for(Plot plot : plots3.values()){
                    if(plot.owner==world.localPlayer&&plot.type==type&&plot.level==level){
                        count++;
                    }
                }
            }
        }
        progress = count;
    }
    @Override
    public String getText(){
        return (additional?"Construct ":"Own ")+count+(level>1?"level "+level:"")+" "+type.name+" ("+(progress-initial>=count?"Complete":count-progress+initial+" left")+")";
    }
    @Override
    public boolean isComplete(){
        return progress-initial>=count;
    }
}
