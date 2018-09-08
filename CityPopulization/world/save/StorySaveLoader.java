package CityPopulization.world.save;
import CityPopulization.world.World;
import CityPopulization.world.WorldInfo;
import CityPopulization.world.story.StoryMission;
import java.io.File;
public class StorySaveLoader implements SaveLoader {
    public File file;
    public StorySaveLoader(File file){
        this.file = file;
    }
    @Override
    public WorldInfo[] listWorlds(){
        return new WorldInfo[0];
    }
    @Override
    public World loadWorld(WorldInfo info){
        return null;
    }
    public void loadWorld(StoryMission mission){
        
    }
    @Override
    public void saveWorld(World world){}
    public void saveWorld(StoryMission mission){
        
    }
}
