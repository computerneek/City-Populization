package CityPopulization.world.aircraft.passenger;
import CityPopulization.world.civillian.Civilian;
public class AircraftPassengerCivilian extends AircraftPassenger{
    @Override
    public Civilian createCivilian(){
        return new Civilian();
    }
}
