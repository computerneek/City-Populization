package CityPopulization;
import java.util.ArrayList;
import multilib.audio.AudioManager;
import multilib.error.Sys;
public class WorkerTaskList{
    public static ArrayList<WorkerTask> tasks = new ArrayList<>();
    public static ArrayList<WorkerTask> importantTasks = new ArrayList<>();
    public static ArrayList<Worker> workers = new ArrayList<>();
    public static ArrayList<Worker> totalWorkers = new ArrayList<>();
    public static final String waitor = "";
    private static Worker worker;
    public static int getAvailableWorkers(){
        return Plot.totalWorkers;
    }
    public static boolean hasWorkers(int countDesired){
        synchronized(waitor){
            return getWorkerCount()>=countDesired;
        }
    }
    public static int getWorkerCount(){
        synchronized(waitor){
            return workers.size()+Plot.totalWorkers;
        }
    }
    public static WorkerTask addTask(WorkerTask task){
        synchronized(waitor){
            tasks.add(task);
            return task;
        }
    }
    public static WorkerTask addImportantTask(WorkerTask task){
        synchronized(waitor){
            importantTasks.add(task);
            return task;
        }
    }
    public static Worker addWorker(Plot workerHome, Plot barracks){
        synchronized(waitor){
            Worker newWorker = new Worker(barracks);
            newWorker.setHome(workerHome);
            workers.add(newWorker);
            totalWorkers.add(newWorker);
            return newWorker;
        }
    }
    public static void removeWorker(Worker worker){
        synchronized(waitor){
            workers.remove(worker);
            totalWorkers.remove(worker);
        }
    }
    public static void notifyTaskComplete(WorkerTask task){
        synchronized(waitor){
            if(tasks.contains(task)){
                tasks.remove(task);
            }
            if(importantTasks.contains(task)){
                importantTasks.remove(task);
            }
        }
    }
    public static void tick(){
        synchronized(waitor){
            Worker[] workerss = workers.toArray(new Worker[workers.size()]);
            for(Worker worker : workerss){
                worker.tick();
            }
            Civillian[] civillians = CivillianTaskList.workers.toArray(new Civillian[CivillianTaskList.workers.size()]);
            for(Civillian worker : civillians){
                worker.tick();
            }
        }
        ArrayList<WorkerTask> totalTasks = new ArrayList<>();
        totalTasks.addAll(importantTasks);
        totalTasks.addAll(tasks);
        WorkerTask[] Tasks = totalTasks.toArray(new WorkerTask[totalTasks.size()]);
        int taskFound = 0;
        int sentCountLimit = 100-(int)(main.lagTicks/10F);
        Profiler.start("tasking");
        for(WorkerTask task : Tasks){
            if(task.cancelled||task.completed){
                importantTasks.remove(task);
                tasks.remove(task);
                continue;
            }
            if(taskFound<(sentCountLimit)){
                if(Plot.totalWorkers>0&&task.workersSent<task.workersRequired&&!task.completed){
                    if(worker==null){
                        worker = findWorker();
                    }
                    if(!(worker==null||!WorkerPath.isPathTo(main.getPlotCoordinates(worker.coords), task.getLocation()))&&task.canWorkerCome(worker)){
                        task.checkWorker(worker);
                        worker.task = task;
                        worker.path = WorkerPath.findPathTo(main.getPlotCoordinates(worker.coords), task.getLocation());
                        worker.home.onWorkerExit(worker);
                        worker.isAtHome = false;
                        worker = null;
                        taskFound++;
                    }
                }
            }
            task.tick();
        }
        worker = null;
        Profiler.end();
    }
    private static Worker findWorker(){
        Plot[] plots = main.getPlotsOfType(PlotType.house);
        Worker worker = null;
        for(Plot plot : plots){
            if((worker = plot.getWorker())!=null){
               return worker;
            }
        }
        return null;
    }
    public static WorkerTask getTaskByIndex(int value){
        for(WorkerTask task : importantTasks){
            if(task.index()==value){
                return task;
            }
        }
        for(WorkerTask task : tasks){
            if(task.index()==value){
                return task;
            }
        }
        return null;
    }
}
