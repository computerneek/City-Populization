package CityPopulization.world.aircraft.landingSequence;
import CityPopulization.world.aircraft.Aircraft;
public class LSEAircraftRotation implements LandingSequenceEvent {
    private int rotation;
    public LSEAircraftRotation(int rotation){
        this.rotation = rotation;
    }
    @Override
    public boolean update(Aircraft aircraft){
        aircraft.setHeading(aircraft.getHeading()+rotation);
        aircraft.setTargetHeading(aircraft.getHeading());
        return true;
    }
}
