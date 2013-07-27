package CityPopulization;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import multilib.error.Sys;
import multilib.net.Packet1Integer;
import multilib.net.Packet2String;
import multilib.net.Packet3Boolean;
public class Worker extends Civillian{
    public static Random rand = new Random();
    public static final String waitor = Sys.generateRandomString(100);
    private boolean deleted;
    public int resources;
    public Material resource;
    public Worker(Plot barracks){
        super();
        coords = new int[]{barracks.getCoords()[0]*50+25, barracks.getCoords()[1]*50+25};
    }
    public void setHome(Plot workerHome){
        home = workerHome;
    }
    public void discharge(){
        if(task!=null){
            task.unassignWorker(this);
            task = null;
        }
        path = WorkerPath.findPathTo(main.getPlotCoordinates(coords[0], coords[1]), Plot.getMainBase().getCoords());
        discharged = true;
        status = Status.traveling;
        home.onWorkerExit(this);
    }
    public void notifyTaskComplete(CivillianTask taskCompleted){
        if(taskCompleted!=task){
            throw new IllegalArgumentException("Wrong worker!");
        }
        cashWanted+=5;
        status = Status.idle;
        task = null;
    }
    public void tick(){
        if(gone){
            WorkerTaskList.workers.remove(this);
            WorkerTaskList.totalWorkers.remove(this);
        }else if(isAtHome){
            return;
        }else if(isAtHome()&&task==null&&shopping==0&&anger<1&&!discharged&&cashWanted==0&&status!=Status.zombie){
            home.registerWorkerContent(this);
            isAtHome = true;
        }
        if(status.equals(Status.traveling)){
            if(path!=null&&(!path.isComplete()||(dest!=null&&(coords[0]!=dest[0]||coords[1]!=dest[1])))){
                distanceTravelled+=main.getPlot(main.getPlotCoordinates(coords)).getWorkerSpeed(this);
                boolean run = true;
                while(path!=null&&(!path.isComplete()||(dest!=null&&(coords[0]!=dest[0]||coords[1]!=dest[1])))&&run){
                    run = false;
                    while(distanceTravelled>=1){
                        if(dest==null||(dest[0]==coords[0]&&dest[1]==coords[1])){
                            dest = path.isComplete()?null:path.getNextPoint();
                            run = true;
                            if(dest==null){
                                distanceTravelled = 0;
                            }
                        }
                        if(dest==null){
                            return;
                        }
                        int dist = Math.max(Math.max(dest[0]-coords[0], coords[0]-dest[0]), Math.max(dest[1]-coords[1], coords[1]-dest[1]));
                        if(dest[0]>coords[0]){
                            direction = 2;
                            coords[0]+=(Math.min(dest[0]-coords[0], (int)distanceTravelled));
                        }else if(dest[0]<coords[0]){
                            direction = 4;
                            coords[0]-=(Math.min(coords[0]-dest[0], (int)distanceTravelled));
                        }else if(dest[1]>coords[1]){
                            direction = 1;
                            coords[1]+=(Math.min(dest[1]-coords[1], (int)distanceTravelled));
                        }else if(dest[1]<coords[1]){
                            direction = 3;
                            coords[1]-=(Math.min(coords[1]-dest[1], (int)distanceTravelled));
                        }
                        distanceTravelled-=Math.max(0, Math.min(dist, (int)distanceTravelled));
                    }
                }
                return;
            }
            path = null;
            status = Status.idle;
            if(task==null&&resources==0){
                resource = null;
            }
            if(discharged&&isAtBase()){
                main.payWorker(cashWanted);
                gone = true;
                WorkerTaskList.removeWorker(this);
                return;
            }
            if(task!=null&&coords[0]==task.getLocation()[0]*50+25&&coords[1]==task.getLocation()[1]*50+25){
                status = Status.waiting;
                task.assignWorker(this);
                return;
            }else if(task!=null&&!(task instanceof WorkerTaskHarvest)){
                path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), task.getLocation());
                status = Status.traveling;
                return;
            }
            if(coords[0]==(Plot.getMainBase().getCoords()[0]*50+25)&&coords[1]==(Plot.getMainBase().getCoords()[1]*50+25)){
                if(cashWanted!=0){
                    main.payWorker(cashWanted);
                    cashWanted = 0;
                    return;
                }else if(resources>0){
                    main.getResources(resource, resources);
                    resources = -resources*40;
                    return;
                }
                if(task==null){
                    resource = null;
                    path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), home.getCoords());
                }else if(task instanceof WorkerTaskHarvest){
                    resource = main.getPlot(task.getLocation()).getType().getResource();
                    path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), task.getLocation());
                }
                status = Status.traveling;
            }
            if(coords[0]%50==25&&coords[1]%50==25&&getLocation().getType()==PlotType.warehouse){
                if(resources>0){
                    main.getResources(resource, 1);
                    resources--;
                    return;
                }
                if(task==null){
                    path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), home.getCoords());
                }else{
                    path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), task.getLocation());
                }
                status = Status.traveling;
            }
        }
        if(status.equals(Status.idle)){
            if(resources<0){
                resources++;
                return;
            }else if(resources>0){
                main.getResources(resource, 1);
                resources--;
                return;
            }
            if(cashWanted!=0){
                path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), Plot.getMainBase().getCoords());
                status = Status.traveling;
                return;
            }
            if(resource!=null&&task==null){
                path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), Plot.getMainBase().getCoords());
            }
            if(task!=null){
                if(task instanceof WorkerTaskHarvest){
                    if(resource==null){
                        path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), Plot.getMainBase().getCoords());
                    }else{
                        path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), task.getLocation());
                    }
                }else{
                    path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), task.getLocation());
                }
                status = Status.traveling;
                return;
            }
            if(coords[0]==home.getCoords()[0]*50+25&&coords[1]==home.getCoords()[1]*50+25){
            }else{
                path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), home.getCoords());
                status = Status.traveling;
                return;
            }
        }
        if(status==Status.working){
            if(task==null){
                status = Status.idle;
                return;
            }
            task.work();
            if(resources>0){
                Plot[] warehouses = main.getPlotsOfType(PlotType.warehouse);
                if(warehouses.length>0){
                    path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), warehouses[rand.nextInt(warehouses.length)].getCoords());
                }else{
                    path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), Plot.getMainBase().getCoords());
                }
                if(path!=null){
                    status = Status.traveling;
                }
            }
        }
    }
    public void clearTask(){
        if(task!=null){
            task.unassignWorker(this);
        }
        task = null;
    }
    public static int[] adjustToWorkerCoords(int[] loc){
        return new int[]{loc[0]*50+25, loc[1]*50+25};
    }
    public boolean isWorker(){
        return true;
    }
    public void save(DataOutputStream out) throws IOException{
        super.save(out);
        out.writeBoolean(deleted);
        out.writeInt(resources);
        out.writeBoolean(resource!=null);
        if(resource!=null){
            out.writeUTF(resource.name());
        }
    }
    public static Worker load(DataInputStream in) throws IOException{
        Plot home = main.getPlot(new int[]{in.readInt(), in.readInt()});
        int[] coords = new int[]{in.readInt(), in.readInt()};
        WorkerPath path = null;
        if(in.readBoolean()){
            path = WorkerPath.load(in);
        }
        Status status = Status.valueOf(in.readUTF());
        boolean gone = in.readBoolean();
        int[] dest = null;
        if(in.readBoolean()){
            dest = new int[]{in.readInt(), in.readInt()};
        }
        boolean discharged = in.readBoolean();
        WorkerTask task = null;
        if(in.readBoolean()){
            task = WorkerTaskList.getTaskByIndex(in.readInt());
        }
        int cashWanted = in.readInt();
        int workingTime = in.readInt();
        int anger = in.readInt();
        int shopping = in.readInt();
        boolean isAtHome = in.readBoolean();
        int tick = in.readInt();
        boolean angry = in.readBoolean();
        boolean dead = in.readBoolean();
        boolean zombie = in.readBoolean();
        int direction = in.readInt();
        boolean deleted = in.readBoolean();
        int resources = in.readInt();
        Material resource = null;
        if(in.readBoolean()){
            resource = Material.valueOf(in.readUTF());
        }
        Worker worker = new Worker(home);
        worker.home = home;
        worker.coords = coords;
        worker.path = path;
        worker.status = status;
        worker.gone = gone;
        worker.dest = dest;
        worker.discharged = discharged;
        worker.task = task;
        if(task!=null){
            task.sentWorkers.add(worker);
            task.workersSent++;
            if(status==Status.working){
                task.workers.add(worker);
            }
        }
        home.civillians.add(worker);
        worker.cashWanted = cashWanted;
        worker.workingTime = workingTime;
        worker.anger = anger;
        worker.shopping = shopping;
        worker.isAtHome = isAtHome;
        worker.angry = angry;
        worker.dead = dead;
        worker.zombie = zombie;
        worker.direction = direction;
        worker.deleted = deleted;
        worker.resources = resources;
        worker.resource = resource;
        return worker;
    }
}
