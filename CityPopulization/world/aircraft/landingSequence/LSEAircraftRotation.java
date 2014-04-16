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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
