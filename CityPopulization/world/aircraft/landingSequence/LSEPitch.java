package CityPopulization.world.aircraft.landingSequence;
import CityPopulization.world.aircraft.Aircraft;
import simplelibrary.config2.Config;
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
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "pitch");
        config.set("pitch", targetPitch);
        return config;
    }
}
