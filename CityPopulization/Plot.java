package CityPopulization;
import CityPopulization.Civillian.Status;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import multilib.error.ErrorCategory;
import multilib.error.ErrorLevel;
import multilib.error.Sys;
import multilib.net.Packet1Integer;
import multilib.net.Packet2String;
import multilib.net.Packet3Boolean;
public class Plot{
    public static int seed;
    public static Random seedrand = new Random(seed);
    private PlotType type = PlotType.empty;
    private int level = 1;
    public boolean isUpgrading;
    private final int y;
    private final int x;
    public WorkerTask task;
    private int workingTime;
    public ArrayList<Civillian> civillians = new ArrayList<>();
    public static int totalCivillians;
    public static int totalWorkers;
    public ArrayList<Civillian> civillianContent = new ArrayList<>();
    public ArrayList<Worker> workerContent = new ArrayList<>();
    public int workersSent;
    private Random rand = new Random(seedrand.nextInt());
    private int repairTimer;
    public int broken;
    private int repairNeed = getNextRepairNeed();
    public int zombiesLeft = 0;
    public int[] getCoords(){
        return new int[]{x, y};
    }
    public PlotType getType(){
        return type;
    }
    public int getLevel(){
        return level;
    }
    public void setLevel(int level){
        setType(type, level);
    }
    public void tick(){
        if(!canNeedRepair()){
            repairTimer = 0;
            broken = 0;
        }else{
            repairTimer++;
            if(repairTimer>=repairNeed&&type!=PlotType.zombieland){
                repairTimer-=repairNeed;
                broken++;
                repairNeed = getNextRepairNeed();
                if(task!=null&&getRepairStation()!=null){
                    task.progress-=task.workersRequired*100*(PlotType.workshop.levels+1);
                    broken--;
                }else if(getRepairStation()!=null&&WorkerTaskList.hasWorkers(1)){
                    task = WorkerTaskList.addImportantTask(new WorkerTask("repair", this, 5, getLevel()*10, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1));
                    isUpgrading = true;
                }else{
                    task.forceCancel();
                    task = null;
                    isUpgrading = false;
                }
            }else if(repairTimer>=repairNeed){
                spawnZombie();
                repairTimer-=repairNeed;
                repairNeed = getNextRepairNeed();
            }
        }
        if(isType(PlotType.zombieland)&&zombiesLeft>0&&main.tick%20==0){
            spawnZombie();
            zombiesLeft--;
            return;
        }
        if(zombiesLeft>0){
            zombiesLeft = 0;
        }
        if(!workerContent.isEmpty()||!civillianContent.isEmpty()){
            workerTick();
            civillianTick();
        }
    }
    public void civillianTick(){
        if(civillianContent.isEmpty()){
            return;
        }
        workingTime+=1;
        while(workingTime>=Civillian.times.get("idle")){
            if(rand.nextInt(1000)==0){
                civillianContent.get(0).shopping = rand.nextInt(20)+1;
                onCivillianExit(civillianContent.get(0));
                workingTime-=Civillian.times.get("idle");
                break;
            }else{
                workingTime-=rand.nextInt(100);
            }
        }
    }
    public void workerTick(){
        if(workerContent.size()<1){
            return;
        }
        for(int i = 0; i<workerContent.size(); i++){
            if(workerContent.get(i).task!=null){
                workerContent.get(i).path = WorkerPath.findPathTo(main.getPlotCoordinates(workerContent.get(i).coords), workerContent.get(i).task.getLocation());
                workerContent.get(i).status = Civillian.Status.traveling;
                onWorkerExit(workerContent.get(i));
                i--;
            }
        }
    }
    public void removeCivillian(Civillian worker){
        civillians.remove(worker);
        onCivillianExit(worker);
    }
    public void onCivillianExit(Civillian civillian){
        civillian.isAtHome = false;
        civillian.workingTime = 0;
        civillianContent.remove(civillian);
        if(!CivillianTaskList.workers.contains(civillian)){
            CivillianTaskList.workers.add(civillian);
        }
    }
    public void onWorkerExit(Worker worker){
        worker.isAtHome = false;
        workerContent.remove(worker);
        if(!WorkerTaskList.workers.contains(worker)){
            WorkerTaskList.workers.add(worker);
        }
        refreshRegisteredWorkers();
    }
    public Plot getPlot(int direction){
        if(!isPlot(direction)){
            return null;
        }
        switch(direction){
            case 1://up
                return main.world[x][y-1];
            case 2://right
                return main.world[x+1][y];
            case 3://down
                return main.world[x][y+1];
            case 4://left
                return main.world[x-1][y];
            default:
                return null;
        }
    }
    public boolean isPlot(int direction){
        if(direction==1&&y>0){
            return true;
        }else if(direction==2&&x<main.world.length-1){
            return true;
        }else if(direction==3&&y<main.world[x].length-1){
            return true;
        }else if(direction==4&&x>0){
            return true;
        }else{
            return false;
        }
    }
    public boolean isSamePlot(int[] dest){
        return dest[0]==x&&dest[1]==y;
    }
    public boolean isType(PlotType type){
        return type==this.type;
    }
    public Civillian getCivillian(){
        Civillian[] civillianss = civillians.toArray(new Civillian[civillians.size()]);
        for(Civillian civil : civillianss){
            if(!civil.isWorker()){
                return civil;
            }
        }
        return null;
    }
    public Worker getWorker(){
        if(workerContent.size()>0){
            return workerContent.get(0);
        }
        return null;
    }
    private boolean canUpgrade(){
        return getMaximumLevel()>getLevel();
    }
    public void registerCivillianContent(Civillian civillian){
        civillianContent.add(civillian);
        while(CivillianTaskList.workers.contains(civillian)){
            CivillianTaskList.workers.remove(civillian);
        }
        refreshRegisteredCivillians();
    }
    public void registerWorkerContent(Worker worker){
        workerContent.add(worker);
        while(WorkerTaskList.workers.contains(worker)){
            WorkerTaskList.workers.remove(worker);
        }
        refreshRegisteredWorkers();
    }
    public static void refreshRegisteredCivillians(){
        totalCivillians = 0;
        for(Plot[] plots : main.world){
            for(Plot plot : plots){
                totalCivillians += plot.getCivillianCount();
            }
        }
    }
    public static void refreshRegisteredWorkers(){
        totalWorkers = 0;
        for(Plot[] plots : main.world){
            for(Plot plot : plots){
                totalWorkers += plot.getWorkerCount();
            }
        }
    }
    public int getCivillianCount(){
        return civillianContent.size();
    }
    public int getWorkerCount(){
        return workerContent.size();
    }
    public Plot(int x, int y){
        this.x = x;
        this.y = y;
    }
    public void setType(PlotType type, int level){
        if(type!=null){
            PlotType oldType = this.type;
            int oldLevel = this.level;
            this.type = type;
            this.level = level;
            this.repairTimer = 0;
            this.broken = 0;
            this.repairNeed = getNextRepairNeed();
        }
    }
    public boolean isImportant(){
        return type==PlotType.workshop||type==PlotType.house||type==PlotType.mainBase||type==PlotType.zombieland;
    }
    public void notifyTaskComplete(WorkerTask task){
        boolean wasUpgrading = isUpgrading;
        if(task==this.task){
            this.task = task.nextTask;
            if(this.task==null){
                isUpgrading = false;
            }else if(this.isImportant()||this.task.getDescription().equals("repair")){
                WorkerTaskList.addImportantTask(this.task);
            }else{
                WorkerTaskList.addTask(this.task);
            }
        }
        if(task.getDescription().equals(PlotType.house.constructionTag)){
            setType(PlotType.house, 1);
            for(int i = 0; i<main.handler.civilliansPerLevel; i++){
                CivillianScheduler.addCivillian(this);
            }
        }else if(task.getDescription().equals("Destroy")){
            setType(PlotType.empty, 1);
            Civillian[] civillianss = civillians.toArray(new Civillian[civillians.size()]);
            while(removeCivillian(this));
            civillianContent = new ArrayList<>();
            workerContent = new ArrayList<>();
        }else if(task.getDescription().equals("Zombify")){
            setType(PlotType.zombieland, 1);
            Civillian[] civillianss = civillians.toArray(new Civillian[civillians.size()]);
            while(removeCivillian(this)){}
            civillianContent = new ArrayList<>();
            workerContent = new ArrayList<>();
            zombiesLeft = (int)(((float)civillianss.length)*Math.abs(rand.nextGaussian()/10));
        }else if(task.getDescription().equals("Upgrade")){
            if(!canUpgrade()){
                return;
            }
            setLevel(level+1);
            if(type==PlotType.house){
                for(int i = 0; i<(((level*level)*main.handler.civilliansPerLevel)-(((level-1)*(level-1))*main.handler.civilliansPerLevel)); i++){
                    CivillianScheduler.addCivillian(this);
                }
            }
        }else if(task.getDescription().equals("Downgrade")){
            if(type==PlotType.house){
                for(int i = 0; i<(((level*level)*main.handler.civilliansPerLevel)-(((level-1)*(level-1))*main.handler.civilliansPerLevel))&&removeCivillian(this); i++);
            }
            setLevel(level-1);
            if(level<=0){
                setType(PlotType.empty, 1);
            }
        }else if(task.getDescription().equals("Harvest")||task.getDescription().equals("HarvestAuto")){
            switch(type){
                case dirtMine:
                    main.dirt+=level*10*task.getWorkerRequirement();
                    break;
                case quarry:
                    main.stone+=level*10*task.getWorkerRequirement();
                    break;
                case ironMine:
                    main.iron+=level*10*task.getWorkerRequirement();
                    break;
                case forest:
                    main.wood+=level*10*task.getWorkerRequirement();
                    break;
                case oilWell:
                    main.oil+=level*10*task.getWorkerRequirement();
                    break;
                case coalMine:
                    main.coal+=level*10*task.getWorkerRequirement();
                    break;
                case sandPit:
                    main.sand+=level*10*task.getWorkerRequirement();
                    break;
                case clayPit:
                    main.clay+=level*10*task.getWorkerRequirement();
                    break;
                case goldMine:
                    main.gold+=level*task.getWorkerRequirement();
                    break;
            }
        }else if(task.getDescription().equals("repair")){
            broken--;
        }else{
            for(PlotType TYPE : PlotType.values()){
                if(task.getDescription().equals(TYPE.constructionTag)){
                    setType(TYPE, 1);
                    return;
                }
            }
            throw new IllegalArgumentException("Uknown worker task- \""+task.getDescription()+"\"");
        }
    }
    public static Plot getMainBase(){
        return main.getNextPlot(PlotType.mainBase);
    }
    private Object getRepairStation(){
        return main.getNextPlot(PlotType.repairStation);
    }
    public double getWorkerSpeed(Civillian civillian){
        double value;
        PlotType thisType = this.type.isTemporary?this.type.permanent:this.type;
        switch(thisType){
            case mainBase:
            case house:
                value = 2*level;
                break;
            case empty:
            case highway:
                value = level;
                break;
            case workshop:
            case dirtMine:
            case coalMine:
            case oilWell:
            case forest:
            case quarry:
            case ironMine:
            case sandPit:
            case clayPit:
            case goldMine:
                value = ((double)level)/2.0;
                break;
            case shoppingMall:
            case amusementPark:
            case departmentStore:
            case park:
            case restaurant:
            case warehouse:
            case school:
                value = level*5;
                break;
            case zombieland:
                if(civillian==null){
                    value = 0.1;
                }else if(civillian.status==Civillian.Status.zombie){
                    return 100;
                }else{
                    value = 0.1;
                }
                break;
            default:
                Sys.error(ErrorLevel.warning, "Unknown Worker Speed for Plot Type- "+thisType, null, ErrorCategory.arguments);
                value = 1;
        }
        for(int i = 0; i<broken; i++){
            value/=2;
        }
        if(civillian!=null&&civillian.status==Civillian.Status.zombie){
            return 0.5;
        }
        if(isUpgrading&&(civillian==null?task!=null:civillian.task!=task)){
            return value/10;
        }else{
            return value;
        }
    }
    public boolean isTravelable(){
        switch(type){
            case highway:
                return true;
            default:
                return false;
        }
    }
    public void use(){
        if(type.isTemporary){
            level--;
            if(level<1){
                setType(PlotType.empty, 1);
            }
        }
    }
    public boolean canEnterFromDirection(int direction){
        boolean canPathAnywhere = type==PlotType.empty||type==PlotType.highway||type==PlotType.dirtMine||type==PlotType.coalMine||type==PlotType.oilWell||type==PlotType.forest||type==PlotType.quarry||type==PlotType.ironMine||type==PlotType.sandPit||type==PlotType.clayPit||type==PlotType.goldMine||type==PlotType.workshop||type==PlotType.zombieland;
        if(canPathAnywhere){
            return true;
        }
        return direction==1||(y==main.world[0].length-1&&direction==3);
    }
    public boolean canEnterFromPlot(Plot nextPlot){
        for(int i = 1; i<5; i++){
            if(isPlot(i)&&nextPlot==getPlot(i)){
                return canEnterFromDirection(invertDirection(i))&&nextPlot.canEnterFromDirection(i);
            }
        }
        return false;
    }
    public static int invertDirection(int i){
        i+=2;
        if(i>4){
            i-=4;
        }
        return i;
    }
    public boolean isSameType(Plot plot){
        return plot.isType(type);
    }
    public boolean isSameLevel(Plot plot){
        return plot.getLevel()==getLevel();
    }
    public int getMaximumLevel(){
        return type.isTemporary?type.permanent.levels*2:type.levels;
    }
    private int getNextRepairNeed(){
        return isType(PlotType.zombieland)?(rand.nextInt(360)+180)*20:((rand.nextInt(3600)+1800)*20);
    }
    public static boolean removeCivillian(Plot plot){
        if(CivillianScheduler.cancelCivillian(plot)){
            return true;
        }
        for(int i = 0; i<plot.civillians.size(); i++){
            if(!plot.civillians.get(i).isWorker()){
                plot.civillians.remove(i).discharge();
                return true;
            }
        }
        for(int i = 0; i<CivillianTaskList.workers.size(); i++){
            if(CivillianTaskList.workers.get(i).home==plot&&!CivillianTaskList.workers.get(i).discharged){
                CivillianTaskList.workers.get(i).discharge();
                return true;
            }
        }
        if(CivillianScheduler.cancelWorker(plot)){
            return true;
        }
        for(int i = 0; i<plot.civillians.size(); i++){
            if(plot.civillians.get(i).isWorker()){
                plot.civillians.remove(i).discharge();
                return true;
            }
        }
        for(int i = 0; i<WorkerTaskList.workers.size(); i++){
            if(WorkerTaskList.workers.get(i).home==plot&&!WorkerTaskList.workers.get(i).discharged){
                WorkerTaskList.workers.get(i).discharge();
                return true;
            }
        }
        return false;
    }
    public static boolean removeWorker(Plot plot){
        for(int i = 0; i<plot.civillians.size(); i++){
            if(plot.civillians.get(i).isWorker()){
                plot.civillians.get(i).discharge();
                return true;
            }
        }
        for(int i = 0; i<WorkerTaskList.workers.size(); i++){
            if(WorkerTaskList.workers.get(i).home==plot){
                WorkerTaskList.workers.get(i).discharge();
                return true;
            }
        }
        return false;
    }
    public boolean isZombieDestination(){
        return level>=type.zombieLevel;
    }
    public boolean canNeedRepair(){
        if(type==PlotType.empty){
            return false;
        }else if(type.isFarm()&&level==1){
            return false;
        }else{
            return true;
        }
    }
    public void fillHouse(){
        if(type==PlotType.house){
            int civillians = level*level*main.handler.civilliansPerLevel;
            for(int i = 0; i<civillians; i++){
                CivillianScheduler.addCivillian(this);
            }
        }
    }
    public void save(DataOutputStream out) throws IOException{
        out.writeUTF(type.name());
        out.writeInt(level);
        out.writeBoolean(isUpgrading);
        out.writeInt(workingTime);
        out.writeInt(civillianContent.size());
        for(int i = 0; i<civillianContent.size(); i++){
            civillianContent.get(i).save(out);
        }
        out.writeInt(workerContent.size());
        for(int i = 0; i<workerContent.size(); i++){
            workerContent.get(i).save(out);
        }
        out.writeInt(workersSent);
        out.writeInt(repairTimer);
        out.writeInt(broken);
        out.writeInt(repairNeed);
    }
    public void load(DataInputStream in) throws IOException{
        type = PlotType.valueOf(in.readUTF());
        level = in.readInt();
        isUpgrading = in.readBoolean();
        workingTime = in.readInt();
        int totalCivillians = in.readInt();
        civillianContent.clear();
        for(int i = 0; i<totalCivillians; i++){
            civillianContent.add(Civillian.load(in));
        }
        int totalWorkers = in.readInt();
        workerContent.clear();
        for(int i = 0; i<totalWorkers; i++){
            workerContent.add(Worker.load(in));
        }
        workersSent = in.readInt();
        repairTimer = in.readInt();
        broken = in.readInt();
        repairNeed = in.readInt();
    }
    private void spawnZombie(){
        Civillian civillian = CivillianTaskList.addCivillian(this);
        civillian.coords = new int[]{x*50+25, y*50+25};
        civillian.zombie = true;
        civillian.status = Status.zombie;
    }
    public void cancelATask(){
        if(task==null){
            isUpgrading = false;
            return;
        }
        WorkerTask atask = task;
        while(atask.nextTask!=null&&atask.nextTask.nextTask!=null){
            atask = atask.nextTask;
        }
        if(task.nextTask!=null){
            atask.nextTask.forceCancel();
            atask.nextTask = null;
        }else{
            task.forceCancel();
            task = null;
            isUpgrading = false;
        }
    }
    public void addTask(WorkerTask task){
        if(this.task==null){
            this.task = task;
            if(isImportant()||task.getDescription().equals("repair")){
                WorkerTaskList.addImportantTask(task);
            }else{
                WorkerTaskList.addTask(task);
            }
            isUpgrading = true;
        }else{
            WorkerTask aTask = this.task;
            while(aTask.nextTask!=null){
                aTask = aTask.nextTask;
            }
            aTask.nextTask = task;
        }
    }
}
