package CityPopulization.world.civilian.event;
import CityPopulization.render.Side;
import CityPopulization.world.civilian.Worker;
import CityPopulization.world.player.Player;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
public class EventPlotSet implements Event{
    private Plot plot;
    private PlotType type;
    private int level;
    private Side front;
    private Player owner;
    private boolean started;
    public EventPlotSet(Plot plot, PlotType type, int level, Side front, Player owner){
        this.plot = plot;
        this.type = type;
        this.level = level;
        this.front = front;
        this.owner = owner;
    }
    @Override
    public boolean isComplete(){
        return started;
    }
    @Override
    public void start(Worker worker){
        plot.setType(type);
        plot.setLevel(level);
        plot.setFront(front);
        plot.setOwner(owner);
        started = true;
    }
    @Override
    public void work(Worker worker){}
}
