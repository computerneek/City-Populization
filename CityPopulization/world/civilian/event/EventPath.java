package CityPopulization.world.civilian.event;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.civilian.Path;
public class EventPath implements Event{
    private Path path;
    private Civilian worker;
    public EventPath(Path path){
        this.path = path;
    }
    @Override
    public boolean isComplete(){
        return path.isComplete();
    }
    @Override
    public void start(Civilian worker){
        worker.path = path;
    }
    @Override
    public void work(Civilian worker){
        this.worker = worker;
    }
}
