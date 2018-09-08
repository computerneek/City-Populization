package CityPopulization.world.aircraft;
import simplelibrary.config2.Config;
public class TaxiEventStart extends TaxiEvent {
    int tick;
    @Override
    public boolean update(Aircraft aircraft){
        aircraft.setTargetSpeed(1);
        tick++;
        return tick>=35;
    }
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "start");
        config.set("tick", tick);
        return config;
    }
}
