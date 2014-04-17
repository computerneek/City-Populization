package CityPopulization.world.aircraft;
import simplelibrary.config2.Config;
public class TaxiEventLeft implements TaxiEvent {
    private int tick;
    public TaxiEventLeft(){}
    @Override
    public boolean update(Aircraft aircraft){
        if(tick==0){
            aircraft.setTargetHeading(aircraft.getHeading()-90);
        }
        tick++;
        return tick>=40;
    }
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "left");
        config.set("tick", tick);
        return config;
    }
}
