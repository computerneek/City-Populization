package CityPopulization.world.civilian;
import java.util.ArrayList;
import simplelibrary.config2.Config;
public class WorkerTaskManager{
    private ArrayList<WorkerTask> tasks = new ArrayList<>();
    public boolean hasTasks(){
        return !tasks.isEmpty();
    }
    public void addTask(WorkerTask task){
        tasks.add(task);
        task.prepare();
    }
    public void removeTask(WorkerTask task){
        tasks.remove(task);
    }
}
