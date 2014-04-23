package CityPopulization.world.aircraft.landingSequence;
import CityPopulization.world.aircraft.Aircraft;
import simplelibrary.config2.Config;
public abstract class LandingSequenceEvent {
    public static LandingSequenceEvent load(Config config){
        LandingSequenceEvent evnt = null;
        switch((String)config.get("type")){
            case "rotate":
                evnt = new LSEAircraftRotation((int)config.get("rotation"));
                break;
            case "movement":
                evnt = new LSEMovement((int)config.get("time"), (int)config.get("speed"));
                ((LSEMovement)evnt).ticks = config.get("ticks");
                break;
            case "pitch":
                evnt = new LSEPitch((int)config.get("pitch"));
                break;
            case "startingPoint":
                evnt = new LSEStartingPoint((int)config.get("dist"), (int)config.get("height"), (int)config.get("speed"));
        }
        return evnt;
    }
    public abstract boolean update(Aircraft aircraft);
    public abstract Config save();
}
