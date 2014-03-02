package CityPopulization.world.player;
import CityPopulization.world.World;
import CityPopulization.world.civillian.CivillianManager;
import CityPopulization.world.civillian.WorkerTaskManager;
import CityPopulization.world.resource.ResourceManager;
public enum Race{
    HUMAN("Human", new PlayerHuman()),
    ZOMBIE("Zombie", null);
    private final String name;
    private final Player player;
    Race(String name, Player player){
        this.name = name;
        this.player=player;
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
    public Player createPlayer(World world){
        return player.createNew(world);
    }
}
