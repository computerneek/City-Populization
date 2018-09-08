package CityPopulization.world.aircraft;
import simplelibrary.config2.Config;
public class TaxiEventStraight extends TaxiEvent {
    int tick;
    @Override
    public boolean update(Aircraft aircraft){
        tick++;
        return tick>=50;
    }
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "straight");
        config.set("tick", tick);
        return config;
    }
}
