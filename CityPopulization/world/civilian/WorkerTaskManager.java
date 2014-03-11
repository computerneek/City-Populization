package CityPopulization.world.civilian;
import java.util.ArrayList;
public class WorkerTaskManager{
    private ArrayList<WorkerTask> tasks = new ArrayList<>();
    public boolean hasTasks(){
        return !tasks.isEmpty();
    }
    public void addTask(WorkerTask task){
        tasks.add(task);
        task.prepare();
    }
    public int getWorkerCarryingCapacity(){
        return 25;
    }
    public void update(){
        for(WorkerTask task : (ArrayList<WorkerTask>)tasks.clone()){
            task.update();
        }
    }
    public void removeTask(WorkerTask task){
        tasks.remove(task);
    }
}
