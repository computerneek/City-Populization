package CityPopulization.world.aircraft.passenger;
import CityPopulization.world.civillian.Civilian;
import CityPopulization.world.civillian.Worker;
public class AircraftPassengerWorker extends AircraftPassenger{
    @Override
    public Civilian createCivilian(){
        return new Worker();
    }
}
