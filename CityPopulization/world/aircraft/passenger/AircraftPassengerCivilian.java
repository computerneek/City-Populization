package CityPopulization.world.aircraft.passenger;
import CityPopulization.world.civilian.Civilian;
import simplelibrary.config2.Config;
public class AircraftPassengerCivilian extends AircraftPassenger{
    @Override
    public Civilian createCivilian(){
        return new Civilian();
    }
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "civilian");
        return config;
    }
}
