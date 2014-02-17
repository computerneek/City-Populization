package CityPopulization.world.player;
import CityPopulization.world.resource.ResourceManager;
import CityPopulization.world.civillian.WorkerTaskManager;
import CityPopulization.world.civillian.CivillianManager;
public class Player{
    private Race race;
    private ResourceManager resourceManager;
    private WorkerTaskManager workerTaskManager;
    private CivillianManager civillianManager;
    private float cameraX;
    private float cameraY;
    private float cameraZ;
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
    public float getCameraX(){
        return cameraX;
    }
    public float getCameraY(){
        return cameraY;
    }
    public float getCameraZ(){
        return cameraZ;
    }
}
