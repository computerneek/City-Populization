package city.populization.core;
import city.populization.connection.PacketChanneled;
import city.populization.connection.PacketPlot;
import city.populization.connection.PacketPlotPos;
import city.populization.menu.MenuMain;
import city.populization.render.Sound;
import city.populization.world.World;
import city.populization.world.civilian.NameGenerator;
import city.populization.world.plot.Plot;
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
import simplelibrary.error.ErrorAdapter;
import simplelibrary.error.ErrorCategory;
import simplelibrary.font.FontManager;
import simplelibrary.game.GameHelper;
import simplelibrary.net.ConnectionManager;
import simplelibrary.openal.SoundStash;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.texture.TexturePack;
import simplelibrary.texture.TexturePackManager;
public class Core{
    //<editor-fold defaultstate="collapsed" desc="Variables">
    public ArrayList<Long> FPStracker = new ArrayList<>();
    public static GameHelper helper;
    private int tick;
    private static int sourceNum;
    private ServerSide server;
    private ClientSide client;
    //</editor-fold>
    public static void main(String[] args) throws NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InterruptedException, IOException, URISyntaxException{
        Sys.initLWJGLUnlogged(new File(getAppdataRoot()), new ErrorAdapter(){
            @Override
            public void warningError(String message, Throwable error, ErrorCategory catagory){
                System.err.println("Warning:  "+message+(error!=null?";  "+error.toString():""));
            }
            @Override
            public void log(String message, Throwable error, ErrorCategory catagory){
                System.err.println("Log:  "+message+(error!=null?";  "+error.toString():""));
            }
        });
        Core core = new Core();
        helper = new GameHelper();
        helper.setBackground(new Color(48, 160, 255));
        helper.setTickInitMethod(Core.class.getDeclaredMethod("tickInit", new Class<?>[0]));
        helper.setRenderInitMethod(Core.class.getDeclaredMethod("renderInit", new Class<?>[0]));
        helper.setFinalInitMethod(Core.class.getDeclaredMethod("finalInit", new Class<?>[0]));
        helper.setTickMethod(Core.class.getDeclaredMethod("tick", boolean.class));
        helper.setRenderMethod(Core.class.getDeclaredMethod("render", int.class));
        helper.setTickInitObject(core);
        helper.setRenderInitObject(core);
        helper.setFinalInitObject(core);
        helper.setTickObject(core);
        helper.setRenderObject(core);
        helper.setMode(GameHelper.MODE_3D);
        helper.setFrameOfView(90);
        helper.setUsesControllers(true);
        helper.setWindowTitle("City Populization "+VersionManager.currentVersion);
        helper.setMaximumFramerate(100);
        helper.setMinRenderDistance(-100F);
        helper.start();
    }
    public void tickInit(){
        server = new ServerSide();
        client = new ClientSide(server);
        World.timerClick.getClass();//Something to cause the World class to initialize...   World.timerClick is a constant.
    }
    public void renderInit() throws LWJGLException{
        helper.frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                helper.running = false;
            }
        });
        FontManager.addFont("/simplelibrary/font");
        FontManager.setFont("font");
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        Keyboard.enableRepeatEvents(true);
        new TexturePackManager(new File(getAppdataRoot(), "Texture packs"), new LocalTexturePack());
        client.gui = new GUI(GameHelper.MODE_3D, helper);
        client.gui.open(new MenuMain(client, null));
        Plot.finalizePlotConstruction();
    }
    public void finalInit() throws LWJGLException{
        AL.create();
        Sound.complete();
        NameGenerator.finalizeNames();
        ConnectionManager.registerPacketClass(new PacketChanneled());
        ConnectionManager.registerPacketClass(new PacketPlotPos());
        ConnectionManager.registerPacketClass(new PacketPlot());
    }
    public void tick(boolean isLastTick){
        if(!isLastTick){
            server.tick();
            client.tick();
        }
        if(isLastTick){
            client.onShutdown();
            server.closeDown();
            AL.destroy();
        }else if(tick%20==0){
            helper.frame.validate();
        }
    }
    public boolean render(int millisSinceLastTick){
        if(client.getWorld()!=null){
            client.getWorld().render();
        }
        client.gui.render(millisSinceLastTick);
        FPStracker.add(System.currentTimeMillis());
        while(FPStracker.get(0)<System.currentTimeMillis()-5_000){
            FPStracker.remove(0);
        }
        if(Settings.guiScale==Settings.GUISCALE_DEFAULT){
            float width = Display.getWidth()/800f;
            float height = Display.getHeight()/500f;
            if(width<height){
                helper.guiScale = height/width;
            }else{
                helper.guiScale = 1;
            }
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
        AL10.alSourceQueueBuffers(SoundStash.getSource("source "+sourceNum), SoundStash.getBuffer(sound));
        AL10.alSourcePlay(SoundStash.getSource("source "+sourceNum));
    }
    public static synchronized void playSound(String sound, String source){
        try{
            AL10.alSourceUnqueueBuffers(SoundStash.getSource(source));
            Util.checkALError();
        }catch(Exception ex){}
        AL10.alSourceQueueBuffers(SoundStash.getSource(source), SoundStash.getBuffer(sound));
        AL10.alSourcePlay(SoundStash.getSource(source));
    }
    public static String getNow(){
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
        return buff.toString();
    }
}
