package CityPopulization;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import multilib.net.Packet1Integer;
import multilib.net.Packet2String;
import multilib.net.Packet3Boolean;
public class WorkerTask extends CivillianTask{
    public boolean cancelled;
    public boolean completed;
    public int workersFinished;
    public WorkerTask nextTask;
    public WorkerTask(String desc, Plot plot, int time, int cash, int dirt, int coal, int oil, int wood, int stone, int iron, int sand, int clay, int gold, int workersRequired){
        super(desc, plot, time, cash, dirt, coal, oil, wood, stone, iron, sand, clay, gold, workersRequired, plot);
    }
    public void assignWorker(Civillian worker){
        workers.add(worker);
        worker.workingTime = 0;
        worker.status = Worker.Status.working;
        if(workersRequired>0&&workers.size()>workersRequired){
            Civillian WORKER = workers.get(0);
            unassignWorker(WORKER);
            WORKER.clearTask();
            WORKER.workingTime = 0;
            WORKER.status = Worker.Status.idle;
        }
    }
    public void unassignWorker(Civillian worker){
        if(workers.contains(worker)){
            workers.remove(worker);
        }
        workersSent--;
    }
    public void work(){
        if(completed){
            while(workers.size()>0){
                workersFinished++;
                workers.remove(0).notifyTaskComplete(this);
            }
            if(workersFinished>=workersRequired){
                WorkerTaskList.notifyTaskComplete(this);
            }
            return;
        }
        progress+=main.getHighestLevel(main.getPlotsOfType(PlotType.workshop))+1;
        if(progress>=time*20*workersRequired*(PlotType.workshop.levels+1)){
            plot.notifyTaskComplete(this);
            while(workers.size()>0){
                workersFinished++;
                workers.remove(0).notifyTaskComplete(this);
            }
            if(workersFinished>=workersRequired){
                WorkerTaskList.notifyTaskComplete(this);
            }
            completed = true;
        }
    }
    public String getDescription(){
        return desc;
    }
    public int getWorkerRequirement(){
        return workersRequired==-1?WorkerTaskList.getWorkerCount():workersRequired;
    }
    public int getCost(){
        return cost;
    }
    public int[] getLocation(){
        return plot.getCoords();
    }
    public void checkWorker(Civillian worker){
        if(cancelled&&workersSent==0&&progress==0){
            throw new IllegalArgumentException("Operation cancelled!");
        }else if(workers.contains(worker)){
            throw new IllegalArgumentException("Worker already in!");
        }else if(cost>0&&main.cash<(cost)&&workersSent==0){
            throw new IllegalArgumentException("Not enough cash!");
        }else if(dirt>0&&main.dirt<(dirt)&&workersSent==0){
            throw new IllegalArgumentException("Not enough dirt!");
        }else if(coal>0&&main.coal<(coal)&&workersSent==0){
            throw new IllegalArgumentException("Not enough coal!");
        }else if(oil>0&&main.oil<(oil)&&workersSent==0){
            throw new IllegalArgumentException("Not enough oil!");
        }else if(wood>0&&main.wood<(wood)&&workersSent==0){
            throw new IllegalArgumentException("Not enough wood!");
        }else if(stone>0&&main.stone<(stone)&&workersSent==0){
            throw new IllegalArgumentException("Not enough stone!");
        }else if(iron>0&&main.iron<(iron)&&workersSent==0){
            throw new IllegalArgumentException("Not enough iron!");
        }else if(sand>0&&main.sand<(sand)&&workersSent==0){
            throw new IllegalArgumentException("Not enough sand!");
        }else if(clay>0&&main.clay<(clay)&&workersSent==0){
            throw new IllegalArgumentException("Not enough clay!");
        }else if(gold>0&&main.gold<(gold)&&workersSent==0){
            throw new IllegalArgumentException("Not enough gold!");
        }else if(WorkerTaskList.getAvailableWorkers()<getWorkerRequirement()-workersSent){
            throw new IllegalArgumentException("Not enough available workers!");
        }else if(workersSent>=getWorkerRequirement()){
            throw new IllegalArgumentException("All workers already sent!");
        }else{
            if(workersSent==0){
                main.cash-=cost;
                main.dirt-=dirt;
                main.coal-=coal;
                main.oil-=oil;
                main.wood-=wood;
                main.stone-=stone;
                main.iron-=iron;
                main.sand-=sand;
                main.clay-=clay;
                main.gold-=gold;
            }
            workersSent++;
            sentWorkers.add(worker);
        }
    }
    public boolean canWorkerCome(Civillian worker){
        if(cancelled&&workersSent==0&&progress==0){
            return false;
        }else if(workers.contains(worker)){
            return false;
        }else if(cost>0&&main.cash<(cost)&&workersSent==0){
            return false;
        }else if(dirt>0&&main.dirt<(dirt)&&workersSent==0){
            return false;
        }else if(coal>0&&main.coal<(coal)&&workersSent==0){
            return false;
        }else if(oil>0&&main.oil<(oil)&&workersSent==0){
            return false;
        }else if(wood>0&&main.wood<(wood)&&workersSent==0){
            return false;
        }else if(stone>0&&main.stone<(stone)&&workersSent==0){
            return false;
        }else if(iron>0&&main.iron<(iron)&&workersSent==0){
            return false;
        }else if(sand>0&&main.sand<(sand)&&workersSent==0){
            return false;
        }else if(clay>0&&main.clay<(clay)&&workersSent==0){
            return false;
        }else if(gold>0&&main.gold<(gold)&&workersSent==0){
            return false;
        }else if(WorkerTaskList.getAvailableWorkers()<getWorkerRequirement()-workersSent){
            return false;
        }else if(workersSent>=getWorkerRequirement()){
            return false;
        }else{
            return true;
        }
    }
    public boolean cancel(){
        if(workersSent==0&&progress==0){
            cancelled = true;
        }else{
            cancelled = false;
        }
        return cancelled;
    }
    public void tick(){
        for(int i = 0; i<workers.size(); i++){
            if(workers.get(i).task == null){
                workers.remove(i);
                i--;
            }
            if(i<0){
                continue;
            }
            if(workers.get(i).getLocation()!=this.plot){
                workers.get(i).task=null;
                workers.remove(i);
                i--;
            }
        }
        for(int i = 0; i<sentWorkers.size(); i++){
            if(workers.contains(sentWorkers.get(i))){
                sentWorkers.remove(i);
                i--;
            }
            if(i<0){
                continue;
            }
            if(sentWorkers.get(i).task == null){
                sentWorkers.remove(i);
                i--;
            }
            if(i<0){
                continue;
            }
            if(sentWorkers.get(i).isAtHome || sentWorkers.get(i).path == null){
                sentWorkers.get(i).task=null;
                sentWorkers.remove(i);
                i--;
            }
        }
        workersSent = workers.size()+sentWorkers.size()+workersFinished;
        if(workersSent>0&&progress>=time*20*workersRequired*(PlotType.workshop.levels+1)){
            plot.notifyTaskComplete(this);
            while(workers.size()>0){
                workersFinished++;
                workers.remove(0).notifyTaskComplete(this);
            }
            if(workersFinished>=workersRequired){
                WorkerTaskList.notifyTaskComplete(this);
            }
            completed = true;
        }
    }
    public void forceCancel(){
        cancelled = true;
        progress = 0;
        for(int i = 0; i<WorkerTaskList.workers.size(); i++){
            if(WorkerTaskList.workers.get(i).task == this){
                WorkerTaskList.workers.get(i).task = null;
            }
        }
        if(workersSent>0){
            main.cash+=cost;
            main.dirt+=dirt;
            main.coal+=coal;
            main.oil+=oil;
            main.wood+=wood;
            main.stone+=stone;
            main.iron+=iron;
            main.sand+=sand;
            main.clay+=clay;
            main.gold+=gold;
        }
    }
    public void save(DataOutputStream out) throws IOException{
        super.save(out);
        out.writeBoolean(this instanceof WorkerTaskHarvest);
        out.writeBoolean(cancelled);
        out.writeBoolean(completed);
        out.writeInt(workersFinished);
        out.writeBoolean(nextTask!=null);
        if(nextTask!=null){
            nextTask.save(out);
        }
    }
    public static WorkerTask load(DataInputStream in) throws IOException{
        Plot plot = main.getPlot(new int[]{in.readInt(), in.readInt()});
        int time = in.readInt();
        int cost = in.readInt();
        int progress = in.readInt();
        String desc = in.readUTF();
        int workersRequired = in.readInt();
        int workersSent = in.readInt();
        Plot civillianHome = null;
        if(in.readBoolean()){
            civillianHome = main.getPlot(new int[]{in.readInt(), in.readInt()});
        }
        int dirt = in.readInt();
        int coal = in.readInt();
        int oil = in.readInt();
        int wood = in.readInt();
        int stone = in.readInt();
        int iron = in.readInt();
        int sand = in.readInt();
        int clay = in.readInt();
        int gold = in.readInt();
        int index = in.readInt();
        boolean isHarvestTask = in.readBoolean();
        boolean cancelled = in.readBoolean();
        boolean completed = in.readBoolean();
        int workersFinished = in.readInt();
        boolean hasNextTask = in.readBoolean();
        WorkerTask nextTask = null;
        if(hasNextTask){
            nextTask = load(in);
        }
        WorkerTask task;
        if(isHarvestTask){
            task = new WorkerTaskHarvest(plot);
        }else{
            task = new WorkerTask(desc, plot, time, cost, dirt, coal, oil, wood, stone, iron, sand, clay, gold, workersRequired);
        }
        task.progress = progress;
        task.workersSent = workersSent;
        task.setIndex(index);
        task.cancelled = cancelled;
        task.completed = completed;
        task.workersFinished = workersFinished;
        task.nextTask = nextTask;
        plot.task = task;
        return task;
    }
    public boolean hasNextTask(){
        return nextTask!=null;
    }
    public WorkerTask getNextTask(){
        return nextTask;
    }
}
