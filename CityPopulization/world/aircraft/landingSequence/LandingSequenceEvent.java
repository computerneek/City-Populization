package CityPopulization.world.aircraft.landingSequence;
import CityPopulization.world.aircraft.Aircraft;
public interface LandingSequenceEvent {
    public abstract boolean update(Aircraft aircraft);
}
