package CityPopulization;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import multilib.net.ConnectionManager;
import multilib.net.Packet;
import multilib.net.Packet1Integer;
import multilib.net.Packet2String;
import multilib.net.Packet3Boolean;
public class CivillianTask{
    public final Plot plot;
    public final int time;
    public final int cost;
    public int progress = 0;
    public final String desc;
    public ArrayList<Civillian> workers = new ArrayList<>();
    public ArrayList<Civillian> sentWorkers = new ArrayList<>();
    public final int workersRequired;
    public int workersSent = 0;
    private final Plot civillianHome;
    public final int dirt;
    public final int coal;
    public final int oil;
    public final int wood;
    public final int stone;
    public final int iron;
    public final int sand;
    public final int clay;
    public final int gold;
    private int index = -1;
    public CivillianTask(String desc, Plot plot, int time, int cost, int dirt, int coal, int oil, int wood, int stone, int iron, int sand, int clay, int gold, int workersRequired, Plot civillianHome){
        this.desc = desc;
        this.plot = plot;
        this.time = time;
        this.cost = cost;
        this.dirt = dirt;
        this.coal = coal;
        this.oil = oil;
        this.wood = wood;
        this.stone = stone;
        this.iron = iron;
        this.sand = sand;
        this.clay = clay;
        this.gold = gold;
        this.workersRequired = workersRequired;
        this.civillianHome = civillianHome;
    }
    public void assignWorker(Civillian worker){
        workers.add(worker);
        worker.status = Civillian.Status.waiting;
        if(workers.size()>=workersRequired){
            for(Civillian WORKER : workers){
                WORKER.status = Civillian.Status.working;
            }
        }else if(workersRequired==-1&&workers.size()>=WorkerTaskList.getWorkerCount()){
            for(Civillian WORKER : workers){
                WORKER.status = Civillian.Status.working;
            }
        }
        if(workersRequired>0&&workers.size()>workersRequired){
            Civillian WORKER = workers.get(0);
            unassignWorker(WORKER);
            WORKER.clearTask();
            WORKER.status = Civillian.Status.idle;
        }
    }
    public void unassignWorker(Civillian worker){
        workers.remove(worker);
        workersSent--;
        if(workers.size()<workersRequired){
            for(Civillian WORKER : workers){
                WORKER.status = Civillian.Status.waiting;
            }
        }
    }
    public void work(){
        progress++;
        if(progress>=time*2*workersRequired){
            CivillianTaskList.notifyTaskComplete(this);
            Civillian[] workerss = workers.toArray(new Civillian[workers.size()]);
            for(Civillian civillian : workerss){
                civillian.notifyTaskComplete(this);
            }
        }
    }
    public String getDescription(){
        return desc;
    }
    public int getWorkerRequirement(){
        return workersRequired;
    }
    public int getCost(){
        return cost;
    }
    public int[] getLocation(){
        return plot.getCoords();
    }
    public void checkWorker(Civillian worker){
        if(workers.contains(worker)){
            throw new IllegalArgumentException("Worker already in!");
        }else if(main.cash<(cost)&&workersSent==0){
            throw new IllegalArgumentException("Not enough cash!");
        }else if(CivillianTaskList.getAvailableWorkers()<getWorkerRequirement()-workersSent){
            throw new IllegalArgumentException("Not enough available workers!");
        }else{
            workersSent++;
            sentWorkers.add(worker);
        }
    }
    public Plot getHome(){
        return civillianHome;
    }
    public int index(){
        return index;
    }
    public void setIndex(int i){
        index = i;
    }
    public void save(DataOutputStream out) throws IOException{
        int[] plotcoords = plot.getCoords();
        out.writeInt(plotcoords[0]);
        out.writeInt(plotcoords[1]);
        out.writeInt(time);
        out.writeInt(cost);
        out.writeInt(progress);
        out.writeUTF(desc);
        out.writeInt(workersRequired);
        out.writeInt(workersSent);
        out.writeBoolean(civillianHome!=null);
        if(civillianHome!=null){
            plotcoords = civillianHome.getCoords();
            out.writeInt(plotcoords[0]);
            out.writeInt(plotcoords[1]);
        }
        out.writeInt(dirt);
        out.writeInt(coal);
        out.writeInt(oil);
        out.writeInt(wood);
        out.writeInt(stone);
        out.writeInt(iron);
        out.writeInt(sand);
        out.writeInt(clay);
        out.writeInt(gold);
        out.writeInt(index);
    }
}
