package CityPopulization.world.save;
import CityPopulization.world.World;
import CityPopulization.world.WorldInfo;
public interface SaveLoader{
    public WorldInfo[] listWorlds();
    public World loadWorld(WorldInfo info);
}
