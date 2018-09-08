package CityPopulization.world.aircraft;
import CityPopulization.Core;
import simplelibrary.config2.Config;
public abstract class TaxiEvent {
    public static TaxiEvent load(Config config){
        TaxiEvent event = null;
        switch((String)config.get("type")){
            case "left":
                event = new TaxiEventLeft();
                ((TaxiEventLeft)event).tick = config.get("tick");
                break;
            case "release":
                event = new TaxiEventRelease(Core.loadingWorld.getPlot((int)config.get("x"), (int)config.get("y"), (int)config.get("z")));
                break;
            case "right":
                event = new TaxiEventRight();
                ((TaxiEventRight)event).tick = config.get("tick");
                break;
            case "start":
                event = new TaxiEventStart();
                ((TaxiEventStart)event).tick = config.get("tick");
                break;
            case "straight":
                event = new TaxiEventStraight();
                ((TaxiEventStraight)event).tick = config.get("tick");
                break;
            default:
                throw new AssertionError(config.get("type"));
        }
        return event;
    }
    public abstract boolean update(Aircraft aThis);
    public abstract Config save();

}
