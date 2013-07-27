package CityPopulization;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import multilib.config.Config;
import multilib.error.ErrorCategory;
import multilib.error.ErrorLevel;
import multilib.error.Sys;
import multilib.game.GameHelper;
import multilib.game.KeyboardStatus;
import multilib.game.MouseStatus;
import multilib.gui.TexturePack;
import multilib.gui.TexturePackManager;
import multilib.gui.WindowControl;
import multilib.lang.TextManager;
import multilib.net.ConnectionManager;
import multilib.net.Packet;
import multilib.net.Packet6Long;
import multilib.net.Packet1Integer;
import multilib.net.Packet2String;
import multilib.net.Packet3Boolean;
public class main{
    //<editor-fold defaultstate="collapsed" desc="Variables">
    public static Plot[][] world;
    public static JFrame frame;
    public static JPanel panel;
    public static Plot highlighted;
    public static long cash;
    public static int speed;
    public static int tick;
    private static Random rand;
    private static long income;
    private static int angryCivillians;
    private static int idleCivillians;
    private static int idleWorkers;
    public static int dirt;
    public static int coal;
    public static int oil;
    public static int wood;
    public static int stone;
    public static int iron;
    public static int sand;
    public static int clay;
    public static int gold;
    private static int zombies;
    public static final int shoppingMallIncome = 1750;
    public static final int resturantIncome = 500;
    public static final int amusementParkIncome = 500;
    public static final int departmentStoreIncome = 5000;
    public static final int parkIncome = 10000;
    private static JSpinner width;
    private static JSpinner height;
    private static JTextField seed;
    public static GameHelper helper;
    public static boolean isPaused;
    public static boolean isGamePaused;
    private static int worldMovementX;
    private static int worldMovementY;
    public static Plot selected;
    public static int[] civillianFrames = new int[11];
    public static int[] civillianTPF = new int[civillianFrames.length];
    public static int[] civillianFrame = new int[civillianFrames.length];
    public static int[] civillianTicks = new int[civillianFrames.length];
    public static int constructionTicks = 0;
    public static int constructionTPF = 1;
    public static int constructionFrame = 1;
    public static int constructionFrames = 1;
    public static int damageTicks = 0;
    public static int damageTPF = 1;
    public static int damageFrame = 1;
    public static int damageFrames = 1;
    public static int[] workerFrames = new int[40];
    public static int[] workerTPF = new int[workerFrames.length];
    public static int[] workerFrame = new int[workerFrames.length];
    public static int[] workerTicks = new int[workerFrames.length];
    private static int dead;
    public static Config config;
    public static GameHandler handler;
    public static float lagTicks;
    public static ArrayList<Long> FPStracker = new ArrayList<>();
    public static TexturePackManager modpackManager;
    public static boolean forceRender = false;
    public static PlotType currentConstruction;
    //</editor-fold>
    public static void main(String[] args) throws NoSuchMethodException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, InterruptedException, IOException, URISyntaxException{
        //<editor-fold defaultstate="collapsed" desc="Exporting LWJGL binaries">
        if(args.length!=1||!args[0].equals("Skip export")){
            File nativesLocation = new File(getAppdataRoot()+"\\natives");
            String[] nativesNames64Bit = new String[]{"jinput-dx8.dll", "jinput-dx8_64.dll", "jinput-raw.dll", "jinput-raw_64.dll", "lwjgl64.dll", "lwjgl.dll", "OpenAL32.dll", "OpenAL64.dll"};
            String[] nativesNames32Bit = new String[]{"jinput-dx8.dll", "jinput-raw.dll", "lwjgl.dll", "OpenAL32.dll"};
            String[] nativesNames;
            String version = System.getenv("PROCESSOR_ARCHITECTURE");
            if(version.equals("x86")){
                nativesNames = nativesNames32Bit;
            }else if(version.equals("x64")){
                nativesNames = nativesNames64Bit;
            }else{
                Exception ex = new UnsupportedOperationException("Unknown OS architecture:  "+version+"!");
                Sys.error(ErrorLevel.critical, "Unknown OS architecture:  "+version+"!", ex, ErrorCategory.bug);
                throw (UnsupportedOperationException)ex;
            }
            nativesLocation.mkdirs();
            for(String name : nativesNames){
                if(!new File(nativesLocation, name).exists()){
                    try(InputStream in = main.class.getResourceAsStream("/"+name);OutputStream out = new FileOutputStream(new File( nativesLocation, name))){
                        if(in==null){
                            throw new IllegalStateException("Could not find "+name+" in the jarfile!");
                        }
                        while(in.available()>0){
                            out.write(in.read());
                        }
                    }catch(IOException ex){
                        throw new RuntimeException(ex);
                    }
                }
            }
            String base = "C:\\Program Files\\Dolan Programmers\\LWJGL\\lwjgl-2.8.3\\jar\\";
            String lwjglFiles = base+"jinput.jar;"+base+"lwjgl.jar;"+base+"lwjgl_util.jar";
            ArrayList<String> params = new ArrayList<>();
            params.add("java");
            params.add("-Djava.library.path="+nativesLocation.getAbsolutePath());
            params.add("-classpath");
            params.add(main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()+";C:\\Program Files\\Dolan Programmers\\Error Management\\dist\\Error_Management.jar;"+lwjglFiles);
            params.add("CityPopulization.main");
            params.add("Skip export");
            ProcessBuilder builder = new ProcessBuilder(params);
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectInput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            System.out.println("Starting up...");
            Process p = builder.start();
            System.exit(p.waitFor());
        }
        //</editor-fold>
        if(config==null){
            File file = new File(getAppdataRoot());
            File[] files = file.listFiles();
            for(int i = 0; files!=null&&i<files.length; i++){
                if(files[i].getName().startsWith("Error ")&&files[i].getName().endsWith(".log")){
                    files[i].delete();
                }
            }
            config = Sys.init(file, null, getAppdataRoot()+"\\settings.properties", null);
        }
        modpackManager = new TexturePackManager(new File(getAppdataRoot(), "Mods"), new TexturePack());
        new TexturePackManager(new File(getAppdataRoot(), "Texture packs"), new TexturePack());
        ActionListener tutorialAction = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                tutorial();
            }
        };
        ActionListener singleplayerAction = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                start();
            }
        };
        ActionListener texturepacksAction = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                frame.dispose();
                TexturePackHandler.open();
            }
        };
        ActionListener modpacksAction = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                frame.dispose();
                ModHandler.open();
            }
        };
        ActionListener quitAction = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        };
        WindowListener wl = new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        };
        frame = WindowControl.createFrameWithoutAppearance("City Populization 2.9.9 (MENUS OVERHAULED IN VERSION 3)", 410, 230, wl);
        JPanel panel = WindowControl.createPanel(frame, null, null, Color.BLUE);
        JButton tutorialButton = WindowControl.createButton(panel, "Tutorial", 5, 5, 400, 20, null, null, tutorialAction);
        JButton storyButton = WindowControl.createButton(panel, "--STORY MODE ADDED IN OVERHAUL VERSION 3--", 5, 30, 400, 20, null, null, null);
        JButton singleplayerButton = WindowControl.createButton(panel, "Singleplayer", 5, 55, 400, 20, null, null, singleplayerAction);
        JButton multiplayerButton = WindowControl.createButton(panel, "--MULTIPLAYER MODE ADDED IN OVERHAUL VERSION 3--", 5, 80, 400, 20, null, null, null);
        JButton texturepacksButton = WindowControl.createButton(panel, "Texture Packs (REMOVED IN OVERHAUL VERSION 3)", 5, 105, 400, 20, null, null, texturepacksAction);
        JButton modpacksButton = WindowControl.createButton(panel, "--MODPACKS REMOVED IN OVERHAUL VERSION 3--", 5, 130, 400, 20, null, null, modpacksAction);
        JButton optionsButton = WindowControl.createButton(panel, "--OPTIONS ADDED IN OVERHAUL VERSION 3--", 5, 155, 400, 20, null, null, null);
        JButton modelPack = WindowControl.createButton(panel, "--MODELPACKS ADDED IN OVERHAUL VERSION 3--", 5, 180, 400, 20, null, null, null);
        JButton quitButton = WindowControl.createButton(panel, "Quit", 5, 205, 400, 20, null, null, quitAction);
        multiplayerButton.setEnabled(false);
        storyButton.setEnabled(false);
