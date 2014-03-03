package CityPopulization.world.aircraft.landingSequence;
import CityPopulization.world.aircraft.Aircraft;
public class LSEMovement implements LandingSequenceEvent {
    private final int distanceBack;
    private final int height;
    private final int speed;
    public LSEMovement(int distanceBack, int height, int speed){
        this.distanceBack=distanceBack;
        this.height=height;
        this.speed=speed;
    }
    @Override
    public boolean update(Aircraft aircraft){
        aircraft.setTargetSpeed(speed);
        aircraft.setTargetHeight(height);
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
