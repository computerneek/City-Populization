package CityPopulization.world.aircraft.landingSequence;
import CityPopulization.world.aircraft.Aircraft;
public class LSEMovement implements LandingSequenceEvent {
    private final int time;
    private final int speed;
    private int ticks;
    public LSEMovement(int time, int speed){
        this.time = time;
        this.speed=speed;
    }
    @Override
    public boolean update(Aircraft aircraft){
        aircraft.setTargetSpeed(speed);
        this.ticks++;
        return ticks>time;
    }
}
