package CityPopulization.world.player;
import CityPopulization.world.resource.ResourceManager;
import CityPopulization.world.civillian.WorkerTaskManager;
import CityPopulization.world.civillian.CivillianManager;
public enum Race{
    HUMAN("Human"),
    ZOMBIE("Zombie");
    private final String name;
    Race(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public ResourceManager createResourceManager(){
        return new ResourceManager();
    }
    public WorkerTaskManager createWorkerTaskManager(){
        return new WorkerTaskManager();
    }
    public CivillianManager createCivillianManager(){
        return new CivillianManager();
    }
}
