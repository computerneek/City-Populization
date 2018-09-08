package CityPopulization.world.aircraft.landingSequence;
import CityPopulization.world.aircraft.Aircraft;
import simplelibrary.config2.Config;
public class LSEMovement extends LandingSequenceEvent {
    private final int time;
    private final int speed;
    int ticks;
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
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "movement");
        config.set("time", time);
        config.set("speed", speed);
        config.set("ticks", ticks);
        return config;
    }
}
