package CityPopulization.world.civillian;
import CityPopulization.render.Side;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import java.util.ArrayList;
public class Path{
    public static void findPotentialTasks(ArrayList<TaskPotential> tasks, Plot startPlot){
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        if(startPlot.task!=null){
            tasks.add(new TaskPotential(startPlot.task, new Path().start(startPlot)));
        }
        coveredPlots.add(startPlot);
        paths.add(new Path().start(startPlot));
        for(Side side : startPlot.getPathableSides()){
            paths.add(new Path().start(startPlot).path(side.getPlot(startPlot.world, startPlot.x, startPlot.y, startPlot.z)));
        }
        while(!paths.isEmpty()){
            Path path = paths.remove(0);
            Plot plot = path.currentPlot;
            if(coveredPlots.contains(plot)){
                continue;
            }else if(plot.task!=null){
                tasks.add(new TaskPotential(plot.task, path));
                continue;
            }else if(plot.getType()!=PlotType.Road){
                continue;
            }
            coveredPlots.add(plot);
            for(Side side : plot.getPathableSides()){
                paths.add(path.copy().path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
            }
        }
    }
    private ArrayList<Plot> path = new ArrayList<>();
    private Plot currentPlot;
    private Path start(Plot startPlot){
        path.clear();
        return path(startPlot);
    }
    private Path path(Plot plot){
        path.add(plot);
        currentPlot = plot;
        return this;
    }
    private Path copy(){
        Path path = new Path();
        path.currentPlot = currentPlot;
        path.path.addAll(this.path);
        return path;
    }
}
