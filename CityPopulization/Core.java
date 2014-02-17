package CityPopulization;
import CityPopulization.world.WorldInfo;
import CityPopulization.world.WorldData;
import CityPopulization.world.World;
import CityPopulization.world.save.SaveLoader;
import CityPopulization.menu.MenuMain;
import CityPopulization.menu.MenuIngame;
import CityPopulization.world.save.LocalSaveLoader;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.Util;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.Sys;
import simplelibrary.font.FontManager;
import simplelibrary.game.GameHelper;
import simplelibrary.openal.SoundStash;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.texture.TexturePack;
import simplelibrary.texture.TexturePackManager;
public class Core{
    //<editor-fold defaultstate="collapsed" desc="Variables">
    public static float lagTicks;
    public static ArrayList<Long> FPStracker = new ArrayList<>();
    public static boolean forceRender = false;
    public static GameHelper helper;
    public static GUI gui;
    private static int tick;
    public static World world;
    private static int sourceNum;
    private static SaveLoader empireSaveLoader;
    //</editor-fold>
    public static void main(String[] args) throws NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InterruptedException, IOException, URISyntaxException{
        Sys.restartAndLoadBinaries(args, new File(getAppdataRoot()), Core.class);
        Sys.initLWJGL(new File(getAppdataRoot()), null);
        empireSaveLoader = new LocalSaveLoader(new File(getAppdataRoot()+"\\Saves"));
        helper = new GameHelper();
        helper.setBackground(Color.BLUE);
        helper.setInitMethod(Core.class.getDeclaredMethod("init", new Class<?>[0]));
        helper.setTickMethod(Core.class.getDeclaredMethod("tick", boolean.class));
        helper.setRenderMethod(Core.class.getDeclaredMethod("render", int.class));
        helper.setMode(GameHelper.MODE_3D);
        helper.setFrameOfView(90);
        helper.setUsesControllers(true);
        helper.setWindowTitle("City Populization "+VersionManager.currentVersion);
        helper.start();
    }
    public static void init() throws LWJGLException{
        helper.frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                helper.running = false;
            }
        });
        GL11.glEnable(org.lwjgl.opengl.GL11.GL_TEXTURE_2D);
        FontManager.addFont("/font");
        FontManager.setFont("font");
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        AL.create();
        Keyboard.enableRepeatEvents(true);
        new TexturePackManager(new File(getAppdataRoot(), "Texture packs"), new TexturePack());
        gui = new GUI(GameHelper.MODE_3D, helper);
        gui.open(new MenuMain(gui, null));
    }
    public static void tick(boolean isLastTick){
        if(world!=null&&(gui.menu==null||gui.menu.pausesGame())){
            world.tick();
        }
        tick++;
        gui.tick();
        if(isLastTick){
            AL.destroy();
        }
    }
    public static boolean render(int millisSinceLastTick){
        if(millisSinceLastTick>50&&tick%5!=0&&!forceRender){
            return true;
        }else if(millisSinceLastTick>50&&tick%5!=0&&forceRender){
            forceRender = false;
        }
        gui.render(millisSinceLastTick);
        lagTicks = ((float)millisSinceLastTick)/50F;
        FPStracker.add(System.currentTimeMillis());
        while(FPStracker.get(0)<System.currentTimeMillis()-5_000){
            FPStracker.remove(0);
        }
        if(Settings.guiScale==Settings.GUISCALE_DEFAULT){
            float height = Display.getHeight()/500f;
            helper.guiScale = Math.min(Display.getWidth()/800f, Display.getHeight()/500f)/height;
        }else{
            helper.guiScale = Settings.guiScale;
        }
        return false;
    }
    public static String getAppdataRoot(){
        return System.getenv("APPDATA")+"\\Dolan Programmers\\City Populization";
    }
    public static synchronized void playSound(String sound){
        sourceNum++;
        if(sourceNum>20){
            sourceNum = 1;
        }
        try{
            AL10.alSourceUnqueueBuffers(SoundStash.getSource("source "+sourceNum));
            Util.checkALError();
        }catch(Exception ex){}
        AL10.alSourceQueueBuffers(SoundStash.getSource("source "+sourceNum), SoundStash.getBuffer("/"+sound+".wav"));
        AL10.alSourcePlay(SoundStash.getSource("source "+sourceNum));
    }
    public static SaveLoader getSingleplayerSaveLoader(){
        return empireSaveLoader;
    }
    public static void createEmpireWorld(WorldData data){
        WorldInfo info = new WorldInfo(empireSaveLoader);
        info.name = data.name;
        info.file = new File(getAppdataRoot()+"\\Saves\\"+data.name+".cpw");
        for(int i = 1; info.file.exists(); i++){
            info.file = new File(getAppdataRoot()+"\\Saves\\"+data.name+"_"+i+".cpw");
        }
        info.type = "Empire";
        info.played = getNow();
        info.created = getNow();
        info.size = "9 plots";
        info.template = data.template.getName();
        info.version = VersionManager.currentVersion;
        World world = info.saveLoader.loadWorld(info);
        world.setTemplate(data.template);
        world.getLocalPlayer().setRace(data.race);
        world.setGoal(data.goal);
        world.setGameSpeed(data.gameSpeed);
        world.setDifficulty(data.difficulty);
        world.getLocalPlayer().getResourceManager().setSandbox(true);
        world.summonInitialWorker();
        playWorld(world);
    }
    private static String getNow(){
        GregorianCalendar calendar = new GregorianCalendar();
        StringBuilder buff = new StringBuilder(13);
        buff.append(calendar.getDisplayName(Calendar.MONTH, GregorianCalendar.SHORT, Locale.US));
        buff.append(" ");
        buff.append(calendar.get(Calendar.DAY_OF_MONTH));
        buff.append(", ");
        buff.append(calendar.get(Calendar.YEAR));
        buff.append("  ");
        buff.append(calendar.get(Calendar.HOUR)+(calendar.get(Calendar.HOUR)==0?12:0));
        buff.append(":");
        buff.append(calendar.get(Calendar.MINUTE));
        buff.append(":");
        buff.append(calendar.get(Calendar.SECOND));
        buff.append(".");
        buff.append(calendar.get(Calendar.MILLISECOND));
        return buff.toString();
    }
    public static void playWorld(World world){
        Core.world = world;
        gui.open(new MenuIngame(gui, gui.menu));
    }
}