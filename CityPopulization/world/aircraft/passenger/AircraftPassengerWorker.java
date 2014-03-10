package CityPopulization.world.aircraft.passenger;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.civilian.Worker;
public class AircraftPassengerWorker extends AircraftPassenger{
    @Override
    public Civilian createCivilian(){
        return new Worker();
    }
}
