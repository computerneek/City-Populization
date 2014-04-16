package CityPopulization.world.aircraft.landingSequence;
import CityPopulization.world.aircraft.Aircraft;
import simplelibrary.config2.Config;
public interface LandingSequenceEvent {
    public abstract boolean update(Aircraft aircraft);
    public Config save();
}
