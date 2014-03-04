package CityPopulization.world.aircraft.landingSequence;
import CityPopulization.world.aircraft.Aircraft;
public class LSEPitch implements LandingSequenceEvent {
    private final int targetPitch;
    public LSEPitch(int targetPitch){
        this.targetPitch=targetPitch;
    }
    @Override
    public boolean update(Aircraft aircraft){
        aircraft.targetPitch=targetPitch;
        return true;
    }
}
