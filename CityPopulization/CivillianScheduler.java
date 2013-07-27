package CityPopulization;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import multilib.net.ConnectionManager;
import multilib.net.Packet;
import multilib.net.Packet1Integer;
public class CivillianScheduler{
    public static ArrayList<Plot> plotsInNeedOfCivillians = new ArrayList<>();
    public static ArrayList<Plot> plotsInNeedOfWorkers = new ArrayList<>();
    public static int workerTimeout;
    public static int civillianTimeout;
    private static int workersToWarp;
    private static int civilliansToWarp;
    public static void addCivillian(Plot plot){
        plotsInNeedOfCivillians.add(plot);
    }
    public static void tick(){
        if(plotsInNeedOfWorkers.isEmpty()&&plotsInNeedOfCivillians.isEmpty()){
            workerTimeout = -20;
            civillianTimeout = -200;
            return;
        }
        int civilliansComing = plotsInNeedOfCivillians.size();
        tickCivillian();
        int covered = 100;
        int toCover = 200;
        while(covered<civilliansComing&&toCover<(15-((int)(main.lagTicks))-5)*100){
            tickCivillian();
            covered+=toCover;
            toCover+=100;
        }
        int workersComing = plotsInNeedOfWorkers.size();
        tickWorker();
        covered = 100;
        toCover = 200;
        while(covered<workersComing&&toCover<(15-((int)(main.lagTicks))-5)*100){
            tickWorker();
            covered+=toCover;
            toCover+=100;
        }
    }
    public static void tickCivillian(){
        if(plotsInNeedOfCivillians.size()>0){
            civillianTimeout++;
        }else{
            civillianTimeout = -200;
        }
        if(plotsInNeedOfCivillians.size()>0&&(civillianTimeout>=(Plot.getMainBase().getMaximumLevel()-Plot.getMainBase().getLevel())*5+1||civilliansToWarp>0)){
            if(plotsInNeedOfCivillians.get(0).getType()==PlotType.house){
                plotsInNeedOfCivillians.get(0).civillians.add(CivillianTaskList.addCivillian(plotsInNeedOfCivillians.get(0)));
                civillianTimeout=0;
                if(civilliansToWarp>0){
                    civilliansToWarp--;
                }
            }
            plotsInNeedOfCivillians.remove(0);
        }
    }
    public static void tickWorker(){
        if(plotsInNeedOfWorkers.size()>0&&plotsInNeedOfWorkers.get(0).getCivillian()!=null&&(main.getPlotsOfType(PlotType.workshop).length>0)){
            workerTimeout++;
        }else if(plotsInNeedOfWorkers.size()>0&&(main.getPlotsOfType(PlotType.workshop).length>0)){
            plotsInNeedOfWorkers.add(plotsInNeedOfWorkers.remove(0));
        }else{
            workerTimeout = -20;
        }
        boolean detract = false;
        ArrayList<Plot> lst = new ArrayList<>();
        lst.addAll(Arrays.asList(main.getPlotsOfType(PlotType.workshop)));
        Plot barracks = lst.size()>0?lst.get(0):null;
        if(barracks==null&&workersToWarp>0){
            barracks = Plot.getMainBase();
        }
        if(barracks!=null&&plotsInNeedOfWorkers.size()>0&&(workerTimeout>=(barracks.getMaximumLevel()-barracks.getLevel())*200+1||workersToWarp>0)){
            Civillian civillian = plotsInNeedOfWorkers.get(0).getCivillian();
            if(civillian!=null&&plotsInNeedOfWorkers.get(0).getType()==PlotType.house){
                Worker worker = WorkerTaskList.addWorker(plotsInNeedOfWorkers.get(0), barracks);
                plotsInNeedOfWorkers.get(0).civillians.add(worker);
                if(workersToWarp>0){
                    workersToWarp--;
                    worker.warpHome();
                }
                if(civillian.isAtHome){
                    civillian.isAtHome = false;
                    civillian.workingTime = 0;
                    civillian.path = WorkerPath.findPathTo(main.getPlotCoordinates(civillian.coords), Plot.getMainBase().getCoords());
                    civillian.status = Civillian.Status.traveling;
                    plotsInNeedOfWorkers.get(0).removeCivillian(civillian);
                }
                civillian.discharge();
                workerTimeout=0;
                plotsInNeedOfWorkers.remove(0);
                barracks.use();
            }else if(civillian==null&&plotsInNeedOfWorkers.get(0).getType()==PlotType.house){
                plotsInNeedOfWorkers.add(plotsInNeedOfWorkers.remove(0));
                detract = true;
            }else{
                plotsInNeedOfWorkers.remove(0);
            }
        }
        if(detract){
            workerTimeout--;
        }
    }
    public static void addWorker(Plot plot){
        plotsInNeedOfWorkers.add(plot);
    }
    public static boolean cancelWorker(Plot plot){
        return plotsInNeedOfWorkers.remove(plot);
    }
    static boolean cancelCivillian(Plot plot){
        return plotsInNeedOfCivillians.remove(plot);
    }
    public static void warpWorker(){
        workersToWarp++;
        civilliansToWarp++;
        Plot[] plots = main.getPlotsOfType(PlotType.house);
        for(int i = 0; i<plots.length; i++){
            if(plots[i].workersSent<(plots[i].getLevel()*plots[i].getLevel()*main.handler.civilliansPerLevel)){
                plots[i].workersSent++;
                plotsInNeedOfWorkers.add(plots[i]);
                return;
            }
        }
        plots[0].workersSent++;
        plotsInNeedOfWorkers.add(plots[0]);
    }
    public static void save(DataOutputStream out) throws IOException{
        out.writeInt(plotsInNeedOfCivillians.size());
        for(int i = 0; i<plotsInNeedOfCivillians.size(); i++){
            out.writeInt(plotsInNeedOfCivillians.get(i).getCoords()[0]);
            out.writeInt(plotsInNeedOfCivillians.get(i).getCoords()[1]);
        }
        out.writeInt(plotsInNeedOfWorkers.size());
        for(int i = 0; i<plotsInNeedOfWorkers.size(); i++){
            out.writeInt(plotsInNeedOfWorkers.get(i).getCoords()[0]);
            out.writeInt(plotsInNeedOfWorkers.get(i).getCoords()[1]);
        }
    }
    public static void load(DataInputStream in) throws IOException{
        plotsInNeedOfCivillians.clear();
        plotsInNeedOfWorkers.clear();
        int count = in.readInt();
        for(int i = 0; i<count; i++){
            plotsInNeedOfCivillians.add(main.getPlot(new int[]{in.readInt(), in.readInt()}));
        }
        count = in.readInt();
        for(int i = 0; i<count; i++){
            plotsInNeedOfWorkers.add(main.getPlot(new int[]{in.readInt(), in.readInt()}));
        }
    }
}
