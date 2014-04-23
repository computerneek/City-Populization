package CityPopulization.world.civilian.event;
import CityPopulization.world.civilian.Civilian;
import simplelibrary.config2.Config;
public class EventWait extends Event{
    private int ticks;
    int time;
    public EventWait(int ticks){
        this.ticks = ticks;
    }
    @Override
    public boolean isComplete(){
        return this.time>=ticks;
    }
    @Override
    public void start(Civilian worker){}
    @Override
    public void work(Civilian worker){
        time++;
    }
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "wait");
        config.set("ticks", ticks);
        config.set("time", time);
        return config;
    }
    @Override
    public boolean validate(){
        return ticks>0;
    }
}
