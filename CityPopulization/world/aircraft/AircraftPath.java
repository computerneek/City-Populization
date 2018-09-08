package CityPopulization.world.aircraft;
import CityPopulization.Core;
import CityPopulization.render.Side;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import java.util.ArrayList;
import simplelibrary.config2.Config;
public class AircraftPath {
    private static final int LEFT = 1;
    private static final int STRAIGHT = 2;
    private static final int RIGHT = 3;
    public static AircraftPath findPath(Runway runway, Terminal terminal){
        ArrayList<AircraftPath> paths = new ArrayList<>();
        Plot startPlot = runway.getStartPlot();
        Plot endPlot = terminal.getPlot();
        if(startPlot.getFrontPlot().getType()!=PlotType.AirportJetway){
            return null;
        }
        ArrayList<Plot> plotsCovered = new ArrayList<>();
        AircraftPath path = new AircraftPath().start(startPlot, startPlot.front);
        paths.add(path);
        path = null;
        while(path==null&&!paths.isEmpty()){
            path = path(paths, endPlot, plotsCovered, Terminal.IN);
        }
        return path;
    }
    private static AircraftPath path(ArrayList<AircraftPath> paths, Plot endPlot, ArrayList<Plot> coveredPlots, int direction){
        AircraftPath path = paths.remove(0);
        if(endPlot==path.currentPlot){
            Side side = path.startPlot.front;
            for(Integer num : path.instructions){
                if(num==LEFT){
                    side = side.left();
                }else if(num==RIGHT){
                    side = side.right();
                }
            }
            if(endPlot.front==side.reverse()){
                return path;
            }else{
                return null;
            }
        }
        coveredPlots.add(path.currentPlot);
        if(path.currentPlot.getType()!=PlotType.AirportJetway||(path.currentPlot.terminal.occupied!=direction&&path.currentPlot.terminal.occupied!=0)){
            return null;
        }
        Plot plot = path.currentPlot;
        for(int i = 1; i<4; i++){
            paths.add(path.copy().path(i));
        }
        return null;
    }
    public static AircraftPath findPath(Terminal terminal, Runway runway){
        ArrayList<AircraftPath> paths = new ArrayList<>();
        Plot startPlot = terminal.getPlot();
        Plot endPlot = runway.getStartPlot();
        if(startPlot.getFrontPlot().getType()!=PlotType.AirportJetway){
            return null;
        }
        ArrayList<Plot> plotsCovered = new ArrayList<>();
        AircraftPath path = new AircraftPath().start(startPlot, startPlot.front);
        paths.add(path);
        path = null;
        while(path==null&&!paths.isEmpty()){
            path = path(paths, endPlot, plotsCovered, Terminal.OUT);
        }
        return path;
    }
    private Plot startPlot;
    private Plot currentPlot;
    private Side currentSide;
    private ArrayList<Integer> instructions = new ArrayList<>();
    private AircraftPath start(Plot startPlot, Side side){
        this.startPlot = startPlot;
        this.currentSide = side;
        this.currentPlot = side.getPlot(startPlot.world, startPlot.x, startPlot.y, startPlot.z);
        return this;
    }
    private AircraftPath path(int direction){
        instructions.add(direction);
        if(direction==LEFT){
            currentSide = currentSide.left();
        }else if(direction==RIGHT){
            currentSide = currentSide.right();
        }
        currentPlot = currentSide.getPlot(currentPlot.world, currentPlot.x, currentPlot.y, currentPlot.z);
        return this;
    }
    public ArrayList<TaxiEvent> generateDirections(){
        PlotType startType = startPlot.getType();
        int direction = startType==PlotType.AirportRunway?Terminal.IN:Terminal.OUT;
        Side side = startPlot.front;
        Plot plot = side.getPlot(startPlot.world, startPlot.x, startPlot.y, startPlot.z);
        plot.terminal.occupied = direction;
        plot.terminal.occupiers++;
        ArrayList<TaxiEvent> events = new ArrayList<TaxiEvent>();
        events.add(new TaxiEventStart());
        events.add(new TaxiEventRelease(startPlot));
        for(Integer num : instructions){
            if(num==LEFT){
                side = side.left();
                events.add(new TaxiEventLeft());
            }else if(num==RIGHT){
                side = side.right();
                events.add(new TaxiEventRight());
            }else{
                events.add(new TaxiEventStraight());
            }
            events.add(new TaxiEventRelease(plot));
            plot = side.getPlot(plot.world, plot.x, plot.y, plot.z);
            if(plot.getType()!=PlotType.AirportTerminal){
                plot.terminal.occupied=direction;
                plot.terminal.occupiers++;
            }
        }
        return events;
    }
    private AircraftPath copy(){
        AircraftPath copy = new AircraftPath();
        copy.startPlot = startPlot;
        copy.currentPlot = currentPlot;
        copy.currentSide = currentSide;
        copy.instructions = new ArrayList<>(instructions);
        return copy;
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("x", startPlot.x);
        config.set("y", startPlot.y);
        config.set("z", startPlot.z);
        config.set("count", instructions.size());
        for(int i = 0; i<instructions.size(); i++){
            config.set(i+"", instructions.get(i).intValue());
        }
        return config;
    }
    public static AircraftPath load(Config config){
        AircraftPath path = new AircraftPath();
        path.startPlot = Core.loadingWorld.getPlot((int)config.get("x"), (int)config.get("y"), (int)config.get("z"));
        for(int i = 0; i<(int)config.get("count"); i++){
            path.instructions.add((Integer)config.get(i+""));
        }
        return path;
    }
}
