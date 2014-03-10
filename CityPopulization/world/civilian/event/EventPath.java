package CityPopulization.world.civilian.event;
import CityPopulization.world.civilian.Path;
import CityPopulization.world.civilian.Worker;
public class EventPath implements Event{
    private Path path;
    public EventPath(Path path){
        this.path = path;
    }
    @Override
    public boolean isComplete(){
        return path.isComplete();
    }
    @Override
    public void start(Worker worker){
        worker.path = path;
    }
    @Override
    public void work(Worker worker){}
}
