package CityPopulization.world.player;
import CityPopulization.world.World;
import CityPopulization.world.civilian.CivilianManager;
import CityPopulization.world.civilian.WorkerTaskManager;
import java.util.ArrayList;
public class Race{
    private static final ArrayList<Race> races = new ArrayList<>();
    public static final Race HUMAN = new Race("Human", new PlayerHuman(), 25);
//                             ZOMBIE = new Race("Zombie", null, 5);
    public static Race[] values(){
        return races.toArray(new Race[races.size()]);
    }
    private final String name;
    private final Player player;
    private int workerResourceCapacity;
    Race(String name, Player player, int workerResourceCapacity){
        this.name = name;
        this.player=player;
        this.workerResourceCapacity = workerResourceCapacity;
        races.add(this);
    }
    public String getName(){
        return name;
    }
    public WorkerTaskManager createWorkerTaskManager(){
        return new WorkerTaskManager();
    }
    public CivilianManager createCivillianManager(){
        return new CivilianManager();
    }
    public Player createPlayer(World world){
        return player.createNew(world);
    }
    public int getWorkerResourceCapacity(){
        return workerResourceCapacity;
    }
}
