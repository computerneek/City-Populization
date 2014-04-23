package CityPopulization.world.player;
import CityPopulization.world.World;
import java.util.ArrayList;
public class Race{
    private static final ArrayList<Race> races = new ArrayList<>();
    public static final Race HUMAN = new Race("Human", new PlayerHuman(), 25);
//                             ZOMBIE = new Race("Zombie", null, 5);
    public static Race[] values(){
        return races.toArray(new Race[races.size()]);
    }
    public static Race getByName(String string){
        for(Race race : races){
            if(race.getName().equals(string)){
                return race;
            }
        }
        return null;
    }
    private final String name;
    private final Player player;
    private int workerResourceCapacity;
    private Race(String name, Player player, int workerResourceCapacity){
        this.name = name;
        this.player=player;
        this.workerResourceCapacity = workerResourceCapacity;
        races.add(this);
    }
    public String getName(){
        return name;
    }
    public Player createPlayer(World world){
        return player.createNew(world);
    }
    public int getWorkerResourceCapacity(World world){
        return (int)(workerResourceCapacity*world.difficulty.incomeModifier);
    }
}
