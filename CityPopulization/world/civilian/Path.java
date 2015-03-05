package CityPopulization.world.civilian;
import CityPopulization.Core;
import CityPopulization.render.Side;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import java.util.ArrayList;
import java.util.Collections;
import simplelibrary.config2.Config;
public class Path implements Comparable<Path>{
    private boolean jumped;
    private ArrayList<Plot> path=new ArrayList<>();
    private Plot currentPlot;
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
    @Override
    public int compareTo(Path o){
        return o.path.size()-path.size();
    }
    private Path setJumped(boolean jumped){
        this.jumped = jumped;
        return this;
    }
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
        path.jumped = jumped;
        return path;
    }
    public static ArrayList<Plot> findPotentialTasks(ArrayList<WorkerTask> tasks, Plot start, boolean isWorker){
        if(start.type.skyscraperBaseType!=null){
            start = start.getSkyscraperPlots()[0];
        }
        if(Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots().length>0&&Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots()[0].task!=null){
            start = Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots()[0];
        }
        ArrayList<Plot> houses = new ArrayList<>();
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        if(start.task!=null){
            tasks.add(start.task);
        }
        paths.add(new Path().start(start));
        if(start.skyscraper!=null){
            for(int x = 0; x<start.skyscraper.width; x++){
                for(int y = 0; y<start.skyscraper.height; y++){
                    if(y>0&&y<start.skyscraper.width-1&&x>0&&x<start.skyscraper.width-1){
                        continue;
                    }
                    Plot plot = start.world.generateAndGetPlot(start.x+x, start.y+y, start.z);
                    for(Side side : plot.getTravelableSides(isWorker)){
                        paths.add(new Path().start(plot).path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
                    }
                }
            }
        }else{
            for(Side side : start.getTravelableSides(isWorker)){
                paths.add(new Path().start(start).path(side.getPlot(start.world, start.x, start.y, start.z)));
            }
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
                plot.lastTaskTimeWorker = start.world.age;
                plot.lastTasksWorker = tasks;
                houses.add(plot);
            }else if(!isWorker&&!plot.civilians.isEmpty()){
                plot.lastTaskTimeCivilian = start.world.age;
                plot.lastTasksCivilian = tasks;
                houses.add(plot);
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
        return houses;
    }
    public static Plot findResourcePlot(Plot start, ResourceList resources, boolean isWorker){
        if(start.type.skyscraperBaseType!=null){
            start = start.getSkyscraperPlots()[0];
        }
        if(Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots().length>0&&Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots()[0].task!=null){
            start = Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots()[0];
        }
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        paths.add(new Path().start(start));
        if(start.skyscraper!=null){
            for(int x = 0; x<start.skyscraper.width; x++){
                for(int y = 0; y<start.skyscraper.height; y++){
                    if(y>0&&y<start.skyscraper.width-1&&x>0&&x<start.skyscraper.width-1){
                        continue;
                    }
                    Plot plot = start.world.generateAndGetPlot(start.x+x, start.y+y, start.z);
                    for(Side side : plot.getTravelableSides(isWorker)){
                        paths.add(new Path().start(plot).path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
                    }
                }
            }
        }else{
            for(Side side : start.getTravelableSides(isWorker)){
                paths.add(new Path().start(start).path(side.getPlot(start.world, start.x, start.y, start.z)));
            }
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
        if(start.type.skyscraperBaseType!=null){
            start = start.getSkyscraperPlots()[0];
        }
        if(Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots().length>0&&Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots()[0].task!=null){
            start = Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots()[0];
        }
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        ArrayList<Plot> warehouses = new ArrayList<>();
        paths.add(new Path().start(start));
        if(start.skyscraper!=null){
            for(int x = 0; x<start.skyscraper.width; x++){
                for(int y = 0; y<start.skyscraper.height; y++){
                    if(y>0&&y<start.skyscraper.width-1&&x>0&&x<start.skyscraper.width-1){
                        continue;
                    }
                    Plot plot = start.world.generateAndGetPlot(start.x+x, start.y+y, start.z);
                    for(Side side : plot.getTravelableSides(isWorker)){
                        paths.add(new Path().start(plot).path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
                    }
                }
            }
        }else{
            for(Side side : start.getTravelableSides(isWorker)){
                paths.add(new Path().start(start).path(side.getPlot(start.world, start.x, start.y, start.z)));
            }
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
        if(start.type.skyscraperBaseType!=null){
            start = start.getSkyscraperPlots()[0];
        }
        if(Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots().length>0&&Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots()[0].task!=null){
            start = Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots()[0];
        }
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        if(start.skyscraper!=null){
            for(int x = 0; x<start.skyscraper.width; x++){
                for(int y = 0; y<start.skyscraper.height; y++){
                    if(y>0&&y<start.skyscraper.width-1&&x>0&&x<start.skyscraper.width-1){
                        continue;
                    }
                    Plot plot = start.world.generateAndGetPlot(start.x+x, start.y+y, start.z);
                    for(Side side : plot.getTravelableSides(isWorker)){
                        paths.add(new Path().start(plot).path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
                    }
                }
            }
        }else{
            for(Side side : start.getTravelableSides(isWorker)){
                paths.add(new Path().start(start).path(side.getPlot(start.world, start.x, start.y, start.z)));
            }
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
        if(start.type.skyscraperBaseType!=null){
            start = start.getSkyscraperPlots()[0];
        }
        if(Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots().length>0){
            start = Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots()[0];
        }
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        if(start.skyscraper!=null){
            for(int x = 0; x<start.skyscraper.width; x++){
                for(int y = 0; y<start.skyscraper.height; y++){
                    if(y>0&&y<start.skyscraper.width-1&&x>0&&x<start.skyscraper.width-1){
                        continue;
                    }
                    Plot plot = start.world.generateAndGetPlot(start.x+x, start.y+y, start.z);
                    for(Side side : plot.getTravelableSides(isWorker)){
                        paths.add(new Path().start(plot).path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
                    }
                }
            }
        }else{
            for(Side side : start.getTravelableSides(isWorker)){
                paths.add(new Path().start(start).path(side.getPlot(start.world, start.x, start.y, start.z)));
            }
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
            if(plot.getType().skyscraperFloorType!=null){
                for(Plot aplot : plot.skyscraper.getAllPlots()){
                    if(aplot.civilians.size()+aplot.workers.size()<aplot.getMaximumCivilianCapacity()){
                        return aplot;
                    }
                }
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
        if(start.type.skyscraperBaseType!=null){
            start = start.getSkyscraperPlots()[0];
        }
        if(Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots().length>0&&Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots()[0].task!=null){
            start = Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots()[0];
        }
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        if(start.skyscraper!=null){
            for(int x = 0; x<start.skyscraper.width; x++){
                for(int y = 0; y<start.skyscraper.height; y++){
                    if(y>0&&y<start.skyscraper.width-1&&x>0&&x<start.skyscraper.width-1){
                        continue;
                    }
                    Plot plot = start.world.generateAndGetPlot(start.x+x, start.y+y, start.z);
                    for(Side side : plot.getTravelableSides(isWorker)){
                        paths.add(new Path().start(plot).path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
                    }
                }
            }
        }else{
            for(Side side : start.getTravelableSides(isWorker)){
                paths.add(new Path().start(start).path(side.getPlot(start.world, start.x, start.y, start.z)));
            }
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
        return findPath(start, end, isWorker, false);
    }
    public static Path findPath(Plot start, Plot end, boolean isWorker, boolean isSkyscrapering){
        if(start!=end&&start.skyscraper!=null&&start.skyscraper==end.skyscraper){
            return new Path().start(start).path(end);
        }
        if(start.type.skyscraperBaseType!=null){
            return findPath(start.getSkyscraperPlots()[0], end, isWorker);
        }
        if(start.skyscraper==null&&Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots().length>0&&Core.world.getPlot(start.x, start.y, start.z-1).getSkyscraperPlots()[0].task!=null){
            return findPath(Core.world.getPlot(start.x, start.y, start.z-1), end, isWorker);
        }
        if(end.skyscraper!=null&&!isSkyscrapering){
            ArrayList<Path> path = new ArrayList<Path>();
            for(int x = 0; x<end.skyscraper.width; x++){
                for(int y = 0; y<end.skyscraper.height; y++){
                    if(x>0&&x<end.skyscraper.width-1&&y>0&&y<end.skyscraper.height-1){
                        continue;
                    }
                    path.add(findPath(start, end.world.getPlot(end.x+x, end.y+y, end.skyscraper.basePlot.z), isWorker, true));
                }
            }
            while(path.remove(null));
            if(path.isEmpty()){
                return null;
            }
            Collections.sort(path);
            return path.get(0).path(end);
        }
        if(end.skyscraper==null&&Core.world.getPlot(end.x, end.y, end.z-1).getSkyscraperPlots().length>0&&Core.world.getPlot(end.x, end.y, end.z-1).getSkyscraperPlots()[0].task!=null){
            Path path = findPath(start, Core.world.getPlot(end.x, end.y, end.z-1), isWorker);
            if(path!=null){
                path.path(end);
            }
            return path;
        }
        if(start==end){
            return new Path().start(start);
        }
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        coveredPlots.add(start);
        if(start.skyscraper!=null){
            for(int x = 0; x<start.skyscraper.width; x++){
                for(int y = 0; y<start.skyscraper.height; y++){
                    if(y>0&&y<start.skyscraper.width-1&&x>0&&x<start.skyscraper.width-1){
                        continue;
                    }
                    Plot plot = start.world.generateAndGetPlot(start.x+x, start.y+y, start.z);
                    for(Side side : plot.getTravelableSides(isWorker)){
                        paths.add(new Path().start(plot).path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
                    }
                }
            }
        }else{
            for(Side side : start.getTravelableSides(isWorker)){
                paths.add(new Path().start(start).path(side.getPlot(start.world, start.x, start.y, start.z)));
            }
        }
        if(isWorker&&!start.getTravelableSides(isWorker).contains(Side.UP)){
            paths.add(new Path().start(start).path(Side.UP.getPlot(start.world, start.x, start.y, start.z)).setJumped(true));
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
                if(isWorker&&plot.getType()==PlotType.Air&&end==plot.world.getPlot(plot.x, plot.y, plot.z-1)&&!path.jumped){
                    paths.add(path.copy().path(Side.DOWN.getPlot(plot.world, plot.x, plot.y, plot.z)));
                }
                continue;
            }
            if(path.jumped&&path.path.size()==3&&path.path.get(2).type!=PlotType.Road&&path.path.get(2).type!=PlotType.Elevator&&(!isWorker||(path.path.get(2).type!=PlotType.AirportJetway&&path.path.get(2).type!=PlotType.AirportRunway))){
                break;
            }
            for(Side side : plot.getTravelableSides(isWorker)){
                paths.add(path.copy().path(side.getPlot(plot.world, plot.x, plot.y, plot.z)));
            }
        }
        return null;
    }
    public static Path load(Config config){
        Path path = new Path();
        for(int i = 0; i<(int)config.get("count"); i++){
            path.path.add(Core.loadingWorld.getPlot((int)config.get(i+"x"), (int)config.get(i+"y"), (int)config.get(i+"z")));
        }
        return path;
    }
}
