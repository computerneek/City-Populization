package CityPopulization.world.civilian.event;
import CityPopulization.world.civilian.Civilian;
import simplelibrary.config2.Config;
public interface Event {
    public boolean isComplete();
    public void start(Civilian worker);
    public void work(Civilian aThis);
    public Config save();
}
