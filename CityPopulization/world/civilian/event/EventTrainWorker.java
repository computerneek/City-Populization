package CityPopulization.world.civilian.event;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.player.Player;
import simplelibrary.config2.Config;
public class EventTrainWorker extends Event {
    public EventTrainWorker(){}
    boolean started = false;
    @Override
    public boolean isComplete(){
        return started;
    }
    @Override
    public void start(Civilian worker){
        started = true;
        Civilian aworker = new Civilian().upgradeToWorker();
        aworker.dest = worker.dest;
        aworker.dist = worker.dist;
        aworker.homePlot = worker.homePlot;
        aworker.path = worker.path;
        aworker.x = worker.x;
        aworker.y = worker.y;
        aworker.z = worker.z;
        aworker.task = worker.task;
        aworker.subtask = worker.subtask;
        worker.homePlot.civilians.remove(worker);
        worker.homePlot.workers.add(aworker);
        aworker.player = worker.player;
        worker.player.world.civilians.remove(worker);
        worker.player.world.civilians.add(aworker);
        for(Player player : worker.player.world.otherPlayers){
            player.civilianTrained(worker, aworker);
        }
    }
    @Override
    public void work(Civilian worker){}
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "train");
        config.set("started", started);
        return config;
    }
    @Override
    public boolean validate(){
        return true;
    }
}
