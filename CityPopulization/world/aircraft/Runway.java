package CityPopulization.world.aircraft;
import CityPopulization.world.plot.Plot;
import java.util.ArrayList;
public class Runway{
    public static Runway findRunway(Plot plot){
        return new Runway(plot);
    }
    public ArrayList<Plot> plots = new ArrayList<>();
    public Runway(Plot plot){
        plots.add(plot);
        while(plot.getBackPlot()!=null&&plot.getOwner()==plot.getBackPlot().getOwner()&&plot.getFront()==plot.getBackPlot().getFront()&&plot.getType()==plot.getBackPlot().getType()){
            plot = plot.getBackPlot();
            plots.add(0, plot);
        }
    }
    public boolean isOccupied(){
        return getStartPlot().terminal.occupied>0;
    }
    public int size(){
        return plots.size();
    }
    public Plot getTouchdownPlot(){
        return plots.get(0);
    }
    public Plot getStartPlot(){
        return plots.get(plots.size()-1);
    }
}
