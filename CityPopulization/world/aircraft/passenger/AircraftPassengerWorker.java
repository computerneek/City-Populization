package CityPopulization.world.aircraft.passenger;
import CityPopulization.world.civilian.Civilian;
import simplelibrary.config2.Config;
public class AircraftPassengerWorker extends AircraftPassenger{
    @Override
    public Civilian createCivilian(){
        return new Civilian().upgradeToWorker();
    }
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "worker");
        return config;
    }
}
