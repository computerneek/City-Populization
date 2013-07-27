package CityPopulization;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;
import multilib.net.Packet1Integer;
import multilib.net.Packet2String;
import multilib.net.Packet3Boolean;
public class Civillian{
    //<editor-fold defaultstate="collapsed" desc="Variables">
    public Plot home;
    public int[] coords;
    public WorkerPath path;
    public Status status = Status.idle;
    public boolean gone;
    public int[] dest;
    private Random rand = new Random();
    public boolean discharged;
    public CivillianTask task;
    public int cashWanted;
    public double distanceTravelled = 0;
    public int workingTime;
    public int anger;
    public int shopping;
    public static final HashMap<String, Integer> times = new HashMap<>();
    public boolean isAtHome;
    private int tick;
    public boolean angry;
    public boolean dead;
    public boolean zombie;
    private Plot nextZombieDest;
    public int direction;
    //</editor-fold>
    public void setHome(Plot newHome){
        home = newHome;
    }
    public Plot getHome(){
        return home;
    }
    public void discharge(){
        if(task!=null){
            task.unassignWorker(this);
        }
        path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), Plot.getMainBase().getCoords());
        discharged = true;
        status = Status.traveling;
        home.removeCivillian(this);
        home.civillians.remove(this);
    }
    public void tick(){
        if(path==null&&!zombie){
            dest=null;
        }
        if(tick==main.tick){
            return;
        }
        tick = main.tick;
        if(anger>0){
            anger--;
        }
        if(gone){
            return;
        }
        workingTime++;
        if(isAtHome){
            return;
        }else if(isAtHome()&&task==null&&shopping==0&&anger<1&&status!=Status.zombie&&status!=Status.dead&&!discharged&&cashWanted==0&&path==null){
            home.registerCivillianContent(this);
            isAtHome = true;
        }else if(isAtHome()&&task==null&&shopping==0&&anger<1&&status!=Status.zombie&&status!=Status.dead&&discharged&&cashWanted==0&&path==null){
            path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), Plot.getMainBase().getCoords());
            if(path!=null){
                status = Status.traveling;
            }else{
                anger = 200;
            }
        }
        if(status!=Status.angry && angry){
            System.out.println("Re-angering");
            status = Status.angry;
        }
        if(status!=Status.dead&&dead){
            System.out.println("Re-killing");
            status = Status.dead;
        }
        if(status!=Status.zombie&&zombie){
            System.out.println("Re-zombifying");
            status = Status.zombie;
        }
        boolean wasAngry = anger>0;
        if(anger>0&&status!=Status.angry&&status!=Status.dead){
            status = Status.angry;
            shopping = 0;
        }else if(status == Status.angry&&!angry&&anger==0&&status!=Status.dead){
            status = Status.idle;
        }
        if(anger==0&&wasAngry){
            workingTime = 0;
        }
        if(angry){
            path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), home.getCoords());
            if(path!=null){
                status = Status.traveling;
                angry =false;
            }
            anger+=2;
        }
        if(angry&&anger>=times.get("angry")){
            if(task!=null){
                task.unassignWorker(this);
                task = null;
            }
            path = null;
            dead = true;
            angry = false;
            status = Status.dead;
            workingTime = 0;
        }
        if(status==Status.dead&&anger==0&&workingTime>=60){
            status = Status.zombie;
            zombie = true;
            dead = false;
            workingTime = 0;
        }
        if(anger>0){
            return;
        }
        if(shopping==0&&anger==0&&status==Status.idle&&workingTime>20&&!angry&&!zombie&&!dead){
            path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), getHome().getCoords());
            if(path!=null){
                status = Status.traveling;
                workingTime = 0;
            }else if(workingTime>=600){
                status = Status.angry;
                angry = true;
                workingTime = 0;
            }
        }else if(shopping!=0&&status==Status.idle&&workingTime<times.get("shoppingIdle")&&isAtDestinationForShoppingStatus()&&!angry&&!zombie&&!dead){
            status = Status.shopping;
            workingTime = 0;
        }else if(shopping!=0&&status==Status.shopping&&workingTime>=times.get("shopping")&&isAtDestinationForShoppingStatus()&&!angry&&!zombie&&!dead){
            status = Status.idle;
            incomeForShoppingStatus();
            shopping = 0;
            workingTime = 0;
        }else if(shopping!=0&&status==Status.idle&&workingTime<times.get("shoppingForcedIdle")&&!isAtDestinationForShoppingStatus()&&!angry&&!zombie&&!dead){
            Plot destination = getDestinationForShoppingStatus();
            if(destination!=null){
                path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), destination.getCoords());
                if(path!=null){
                    status = Status.traveling;
                    workingTime = 0;
                }
            }
        }else if(shopping!=0&&status==Status.idle&&workingTime>=times.get("shoppingForcedIdle")&&!angry&&!zombie&&!dead){
            shopping = 0;
            workingTime = 0;
            if(isAtHome()){
                home.registerCivillianContent(this);
                isAtHome = true;
            }
        }
        if(status==Status.working&&workingTime>=times.get("working")&&!angry&&!zombie&&!dead){
            task.unassignWorker(this);
            task = null;
            status = Status.idle;
            path = null;
            workingTime = 0;
        }
        if(status==Status.waiting&&workingTime>=times.get("waiting")&&!angry&&!zombie&&!dead){
            task.unassignWorker(this);
            task = null;
            status = Status.idle;
            path = null;
            workingTime = 0;
            anger = 200;
        }
        if(status==Status.traveling&&workingTime>=times.get("traveling")&&anger==0&&!angry&&!zombie&&!dead){
            anger = 50;
            if(task!=null){
                task.unassignWorker(this);
                task = null;
            }
            status = Status.idle;
            workingTime = 0;
        }
        if(status==Status.idle&&workingTime>=times.get("idle")&&shopping==0&&anger<1&&!angry&&!zombie&&!dead){
            if(rand.nextInt(1000)==50){
                status = Status.angry;
                angry = true;
                workingTime = 0;
            }else{
                workingTime-=rand.nextInt(20);
            }
        }
        if(zombie){
            Plot plot = getLocation();
            if(plot.getType().zombieLevel<=plot.getLevel()&&plot.getType().zombieLevel>0){
                if(workingTime>=times.get("destroying")){
                    workingTime = 0;
                    plot.setLevel(plot.getLevel()-1);
                    if(plot.getLevel()<=0){
                        while(plot.task!=null){
                            plot.cancelATask();
                        }
                        plot.setType(PlotType.zombieland, 1);
                        Civillian[] civillianss = plot.civillians.toArray(new Civillian[plot.civillians.size()]);
                        while(Plot.removeCivillian(plot)){}
                        plot.civillianContent.clear();
                        plot.workerContent.clear();
                        plot.zombiesLeft = (int)(((float)civillianss.length)*Math.abs(rand.nextGaussian()/10));
                    }
                }
            }else{
                workingTime = 0;
            }
        }else if(zombie){
            workingTime = 0;
        }
        if(status.equals(Status.traveling)||(status.equals(Status.zombie))){
            if(status==Status.zombie||zombie){
                if(path==null&&(nextZombieDest==null||(!nextZombieDest.isZombieDestination()&&!getLocation().isZombieDestination()))){
                    Plot lastZombieDest = nextZombieDest;
                    nextZombieDest = main.findNextZombieDestination();
                    if(nextZombieDest!=null&&nextZombieDest!=lastZombieDest){
                        path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), nextZombieDest.getCoords());
                        if(path==null){
                            dest = new int[]{nextZombieDest.getCoords()[0]*50+25, nextZombieDest.getCoords()[1]*50+25};
                        }
                    }
                }
            }
            if((zombie||path!=null)&&((zombie||!path.isComplete())||(dest!=null&&(coords[0]!=dest[0]||coords[1]!=dest[1])))&&!(path==null&&dest==null)){
                if((dest==null||(dest[0]==coords[0]&&dest[1]==coords[1]))&&path!=null&&!path.isComplete()){
                    dest = path.getNextPoint();
                }
                if(path!=null&&path.isComplete()&&dest==null){
                    path = null;
                }
                distanceTravelled+=(main.getPlot(main.getPlotCoordinates(coords)).getWorkerSpeed(this)/(zombie||status==Status.zombie?10:1));
                while(distanceTravelled>=1){
                    if(dest==null||(dest[0]==coords[0]&&dest[1]==coords[1])){
                        if(path==null){
                            distanceTravelled = 0;
                        }else{
                            dest = path.isComplete()?null:path.getNextPoint();
                        }
                    }
                    if(dest==null){
                        distanceTravelled = 0;
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
                return;
            }
            path = null;
            status = status==Status.traveling?Status.idle:Status.zombie;
            if(!zombie){
                workingTime = 0;
            }
            if(task!=null&&coords[0]==task.getLocation()[0]*50+25&&coords[1]==task.getLocation()[1]*50+25&&status!=Status.zombie&&!angry&&!zombie&&!dead){
                status = Status.waiting;
                task.assignWorker(this);
                return;
            }else if(task!=null&&status!=Status.zombie&&!angry&&!zombie&&!dead){
                path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), task.getLocation());
                status = Status.traveling;
                return;
            }
            if(coords[0]==(Plot.getMainBase().getCoords()[0]*50+25)&&coords[1]==(Plot.getMainBase().getCoords()[1]*50+25)&&status!=Status.zombie&&!angry&&!zombie&&!dead){
                if(cashWanted>0){
                    main.payWorker(cashWanted);
                    cashWanted = 0;
                    return;
                }
                if(discharged){
                    gone = true;
                    CivillianTaskList.removeWorker(this);
                    return;
                }
                path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), home.getCoords());
            }
        }
        if(shopping!=0||angry||dead||zombie){
            return;
        }
        if(status.equals(Status.idle)){
            if(cashWanted!=0){
                path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), Plot.getMainBase().getCoords());
                if(path!=null){
                    status = Status.traveling;
                    workingTime = 0;
                }
                return;
            }
            if(discharged){
                gone = true;
                CivillianTaskList.removeWorker(this);
            }else if(task!=null){
                path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), task.getLocation());
                if(path!=null){
                    status = Status.traveling;
                    workingTime = 0;
                }
            }else if(!isAtHome()){
                path = WorkerPath.findPathTo(main.getPlotCoordinates(coords), home.getCoords());
                if(path!=null){
                    status = Status.traveling;
                    workingTime = 0;
                }
            }
        }
        if(status==Status.working){
            if(task==null){
                status = Status.idle;
                workingTime = 0;
                return;
            }
            task.work();
        }
    }
    public void notifyTaskComplete(CivillianTask taskCompleted){
        if(taskCompleted!=task){
            throw new IllegalArgumentException("Wrong worker!");
        }
        if(task.getDescription().equals("trainWorker")){
            discharge();
            WorkerTaskList.addWorker(task.getHome(), main.getPlot(main.getPlotCoordinates(coords)));
        }else{
            cashWanted+=task.getCost()+5;
        }
        task = null;
        status = Status.idle;
    }
    public boolean isAtHome(){
        return coords[0]==home.getCoords()[0]*50+25&&coords[1]==home.getCoords()[1]*50+25;
    }
    public boolean isAtBase(){
        return coords[0]==Plot.getMainBase().getCoords()[0]*50+25&&coords[1]==Plot.getMainBase().getCoords()[1]*50+25;
    }
    public boolean isWorker(){
        return false;
    }
    private boolean isAtDestinationForShoppingStatus(){
        switch(shopping){
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
                return main.getPlot(main.getPlotCoordinates(coords)).isType(getDestinationTypeForShoppingStatus());
            default:
                return false;
        }
    }
    private PlotType getDestinationTypeForShoppingStatus(){
        switch(shopping){
            case 1:
            case 2:
            case 3:
            case 4:
                return PlotType.shoppingMall;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                return PlotType.restaurant;
            case 13:
                return PlotType.amusementPark;
            case 14:
            case 15:
                return PlotType.departmentStore;
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
                return PlotType.park;
            default:
                return null;
        }
    }
    private void incomeForShoppingStatus(){
        switch(shopping){
            case 1:
            case 2:
            case 3:
            case 4:
                main.income(rand.nextInt(main.shoppingMallIncome*getLocation().getLevel()));
                return;
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                main.income(rand.nextInt(main.resturantIncome*getLocation().getLevel()));
                return;
            case 13:
                main.income(rand.nextInt(main.amusementParkIncome*getLocation().getLevel()));
                return;
            case 14:
            case 15:
                main.income(rand.nextInt(main.departmentStoreIncome*getLocation().getLevel()));
                return;
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
                main.income(main.parkIncome*getLocation().getLevel());
                return;
        }
    }
    public Plot getLocation(){
        return main.getPlot(main.getPlotCoordinates(coords));
    }
    private Plot getDestinationForShoppingStatus(){
        switch(shopping){
            case 1:
            case 2:
            case 3:
            case 4:
                return main.getNextShoppingMall();
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                return main.getNextResturant();
            case 13:
                return main.getNextAmusementPark();
            case 14:
            case 15:
                return main.getNextDepartmentStore();
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
                return main.getNextPark();
            default:
                return null;
        }
    }
    public void clearTask(){
        task.unassignWorker(this);
        task = null;
    }
    public void save(DataOutputStream out) throws IOException{
        out.writeInt(home.getCoords()[0]);
        out.writeInt(home.getCoords()[1]);
        out.writeInt(coords[0]);
        out.writeInt(coords[1]);
        out.writeBoolean(path!=null);
        if(path!=null){
            path.save(out);
        }
        out.writeUTF(status.name());
        out.writeBoolean(gone);
        out.writeBoolean(dest!=null);
        if(dest!=null){
            out.writeInt(dest[0]);
            out.writeInt(dest[1]);
        }
        out.writeBoolean(discharged);
        out.writeBoolean(task!=null);
        if(task!=null){
            out.writeInt(task.index());
        }
        out.writeInt(cashWanted);
        out.writeInt(workingTime);
        out.writeInt(anger);
        out.writeInt(shopping);
        out.writeBoolean(isAtHome);
        out.writeInt(tick);
        out.writeBoolean(angry);
        out.writeBoolean(dead);
        out.writeBoolean(zombie);
        out.writeInt(direction);
    }
    static Civillian load(DataInputStream in) throws IOException{
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
        Civillian civillian = new Civillian();
        civillian.home = home;
        civillian.coords = coords;
        civillian.path = path;
        civillian.status = status;
        civillian.gone = gone;
        civillian.dest = dest;
        civillian.discharged = discharged;
        civillian.task = task;
        if(task!=null){
            task.sentWorkers.add(civillian);
            task.workersSent++;
        }
        home.civillians.add(civillian);
        civillian.cashWanted = cashWanted;
        civillian.workingTime = workingTime;
        civillian.anger = anger;
        civillian.shopping = shopping;
        civillian.isAtHome = isAtHome;
        civillian.tick = tick;
        civillian.angry = angry;
        civillian.dead = dead;
        civillian.zombie = zombie;
        civillian.direction = direction;
        return civillian;
    }
    public enum Status{idle, working, waiting, traveling, angry, shopping, zombie, dead};
    public Civillian(){
        coords = new int[]{Plot.getMainBase().getCoords()[0]*50+25, Plot.getMainBase().getCoords()[1]*50+25};
    }
    public Civillian warpHome(){
        coords[0] = getHome().getCoords()[0]*50+25;
        coords[1] = getHome().getCoords()[1]*50+25;
        return this;
    }
    static{
        times.put("shoppingIdle", new Integer(200));
        times.put("shoppingForcedIdle", new Integer(5));
        times.put("shopping", new Integer(50));
        times.put("working", new Integer(100));
        times.put("waiting", new Integer(50));
        times.put("traveling", new Integer(500));
        times.put("idle", new Integer(240));
        times.put("angry", new Integer(240));
        times.put("destroying", new Integer(1000));
    }
}
