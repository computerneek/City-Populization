package city.populization.world;
import city.populization.core.Core;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import simplelibrary.config2.Config;
public class WorldLoader {
    public static final File savesDir = new File(Core.getAppdataRoot()+"\\Saves");
    public static WorldData[] getSingleplayerList() {
        File[] files = savesDir.listFiles();
        if(files==null){
            return new WorldData[0];
        }
        ArrayList<WorldData> lst = new ArrayList<>(files.length);
        for(File f : files){
            if(f.isFile()&&f.getName().endsWith(".cps.new")){
                File f2 = new File(savesDir, f.getName().substring(0, f.getName().length()-4));
                if(f2.exists()){
                    f.delete();
                    continue;
                }else{
                    f.renameTo(f2);
                    f = f2;
                }
            }
            if(f.isFile()&&f.getName().endsWith(".cps")){
                long modified = f.lastModified();
                Config c = Config.newConfig(f).load();
                if(c!=null){
                    WorldData d = new WorldData().load(c);
                    if(!d.isMultiplayer&&d.filepath!=null){//This is used as a make-sure-its-the-right-file check, against old City Populization versions too
                        d.filepath = f.getAbsolutePath();
                        lst.add(d);
                    }
                }
            }
        }
        return lst.toArray(new WorldData[lst.size()]);
    }
    public static World createSingleplayerWorld(WorldData d) {
        int which = 0;
        do{
            which++;
            d.filepath = savesDir.getAbsolutePath()+"\\"+d.name+(which>1?" "+which:"")+".cps";
        }while(new File(d.filepath).exists());
        World world = new World();
        world.setData(d);
        d.save().save(d.filepath);
        return world;
    }
    public static void deleteSingleplayerWorld(WorldData d) {
        new File(d.filepath).delete();
    }
    public static World loadSingleplayerWorld(WorldData d) {
        World world = new World();
        world.setData(d);
        d.save().save(d.filepath);
        return world;
    }
}
