package CityPopulization.world.civillian;
import java.util.ArrayList;
public class WorkerTaskManager{
    private static ArrayList<WorkerTask> tasks = new ArrayList<>();
    public static boolean hasTasks(){
        return !tasks.isEmpty();
    }
    public void addTask(WorkerTask task){
        tasks.add(task);
    }
}
