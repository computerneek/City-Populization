package city.populization.core;
import simplelibrary.config.Config;
public class Settings{
    public static final int GUISCALE_DEFAULT = 0;
    public static final int DEFAULT_PORT = 25566;//One higher than Minecraft, hehehe!
    public static int guiScale = GUISCALE_DEFAULT;
    public static int port = DEFAULT_PORT;
    public static String username;
    private static final String file = Core.getAppdataRoot()+"\\config.cfg";
    static{
        Config cnfg = Config.loadConfig(file);
        guiScale = parseInt(cnfg.str("guiScale"), GUISCALE_DEFAULT);
        port = parseInt(cnfg.str("port"), DEFAULT_PORT);
        username = cnfg.str("username");
        if(username!=null&&username.isEmpty()) username = null;
        save();
    }
    public static void save(){
        Config cnfg = Config.loadConfig(file);
        cnfg.putProperty("guiScale", guiScale+"");
        cnfg.putProperty("port", port+"");
        if(username!=null&&!username.isEmpty()) cnfg.putProperty("username", username);
        cnfg.save();
    }
    private static int parseInt(String val, int defaultValue){
        try{
            return Integer.parseInt(val);
        }catch(NumberFormatException | NullPointerException ex){
            return defaultValue;
        }
    }
}
