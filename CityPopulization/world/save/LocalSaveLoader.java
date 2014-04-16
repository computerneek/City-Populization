package CityPopulization.world.save;
import CityPopulization.Core;
import CityPopulization.world.World;
import CityPopulization.world.WorldInfo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import simplelibrary.config2.Config;
public class LocalSaveLoader implements SaveLoader{
    private final File file;
    public LocalSaveLoader(File file){
        this.file = file;
    }
    @Override
    public WorldInfo[] listWorlds(){
        ArrayList<WorldInfo> value = new ArrayList<>();
        ArrayList<File> files = new ArrayList<>();
        files.add(file);
        while(!files.isEmpty()){
            File file = files.remove(0);
            if(file.isDirectory()){
                List<File> list = Arrays.asList(file.listFiles());
                Collections.sort(list);
                files.addAll(0, list);
            }else if(file.getName().endsWith(".cpw")){
                Config config = Config.newConfig(file);
                config = config.load();
                if(config!=null){
                    WorldInfo info = new WorldInfo(this);
                    info.name = config.get("name");
                    info.file = file;
                    info.type = config.get("type");
                    info.played = config.get("last played");
                    info.created = config.get("created");
                    info.size = config.get("size");
                    info.template = config.get("template");
                    info.version = config.get("version");
                    if(info.name!=null&&info.type!=null&&info.played!=null&&info.created!=null&&info.size!=null&&info.template!=null&&info.version!=null){
                        value.add(info);
                    }
                }
            }
        }
        return value.toArray(new WorldInfo[value.size()]);
    }
    @Override
    public World loadWorld(WorldInfo info){
        switch(info.version){
            default:
                return load3_1(info);
        }
    }
    private World load3_1(WorldInfo info){
        World world = new World();
        world.info = info;
        try(FileInputStream in = new FileInputStream(info.file)){
            Config config = Config.newConfig(in).load().load();
            if(config==null){
                return world;
            }
            world.load3_1(config);
        }catch(IOException|NullPointerException ex){}
        return world;
    }
    @Override
    public void saveWorld(World world){
        WorldInfo info = world.info;
        info.file.getParentFile().mkdirs();
        try(FileOutputStream out = new FileOutputStream(info.file)){
            Config config = Config.newConfig();
            config.set("name", info.name);
            config.set("type", info.type);
            config.set("last played", Core.getNow());
            config.set("created", info.created);
            config.set("size", world.size());
            config.set("template", info.template);
            config.set("version", info.version);
            config.save(out);
            config = Config.newConfig();
            world.save(config);
            config.save(out);
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
}
