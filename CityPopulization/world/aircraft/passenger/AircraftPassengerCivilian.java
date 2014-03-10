package CityPopulization.world.aircraft.passenger;
import CityPopulization.world.civilian.Civilian;
public class AircraftPassengerCivilian extends AircraftPassenger{
    @Override
    public Civilian createCivilian(){
        return new Civilian();
    }
}
