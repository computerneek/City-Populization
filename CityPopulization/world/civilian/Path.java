package CityPopulization.world.civilian;
import CityPopulization.Core;
import CityPopulization.render.Side;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import java.util.ArrayList;
import simplelibrary.config2.Config;
public class Path{
    public static void findPotentialTasks(ArrayList<WorkerTask> tasks, Plot startPlot, boolean isWorker){
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        if(startPlot.task!=null){
            tasks.add(startPlot.task);
        }
        paths.add(new Path().start(startPlot));
        for(Side side : startPlot.getTravelableSides(isWorker)){
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
            }
            if(isWorker&&!plot.workers.isEmpty()){
                plot.lastTaskTimeWorker = startPlot.world.age;
                plot.lastTasksWorker = tasks;
            }else if(!isWorker&&!plot.civilians.isEmpty()){
                plot.lastTaskTimeCivilian = startPlot.world.age;
                plot.lastTasksCivilian = tasks;
            }
            if(plot.getType()!=PlotType.Road&&plot.getType()!=PlotType.Elevator&&(!isWorker||(plot.getType()!=PlotType.AirportJetway&&plot.getType()!=PlotType.AirportRunway))){
                Plot plotUnder = plot.world.getPlot(plot.x, plot.y, plot.z-1);
                if(isWorker&&plot.getType()==PlotType.Air&&plotUnder!=null&&plotUnder.task!=null&&plotUnder.getPathableSides(true).contains(Side.UP)){
                    tasks.add(plotUnder.task);
                }
                continue;
            }
            coveredPlots.add(plot);
            for(Side side : plot.getTravelableSides(isWorker)){
                paths.add(path.copy().path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
            }
        }
    }
    public static Plot findResourcePlot(Plot start, ResourceList resources, boolean isWorker){
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        paths.add(new Path().start(start));
        for(Side side : start.getTravelableSides(isWorker)){
            paths.add(new Path().start(start).path(side.getPlot(start.world, start.x, start.y, start.z)));
        }
        if(isWorker&&!start.getTravelableSides(isWorker).contains(Side.UP)){
            paths.add(new Path().start(start).path(Side.UP.getPlot(start.world, start.x, start.y, start.z)));
        }
        while(!paths.isEmpty()){
            Path path = paths.remove(0);
            Plot plot = path.currentPlot;
            if(plot.getType()==PlotType.Warehouse){
                for(Resource resource : resources.listResources()){
                    if(plot.resources.get(resource)>0&&resources.get(resource)>0){
                        return plot;
                    }
                }
            }
            if(coveredPlots.contains(plot)){
                continue;
            }
            coveredPlots.add(plot);
            if(plot.getType()!=PlotType.Road&&plot.getType()!=PlotType.Elevator&&(!isWorker||(plot.getType()!=PlotType.AirportJetway&&plot.getType()!=PlotType.AirportRunway))&&!(plot.getType()==PlotType.Air&&plot.x==start.x&&plot.y==start.y&&plot.z==start.z+1)){
                continue;
            }
            for(Side side : plot.getTravelableSides(isWorker)){
                paths.add(path.copy().path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
            }
        }
        return null;
    }
    public static ArrayList<Plot> findWarehouse(Plot start, boolean isWorker){
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        ArrayList<Plot> warehouses = new ArrayList<>();
        paths.add(new Path().start(start));
        for(Side side : start.getTravelableSides(isWorker)){
            paths.add(new Path().start(start).path(side.getPlot(start.world, start.x, start.y, start.z)));
        }
        if(isWorker&&!start.getTravelableSides(isWorker).contains(Side.UP)){
            paths.add(new Path().start(start).path(Side.UP.getPlot(start.world, start.x, start.y, start.z)));
        }
        while(!paths.isEmpty()){
            Path path = paths.remove(0);
            Plot plot = path.currentPlot;
            if(coveredPlots.contains(plot)){
                continue;
            }
            coveredPlots.add(plot);
            if(plot.getType()==PlotType.Warehouse&&plot.resources.count()<(plot.getLevel()+1)*plot.owner.getResourcesPerWarehouse()){
                warehouses.add(plot);
            }
            if(plot.getType()!=PlotType.Road&&plot.getType()!=PlotType.Elevator&&(!isWorker||(plot.getType()!=PlotType.AirportJetway&&plot.getType()!=PlotType.AirportRunway))&&!(plot.getType()==PlotType.Air&&plot.x==start.x&&plot.y==start.y&&plot.z==start.z+1)){
                continue;
            }
            for(Side side : plot.getTravelableSides(isWorker)){
                paths.add(path.copy().path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
            }
        }
        return warehouses;
    }
    public static Plot findAirportEntrance(Plot start, boolean isWorker){
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        for(Side side : start.getTravelableSides(isWorker)){
            paths.add(new Path().start(start).path(side.getPlot(start.world, start.x, start.y, start.z)));
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
            if(plot.getType()!=PlotType.Road&&plot.getType()!=PlotType.Elevator&&(!isWorker||(plot.getType()!=PlotType.AirportJetway&&plot.getType()!=PlotType.AirportRunway))){
                continue;
            }
            for(Side side : plot.getTravelableSides(isWorker)){
                paths.add(path.copy().path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
            }
        }
        return null;
    }
    public static Plot findHouseWithSpace(Plot start, boolean isWorker){
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        for(Side side : start.getTravelableSides(isWorker)){
            paths.add(new Path().start(start).path(side.getPlot(start.world, start.x, start.y, start.z)));
        }
        while(!paths.isEmpty()){
            Path path = paths.remove(0);
            Plot plot = path.currentPlot;
            if(coveredPlots.contains(plot)){
                continue;
            }
            coveredPlots.add(plot);
            if(plot.getType()==PlotType.House&&plot.civilians.size()+plot.workers.size()<plot.getMaximumCivilianCapacity()){
                return plot;
            }
            if(plot.getType()!=PlotType.Road&&plot.getType()!=PlotType.Elevator&&(!isWorker||(plot.getType()!=PlotType.AirportJetway&&plot.getType()!=PlotType.AirportRunway))){
                continue;
            }
            for(Side side : plot.getTravelableSides(isWorker)){
                paths.add(path.copy().path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
            }
        }
        return null;
    }
    public static Plot findWorkshop(Plot start, boolean isWorker){
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        for(Side side : start.getTravelableSides(isWorker)){
            paths.add(new Path().start(start).path(side.getPlot(start.world, start.x, start.y, start.z)));
        }
        while(!paths.isEmpty()){
            Path path = paths.remove(0);
            Plot plot = path.currentPlot;
            if(coveredPlots.contains(plot)){
                continue;
            }
            coveredPlots.add(plot);
            if(plot.getType()==PlotType.Workshop){
                return plot;
            }
            if(plot.getType()!=PlotType.Road&&plot.getType()!=PlotType.Elevator&&(!isWorker||(plot.getType()!=PlotType.AirportJetway&&plot.getType()!=PlotType.AirportRunway))){
                continue;
            }
            for(Side side : plot.getTravelableSides(isWorker)){
                paths.add(path.copy().path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
            }
        }
        return null;
    }
    public static Path findPath(Plot start, Plot end, boolean isWorker){
        if(start==end){
            return new Path().start(start);
        }
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        coveredPlots.add(start);
        for(Side side : start.getTravelableSides(isWorker)){
            paths.add(new Path().start(start).path(side.getPlot(start.world, start.x, start.y, start.z)));
        }
        if(isWorker&&!start.getTravelableSides(isWorker).contains(Side.UP)){
            paths.add(new Path().start(start).path(Side.UP.getPlot(start.world, start.x, start.y, start.z)));
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
            if(plot.getType()!=PlotType.Road&&plot.getType()!=PlotType.Elevator&&(!isWorker||(plot.getType()!=PlotType.AirportJetway&&plot.getType()!=PlotType.AirportRunway))&&!(plot.getType()==PlotType.Air&&plot.x==start.x&&plot.y==start.y&&plot.z==start.z+1)){
                if(isWorker&&plot.getType()==PlotType.Air&&end==plot.world.getPlot(plot.x, plot.y, plot.z-1)){
                    paths.add(path.copy().path(Side.DOWN.getPlot(plot.world, plot.x, plot.y, plot.z)));
                }
                continue;
            }
            for(Side side : plot.getTravelableSides(isWorker)){
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
    public Config save(){
        Config config = Config.newConfig();
        config.set("count", path.size());
        for(int i = 0; i<path.size(); i++){
            Plot plt = path.get(i);
            config.set(i+"x", plt.x);
            config.set(i+"y", plt.y);
            config.set(i+"z", plt.z);
        }
        return config;
    }
    public static Path load(Config config){
        Path path = new Path();
        for(int i = 0; i<(int)config.get("count"); i++){
            path.path.add(Core.loadingWorld.getPlot((int)config.get(i+"x"), (int)config.get(i+"y"), (int)config.get(i+"z")));
        }
        return path;
    }
}
