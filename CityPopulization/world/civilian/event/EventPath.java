package CityPopulization.world.civilian.event;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.civilian.Path;
import simplelibrary.config2.Config;
public class EventPath implements Event{
    private Path path;
    private Civilian worker;
    public EventPath(Path path){
        this.path = path;
    }
    @Override
    public boolean isComplete(){
        return worker!=null&&worker.path==null;
    }
    @Override
    public void start(Civilian worker){
        worker.path = path;
    }
    @Override
    public void work(Civilian worker){
        this.worker = worker;
    }
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "path");
        config.set("path", path.save());
        return config;
    }
}
