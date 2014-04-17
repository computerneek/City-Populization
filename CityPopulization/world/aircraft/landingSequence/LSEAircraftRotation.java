package CityPopulization.world.aircraft.landingSequence;
import CityPopulization.world.aircraft.Aircraft;
import simplelibrary.config2.Config;
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
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "rotate");
        config.set("rotation", rotation);
        return config;
    }
}
