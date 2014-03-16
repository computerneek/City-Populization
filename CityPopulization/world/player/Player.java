package CityPopulization.world.player;
import CityPopulization.Core;
import CityPopulization.menu.MenuIngame;
import CityPopulization.world.World;
import CityPopulization.world.aircraft.Aircraft;
import CityPopulization.world.civilian.CivilianManager;
import CityPopulization.world.civilian.WorkerTaskManager;
import CityPopulization.world.plot.Plot;
import java.util.ArrayList;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
public abstract class Player{
    public Race race;
    private WorkerTaskManager workerTaskManager;
    private CivilianManager civillianManager;
    public double cameraX;
    public double cameraY;
    public int cameraZ;
    public final World world;
    public static ArrayList<Plot> resourceStructures = new ArrayList<>();
    private boolean sandbox;
    public int cash;
    public Player(World world){
        this.world = world;
    }
    public WorkerTaskManager getWorkerTaskManager(){
        return workerTaskManager;
    }
    public CivilianManager getCivillianManager(){
        return civillianManager;
    }
    public void setRace(Race race){
        this.race = race;
        if(race!=null){
            this.workerTaskManager = race.createWorkerTaskManager();
            this.civillianManager = race.createCivillianManager();
        }
    }
    public double getCameraX(){
        return cameraX;
    }
    public double getCameraY(){
        return cameraY;
    }
    public int getCameraZ(){
        return cameraZ;
    }
    public abstract void summonInitialWorkers();
    public abstract Player createNew(World world);
    public abstract void onPlotClicked(int plotX, int plotY, MenuIngame menu, int button);
    public void setSandbox(boolean sandbox){
        this.sandbox = sandbox;
    }
    public void update(){
        workerTaskManager.update();
        if(world.getLocalPlayer()==this&&Core.gui.menu instanceof MenuIngame){
            if(Mouse.getX()<=30){
                cameraX+=0.1;
            }else if(Mouse.getX()>=Display.getWidth()-30){
                cameraX-=0.1;
            }
            if(Mouse.getY()<=30){
                cameraY+=0.1;
            }else if(Mouse.getY()>=Display.getHeight()-30){
                cameraY-=0.1;
            }
        }
    }
    public int getResourcesPerWarehouse(){
        return 1000;
    }
    public void render(){}
    public void mousewheel(int dist){
        cameraZ-=dist;
    }
}
