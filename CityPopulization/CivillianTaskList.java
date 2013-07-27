package CityPopulization;
import java.util.ArrayList;
public class CivillianTaskList{
    public static ArrayList<CivillianTask> tasks = new ArrayList<>();
    public static ArrayList<ArrayList<Civillian>> taskWorkers = new ArrayList<>();
    public static ArrayList<Civillian> workers = new ArrayList<>();
    public static final String waitor = "";
    public static int getAvailableWorkers(){
        return Plot.totalCivillians;
    }
    public static boolean hasWorkers(int countDesired){
        synchronized(waitor){
            return getWorkerCount()>=countDesired;
        }
    }
    public static int getWorkerCount(){
        synchronized(waitor){
            return workers.size()+Plot.totalCivillians;
        }
    }
    public static void addTask(CivillianTask workerTask){
        synchronized(waitor){
            tasks.add(workerTask);
            taskWorkers.add(new ArrayList<Civillian>());
        }
    }
    public static Civillian addCivillian(Plot workerHome){
        synchronized(waitor){
            Civillian newWorker = new Civillian();
            newWorker.setHome(workerHome);
            workers.add(newWorker);
            return newWorker;
        }
    }
    public static void removeWorker(Civillian worker){
        synchronized(waitor){
            workers.remove(worker);
        }
    }
    public static void notifyTaskComplete(CivillianTask task){
        synchronized(waitor){
            if(tasks.contains(task)){
                ArrayList<Civillian> taskWorkerss = taskWorkers.get(tasks.indexOf(task));
                tasks.remove(task);
                taskWorkers.remove(taskWorkerss);
            }
        }
    }
}
