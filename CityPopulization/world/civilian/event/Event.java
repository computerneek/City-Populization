package CityPopulization.world.civilian.event;
import CityPopulization.world.civilian.Civilian;
public interface Event {
    public boolean isComplete();
    public void start(Civilian worker);
    public void work(Civilian aThis);

}
