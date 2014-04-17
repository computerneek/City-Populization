package CityPopulization.world.aircraft;
import simplelibrary.config2.Config;
public class TaxiEventRight implements TaxiEvent {
    private int tick;
    public TaxiEventRight(){}
    @Override
    public boolean update(Aircraft aircraft){
        if(tick==0){
            aircraft.setTargetHeading(aircraft.getHeading()+90);
        }
        tick++;
        return tick>=40;
    }
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "right");
        config.set("tick", tick);
        return config;
    }
}
