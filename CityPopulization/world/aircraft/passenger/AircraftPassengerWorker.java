package CityPopulization.world.aircraft.passenger;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.civilian.Worker;
import simplelibrary.config2.Config;
public class AircraftPassengerWorker extends AircraftPassenger{
    @Override
    public Civilian createCivilian(){
        return new Worker();
    }
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "worker");
        return config;
    }
}
