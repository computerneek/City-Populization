package CityPopulization.world;
import CityPopulization.world.save.SaveLoader;
import java.io.File;
public class WorldInfo{
    public String name;//World name
    public File file;//World file; used when loading the actual world
    public String type;//World type; story, multiplayer, singleplayer, etc.
    public String played;//String representing the last time this world was played
    public String created;//String representing the time that this world was created
    public String size;//String representation of the size of this world (Plot count)
    public String template;//The name of the template this world was derived from
    public String version;//The version of City Populization that this world was saved by
    public final SaveLoader saveLoader;
    public WorldInfo(SaveLoader saveLoader){
        this.saveLoader = saveLoader;
    }
}
