package CityPopulization.world.player;
import CityPopulization.world.World;
import CityPopulization.world.civillian.CivillianManager;
import CityPopulization.world.civillian.WorkerTaskManager;
import CityPopulization.world.resource.ResourceManager;
public abstract class Player{
    private Race race;
    private ResourceManager resourceManager;
    private WorkerTaskManager workerTaskManager;
    private CivillianManager civillianManager;
    private double cameraX;
    private double cameraY;
    private double cameraZ;
    public final World world;
    public Player(World world){
        this.world = world;
    }
    public ResourceManager getResourceManager(){
        return resourceManager;
    }
    public WorkerTaskManager getWorkerTaskManager(){
        return workerTaskManager;
    }
    public CivillianManager getCivillianManager(){
        return civillianManager;
    }
    public void setRace(Race race){
        this.race = race;
        this.resourceManager = race.createResourceManager();
        this.workerTaskManager = race.createWorkerTaskManager();
        this.civillianManager = race.createCivillianManager();
    }
    public double getCameraX(){
        return cameraX;
    }
    public double getCameraY(){
        return cameraY;
    }
    public double getCameraZ(){
        return cameraZ;
    }
    public abstract void summonInitialWorkers();
    public abstract Player createNew(World world);
}
