package CityPopulization.world.civilian.event;
import CityPopulization.world.civilian.Worker;
public interface Event {
    public boolean isComplete();
    public void start(Worker worker);
    public void work(Worker aThis);

}