//        ModpackHandler.setModpack();
        modpacksButton.setEnabled(false);
        optionsButton.setEnabled(false);
        modelPack.setEnabled(false);
        TexturePackHandler.setTexturePack();
        frame.setVisible(true);
    }
    public static void init() throws InterruptedException{
        Profiler.start("Initialization");
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_TEXTURE_2D);
        Profiler.start("Key assignment");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_PAUSE, "Pause");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_LEFT, "Left");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_RIGHT, "Right");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_DOWN, "Down");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_UP, "Up");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_1, "1");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_2, "2");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_3, "3");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_4, "4");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_5, "5");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_6, "6");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_7, "7");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_8, "8");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_9, "9");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_A, "A");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_B, "B");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_C, "C");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_D, "D");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_E, "E");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_F, "F");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_G, "G");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_H, "H");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_I, "I");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_J, "J");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_K, "K");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_L, "L");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_M, "M");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_N, "N");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_O, "O");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_P, "P");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_Q, "Q");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_R, "R");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_S, "S");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_T, "T");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_U, "U");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_V, "V");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_W, "W");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_X, "X");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_Y, "Y");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_Z, "Z");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_ESCAPE, "Escape");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_LSHIFT, "LSHIFT");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_RSHIFT, "RSHIFT");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_SPACE, " ");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_UNDERLINE, "_");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_PERIOD, ".");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_BACK, "BACKSPACE");
        KeyboardStatus.addKeyToWatch(org.lwjgl.input.Keyboard.KEY_RETURN, "RETURN");
        Profiler.endStart("Font registry");
        TextManager.addText("/CityPopulization/CharactersLWJGL", false);
        TextManager.setText("/CityPopulization/CharactersLWJGL");
        Profiler.endStart("LWJGL Preperation");
        org.lwjgl.opengl.GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_TEXTURE_2D);
        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_LIGHTING);
        org.lwjgl.opengl.GL11.glDisable(org.lwjgl.opengl.GL11.GL_FOG);
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_ALPHA_TEST);
        org.lwjgl.opengl.GL11.glAlphaFunc(org.lwjgl.opengl.GL11.GL_GREATER, 0.1F);
        Profiler.end();
        Profiler.end();
    }
    public static void tick(boolean isLastTick){
        tick++;
        try{
            Profiler.start("core");
            boolean isOpen = EscapeMenu.isOpen();
            EscapeMenu.tick();
            if(!isOpen){
                for(int i = 0; i<civillianTicks.length; i++){
                    civillianTicks[i]++;
                    if(civillianTicks[i]>=civillianTPF[i]){
                        civillianFrame[i]++;
                        if(civillianFrame[i]>civillianFrames[i]){
                            civillianFrame[i] = 1;
                        }
                    }
                }
                constructionTicks++;
                if(constructionTicks>=constructionTPF){
                    constructionFrame++;
                    if(constructionFrame>constructionFrames){
                        constructionFrame = 1;
                    }
                }
                damageTicks++;
                if(damageTicks>=damageTPF){
                    damageFrame++;
                    if(damageFrame>damageFrames){
                        damageFrame = 1;
                    }
                }
                for(int i = 0; i<workerTicks.length; i++){
                    workerTicks[i]++;
                    if(workerTicks[i]>=workerTPF[i]){
                        workerFrame[i]++;
                        if(workerFrame[i]>workerFrames[i]){
                            workerFrame[i] = 1;
                        }
                    }
                }
                if(worldMovementY<-(world[0].length*50)+17*50){
                    worldMovementY++;
                }
                if(worldMovementY>0){
                    worldMovementY--;
                }
                if(worldMovementX<-(world.length*50)+32*50){
                    worldMovementX++;
                }
                if(worldMovementX>0){
                    worldMovementX--;
                }
                Profiler.start("tick");
                Profiler.start("Pre");
                handler.preTick();
                Profiler.end();
                if(world!=null&&!isPaused&&!isGamePaused&&!EscapeMenu.isOpen()){
                    Profiler.start("core");
                    tick();
                    Profiler.endStart("workers");
                    WorkerTaskList.tick();
                    Profiler.endStart("Animation updates");
                    for(PlotType type : PlotType.values()){
                        type.updateAnimations();
                    }
                    Profiler.end();
                }
                Profiler.start("Post");
                boolean shouldNotControl = handler.tick();
                Profiler.end();
                Profiler.end();
                if(!shouldNotControl){
                    Profiler.start("Keyboard");
                    handleKeyboard(helper.keyboard);
                    Profiler.endStart("Mouse");
                    handleMouse(helper.mouse);
                    Profiler.end();
                }
                if(selected!=null&&selected.isType(PlotType.empty)&&!selected.isUpgrading){
                    selected = null;
                }
                Profiler.end();
                Renderer.update();
            }
        }catch(UnsupportedOperationException ex){
            if(ex.getMessage()!=null&&(ex.getMessage().equals("Not yet implemented")||ex.getMessage().equals("Not supported yet."))){
                throw new RuntimeException(ex);
            }else{
                throw ex;
            }
        }catch(RuntimeException ex){
            if(ex.getMessage()!=null&&ex.getMessage().equals("Uncompilable source code!")){
                throw new RuntimeException(ex);
            }else{
                throw ex;
            }
        }finally{
            Profiler.endAll();
        }
    }
    public static boolean render(int millisSinceLastTick){
        if(millisSinceLastTick>50&&tick%5!=0&&!forceRender){
            return true;
        }else if(millisSinceLastTick>50&&tick%5!=0&&forceRender){
            forceRender = false;
        }
        try{
            lagTicks = ((float)millisSinceLastTick)/50F;
            FPStracker.add(System.currentTimeMillis());
            while(FPStracker.get(0)<System.currentTimeMillis()-5_000){
                FPStracker.remove(0);
            }
            Profiler.start("Render");
            Profiler.start("World");
//            if(tick%5==0){
                Renderer.renderWorld(world, worldMovementX, worldMovementY+50);
//            }
//            org.lwjgl.opengl.GL11.glCallList(1);
            Profiler.endStart("Civillians");
            Renderer.renderCivillians(CivillianTaskList.workers, worldMovementX, worldMovementY+50);
            Profiler.endStart("Workers");
            Renderer.renderWorkers(WorkerTaskList.workers, worldMovementX, worldMovementY+50);
            Profiler.endStart("GUI");
            renderGUI();
            Profiler.endStart("Timers");
            if(org.lwjgl.input.Keyboard.isKeyDown(org.lwjgl.input.Keyboard.KEY_SPACE)){
                TimerDisplayer.render();
            }
            Profiler.endStart("Handler");
            handler.render();
            Profiler.endStart("Escape Menu");
            EscapeMenu.render();
            Profiler.end();
            Profiler.end();
        }catch(UnsupportedOperationException ex){
            if(ex.getMessage()!=null&&(ex.getMessage().equals("Not yet implemented")||ex.getMessage().equals("Not supported yet."))){
                throw new RuntimeException(ex);
            }else{
                throw ex;
            }
        }catch(RuntimeException ex){
            if(ex.getMessage()!=null&&ex.getMessage().equals("Uncompilable source code!")){
                throw new RuntimeException(ex);
            }else{
                throw ex;
            }
        }finally{
            Profiler.endAll();
        }
        return false;
    }
    public static String getAppdataRoot(){
        return System.getenv("APPDATA")+"\\Dolan Programmers\\City Populization";
    }
    public static void start(){
        frame.dispose();
        ActionListener action = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                playCustom();
            }
        };
        WindowListener wl = new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                System.exit(0);
            }
        };
        frame = WindowControl.createFrame("City Populization", 815, 105, wl);
        JPanel panel = WindowControl.createPanel(frame, null, null, Color.BLUE);
        JTextField widthLabel = WindowControl.createTextField(panel, "How many plots wide do you want your world?", 5, 5, 400, 20, false, null, null);
        JTextField heightLabel = WindowControl.createTextField(panel, "How many plots high do you want your world?", 5, 30, 400, 20, false, null, null);
        JTextField seedLabel = WindowControl.createTextField(panel, "What do you want the seed of your world to be?", 5, 55, 400, 20, false, null, null);
        width = WindowControl.createSpinner(panel, 410, 5, 400, 20, 9, 1000, 1, null, null);
        width.setValue(50);
        height = WindowControl.createSpinner(panel, 410, 30, 400, 20, 2, 1000, 1, null, null);
        height.setValue(50);
        seed = WindowControl.createTextField(panel, "", 410, 55, 400, 20, true, null, null);
        JButton button = WindowControl.createButton(panel, "Generate and Play World", 5, 80, 805, 20, null, null, action);
        panel.setBounds(0, 0, 815, 105);
        frame.repaint();
    }
    public static void playCustom(){
        int x = (int)width.getValue();
        int y = (int)height.getValue();
        int seed = getInt(main.seed.getText());
        if(seed==0){
            seed = new Random().nextInt();
        }
        boolean isSizeChallenger = JOptionPane.showConfirmDialog(panel, "Do you want this map to be a size challenger map?", "Size Challenger Maps", JOptionPane.YES_NO_OPTION)==0;
        playMap(x, y, new GameHandler().setIsSizeChallenger(isSizeChallenger).setSeed(seed));
    }
    public static void tutorial(){
        playMap(50, 50, new GameHandler().setSeed(2).setWorkersToWarpIn(2).setCivilliansPerLevel(10).setType(GameHandler.TYPE_TUTORIAL));
    }
    public static void playMap(int x, int y, GameHandler theHandler){
        main.handler = theHandler;
        if(theHandler==null){
            throw new IllegalArgumentException("The handler must not be null!");
        }
        int seed = theHandler.seed;
        Plot.seed = seed;
        Plot.seedrand = new Random(seed);
        world = new Plot[x][y];
        frame.dispose();
        panel = null;
        for(int i = 0; i<x; i++){
            for(int j = 0; j<y; j++){
                world[i][j] = new Plot(i, j);
            }
        }
        rand = new Random(seed);
        if(theHandler.shouldSetupBase){
            world[0][0].setType(PlotType.mainBase, 1);
            world[0][1].setType(PlotType.highway, 1);
            world[1][1].setType(PlotType.highway, 1);
            world[1][0].setType(PlotType.house, 5);
            world[1][0].fillHouse();
        }else{
            theHandler.setupBase();
        }
        for(int i = 0; i<theHandler.workersToWarpIn; i++){
            CivillianScheduler.warpWorker();
        }
        speed = theHandler.speed;
        reloadCosts();
        dirt = 1000;
        coal = 500;
        oil = 500;
        wood = 500;
        stone = 500;
        iron = 500;
        gold = 500;
        clay = 500;
        sand = 500;
        cash = theHandler.getCash();
        isPaused = false;
        helper = new GameHelper();
        helper.setBackground(Color.BLUE);
        helper.setDisplaySize(800, 500);
        helper.setMaximumFramerate(100);
        helper.setUsesControllers(true);
        helper.setWindowTitle("City Populization");
        try{
            helper.setInitMethod(main.class.getDeclaredMethod("init", (Class[])null));
            helper.setTickMethod(main.class.getDeclaredMethod("tick", boolean.class));
            helper.setRenderMethod(main.class.getDeclaredMethod("render", int.class));
        }catch(NoSuchMethodException ex){
            throw new RuntimeException(ex);
        }
        Sys.initLWJGLGame(new File(getAppdataRoot()), new ErrorHandler(), null, null, helper);
    }
    public static int getWorldWidth(){
        if(world==null){
            return 0;
        }else{
            return world.length*50;
        }
    }
    public static int getWorldHeight(){
        if(getWorldWidth()==0){
            return 0;
        }else{
            return world[0].length*50;
        }
    }
    public static void act(int[] coords, boolean leftClick){
        if(coords[0]<0||coords[0]>=world.length||coords[1]<0||coords[1]>=world[coords[0]].length){
            return;
        }
        Plot plot = world[coords[0]][coords[1]];
        if(!plot.isType(PlotType.empty)||plot.isUpgrading){
            selected = world[coords[0]][coords[1]];
        }else{
            selected = null;
        }
    }
    private static final Logger LOG = Logger.getLogger(main.class.getName());
    public static int[] getPlotCoordinates(int[] coords){
        return getPlotCoordinates(coords[0], coords[1]);
    }
    public static int[] getPlotCoordinates(int X, int Y){
        return new int[]{(X-(X%50))/50, (Y-(Y%50))/50};
    }
    public static void payWorker(int cashWanted){
        cash-=cashWanted;
    }
    public static void tick(){
        Plot.refreshRegisteredCivillians();
        Plot.refreshRegisteredWorkers();
        Profiler.start("Plots");
        for(int i = 0; i<world.length; i++){
            for(int j = 0; j<world[0].length; j++){
                world[i][j].tick();
            }
        }
        Profiler.endStart("counting");
        Profiler.start("civillians");
        int idleWorkers = Plot.totalWorkers, idleCivillians = Plot.totalCivillians, angryCivillians = 0, zombies = 0, dead = 0;
        synchronized(CivillianTaskList.waitor){
            Civillian[] workers = CivillianTaskList.workers.toArray(new Civillian[CivillianTaskList.workers.size()]);
            for(Civillian worker : workers){
                if(worker.status==Civillian.Status.zombie){
                    zombies++;
                }else if(worker.status==Civillian.Status.dead){
                    dead++;
                }else if(worker.status==Civillian.Status.angry||worker.anger>0){
                    angryCivillians++;
                }else if(worker.isAtHome){
                    idleCivillians++;
                }
            }
        }
        Profiler.endStart("upkeep");
        int structureUpkeep = 0;
        for(int x = 0; x<world.length; x++){
            Y:
            for(int y = 0; y<world[x].length; y++){
                PlotType type = world[x][y].getType();
                if(type==PlotType.empty){
                    continue Y;
                }
                structureUpkeep+=world[x][y].getLevel();
            }
        }
        Profiler.endStart("income");
        long income = CivillianTaskList.getWorkerCount()-angryCivillians
                     +WorkerTaskList.getWorkerCount()
                     -(handler.isSizeChallenger?(world.length*world[0].length):0)
                     -structureUpkeep;
        long maxIncome = CivillianTaskList.getWorkerCount()
                        +CivillianScheduler.plotsInNeedOfCivillians.size()
                        +WorkerTaskList.getWorkerCount()
                        -(handler.isSizeChallenger?(world.length*world[0].length):0)
                        -structureUpkeep;
        if(tick%20==0){
            cash+=income;
        }
        main.income = income;
        main.idleWorkers = idleWorkers;
        main.idleCivillians = idleCivillians;
        main.angryCivillians = angryCivillians;
        main.dead = dead;
        main.zombies = zombies;
        Profiler.end();
        if(maxIncome<0&&income<0&&cash<0&&tick%20==0&&WorkerTaskList.getAvailableWorkers()==WorkerTaskList.getWorkerCount()){
            lose("economyCollapsed");
        }
        Profiler.endStart("Civillians");
        CivillianScheduler.tick();
        Profiler.end();
    }
    public static void income(int money){
        cash += money;
    }
    public static Plot getPlot(int[] loc){
        if(loc[0]>=world.length){
            return null;
        }else if(loc[1]>=world[loc[0]].length){
            return null;
        }
        return world[loc[0]][loc[1]];
    }
    public static String getCoordString(int[] loc){
        return "("+loc[0]+", "+loc[1]+")";
    }
    private static void lose(String reason){
        throw new RuntimeException("Uncompilable source code");
    }
    public static int getInt(String text){
        if(text==null){
            return 0;
        }
        try{
            return Integer.parseInt(text);
        }catch(NumberFormatException ex){
            return text.hashCode();
        }
    }
    public static Plot[] getPlotsOfType(PlotType plotType){
        ArrayList<Plot> plots = new ArrayList<>();
        for(int i = 0; i<world[0].length; i++){
            for(int j = 0; j<world.length; j++){
                if(world[j][i].getType()==plotType){
                    plots.add(world[j][i]);
                }
            }
        }
        return plots.toArray(new Plot[plots.size()]);
    }
    public static Plot[] getPlotsOfTypeVert(PlotType plotType){
        ArrayList<Plot> plots = new ArrayList<>();
        for(int x = 0; x<world.length; x++){
            for(int y = 0; y<world[x].length; y++){
                if(world[x][y].getType()==plotType){
                    plots.add(world[x][y]);
                }
            }
        }
        return plots.toArray(new Plot[plots.size()]);
    }
    public static Plot getNextShoppingMall(){
        return getNextPlot(PlotType.shoppingMall);
    }
    public static Plot getNextPlot(PlotType type){
        Plot[] plots = getPlotsOfType(type);
        if(plots.length==0){
            return null;
        }
        return plots[rand.nextInt(plots.length)];
    }
    public static Plot getNextPark(){
        return getNextPlot(PlotType.park);
    }
    public static Plot getNextDepartmentStore(){
        return getNextPlot(PlotType.departmentStore);
    }
    public static Plot getNextAmusementPark(){
        return getNextPlot(PlotType.amusementPark);
    }
    public static Plot getNextResturant(){
        return getNextPlot(PlotType.restaurant);
    }
    public static boolean canReach(Plot plot){
        return WorkerPath.findPathTo(Plot.getMainBase().getCoords(), plot.getCoords())!=null;
    }
    private static void reloadCosts(){
        Material[] materials = new Material[]{Material.Dirt, Material.Coal, Material.Oil, Material.Wood, Material.Stone, Material.Iron, Material.Sand, Material.Clay, Material.Gold};
        PlotType.setCosts(PlotType.mainBase, materials, new int[][]{
           null
        });
        PlotType.setCosts(PlotType.highway, materials, new int[][]{
            {1, 10, 0, 0, 0, 0, 0, 0, 0, 0},
            {2, 20, 0, 0, 0, 0, 0, 0, 0, 0},
            {3, 30, 0, 0, 0, 0, 0, 0, 0, 0}
        });
        PlotType.setCosts(PlotType.house, materials, new int[][]{
            {1, 50, 0, 0, 0, 0, 0, 0, 0, 0},
            {2, 100, 0, 0, 0, 0, 0, 0, 0, 0},
            {3, -10, 0, 5, 0, 0, 10, 0, 0, 0},
            {4, -10, 0, 5, 0, 0, 10, 0, 0, 0},
            {5, -10, 0, 5, 0, 0, 10, 0, 0, 0},
            {6, -10, 0, 50, 0, 0, 15, 0, 0, 0},
            {7, -10, 0, 50, 0, 0, 15, 0, 0, 0},
            {8, -10, 0, 50, 0, 0, 15, 0, 0, 0},
            {9, 150, 0, 0, 0, 0, 0, 0, 0, 0},
            {10, 10, 0, 0, 50, 0, 10, 0, 0, 0},
            {11, 10, 0, 10, 100, 1, 50, 0, 0, 0},
            {12, 0, 0, 5, 10, 5, 30, 0, 50, 0},
            {13, 0, 50, 1, 5, 10, 10, 0, 0, 0},
            {14, 0, 100, 30, 50, 5, 5, 100, 0, 0},
            {15, 0, 20, 100, 5, 5, 5, 150, 0, 0}
        });
        PlotType.setCosts(PlotType.workshop, materials, new int[][]{
            {1, 5, 0, 0, 0, 0, 0, 0, 0, 0}
        });
        PlotType.setCosts(PlotType.shoppingMall, materials, new int[][]{
            {1, 50, 5, 5, 50, 50, 10, 0, 0, 0}
        });
    }
    public static int getHighestLevel(ArrayList<Plot> lst){
        return getHighestLevel(lst.toArray(new Plot[0]));
    }
    public static int getHighestLevel(Plot[] plots){
        int highestLevel = 0;
        for(Plot plot : plots){
            highestLevel = Math.max(plot.getLevel(), highestLevel);
        }
        return highestLevel;
    }
    public static Plot findNextZombieDestination(){
        ArrayList<Plot> value = new ArrayList<>();
        for(PlotType type : PlotType.values()){
            if(type.zombieLevel==0){
                continue;
            }
            ArrayList<Plot> plots = new ArrayList<>();
            plots.addAll(Arrays.asList(getPlotsOfType(type)));
            for(Plot plot : plots){
                if(plot.getLevel()>=type.zombieLevel){
                    value.add(plot);
                }
            }
        }
        return value.size()>0?value.get(rand.nextInt(value.size())):null;
    }
    private static void renderGUI(){
        Renderer.renderGUI();
        Renderer.renderText(0, 0, "$"+cash+", Income = $"+income+"; Workers = "+WorkerTaskList.getWorkerCount()+", Idle = "+idleWorkers+"; Civillians = "+CivillianTaskList.getWorkerCount()+", Angry = "+angryCivillians+"; Zombies = "+zombies+(dead>0?"."+dead:""));
        Renderer.renderText(0, 25, "Dirt = "+dirt+", Coal = "+coal+", Oil = "+oil+", Wood = "+wood+", Stone = "+stone+", Iron = "+iron+", Sand = "+sand+", Clay = "+clay+", Gold = "+gold);
    }
    public static void saveGame(String name) throws IOException{
        File file = new File(getAppdataRoot()+"\\Saves\\"+name+".cps");//City Populization Save
        file.getParentFile().mkdirs();
        int num = 1;
        while(file.exists()&&file.isDirectory()){
            num++;
            file = new File(getAppdataRoot()+"\\Saves\\"+name+" ("+num+").cps");
        }
        if(file.exists()){
            file.delete();
        }
        DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
        out.writeUTF(name);
        out.writeLong(cash);
        out.writeInt(dirt);
        out.writeInt(coal);
        out.writeInt(oil);
        out.writeInt(wood);
        out.writeInt(stone);
        out.writeInt(iron);
        out.writeInt(sand);
        out.writeInt(clay);
        out.writeInt(gold);
        out.writeInt(speed);
        out.writeInt(tick);
        out.writeBoolean(isGamePaused);
        handler.save(out);
        out.writeInt(worldMovementX);
        out.writeInt(worldMovementY);
        out.writeInt(world.length);
        out.writeInt(world[0].length);
        ArrayList<WorkerTask> tasks = new ArrayList<>();
        tasks.addAll(WorkerTaskList.importantTasks);
        tasks.addAll(WorkerTaskList.tasks);
        for(int i = 0; i<tasks.size(); i++){
            tasks.get(i).setIndex(i);
        }
        out.writeInt(WorkerTaskList.importantTasks.size());
        for(WorkerTask task : WorkerTaskList.importantTasks){
            task.save(out);
        }
        out.writeInt(WorkerTaskList.tasks.size());
        for(WorkerTask task : WorkerTaskList.tasks){
            task.save(out);
        }
        for(int i = 0; i<world.length; i++){
            for(int j = 0; j<world[0].length; j++){
                world[i][j].save(out);
            }
        }
        out.writeInt(CivillianTaskList.workers.size());
        for(Civillian civillian : CivillianTaskList.workers){
            civillian.save(out);
        }
        out.writeInt(WorkerTaskList.workers.size());
        for(Worker worker : WorkerTaskList.workers){
            worker.save(out);
        }
        CivillianScheduler.save(out);
        out.flush();
        out.close();
    }
    static void loadGame(String name) throws IOException{
        File file = new File(getAppdataRoot()+"\\Saves\\"+name+".cps");//City Populization Save
        int num = 1;
        while((!file.exists()||file.isDirectory())&&num<500){
            num++;
            file = new File(getAppdataRoot()+"\\Saves\\"+name+" ("+num+").cps");
        }
        if(!file.exists()){
            return;
        }
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        in.readUTF();
        cash = in.readLong();
        dirt = in.readInt();
        coal = in.readInt();
        oil = in.readInt();
        wood = in.readInt();
        stone = in.readInt();
        iron = in.readInt();
        sand = in.readInt();
        clay = in.readInt();
        gold = in.readInt();
        speed = in.readInt();
        tick = in.readInt();
        isPaused = true;
        isGamePaused = in.readBoolean();
        handler.load(in);
        worldMovementX = in.readInt();
        worldMovementY = in.readInt();
        world = new Plot[in.readInt()][in.readInt()];
        for(int i = 0; i<world.length; i++){
            for(int j = 0; j<world[i].length; j++){
                world[i][j] = new Plot(i, j);
            }
        }
        int importantTaskCount = in.readInt();
        WorkerTaskList.importantTasks.clear();
        for(int i = 0; i<importantTaskCount; i++){
            WorkerTaskList.importantTasks.add(WorkerTask.load(in));
        }
        int standardTaskCount = in.readInt();
        WorkerTaskList.tasks.clear();
        for(int i = 0; i<standardTaskCount; i++){
            WorkerTaskList.tasks.add(WorkerTask.load(in));
        }
        for(int i = 0; i<world.length; i++){
            for(int j = 0; j<world[0].length; j++){
                world[i][j].load(in);
            }
        }
        int civillianCount = in.readInt();
        CivillianTaskList.workers.clear();
        for(int i = 0; i<civillianCount; i++){
            CivillianTaskList.workers.add(Civillian.load(in));
        }
        int workerCount = in.readInt();
        WorkerTaskList.workers.clear();
        for(int i = 0; i<workerCount; i++){
            WorkerTaskList.workers.add(Worker.load(in));
        }
        CivillianScheduler.load(in);
        in.close();
    }
    public static void handleKeyboard(KeyboardStatus helper){
        String[] keys = helper.getPressedKeys().getKeys();
        for(int i = 0; i<keys.length; i++){
            int ID = KeyboardStatus.getButtonIDForName(keys[i]);
            if(ID==org.lwjgl.input.Keyboard.KEY_PAUSE){
                isPaused = !isPaused;
            }else if(ID==org.lwjgl.input.Keyboard.KEY_ESCAPE){
                EscapeMenu.toggleState();
            }else{
                for(int j = 1; j<10; j++){
                    if(keys[i].equals(j+"")){
                        Renderer.onKeyboard(j);
                    }
                }
            }
        }
        keys = helper.getDownKeys().getKeys();
        for(int i = 0; i<keys.length; i++){
            int ID = KeyboardStatus.getButtonIDForName(keys[i]);
            if(ID==org.lwjgl.input.Keyboard.KEY_DOWN&&worldMovementY>-(world[0].length*50)+17*50){
                worldMovementY-=10*(org.lwjgl.input.Keyboard.isKeyDown(org.lwjgl.input.Keyboard.KEY_RCONTROL)?5:1);
            }else if(ID==org.lwjgl.input.Keyboard.KEY_UP&&worldMovementY<0){
                worldMovementY+=10*(org.lwjgl.input.Keyboard.isKeyDown(org.lwjgl.input.Keyboard.KEY_RCONTROL)?5:1);
            }else if(ID==org.lwjgl.input.Keyboard.KEY_RIGHT&&worldMovementX>-(world.length*50)+32*50){
                worldMovementX-=10*(org.lwjgl.input.Keyboard.isKeyDown(org.lwjgl.input.Keyboard.KEY_RCONTROL)?5:1);
            }else if(ID==org.lwjgl.input.Keyboard.KEY_LEFT&&worldMovementX<0){
                worldMovementX+=10*(org.lwjgl.input.Keyboard.isKeyDown(org.lwjgl.input.Keyboard.KEY_RCONTROL)?5:1);
            }else{
                for(int j = 1; j<10; j++){
                    if(keys[i].equals(j+"")){
                        Renderer.keyboardHeld(j);
                    }
                }
            }
        }
    }
    public static void handleMouse(MouseStatus mouse){
        if(!Renderer.onClick(mouse, mouse.posX, mouse.posY)){
            if(mouse.leftButtonDown&&!mouse.middleButtonDown&&!mouse.rightButtonDown){
                Plot plot = getPlot(getPlotCoordinates((int)((mouse.posX)*Renderer.widthScale)-worldMovementX, (int)((mouse.posY)*Renderer.heightScale)-worldMovementY-50));
                if(currentConstruction!=null&&plot!=null&&plot.isType(PlotType.empty)&&!plot.isUpgrading){
                    PlotType type = currentConstruction;
                    int dirt = type.getCost(1, Material.Dirt);
                    int coal = type.getCost(1, Material.Coal);
                    int oil = type.getCost(1, Material.Oil);
                    int wood = type.getCost(1, Material.Wood);
                    int stone = type.getCost(1, Material.Stone);
                    int iron = type.getCost(1, Material.Iron);
                    int sand = type.getCost(1, Material.Sand);
                    int clay = type.getCost(1, Material.Clay);
                    int gold = type.getCost(1, Material.Gold);
                    plot.task = WorkerTaskList.addTask(new WorkerTask(type.constructionTag, plot, 1, 100, dirt, coal, oil, wood, stone, iron, sand, clay, gold, 1));
                    plot.isUpgrading = true;
                }else if(mouse.leftButtonPressed&&!mouse.middleButtonDown&&!mouse.rightButtonDown){
                    act(getPlotCoordinates((int)((mouse.posX)*Renderer.widthScale)-worldMovementX, (int)((mouse.posY)*Renderer.heightScale)-worldMovementY-50), true);
                }
            }else if(currentConstruction!=null&&mouse.leftButtonReleased&&!(org.lwjgl.input.Keyboard.isKeyDown(org.lwjgl.input.Keyboard.KEY_LSHIFT)||org.lwjgl.input.Keyboard.isKeyDown(org.lwjgl.input.Keyboard.KEY_RSHIFT))){
                Plot plot = getPlot(getPlotCoordinates((int)((mouse.posX)*Renderer.widthScale)-worldMovementX, (int)((mouse.posY)*Renderer.heightScale)-worldMovementY-50));
                currentConstruction = null;
            }
            if(!mouse.leftButtonDown&&!mouse.middleButtonDown&&mouse.rightButtonPressed){
                selected = null;
                currentConstruction = null;
            }
        }
    }
    public static void getResources(Material resource, int resources){
        switch(resource){
            case Dirt:
                dirt+=resources;
                break;
            case Coal:
                coal+=resources;
                break;
            case Oil:
                oil+=resources;
                break;
            case Stone:
                stone+=resources;
                break;
            case Wood:
                wood+=resources;
                break;
            case Iron:
                iron+=resources;
                break;
            case Sand:
                sand+=resources;
                break;
            case Clay:
                clay+=resources;
                break;
            case Gold:
                gold+=resources;
                break;
        }
    }
}
