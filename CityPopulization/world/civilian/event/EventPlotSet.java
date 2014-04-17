package CityPopulization.world.civilian.event;
import CityPopulization.render.Side;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.player.Player;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import simplelibrary.config2.Config;
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
    public void start(Civilian worker){
        plot.setType(type);
        plot.setLevel(level);
        plot.setFront(front);
        plot.setOwner(type==PlotType.Air?null:owner);
        started = true;
    }
    @Override
    public void work(Civilian worker){}
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "plot set");
        config.set("x", plot.x);
        config.set("y", plot.y);
        config.set("z", plot.z);
        config.set("type", type.name());
        config.set("level", level);
        config.set("front", front.name());
        config.set("owner", owner.world.otherPlayers.indexOf(owner));
        config.set("started", started);
        return config;
    }
}
