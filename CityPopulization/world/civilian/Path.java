package CityPopulization.world.civilian;
import CityPopulization.render.Side;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import java.util.ArrayList;
public class Path{
    public static void findPotentialTasks(ArrayList<WorkerTask> tasks, Plot startPlot){
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        if(startPlot.task!=null){
            tasks.add(startPlot.task);
        }
        coveredPlots.add(startPlot);
        paths.add(new Path().start(startPlot));
        for(Side side : startPlot.getTravelableSides()){
            paths.add(new Path().start(startPlot).path(side.getPlot(startPlot.world, startPlot.x, startPlot.y, startPlot.z)));
        }
        while(!paths.isEmpty()){
            Path path = paths.remove(0);
            Plot plot = path.currentPlot;
            if(coveredPlots.contains(plot)){
                continue;
            }else if(plot.task!=null){
                tasks.add(plot.task);
                coveredPlots.add(plot);
                continue;
            }else if(plot.getType()!=PlotType.Road&&plot.getType()!=PlotType.Elevator){
                continue;
            }
            coveredPlots.add(plot);
            for(Side side : plot.getTravelableSides()){
                paths.add(path.copy().path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
            }
        }
    }
    public static Plot findResourcePlot(Plot startPlot, ResourceList resources){
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        coveredPlots.add(startPlot);
        for(Side side : startPlot.getTravelableSides()){
            paths.add(new Path().start(startPlot).path(side.getPlot(startPlot.world, startPlot.x, startPlot.y, startPlot.z)));
        }
        while(!paths.isEmpty()){
            Path path = paths.remove(0);
            Plot plot = path.currentPlot;
            if(plot.getType()==PlotType.AirportEntrance||plot.getType()==PlotType.Warehouse){
                for(Resource resource : resources.listResources()){
                    if(plot.resources.get(resource)>0){
                        return plot;
                    }
                }
            }
            if(coveredPlots.contains(plot)){
                continue;
            }
            coveredPlots.add(plot);
            if(plot.getType()!=PlotType.Road&&plot.getType()!=PlotType.Elevator){
                continue;
            }
            for(Side side : plot.getTravelableSides()){
                paths.add(path.copy().path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
            }
        }
        return null;
    }
    public static ArrayList<Plot> findWarehouse(Plot startPlot){
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        ArrayList<Plot> warehouses = new ArrayList<>();
        coveredPlots.add(startPlot);
        for(Side side : startPlot.getTravelableSides()){
            paths.add(new Path().start(startPlot).path(side.getPlot(startPlot.world, startPlot.x, startPlot.y, startPlot.z)));
        }
        while(!paths.isEmpty()){
            Path path = paths.remove(0);
            Plot plot = path.currentPlot;
            if(coveredPlots.contains(plot)){
                continue;
            }
            coveredPlots.add(plot);
            if(plot.getType()==PlotType.Warehouse){
                warehouses.add(plot);
            }
            if(plot.getType()!=PlotType.Road&&plot.getType()!=PlotType.Elevator){
                continue;
            }
            for(Side side : plot.getTravelableSides()){
                paths.add(path.copy().path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
            }
        }
        return warehouses;
    }
    public static Plot findAirportEntrance(Plot startPlot){
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        coveredPlots.add(startPlot);
        for(Side side : startPlot.getTravelableSides()){
            paths.add(new Path().start(startPlot).path(side.getPlot(startPlot.world, startPlot.x, startPlot.y, startPlot.z)));
        }
        while(!paths.isEmpty()){
            Path path = paths.remove(0);
            Plot plot = path.currentPlot;
            if(coveredPlots.contains(plot)){
                continue;
            }
            coveredPlots.add(plot);
            if(plot.getType()==PlotType.AirportEntrance){
                return plot;
            }
            if(plot.getType()!=PlotType.Road&&plot.getType()!=PlotType.Elevator){
                continue;
            }
            for(Side side : plot.getTravelableSides()){
                paths.add(path.copy().path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
            }
        }
        return null;
    }
    public static Path findPath(Plot start, Plot end){
        if(start==end){
            return new Path().start(start);
        }
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        coveredPlots.add(start);
        for(Side side : start.getTravelableSides()){
            paths.add(new Path().start(start).path(side.getPlot(start.world, start.x, start.y, start.z)));
        }
        while(!paths.isEmpty()){
            Path path = paths.remove(0);
            Plot plot = path.currentPlot;
            if(plot==end){
                return path;
            }else if(coveredPlots.contains(plot)){
                continue;
            }
            coveredPlots.add(plot);
            if(plot.getType()!=PlotType.Road&&plot.getType()!=PlotType.Elevator){
                continue;
            }
            for(Side side : plot.getTravelableSides()){
                paths.add(path.copy().path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
            }
        }
        return null;
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
    public boolean isComplete(){
        return path.isEmpty();
    }
    public int[] next(){
        Plot plot = path.remove(0);
        return new int[]{plot.x, plot.y, plot.z};
    }
}
