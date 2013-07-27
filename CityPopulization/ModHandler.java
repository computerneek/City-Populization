package CityPopulization;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import multilib.config.Config;
import multilib.error.ErrorCategory;
import multilib.error.ErrorLevel;
import multilib.error.Sys;
import multilib.game.GameHelper;
import multilib.game.KeyboardStatus;
import multilib.game.MouseStatus;
import multilib.gui.ImageStash;
import multilib.gui.TexturePack;
import multilib.gui.TexturePackManager;
import multilib.gui.WindowControl;
import multilib.lang.TextManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
public class ModHandler {
    //<editor-fold defaultstate="collapsed"> desc="Variables>
    private static JFrame frame;
    private static JPanel panel;
    private static JComboBox<String> combo;
    private static GameHelper helper;
    private static HashMap<String, String> submitted = new HashMap<>();
    private static boolean isStarted;
    private static final ArrayList<Menu> menus = new ArrayList<>();
    private static int maxHeight;
    private static int shift;
    private static ArrayList<Menu> staticClicks = new ArrayList<>();
    private static String displayType;
    private static String imageDisplayed;
    private static TexturePack customTexturePack = new TexturePackInProgress();
    private static int[] imageSpecs;
    private static Config config;
    private static String[] imagesDisplayed;
    private static int whichFrame;
    private static int currentTPF = 1;
    private static int frameTimer;
    private static TexturePack imageTexturePack;
    private static TexturePack[] imageTexturePacks;
    private static File currentDirectory;
    //</editor-fold>
    public static void open(){
        WindowListener wl = new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                main.config.putProperty("Mod", main.modpackManager.currentTexturePack.name());
                main.config.save();
                System.exit(0);
            }
        };
        ActionListener comboListener = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                main.modpackManager.setTexturePack((String)combo.getSelectedItem());
                main.config.putProperty("Mod", (String)combo.getSelectedItem());
                main.config.save();
                frame.dispose();
                open();
            }
        };
        ActionListener createAction = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                frame.dispose();
                try{
                    run();
                }catch(NoSuchMethodException ex){
                    Sys.error(ErrorLevel.severe, "Couldn't run...", ex, ErrorCategory.classFinding);
                }
            }
        };
        ActionListener backAction = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                frame.dispose();
                try{
                    main.main(new String[]{"Skip export"});
                }catch(NoSuchMethodException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException | InterruptedException | IOException | URISyntaxException ex){
                    throw new RuntimeException(ex);
                }
            }
        };
        frame = WindowControl.createFrameWithoutAppearance("City Populization- Mods", 410, 105, wl);
        panel = WindowControl.createPanel(frame, null, null, Color.BLUE);
        WindowControl.createTextField(panel, "Please select a mod:", 5, 5, 400, 20, false, null, null);
        main.modpackManager.findTexturePacks();
        combo = WindowControl.createComboBox(panel, main.modpackManager.listTexturePacks(), 5, 30, 400, 20, null, null, null);
        combo.setSelectedItem(main.modpackManager.currentTexturePack.name());
        combo.addActionListener(comboListener);
        WindowControl.createButton(panel, "Create a Mod", 5, 55, 400, 20, null, null, createAction);
        WindowControl.createButton(panel, "Back", 5, 80, 400, 20, null, null, backAction);
        frame.setVisible(true);
    }
    public static void setModpack(){
        if(main.config.hasProperty("Mod")){
            main.modpackManager.setTexturePack(main.config.str("Mod"));
        }
        configureModpack();
    }
    public static void run() throws NoSuchMethodException{
        staticClicks.clear();
        helper = new GameHelper();
        helper.setBackground(Color.BLUE);
        helper.setDisplaySize(800, 500);
        helper.setMaximumFramerate(40);
        helper.setUsesControllers(true);
        helper.setWindowTitle("City Populization Mod Creator");
        helper.setInitMethod(ModHandler.class.getDeclaredMethod("init", (Class[])null));
        helper.setTickMethod(ModHandler.class.getDeclaredMethod("tick", boolean.class));
        helper.setRenderMethod(ModHandler.class.getDeclaredMethod("render", int.class));
        Sys.initLWJGLGame(Sys.getRoot(), null, null, null, helper);
    }
    public static void init() throws InterruptedException{
        while(config==null){
            Thread.sleep(1);
        }
        GL11.glEnable(org.lwjgl.opengl.GL11.GL_TEXTURE_2D);
        TextManager.addText("/CityPopulization/CharactersLWJGL", false);
        TextManager.setText("/CityPopulization/CharactersLWJGL");
        Display.setResizable(true);
        KeyboardStatus.addKeyToWatch(Keyboard.KEY_UP, "Up");
        KeyboardStatus.addKeyToWatch(Keyboard.KEY_DOWN, "Down");
        KeyboardStatus.addKeyToWatch(Keyboard.KEY_LEFT, "Left");
        KeyboardStatus.addKeyToWatch(Keyboard.KEY_RIGHT, "Right");
        org.lwjgl.opengl.GL11.glEnable(org.lwjgl.opengl.GL11.GL_ALPHA_TEST);
        org.lwjgl.opengl.GL11.glAlphaFunc(org.lwjgl.opengl.GL11.GL_GREATER, 0.1F);
    }
    public static void tick(boolean isLastTick){
        if(isLastTick){
            save("AUTO");
            ImageStash.instance.clearTextures();
            open();
            return;
        }
        //<editor-fold defaultstate="collapsed" desc="Mouse">
        int width = Display.getWidth(), height = Display.getHeight();
        MouseStatus mouse = helper.mouse;
        if(mouse.leftButtonPressed){
            if(!isStarted){
                if(isScaledClickWithinBounds(width, height, 800, 500, mouse.posX, mouse.posY, 200, 180, 600, 220)){
                    isStarted = true;
                }else if(isScaledClickWithinBounds(width, height, 800, 500, mouse.posX, mouse.posY, 200, 230, 600, 270)){
                    loadModpack();
                }else if(isScaledClickWithinBounds(width, height, 800, 500, mouse.posX, mouse.posY, 200, 280, 600, 320)){
                    helper.running = false;
                }
            }else{
                if(mouse.posX<=300){
                    int distance = 0;
                    for(Menu menu : menus){
                        distance+=menu.onClick(distance, mouse.posX, mouse.posY, 0, shift);
                    }
                }
            }
        }
        if(isStarted&&mouse.posX<=300){
            shift-=mouse.wheelChange/6;
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Keyboard">
        KeyboardStatus keyboard = helper.keyboard;
        for(String key : keyboard.getDownKeys().getKeys()){
            if(key.equals("Up")&&shift>0){
                shift-=(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)||Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))?200:20;
            }
            if(key.equals("Down")&&shift<maxHeight-400){
                shift+=(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)||Keyboard.isKeyDown(Keyboard.KEY_RCONTROL))?200:20;
            }
            if(key.equals("Left")){
                for(Menu menu : menus){
                    menu.collapse();
                }
            }
            if(key.equals("Right")){
                for(Menu menu : menus){
                    menu.expandAll();
                }
            }
        }
        if(shift<0){
            shift+=Math.min(20, -shift);
        }
        if(shift>=maxHeight-500&&shift>0){
            shift-=Math.min(20, shift-(maxHeight-500));
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Menu updating">
        if(isStarted){
            maxHeight+=(maxHeight-(maxHeight&500))/500+1;
            int diff = 0;
            int maximumHeight = maxHeight;
            for(Menu menu : menus){
                maximumHeight-=menu.update(maximumHeight);
            }
            diff = maximumHeight;
            if(diff>0){
                maxHeight-=diff;
            }
        }
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Static menu clicks">
        while(!staticClicks.isEmpty()){
            Menu click = staticClicks.remove(0);
            try{
                switch(click.title){
                    case "Save"://<editor-fold defaultstate="collapsed">
                        save("MANUAL");
                        break;//</editor-fold>
                    case "Close"://<editor-fold defaultstate="collapsed">
                        helper.running = false;
                        break;//</editor-fold>
                    case "Get Image Specs"://<editor-fold defaultstate="collapsed">
                        imageSpecs = new int[]{click.parent.specs[0], click.parent.specs[1]};
                        displayType = "IMAGE_SPECS";
                        imageTexturePack = null;
                        break;//</editor-fold>
                    case "Load Frame":
                    case "Load Image"://<editor-fold defaultstate="collapsed">
                        click.parent.image = findFileWithExtension(".png", true);//</editor-fold>
                    case "View Frame":
                    case "View Image"://<editor-fold defaultstate="collapsed">
                        imageSpecs = new int[]{click.parent.specs[0], click.parent.specs[1]};
                        if(imageSpecs[0]<=50&&imageSpecs[1]<=50){
                            imageSpecs = new int[]{imageSpecs[0]*10, imageSpecs[1]*10};
                        }
                        while(imageSpecs[0]>500||imageSpecs[1]>500){
                            imageSpecs = new int[]{imageSpecs[0]/10, imageSpecs[1]/10};
                        }
                        displayType = "IMAGE";
                        imageDisplayed = click.parent.image;
                        imageTexturePack = click.parent.texturepack;
                        break;//</editor-fold>
                    case "Generate standard image"://<editor-fold defaultstate="collapsed">
                        imageSpecs = new int[]{click.parent.specs[0], click.parent.specs[1]};
                        generateTexture(click, findFileWithExtension(".png", false));
                        if(imageSpecs[0]<=50&&imageSpecs[1]<=50){
                            imageSpecs = new int[]{imageSpecs[0]*10, imageSpecs[1]*10};
                        }
                        while(imageSpecs[0]>500||imageSpecs[1]>500){
                            imageSpecs = new int[]{imageSpecs[0]/10, imageSpecs[1]/10};
                        }
                        displayType = "IMAGE";
                        imageDisplayed = click.parent.image;
                        imageTexturePack = click.parent.texturepack;
                        break;//</editor-fold>
                    case "Generate Blank Frame"://<editor-fold defaultstate="collapsed">
                        imageSpecs = new int[]{click.parent.specs[0], click.parent.specs[1]};
                        imageSpecs = new int[]{imageSpecs[0]*10, imageSpecs[1]*10};
                        generateTexture(click, findFileWithExtension(".png", false));
                        imageSpecs = new int[]{click.parent.specs[0], click.parent.specs[1]};
                        if(imageSpecs[0]<=50&&imageSpecs[1]<=50){
                            imageSpecs = new int[]{imageSpecs[0]*10, imageSpecs[1]*10};
                        }
                        while(imageSpecs[0]>500||imageSpecs[1]>500){
                            imageSpecs = new int[]{imageSpecs[0]/10, imageSpecs[1]/10};
                        }
                        displayType = "IMAGE";
                        imageDisplayed = click.parent.image;
                        imageTexturePack = click.parent.texturepack;
                        break;//</editor-fold>
                    case "Generate high-res image"://<editor-fold defaultstate="collapsed">
                        imageSpecs = new int[]{click.parent.specs[0], click.parent.specs[1]};
                        imageSpecs = new int[]{imageSpecs[0]*10, imageSpecs[1]*10};
                        generateTexture(click, findFileWithExtension(".png", false));
                        imageSpecs = new int[]{click.parent.specs[0], click.parent.specs[1]};
                        if(imageSpecs[0]<=50&&imageSpecs[1]<=50){
                            imageSpecs = new int[]{imageSpecs[0]*10, imageSpecs[1]*10};
                        }
                        while(imageSpecs[0]>500||imageSpecs[1]>500){
                            imageSpecs = new int[]{imageSpecs[0]/10, imageSpecs[1]/10};
                        }
                        displayType = "IMAGE";
                        imageDisplayed = click.parent.image;
                        imageTexturePack = click.parent.texturepack;
                        break;//</editor-fold>
                    case "Clear Frame":
                    case "Clear Image"://<editor-fold defaultstate="collapsed">
                        click.parent.image = null;
                        click.parent.texturepack = null;
                        break;//</editor-fold>
                    case "Add a Frame"://<editor-fold defaultstate="collapsed">
                        click.parent.add(new Menu("Frame "+(click.parent.children.size()-5), 4, click.parent.specs[0], click.parent.specs[1], click.parent.location.replace("<FRAME>", (click.parent.children.size()-5)+""), null, null, null, -1, -1, null, null));
                        break;//</editor-fold>
                    case "Remove a Frame"://<editor-fold defaultstate="collapsed">
                        if(click.parent.children.size()>6){
                            click.parent.children.remove(click.parent.children.size()-1);
                        }
                        break;//</editor-fold>
                    case "Preview Animation"://<editor-fold defaultstate="collapsed">
                        imageSpecs = new int[]{click.parent.specs[0], click.parent.specs[1]};
                        if(imageSpecs[0]<=50&&imageSpecs[1]<=50){
                            imageSpecs = new int[]{imageSpecs[0]*10, imageSpecs[1]*10};
                        }
                        while(imageSpecs[0]>500||imageSpecs[1]>500){
                            imageSpecs = new int[]{imageSpecs[0]/10, imageSpecs[1]/10};
                        }
                        displayType = "ANIMATION";
                        imagesDisplayed = click.parent.getAnimationFrames();
                        currentTPF = click.parent.tpf;
                        imageTexturePacks = click.parent.getAnimationTexturepacks();
                        break;//</editor-fold>
                    case "Speed up the animation"://<editor-fold defaultstate="collapsed">
                        click.parent.tpf--;
                        if(click.parent.tpf<1){
                            click.parent.tpf = 1;
                        }
                        imageSpecs = new int[]{click.parent.specs[0], click.parent.specs[1]};
                        if(imageSpecs[0]<=50&&imageSpecs[1]<=50){
                            imageSpecs = new int[]{imageSpecs[0]*10, imageSpecs[1]*10};
                        }
                        while(imageSpecs[0]>500||imageSpecs[1]>500){
                            imageSpecs = new int[]{imageSpecs[0]/10, imageSpecs[1]/10};
                        }
                        displayType = "ANIMATION";
                        imagesDisplayed = click.parent.getAnimationFrames();
                        currentTPF = click.parent.tpf;
                        imageTexturePacks = click.parent.getAnimationTexturepacks();
                        break;//</editor-fold>
                    case "Slow down the animation"://<editor-fold defaultstate="collapsed">
                        click.parent.tpf++;
                        imageSpecs = new int[]{click.parent.specs[0], click.parent.specs[1]};
                        if(imageSpecs[0]<=50&&imageSpecs[1]<=50){
                            imageSpecs = new int[]{imageSpecs[0]*10, imageSpecs[1]*10};
                        }
                        while(imageSpecs[0]>500||imageSpecs[1]>500){
                            imageSpecs = new int[]{imageSpecs[0]/10, imageSpecs[1]/10};
                        }
                        displayType = "ANIMATION";
                        imagesDisplayed = click.parent.getAnimationFrames();
                        currentTPF = click.parent.tpf;
                        imageTexturePacks = click.parent.getAnimationTexturepacks();
                        break;//</editor-fold>
                    case "Get Animation Specs"://<editor-fold defaultstate="collapsed">
                        imageSpecs = new int[]{click.parent.specs[0], click.parent.specs[1]};
                        displayType = "ANIMATION_SPECS";
                        break;//</editor-fold>
                    case "Export":
                        saveTexturepack();
                        break;
                    case "Add Level":
                        PlotType type = null;
                        for(PlotType atype : PlotType.values()){
                            if(atype.name.equals(click.parent.title)){
                                type = atype;
                            }
                        }
                        if(click.parent.type==6){
                            click.parent.add(new Menu("Level "+(click.parent.children.size()-3), 9, 50, 50, type.name, null, null, null, -1, -1, null, null));
                        }else if(click.parent.type==7){
                            click.parent.add(new Menu("Level "+(click.parent.children.size()-3), 10, 50, 50, type.name, null, null, null, -1, -1, null, null));
                        }
                        break;
                    case "Remove Level":
                        if(click.parent.children.size()>4){
                            click.parent.children.remove(click.parent.children.size()-1);
                        }
                        break;
                    default:
                        if(click.parent!=null&&click.parent.title.equals("Import Textures")){
                            importTextures(click.parent, click.title);
                            break;
                        }
                        if(click.parent!=null&&click.parent.title.equals("Import Mod Info")){
                            importModInfo(click.parent, click.title);
                            break;
                        }
                        throw new UnsupportedOperationException("Not yet implemented: "+click.title);
                }
            }catch(Exception ex){
                Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.uncaught);
            }
            ImageStash.instance.clearTextures();
        }
        frameTimer++;
        if(frameTimer>=currentTPF){
            whichFrame++;
            frameTimer-=currentTPF;
        }
    }
    public static void render(int timeSinceLastTick){
        int width = Display.getWidth(), height = Display.getHeight();
        GL11.glLoadIdentity();
        GL11.glTranslatef(0, 0, -1);
        if(!isStarted){
            drawScaledRect(width, height, 800, 500, 200, 180, 600, 220, ImageStash.instance.getTexture("/texturepackHelper/mainMenu/new.png"));
            drawScaledRect(width, height, 800, 500, 200, 230, 600, 270, ImageStash.instance.getTexture("/texturepackHelper/mainMenu/load.png"));
            drawScaledRect(width, height, 800, 500, 200, 280, 600, 320, ImageStash.instance.getTexture("/texturepackHelper/mainMenu/back.png"));
        }else{
            scaledTranslate(height, 500, 0, -shift, 0);
            int distanceDown = 0;
            for(Menu menu : menus){
                distanceDown += menu.draw(distanceDown, 0);
            }
            scaledTranslate(height, 500, 0, shift, 0);
            GL11.glColor4f(1, 1, 1, 1);
            if(imageSpecs!=null){
                int left = 300+(500-(imageSpecs[0]))/2;
                int right = left+imageSpecs[0];
                int top = (500-imageSpecs[1])/2;
                int bottom = top+imageSpecs[1];
                if("IMAGE".equals(displayType)&&imageDisplayed!=null&&!imageDisplayed.isEmpty()){
                    TexturePack last = TexturePackManager.instance.currentTexturePack;
                    TexturePackManager.instance.currentTexturePack = imageTexturePack==null?customTexturePack:imageTexturePack;
                    drawScaledRect(width, height, 800, 500, left, top, right, bottom, ImageStash.instance.getTexture(imageDisplayed));
                    TexturePackManager.instance.currentTexturePack = last;
                }else if("IMAGE_SPECS".equals(displayType)&&imageSpecs!=null){
                    drawScaledText(width, height, 800, 500, 300, 100, 800, 150, "Standard Image:");
                    drawScaledText(width, height, 800, 500, 300, 150, 800, 200, imageSpecs[0]+"x"+imageSpecs[1]);
                    drawScaledText(width, height, 800, 500, 300, 250, 800, 300, "High-def Image:");
                    drawScaledText(width, height, 800, 500, 300, 300, 800, 350, (imageSpecs[0]*10)+"x"+(imageSpecs[1]*10));
                }else if("ANIMATION".equals(displayType)&&imageSpecs!=null&&imagesDisplayed!=null&&imagesDisplayed.length>0){
                    if(whichFrame>=imagesDisplayed.length){
                        whichFrame = 0;
                    }
                    TexturePack last = TexturePackManager.instance.currentTexturePack;
                    TexturePackManager.instance.currentTexturePack = imageTexturePacks[whichFrame]==null?customTexturePack:imageTexturePacks[whichFrame];
                    if(imagesDisplayed[whichFrame]!=null&&!imagesDisplayed[whichFrame].isEmpty()){
                        drawScaledRect(width, height, 800, 500, left, top, right, bottom, ImageStash.instance.getTexture(imagesDisplayed[whichFrame]));
                    }
                    TexturePackManager.instance.currentTexturePack = last;
                }else if("ANIMATION_SPECS".equals(displayType)&&imageSpecs!=null){
                    drawScaledText(width, height, 800, 500, 300, 200, 800, 250, "Animation Size:");
                    drawScaledText(width, height, 800, 500, 300, 250, 800, 300, (imageSpecs[0])+"x"+(imageSpecs[1]));
                }
            }
        }
    }
    private static boolean isScaledClickWithinBounds(int displayWidth, int displayHeight, int destinationWidth, int destinationHeight, int clickX, int clickY, int targetXMin, int targetYMin, int targetXMax, int targetYMax){
        float widthScale = ((float)destinationWidth)/((float)displayWidth), heightScale = ((float)destinationHeight)/((float)displayHeight);
        float scaledX = clickX*widthScale, scaledY = clickY*heightScale;
        return scaledX>=targetXMin&&scaledY>=targetYMin&&scaledX<=targetXMax&&scaledY<=targetYMax;
    }
    private static void drawScaledRect(float displayWidth, float displayHeight, float destinationWidth, float destinationHeight, float leftEdge, float topEdge, float rightEdge, float bottomEdge, int texture){
        float widthScale = destinationWidth/displayWidth, heightScale = destinationHeight/displayHeight;
        float left = leftEdge/widthScale, top = topEdge/heightScale, right = rightEdge/widthScale, bottom = bottomEdge/heightScale;
        ImageStash.instance.bindTexture(texture);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(left, top);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(right, top);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(right, bottom);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(left, bottom);
        GL11.glEnd();
    }
    private static boolean drawScaledText(float displayWidth, float displayHeight, float destinationWidth, float destinationHeight, float leftEdge, float topEdge, float rightPossibleEdge, float bottomEdge, String text){
        boolean trimmed = false;
        float distanceForText = TextManager.getLengthForStringWithHeight(text, bottomEdge-topEdge);
        while(distanceForText>rightPossibleEdge-leftEdge&&!text.isEmpty()){
            trimmed = true;
            text = text.substring(0, text.length()-1);
            distanceForText = TextManager.getLengthForStringWithHeight(text+"...", bottomEdge-topEdge);
        }
        if(text.isEmpty()){
            return false;
        }
        if(trimmed){
            text+="...";
        }
        float scale = TextManager.getLengthForStringWithHeight("M", bottomEdge-topEdge)/((float)TextManager.getCharLength('M'));
        float skip = 0;
        char[] chars = new char[text.length()];
        text.getChars(0, chars.length, chars, 0);
        for(char character : chars){
            drawScaledRect(displayWidth, displayHeight, destinationWidth, destinationHeight, leftEdge+skip, topEdge, leftEdge+skip+(((float)TextManager.getCharLength(character))*scale), bottomEdge, (int)TextManager.getImageForChar(character));
            skip+=(((float)TextManager.getCharLength(character))*scale);
        }
        return true;
    }
    private static void scaledTranslate(float existingValue, float targetValue, float Xtranslate, float Ytranslate, float Ztranslate){
        float scale = targetValue/existingValue;
        float X = Xtranslate/scale, Y = Ytranslate/scale, Z = Ztranslate/scale;
        GL11.glTranslatef(X, Y, Z);
    }
    private static void loadModpack(){
        File file = new File(System.getenv("APPDATA")+"\\Dolan Programmers\\City Populization\\modpack.autosave");
        if(!file.exists()){
            return;
        }
        Config config = Config.loadConfig(file);
        ArrayList<Menu> menusToSave = new ArrayList<>();
        for(Menu menu : menus){
            menusToSave.add(menu);
        }
        while(!menusToSave.isEmpty()){
            Menu menu = menusToSave.remove(0);
            if(menu.type==1){
                for(Menu child : menu.children){
                    menusToSave.add(child);
                }
            }else if(menu.type==2&&config.hasProperty(menu.getName()+".image")){
                if(config.hasProperty(menu.getName()+".texturepack")){
                    TexturePack old = TexturePackManager.instance.currentTexturePack;
                    TexturePackManager.instance.setTexturePack(config.str(menu.getName()+".texturepack"));
                    menu.texturepack = TexturePackManager.instance.currentTexturePack;
                    TexturePackManager.instance.currentTexturePack = old;
                }
                menu.image = checkForFile(menu.texturepack, config.str(menu.getName()+".image"));
                if(menu.image==null){
                    menu.texturepack = null;
                }
            }else if(menu.type==3&&config.hasProperty(menu.getName()+".frames")){
                int frames = Integer.parseInt(config.str(menu.getName()+".frames"));
                for(int i = 0; i<frames; i++){
                    menu.add(new Menu("Frame "+(menu.children.size()-5), 4, menu.specs[0]*10, menu.specs[1]*10, menu.location.replace("<FRAME>", (menu.children.size()-5)+""), null, null, null, -1, -1, null, null));
                }
                for(Menu child : menu.children){
                    menusToSave.add(child);
                }
            }else if(menu.type==4&&config.hasProperty(menu.getName()+".frame")){
                if(config.hasProperty(menu.getName()+".texturepack")){
                    TexturePack old = TexturePackManager.instance.currentTexturePack;
                    TexturePackManager.instance.setTexturePack(config.str(menu.getName()+".texturepack"));
                    menu.texturepack = TexturePackManager.instance.currentTexturePack;
                    TexturePackManager.instance.currentTexturePack = old;
                }
                menu.image = checkForFile(menu.texturepack, config.str(menu.getName()+".frame"));
                if(menu.image==null){
                    menu.texturepack = null;
                }
            }
        }
        isStarted = true;
    }
    private static void onStaticClick(Menu menuClicked){
        staticClicks.add(menuClicked);
    }
    private static void onNumberClick(Menu menuClicked){
        String text = JOptionPane.showInputDialog(null, menuClicked.animationInfoClassName, menuClicked.frameCountVariableName, JOptionPane.QUESTION_MESSAGE);
        int number = 0;
        try{
            number = Integer.parseInt(text);
        }catch(NumberFormatException ex){
            return;
        }
        menuClicked.value = number;
    }
    private static void save(String saveType){
        switch(saveType){
            case "AUTO":
                saveMap(new File(System.getenv("APPDATA")+"\\Dolan Programmers\\City Populization\\modpack.autosave"));
                break;
            case "MANUAL":
                String file = findFileWithExtension(".imp", false);//Incomplete Mod Pack
                if(file!=null){
                    saveMap(new File(file));
                }
                break;
            default:
                throw new IllegalArgumentException("Could not save!");
        }
    }
    private static String findFileWithExtension(final String extension, boolean open){
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileFilter(){
            public boolean accept(File f){
                return f.isDirectory()||f.getName().endsWith(extension);
            }
            public String getDescription(){
                return "*"+extension+" files";
            }
        });
        if(currentDirectory!=null){
            chooser.setCurrentDirectory(currentDirectory);
        }
        if(open){
            chooser.showOpenDialog(null);
        }else{
            chooser.showSaveDialog(null);
        }
        File file = chooser.getSelectedFile();
        if(file!=null&&(file.exists()||!open)&&(file.isFile()||!open)&&file.getAbsolutePath().endsWith(extension)){
            currentDirectory = file.getParentFile();
            return file.getAbsolutePath();
        }else if(file!=null&&(file.exists()||!open)&&(file.isFile()||!open)&&!file.getAbsolutePath().endsWith(extension)){
            currentDirectory = file.getParentFile();
            return file.getAbsolutePath()+extension;
        }else{
            return null;
        }
    }
    private static void saveMap(File file){
        file.delete();
        Config config = Config.loadConfig(file);
        ArrayList<Menu> menusToSave = new ArrayList<>();
        for(Menu menu : menus){
            menusToSave.add(menu);
        }
        while(!menusToSave.isEmpty()){
            Menu menu = menusToSave.remove(0);
            if(menu.type==2){
                if(menu.image!=null){
                    config.putProperty(menu.getName()+".image", menu.image);
                    if(menu.texturepack!=null){
                        config.putProperty(menu.getName()+".texturepack", menu.texturepack.name());
                    }
                }
            }else if(menu.type==3){
                config.putProperty(menu.getName()+".frames", menu.children.size()-6+"");
                for(Menu child : menu.children){
                    menusToSave.add(child);
                }
            }else if(menu.type==4){
                if(menu.image!=null){
                    config.putProperty(menu.getName()+".frame", menu.image);
                    if(menu.texturepack!=null){
                        config.putProperty(menu.getName()+".texturepack", menu.texturepack.name());
                    }
                }
            }else if(menu.type==1){
                for(Menu child : menu.children){
                    menusToSave.add(child);
                }
            }else if(menu.type==5||menu.type==8){
            }else if(menu.type==6||menu.type==7){
                config.putProperty(menu.getName()+".levels", menu.children.size()-4+"");
                for(Menu child : menu.children){
                    menusToSave.add(child);
                }
            }else if(menu.type==9||menu.type==10||menu.type==11){
                for(Menu child : menu.children){
                    menusToSave.add(child);
                }
            }else if(menu.type==12){
                config.putProperty(menu.getName()+".value", menu.value+"");
            }else{
                throw new UnsupportedOperationException("Unknown type- "+menu.type);
            }
        }
        config.save();
    }
    private static boolean generateTexture(Menu click, String filepath) throws IOException{
        if(filepath==null){
            return false;
        }
        File file = new File(filepath);
        if(file.exists()){
            return false;
        }
        file.getParentFile().mkdirs();
        BufferedImage image = new BufferedImage(imageSpecs[0], imageSpecs[1], 6);
        ImageIO.write(image, "png", new FileOutputStream(file));
        click.parent.image = filepath;
        click.parent.texturepack = null;
        return true;
    }
    private static void saveTexturepack() throws IOException{
        for(int i = 0; i<5; i++){
            if(!menus.get(i).completed){
                int[] count = countTextures();
                if(JOptionPane.showConfirmDialog(null, "Incomplete modpack detected:\n"
                                                       + "Total Plots: "+count[0]+"\n"
                                                       + "Total images: "+count[1]+"\n"
                                                       + "Total animations: "+count[2]+"\n"
                                                       + "Unfinished Plots: "+count[3]+"\n"
                                                       + "Unfinished images: "+count[4]+"\n"
                                                       + "Unfinished animations: "+count[5]+"\n"
                                                       + "Unfinished animation frames: "+count[6]+"\n"
                                                       + "Do you wish to export anyways?", "Incomplete Export", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE)!=JOptionPane.YES_OPTION){
                    return;
                }else{
                    break;
                }
            }
        }
        String filepath = findFileWithExtension(".zip", false);
        if(filepath==null){
            return;
        }
        File file = new File(filepath);
        String name = JOptionPane.showInputDialog(null, "What is this modpack's name?", "Modpack Name", JOptionPane.QUESTION_MESSAGE);
        if(name==null||name.isEmpty()){
            return;
        }
        file.getParentFile().mkdirs();
        ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(file));
        ZipEntry entry = new ZipEntry("texturepack.properties");
        zipout.putNextEntry(entry);
        zipout.write("1".getBytes());//<editor-fold defaultstate="collapsed">
        ArrayList<Menu> menusToScan = new ArrayList<>(menus);
        while(!menusToScan.isEmpty()){
            Menu menu = menusToScan.remove(0);
            if(menu.type==3){
                zipout.write((menu.key+".frames="+(menu.children.size()-6)+"\n").getBytes());
                zipout.write((menu.key+".tpf="+menu.tpf+"\n").getBytes());
                System.out.println("Animation at "+menu.getName()+" ("+menu.key+")");
            }else{
                menusToScan.addAll(menu.children);
            }
        }
        zipout.closeEntry();//</editor-fold>
        ArrayList<Menu> lst = new ArrayList<>(menus);
        byte[] data = new byte[1024];
        int completed = 0;
        while(!lst.isEmpty()){
            Menu menu = lst.remove(0);
            if((menu.type==2||menu.type==4)&&menu.image!=null){
                InputStream in = menu.texturepack==null?new FileInputStream(new File(menu.image)):menu.texturepack.getResourceAsStream(menu.image);
                if(in!=null){
                    entry = new ZipEntry(menu.location.substring(1));
                    zipout.putNextEntry(entry);
                    while(true){
                        int read = in.read(data);
                        if(read<0){
                            break;
                        }
                        zipout.write(data, 0, read);
                    }
                    in.close();
                    zipout.closeEntry();
                }
            }else if(menu.children.size()>0){
                lst.addAll(menu.children);
            }
            completed++;
            System.out.println("Completed "+completed+" of "+(completed+lst.size()));
        }
        InputStream in = TexturePackHandler.class.getResourceAsStream("/CityPopulization/CharactersLWJGL/character sizes");
        entry = new ZipEntry("CityPopulization/CharactersLWJGL/character sizes");
        zipout.putNextEntry(entry);
        while(true){
            int read = in.read(data);
            if(read<0){
                break;
            }
            zipout.write(data, 0, read);
        }
        in.close();
        zipout.closeEntry();
        entry = new ZipEntry("texturepack.info");
        zipout.putNextEntry(entry);
        zipout.write(name.getBytes());
        zipout.closeEntry();
        zipout.close();
        JOptionPane.showMessageDialog(null, "Export completed!", "Export Complete", JOptionPane.INFORMATION_MESSAGE);
    }
    private static String checkForFile(TexturePack texturepack, String str){
        if(texturepack==null&&new File(str).exists()){
            return str;
        }else if(texturepack!=null){
            InputStream input = texturepack.getResourceAsStream(str);
            if(input==null){
                return null;
            }else{
                try{
                    input.close();
                }catch(IOException ex){
                    throw new RuntimeException(ex);
                }
                return str;
            }
        }else{
            return null;
        }
    }
    private static void importModInfo(Menu importMenu, String title){
        importMenu = importMenu.parent;
        TexturePack oldPack = main.modpackManager.currentTexturePack;
        main.modpackManager.setTexturePack(title);
        TexturePack texturepack = main.modpackManager.currentTexturePack;
        configureModpack();
        ArrayList<Menu> menusToImport = new ArrayList<>();
        if(importMenu==null){
            menusToImport.addAll(menus);
        }else{
            menusToImport.add(importMenu);
        }
        while(!menusToImport.isEmpty()){
            Menu menu = menusToImport.remove(0);
            if(menu.type==1){
                menusToImport.addAll(menu.children);
            }else if(menu.type==2){
                String image = checkForFile(texturepack, menu.location);
                if(image!=null){
                    menu.image = image;
                    menu.texturepack = texturepack;
                }
            }else if(menu.type==3){
                try{
                    Class<?> theClass = Class.forName(menu.animationInfoClassName);
                    Field frameCountVariable = theClass.getDeclaredField(menu.frameCountVariableName);
                    Field TPFVariable = theClass.getDeclaredField(menu.TPFVariableName);
                    Object frameCountObj = frameCountVariable.get(menu.variableObject);
                    Object TPFObj = TPFVariable.get(menu.variableObject);
                    int indexInArrays = menu.indexInArrays;
                    int secondIndexInArrays = menu.secondIndexInArrays;
                    if(indexInArrays>=0&&secondIndexInArrays>=0){
                        Object[] frameCountArray = (Object[])frameCountObj;
                        Object[] TPFArray = (Object[])TPFObj;
                        if(indexInArrays<frameCountArray.length&&indexInArrays<TPFArray.length){
                            frameCountObj = frameCountArray[indexInArrays];
                            TPFObj = TPFArray[indexInArrays];
                            int[] frameCountArrayI = (int[])frameCountObj;
                            int[] TPFArrayI = (int[])TPFObj;
                            if(secondIndexInArrays<frameCountArrayI.length&&secondIndexInArrays<TPFArrayI.length){
                                frameCountObj = frameCountArrayI[secondIndexInArrays];
                                TPFObj = TPFArrayI[secondIndexInArrays];
                            }
                        }
                    }else if(indexInArrays>=0){
                        int[] frameCountArray = (int[])frameCountObj;
                        int[] TPFArray = (int[])TPFObj;
                        if(indexInArrays<frameCountArray.length&&indexInArrays<TPFArray.length){
                            frameCountObj = frameCountArray[indexInArrays];
                            TPFObj = TPFArray[indexInArrays];
                        }
                    }
                    int frameCount = (int)frameCountObj;
                    int TPF = (int)TPFObj;
                    menu.setFrameCount(frameCount);
                    menu.tpf = TPF;
                    menusToImport.addAll(menu.children);
                }catch(ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ClassCastException | NullPointerException ex){
                    Sys.error(ErrorLevel.warning, "Could not import "+menu.getName(), ex, ErrorCategory.other);
                }
            }else if(menu.type==4){
                String image = checkForFile(texturepack, menu.location);
                if(image!=null){
                    menu.image = image;
                    menu.texturepack = texturepack;
                }
            }
        }
        main.modpackManager.currentTexturePack = oldPack;
        configureModpack();
        JOptionPane.showMessageDialog(null, "Import complete!", "Import Complete", JOptionPane.INFORMATION_MESSAGE);
    }
    private static void importTextures(Menu importMenu, String title){
        importMenu = importMenu.parent;
        TexturePack oldPack = TexturePackManager.instance.currentTexturePack;
        TexturePackManager.instance.setTexturePack(title);
        TexturePack texturepack = TexturePackManager.instance.currentTexturePack;
        configureTexturePack();
        ArrayList<Menu> menusToImport = new ArrayList<>();
        if(importMenu==null){
            menusToImport.addAll(menus);
        }else{
            menusToImport.add(importMenu);
        }
        while(!menusToImport.isEmpty()){
            Menu menu = menusToImport.remove(0);
            if(menu.type==2){
                String image = checkForFile(texturepack, menu.location);
                if(image!=null){
                    menu.image = image;
                    menu.texturepack = texturepack;
                }
            }else if(menu.type==3){
                try{
                    Class<?> theClass = Class.forName(menu.animationInfoClassName);
                    Field frameCountVariable = theClass.getDeclaredField(menu.frameCountVariableName);
                    Field TPFVariable = theClass.getDeclaredField(menu.TPFVariableName);
                    Object frameCountObj = frameCountVariable.get(menu.variableObject);
                    Object TPFObj = TPFVariable.get(menu.variableObject);
                    int indexInArrays = menu.indexInArrays;
                    int secondIndexInArrays = menu.secondIndexInArrays;
                    if(indexInArrays>=0&&secondIndexInArrays>=0){
                        Object[] frameCountArray = (Object[])frameCountObj;
                        Object[] TPFArray = (Object[])TPFObj;
                        if(indexInArrays<frameCountArray.length&&indexInArrays<TPFArray.length){
                            frameCountObj = frameCountArray[indexInArrays];
                            TPFObj = TPFArray[indexInArrays];
                            int[] frameCountArrayI = (int[])frameCountObj;
                            int[] TPFArrayI = (int[])TPFObj;
                            if(secondIndexInArrays<frameCountArrayI.length&&secondIndexInArrays<TPFArrayI.length){
                                frameCountObj = frameCountArrayI[secondIndexInArrays];
                                TPFObj = TPFArrayI[secondIndexInArrays];
                            }
                        }
                    }else if(indexInArrays>=0){
                        int[] frameCountArray = (int[])frameCountObj;
                        int[] TPFArray = (int[])TPFObj;
                        if(indexInArrays<frameCountArray.length&&indexInArrays<TPFArray.length){
                            frameCountObj = frameCountArray[indexInArrays];
                            TPFObj = TPFArray[indexInArrays];
                        }
                    }
                    int frameCount = (int)frameCountObj;
                    int TPF = (int)TPFObj;
                    menu.setFrameCount(frameCount);
                    menu.tpf = TPF;
                    menusToImport.addAll(menu.children);
                }catch(ClassNotFoundException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException | ClassCastException | NullPointerException ex){
                    Sys.error(ErrorLevel.warning, "Could not import "+menu.getName(), ex, ErrorCategory.other);
                }
            }else if(menu.type==4){
                String image = checkForFile(texturepack, menu.location);
                if(image!=null){
                    menu.image = image;
                    menu.texturepack = texturepack;
                }
            }
            menusToImport.addAll(menu.children);
        }
        TexturePackManager.instance.currentTexturePack = oldPack;
        configureTexturePack();
        JOptionPane.showMessageDialog(null, "Import complete!", "Import Complete", JOptionPane.INFORMATION_MESSAGE);
    }
    private static int findComponent(InputStream stream, Config config, TexturePack texturepack, String key){
        if(config.hasProperty(key)){
            return Integer.parseInt(config.str(key));
        }else if(stream!=null){
            System.err.println("Could not find "+key+" component in texture pack "+texturepack.name());
        }
        return 1;
    }
    private static void configureTexturePack(){
        TexturePack texturepack = TexturePackManager.instance.currentTexturePack;
        InputStream stream = texturepack.getResourceAsStream("/texturepack.properties");
        Config config = Config.loadConfig(stream);
        if(stream==null){
            System.err.println("Could not find texturepack properties file!");
        }
        for(int i = 0; i<main.workerFrames.length; i++){
            int whichName = (i-(i%4))/4;
            int whichDirection = i%4;
            String name = whichName==0?"worker":(whichName==1?"dirtDigger":(whichName==2?"oilDriller":(whichName==3?"coalMiner":(whichName==4?"stoneMiner":(whichName==5?"ironMiner":(whichName==6?"woodcutter":(whichName==7?"sandDigger":(whichName==8?"clayDigger":(whichName==9?"goldMiner":"Nothing!")))))))));
            String direction = whichDirection==1?"Up":(whichDirection==2?"Right":(whichDirection==3?"Down":"Left"));
            main.workerFrames[i] = findComponent(stream, config, texturepack, name+direction+".Frames");
            main.workerTPF[i] = findComponent(stream, config, texturepack, name+direction+".TPF");
        }
        String[] tags = new String[]{"civillianUp", "civillianRight", "civillianDown", "civillianLeft", "angry", "dead", "deadZombie", "zombieUp", "zombieRight", "zombieDown", "zombieLeft"};
        for(int i = 0; i<main.civillianFrames.length; i++){
            main.civillianFrames[i] = findComponent(stream, config, texturepack, tags[i]+".Frames");
            main.civillianTPF[i] = findComponent(stream, config, texturepack, tags[i]+".TPF");
        }
        main.constructionFrames = findComponent(stream, config, texturepack, "constructionFrames");
        main.constructionTPF = findComponent(stream, config, texturepack, "constructionTPF");
        main.damageFrames = findComponent(stream, config, texturepack, "damageFrames");
        main.damageTPF = findComponent(stream, config, texturepack, "damageTPF");
        for(PlotType type : PlotType.values()){
            if(!type.isFarm()){
                for(int i = 0; i<type.levels; i++){
                    int level = i+1;
                    type.frames[level-1][0] = findComponent(stream, config, texturepack, type+" level "+level+".frames");
                    type.tpf[level-1][0] = findComponent(stream, config, texturepack, type+" level "+level+".tpf");
                }
            }else{
                for(int i = 0; i<type.levels; i++){
                    int level = i+1;
                    type.frames[level-1][0] = findComponent(stream, config, texturepack, type+" (Idle) level "+level+".frames");
                    type.tpf[level-1][0] = findComponent(stream, config, texturepack, type+" (Idle) level "+level+".tpf");
                }
                for(int i = 0; i<type.levels; i++){
                    int level = i+1;
                    type.frames[level-1][0] = findComponent(stream, config, texturepack, type+" (Pending Harvesting) level "+level+".frames");
                    type.tpf[level-1][0] = findComponent(stream, config, texturepack, type+" (Pending Harvesting) level "+level+".tpf");
                }
                for(int i = 0; i<type.levels; i++){
                    int level = i+1;
                    type.frames[level-1][0] = findComponent(stream, config, texturepack, type+" (Harvesting) level "+level+".frames");
                    type.tpf[level-1][0] = findComponent(stream, config, texturepack, type+" (Harvesting) level "+level+".tpf");
                }
            }
        }
    }
    private static void configureModpack(){
        TexturePack texturepack = main.modpackManager.currentTexturePack;
        InputStream stream = texturepack.getResourceAsStream("/modpack.properties");
        Config config = Config.loadConfig(stream);
        if(stream==null){
            System.err.println("Could not find modpack properties file!");
        }
        for(int i = 0; i<main.workerFrames.length; i++){
            int whichName = (i-(i%4))/4;
            int whichDirection = i%4;
            String name = whichName==0?"worker":(whichName==1?"dirtDigger":(whichName==2?"oilDriller":(whichName==3?"coalMiner":(whichName==4?"stoneMiner":(whichName==5?"ironMiner":(whichName==6?"woodcutter":(whichName==7?"sandDigger":(whichName==8?"clayDigger":(whichName==9?"goldMiner":"Nothing!")))))))));
            String direction = whichDirection==1?"Up":(whichDirection==2?"Right":(whichDirection==3?"Down":"Left"));
            main.workerFrames[i] = findComponent(stream, config, texturepack, name+direction+".Frames");
            main.workerTPF[i] = findComponent(stream, config, texturepack, name+direction+".TPF");
        }
        String[] tags = new String[]{"civillianUp", "civillianRight", "civillianDown", "civillianLeft", "angry", "dead", "deadZombie", "zombieUp", "zombieRight", "zombieDown", "zombieLeft"};
        for(int i = 0; i<main.civillianFrames.length; i++){
            main.civillianFrames[i] = findComponent(stream, config, texturepack, tags[i]+".Frames");
            main.civillianTPF[i] = findComponent(stream, config, texturepack, tags[i]+".TPF");
        }
        main.constructionFrames = findComponent(stream, config, texturepack, "constructionFrames");
        main.constructionTPF = findComponent(stream, config, texturepack, "constructionTPF");
        main.damageFrames = findComponent(stream, config, texturepack, "damageFrames");
        main.damageTPF = findComponent(stream, config, texturepack, "damageTPF");
        for(PlotType type : PlotType.values()){
            if(!type.isFarm()){
                for(int i = 0; i<type.levels; i++){
                    int level = i+1;
                    type.frames[level-1][0] = findComponent(stream, config, texturepack, type+" level "+level+".frames");
                    type.tpf[level-1][0] = findComponent(stream, config, texturepack, type+" level "+level+".tpf");
                }
            }else{
                for(int i = 0; i<type.levels; i++){
                    int level = i+1;
                    type.frames[level-1][0] = findComponent(stream, config, texturepack, type+" (Idle) level "+level+".frames");
                    type.tpf[level-1][0] = findComponent(stream, config, texturepack, type+" (Idle) level "+level+".tpf");
                }
                for(int i = 0; i<type.levels; i++){
                    int level = i+1;
                    type.frames[level-1][0] = findComponent(stream, config, texturepack, type+" (Pending Harvesting) level "+level+".frames");
                    type.tpf[level-1][0] = findComponent(stream, config, texturepack, type+" (Pending Harvesting) level "+level+".tpf");
                }
                for(int i = 0; i<type.levels; i++){
                    int level = i+1;
                    type.frames[level-1][0] = findComponent(stream, config, texturepack, type+" (Harvesting) level "+level+".frames");
                    type.tpf[level-1][0] = findComponent(stream, config, texturepack, type+" (Harvesting) level "+level+".tpf");
                }
            }
        }
    }
    @Deprecated
    private static int[] countTextures(){
        ArrayList<Menu> theMenus = new ArrayList<>(menus);
        int plots = 0, unfinishedPlots = 0, images = 0, unfinishedImages = 0, animations = 0, unfinishedAnimations = 0, unfinishedAnimationFrames = 0;
        while(!theMenus.isEmpty()){
            Menu menu = theMenus.remove(0);
            if(menu.type==2){
                images++;
                if(!menu.completed){
                    unfinishedImages++;
                }
            }else if(menu.type==3){
                animations++;
                if(!menu.completed){
                    unfinishedAnimations++;
                }
                theMenus.addAll(menu.children);
            }else if(menu.type==4&&!menu.completed){
                unfinishedAnimationFrames++;
            }else{
                theMenus.addAll(menu.children);
            }
        }
        return new int[]{plots, images, animations, unfinishedPlots, unfinishedImages, unfinishedAnimations, unfinishedAnimationFrames};
    }
    static{
        config = Config.loadConfig(new File(System.getenv("APPDATA")+"\\Dolan Programmers\\City Populization\\Texturepack Handler.config"));
        Menu menu = new Menu("Font Textures", 1);//<editor-fold defaultstate="collapsed">
        menus.add(menu);
        Menu menuChild = menu.add(new Menu("Numbers", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Zero", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_0.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("One", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_1.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Two", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_2.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Three", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_3.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Four", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_4.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Five", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_5.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Six", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_6.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Seven", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_7.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Eight", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_8.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Nine", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_9.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChild = menu.add(new Menu("Letters", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("A", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_A.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("B", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_B.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("C", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_C.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("D", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_D.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("E", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_E.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("F", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_F.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("G", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_G.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("H", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_H.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("I", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_I.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("J", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_J.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("K", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_K.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("L", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_L.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("M", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_M.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("N", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_N.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("O", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_O.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("P", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_P.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Q", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_Q.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("R", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_R.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("S", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_S.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("T", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_T.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("U", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_U.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("V", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_V.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("W", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_W.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("X", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_X.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Y", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_Y.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Z", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_Z.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChild = menu.add(new Menu("Symbols", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Comma", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_COMMA.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Dollar sign", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_DOLLAR_SIGN.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Equals", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_EQUALS.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Minus", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_MINUS.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Question mark", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_QUESTION_MARK.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Semicolon", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_SEMICOLON.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Period", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_PERIOD.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Apostrophe", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_APOSTROPHE.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Space", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_ .png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Exclamation Point", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_EXCLAMATION_POINT.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Colon", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_COLON.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Left Perenthesis", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_LEFT_PARENTHESIS.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Right Perenthesis", 2, 10, 20, "/CityPopulization/CharactersLWJGL/character_RIGHT_PARENTHESIS.png", null, null, null, -1, -1, null, null));//</editor-fold>
        //</editor-fold>
        menu = new Menu("GUI Textures", 1);//<editor-fold defaultstate="collapsed">
        menus.add(menu);
        menu.add(new Menu("Ingame Background", 2, 1600, 1000, "/gui/ingame/background.png", null, null, null, -1, -1, null, null));
        menuChild = menu.add(new Menu("Buttons", 1));//<editor-fold defaultstate="collapsed">
        Menu menuChildChild = menuChild.add(new Menu("Empty Plot", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild.add(new Menu("Highway", 2, 80, 80, "/gui/buttons/Empty/Highway.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("House", 2, 80, 80, "/gui/buttons/Empty/House.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Workshop", 2, 80, 80, "/gui/buttons/Empty/Workshop.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Dirt Mine", 2, 80, 80, "/gui/buttons/Empty/Dirt Mine.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Coal Mine", 2, 80, 80, "/gui/buttons/Empty/Coal Mine.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Oil Well", 2, 80, 80, "/gui/buttons/Empty/Oil Well.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Forest", 2, 80, 80, "/gui/buttons/Empty/Forest.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Stone Mine", 2, 80, 80, "/gui/buttons/Empty/Stone Mine.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Iron Mine", 2, 80, 80, "/gui/buttons/Empty/Iron Mine.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Sand Pit", 2, 80, 80, "/gui/buttons/Empty/Sand Pit.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Clay Pit", 2, 80, 80, "/gui/buttons/Empty/Clay Pit.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Gold Mine", 2, 80, 80, "/gui/buttons/Empty/Gold Mine.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Shopping Mall", 2, 80, 80, "/gui/buttons/Empty/Shopping Mall.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Restaurant", 2, 80, 80, "/gui/buttons/Empty/Resturaunt.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Park", 2, 80, 80, "/gui/buttons/Empty/Park.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Amusement Park", 2, 80, 80, "/gui/buttons/Empty/Amusement Park.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Hospital", 2, 80, 80, "/gui/buttons/Empty/Hospital.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Department Store", 2, 80, 80, "/gui/buttons/Empty/Department Store.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Fire Department", 2, 80, 80, "/gui/buttons/Empty/Fire Department.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Police Department", 2, 80, 80, "/gui/buttons/Empty/Police Department.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Military Base", 2, 80, 80, "/gui/buttons/Empty/Military Base.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("School", 2, 80, 80, "/gui/buttons/Empty/School.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Warehouse", 2, 80, 80, "/gui/buttons/Empty/Warehouse.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Cancel", 2, 80, 80, "/gui/buttons/Empty/cancel.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChildChild = menuChild.add(new Menu("Main Base", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild.add(new Menu("Upgrade", 2, 80, 80, "/gui/buttons/Main Base/upgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Downgrade", 2, 80, 80, "/gui/buttons/Main Base/downgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Cancel", 2, 80, 80, "/gui/buttons/Main Base/cancel.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChildChild = menuChild.add(new Menu("Highway", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild.add(new Menu("Upgrade", 2, 80, 80, "/gui/buttons/Highway/upgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Downgrade", 2, 80, 80, "/gui/buttons/Highway/downgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Destroy", 2, 80, 80, "/gui/buttons/Highway/destroy.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Cancel", 2, 80, 80, "/gui/buttons/Highway/cancel.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChildChild = menuChild.add(new Menu("House", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild.add(new Menu("Upgrade", 2, 80, 80, "/gui/buttons/House/upgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Downgrade", 2, 80, 80, "/gui/buttons/House/downgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Add Worker", 2, 80, 80, "/gui/buttons/House/add worker.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Remove Worker", 2, 80, 80, "/gui/buttons/House/remove worker.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Destroy", 2, 80, 80, "/gui/buttons/House/destroy.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Cancel", 2, 80, 80, "/gui/buttons/House/cancel.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChildChild = menuChild.add(new Menu("Workshop", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild.add(new Menu("Upgrade", 2, 80, 80, "/gui/buttons/Workshop/upgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Downgrade", 2, 80, 80, "/gui/buttons/Workshop/downgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Destroy", 2, 80, 80, "/gui/buttons/Workshop/destroy.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Cancel", 2, 80, 80, "/gui/buttons/Workshop/cancel.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChildChild = menuChild.add(new Menu("Dirt Mine", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild.add(new Menu("Upgrade", 2, 80, 80, "/gui/buttons/Dirt Mine/upgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Downgrade", 2, 80, 80, "/gui/buttons/Dirt Mine/downgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Start Harvesting", 2, 80, 80, "/gui/buttons/Dirt Mine/startHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Stop Harvesting", 2, 80, 80, "/gui/buttons/Dirt Mine/stopHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Cancel", 2, 80, 80, "/gui/buttons/Dirt Mine/cancel.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Destroy", 2, 80, 80, "/gui/buttons/Dirt Mine/destroy.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChildChild = menuChild.add(new Menu("Coal Mine", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild.add(new Menu("Upgrade", 2, 80, 80, "/gui/buttons/Coal Mine/upgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Downgrade", 2, 80, 80, "/gui/buttons/Coal Mine/downgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Start Harvesting", 2, 80, 80, "/gui/buttons/Coal Mine/startHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Stop Harvesting", 2, 80, 80, "/gui/buttons/Coal Mine/stopHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Cancel", 2, 80, 80, "/gui/buttons/Coal Mine/cancel.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Destroy", 2, 80, 80, "/gui/buttons/Coal Mine/destroy.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChildChild = menuChild.add(new Menu("Oil Well", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild.add(new Menu("Upgrade", 2, 80, 80, "/gui/buttons/Oil Well/upgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Downgrade", 2, 80, 80, "/gui/buttons/Oil Well/downgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Start Harvesting", 2, 80, 80, "/gui/buttons/Oil Well/startHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Stop Harvesting", 2, 80, 80, "/gui/buttons/Oil Well/stopHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Cancel", 2, 80, 80, "/gui/buttons/Oil Well/cancel.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Destroy", 2, 80, 80, "/gui/buttons/Oil Well/destroy.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChildChild = menuChild.add(new Menu("Forest", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild.add(new Menu("Upgrade", 2, 80, 80, "/gui/buttons/Forest/upgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Downgrade", 2, 80, 80, "/gui/buttons/Forest/downgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Start Harvesting", 2, 80, 80, "/gui/buttons/Forest/startHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Stop Harvesting", 2, 80, 80, "/gui/buttons/Forest/stopHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Cancel", 2, 80, 80, "/gui/buttons/Forest/cancel.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Destroy", 2, 80, 80, "/gui/buttons/Forest/destroy.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChildChild = menuChild.add(new Menu("Quarry", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild.add(new Menu("Upgrade", 2, 80, 80, "/gui/buttons/Quarry/upgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Downgrade", 2, 80, 80, "/gui/buttons/Quarry/downgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Start Harvesting", 2, 80, 80, "/gui/buttons/Quarry/startHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Stop Harvesting", 2, 80, 80, "/gui/buttons/Quarry/stopHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Cancel", 2, 80, 80, "/gui/buttons/Quarry/cancel.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Destroy", 2, 80, 80, "/gui/buttons/Quarry/destroy.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChildChild = menuChild.add(new Menu("Iron Mine", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild.add(new Menu("Upgrade", 2, 80, 80, "/gui/buttons/Iron Mine/upgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Downgrade", 2, 80, 80, "/gui/buttons/Iron Mine/downgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Start Harvesting", 2, 80, 80, "/gui/buttons/Iron Mine/startHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Stop Harvesting", 2, 80, 80, "/gui/buttons/Iron Mine/stopHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Cancel", 2, 80, 80, "/gui/buttons/Iron Mine/cancel.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Destroy", 2, 80, 80, "/gui/buttons/Iron Mine/destroy.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChildChild = menuChild.add(new Menu("Sand Pit", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild.add(new Menu("Upgrade", 2, 80, 80, "/gui/buttons/Sand Pit/upgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Downgrade", 2, 80, 80, "/gui/buttons/Sand Pit/downgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Start Harvesting", 2, 80, 80, "/gui/buttons/Sand Pit/startHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Stop Harvesting", 2, 80, 80, "/gui/buttons/Sand Pit/stopHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Cancel", 2, 80, 80, "/gui/buttons/Sand Pit/cancel.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Destroy", 2, 80, 80, "/gui/buttons/Sand Pit/destroy.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChildChild = menuChild.add(new Menu("Clay Pit", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild.add(new Menu("Upgrade", 2, 80, 80, "/gui/buttons/Clay Pit/upgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Downgrade", 2, 80, 80, "/gui/buttons/Clay Pit/downgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Start Harvesting", 2, 80, 80, "/gui/buttons/Clay Pit/startHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Stop Harvesting", 2, 80, 80, "/gui/buttons/Clay Pit/stopHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Cancel", 2, 80, 80, "/gui/buttons/Clay Pit/cancel.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Destroy", 2, 80, 80, "/gui/buttons/Clay Pit/destroy.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChildChild = menuChild.add(new Menu("Gold Mine", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild.add(new Menu("Upgrade", 2, 80, 80, "/gui/buttons/Gold Mine/upgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Downgrade", 2, 80, 80, "/gui/buttons/Gold Mine/downgrade.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Start Harvesting", 2, 80, 80, "/gui/buttons/Gold Mine/startHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Stop Harvesting", 2, 80, 80, "/gui/buttons/Gold Mine/stopHarvesting.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Cancel", 2, 80, 80, "/gui/buttons/Gold Mine/cancel.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Destroy", 2, 80, 80, "/gui/buttons/Gold Mine/destroy.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChildChild = menuChild.add(new Menu("Shopping Mall", 1));
        menuChildChild = menuChild.add(new Menu("Restaurant", 1));
        menuChildChild = menuChild.add(new Menu("Park", 1));
        menuChildChild = menuChild.add(new Menu("Amusement Park", 1));
        menuChildChild = menuChild.add(new Menu("Hospital", 1));
        menuChildChild = menuChild.add(new Menu("Department Store", 1));
        menuChildChild = menuChild.add(new Menu("Fire Department", 1));
        menuChildChild = menuChild.add(new Menu("Police Department", 1));
        menuChildChild = menuChild.add(new Menu("Military Base", 1));
        menuChildChild = menuChild.add(new Menu("School", 1));
        menuChildChild = menuChild.add(new Menu("Warehouse", 1));
        menuChildChild = menuChild.add(new Menu("Zombie Land", 1));
        menuChild.add(new Menu("Previous Page", 2, 80, 80, "/gui/buttons/Previous Page.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Next Page", 2, 80, 80, "/gui/buttons/Next Page.png", null, null, null, -1, -1, null, null));//</editor-fold>
        //</editor-fold>
        menu = new Menu("Plot Textures", 1);//<editor-fold defaultstate="collapsed">
        menus.add(menu);
        for(PlotType type : PlotType.values()){
            if(!type.isFarm()){
                menu.add(new Menu(type.name, 6));
            }else{
                menu.add(new Menu(type.name, 7));
            }
        }//</editor-fold>
        menu = new Menu("Miscelaneous Textures", 1);//<editor-fold defaultstate="collapsed">
        menus.add(menu);
        menuChild = menu.add(new Menu("Civillians", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Facing Up", 3, 30, 30, "/Plots/civillian/Up<FRAME>.png", "CityPopulization.main", "civillianFrames", "civillianTPF", 0, -1, null, "civillianUp"));
        menuChild.add(new Menu("Facing Right", 3, 30, 30, "/Plots/civillian/Right<FRAME>.png", "CityPopulization.main", "civillianFrames", "civillianTPF", 1, -1, null, "civillianRight"));
        menuChild.add(new Menu("Facing Down", 3, 30, 30, "/Plots/civillian/Down<FRAME>.png", "CityPopulization.main", "civillianFrames", "civillianTPF", 2, -1, null, "civillianDown"));
        menuChild.add(new Menu("Facing Left", 3, 30, 30, "/Plots/civillian/Left<FRAME>.png", "CityPopulization.main", "civillianFrames", "civillianTPF", 3, -1, null, "civillianLeft"));
        menuChild.add(new Menu("Angry", 3, 30, 30, "/Plots/civillian/Angry<FRAME>.png", "CityPopulization.main", "civillianFrames", "civillianTPF", 4, -1, null, "angry"));
        menuChild.add(new Menu("Dead", 3, 30, 30, "/Plots/civillian/Dead<FRAME>.png", "CityPopulization.main", "civillianFrames", "civillianTPF", 5, -1, null, "dead"));//</editor-fold>
        menuChild = menu.add(new Menu("Zombies", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Dead", 3, 30, 30, "/Plots/zombie/Dead<FRAME>.png", "CityPopulization.main", "civillianFrames", "civillianTPF", 6, -1, null, "deadZombie"));
        menuChild.add(new Menu("Facing Up", 3, 30, 30, "/Plots/zombie/Up<FRAME>.png", "CityPopulization.main", "civillianFrames", "civillianTPF", 7, -1, null, "zombieUp"));
        menuChild.add(new Menu("Facing Right", 3, 30, 30, "/Plots/zombie/Right<FRAME>.png", "CityPopulization.main", "civillianFrames", "civillianTPF", 8, -1, null, "zombieRight"));
        menuChild.add(new Menu("Facing Down", 3, 30, 30, "/Plots/zombie/Down<FRAME>.png", "CityPopulization.main", "civillianFrames", "civillianTPF", 9, -1, null, "zombieDown"));
        menuChild.add(new Menu("Facing Left", 3, 30, 30, "/Plots/zombie/Left<FRAME>.png", "CityPopulization.main", "civillianFrames", "civillianTPF", 10, -1, null, "zombieLeft"));//</editor-fold>
        menuChild = menu.add(new Menu("Workers", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Facing Up", 3, 30, 30, "/Plots/worker/Up<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 0, -1, null, "workerUp"));
        menuChild.add(new Menu("Facing Right", 3, 30, 30, "/Plots/worker/Right<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 1, -1, null, "workerRight"));
        menuChild.add(new Menu("Facing Down", 3, 30, 30, "/Plots/worker/Down<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 2, -1, null, "workerDown"));
        menuChild.add(new Menu("Facing Left", 3, 30, 30, "/Plots/worker/Left<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 3, -1, null, "workerLeft"));//</editor-fold>
        menuChild = menu.add(new Menu("Dirt Diggers", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Facing Up", 3, 30, 30, "/Plots/worker/dirtDiggerUp<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 4, -1, null, "dirtDiggerUp"));
        menuChild.add(new Menu("Facing Right", 3, 30, 30, "/Plots/worker/dirtDiggerRight<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 5, -1, null, "dirtDiggerRight"));
        menuChild.add(new Menu("Facing Down", 3, 30, 30, "/Plots/worker/dirtDiggerDown<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 6, -1, null, "dirtDiggerDown"));
        menuChild.add(new Menu("Facing Left", 3, 30, 30, "/Plots/worker/dirtDiggerLeft<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 7, -1, null, "dirtDiggerLeft"));//</editor-fold>
        menuChild = menu.add(new Menu("Oil Drillers", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Facing Up", 3, 30, 30, "/Plots/worker/oilDrillerUp<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 8, -1, null, "oilDrillerUp"));
        menuChild.add(new Menu("Facing Right", 3, 30, 30, "/Plots/worker/oilDrillerRight<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 9, -1, null, "oilDrillerRight"));
        menuChild.add(new Menu("Facing Down", 3, 30, 30, "/Plots/worker/oilDrillerDown<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 10, -1, null, "oilDrillerDown"));
        menuChild.add(new Menu("Facing Left", 3, 30, 30, "/Plots/worker/oilDrillerLeft<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 11, -1, null, "oilDrillerLeft"));//</editor-fold>
        menuChild = menu.add(new Menu("Coal Miners", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Facing Up", 3, 30, 30, "/Plots/worker/coalMinerUp<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 12, -1, null, "coalMinerUp"));
        menuChild.add(new Menu("Facing Right", 3, 30, 30, "/Plots/worker/coalMinerRight<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 13, -1, null, "coalMinerRight"));
        menuChild.add(new Menu("Facing Down", 3, 30, 30, "/Plots/worker/coalMinerDown<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 14, -1, null, "coalMinerDown"));
        menuChild.add(new Menu("Facing Left", 3, 30, 30, "/Plots/worker/coalMinerLeft<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 15, -1, null, "coalMinerLeft"));//</editor-fold>
        menuChild = menu.add(new Menu("Stone Miners", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Facing Up", 3, 30, 30, "/Plots/worker/stoneMinerUp<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 16, -1, null, "stoneMinerUp"));
        menuChild.add(new Menu("Facing Right", 3, 30, 30, "/Plots/worker/stoneMinerRight<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 17, -1, null, "stoneMinerRight"));
        menuChild.add(new Menu("Facing Down", 3, 30, 30, "/Plots/worker/stoneMinerDown<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 18, -1, null, "stoneMinerDown"));
        menuChild.add(new Menu("Facing Left", 3, 30, 30, "/Plots/worker/stoneMinerLeft<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 19, -1, null, "stoneMinerLeft"));//</editor-fold>
        menuChild = menu.add(new Menu("Iron Miners", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Facing Up", 3, 30, 30, "/Plots/worker/ironMinerUp<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 20, -1, null, "ironMinerUp"));
        menuChild.add(new Menu("Facing Right", 3, 30, 30, "/Plots/worker/ironMinerRight<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 21, -1, null, "ironMinerRight"));
        menuChild.add(new Menu("Facing Down", 3, 30, 30, "/Plots/worker/ironMinerDown<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 22, -1, null, "ironMinerDown"));
        menuChild.add(new Menu("Facing Left", 3, 30, 30, "/Plots/worker/ironMinerLeft<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 23, -1, null, "ironMinerLeft"));//</editor-fold>
        menuChild = menu.add(new Menu("Woodcutters", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Facing Up", 3, 30, 30, "/Plots/worker/woodCutterUp<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 24, -1, null, "woodcutterUp"));
        menuChild.add(new Menu("Facing Right", 3, 30, 30, "/Plots/worker/woodCutterRight<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 25, -1, null, "woodcutterRight"));
        menuChild.add(new Menu("Facing Down", 3, 30, 30, "/Plots/worker/woodCutterDown<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 26, -1, null, "woodcutterDown"));
        menuChild.add(new Menu("Facing Left", 3, 30, 30, "/Plots/worker/woodCutterLeft<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 27, -1, null, "woodcutterLeft"));//</editor-fold>
        menuChild = menu.add(new Menu("Sand Diggers", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Facing Up", 3, 30, 30, "/Plots/worker/sandDiggerUp<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 28, -1, null, "sandDiggerUp"));
        menuChild.add(new Menu("Facing Right", 3, 30, 30, "/Plots/worker/sandDiggerRight<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 29, -1, null, "sandDiggerRight"));
        menuChild.add(new Menu("Facing Down", 3, 30, 30, "/Plots/worker/sandDiggerDown<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 30, -1, null, "sandDiggerDown"));
        menuChild.add(new Menu("Facing Left", 3, 30, 30, "/Plots/worker/sandDiggerLeft<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 31, -1, null, "sandDiggerLeft"));//</editor-fold>
        menuChild = menu.add(new Menu("Clay Diggers", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Facing Up", 3, 30, 30, "/Plots/worker/clayDiggerUp<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 32, -1, null, "clayDiggerUp"));
        menuChild.add(new Menu("Facing Right", 3, 30, 30, "/Plots/worker/clayDiggerRight<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 33, -1, null, "clayDiggerRight"));
        menuChild.add(new Menu("Facing Down", 3, 30, 30, "/Plots/worker/clayDiggerDown<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 34, -1, null, "clayDiggerDown"));
        menuChild.add(new Menu("Facing Left", 3, 30, 30, "/Plots/worker/clayDiggerLeft<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 35, -1, null, "clayDiggerLeft"));//</editor-fold>
        menuChild = menu.add(new Menu("Gold Miners", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Facing Up", 3, 30, 30, "/Plots/worker/goldMinerUp<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 36, -1, null, "goldMinerUp"));
        menuChild.add(new Menu("Facing Right", 3, 30, 30, "/Plots/worker/goldMinerRight<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 37, -1, null, "goldMinerRight"));
        menuChild.add(new Menu("Facing Down", 3, 30, 30, "/Plots/worker/goldMinerDown<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 38, -1, null, "goldMinerDown"));
        menuChild.add(new Menu("Facing Left", 3, 30, 30, "/Plots/worker/goldMinerLeft<FRAME>.png", "CityPopulization.main", "workerFrames", "workerTPF", 39, -1, null, "goldMinerLeft"));//</editor-fold>
        menu.add(new Menu("Construction", 3, 30, 30, "/Plots/construction<FRAME>.png", "CityPopulization.main", "constructionFrames", "constructionTPF", -1, -1, null, "construction"));
        menu.add(new Menu("Damaged", 3, 40, 40, "/Plots/damage<FRAME>.png", "CityPopulization.main", "damageFrames", "damageTPF", -1, -1, null, "damage"));//</editor-fold>
        menu = new Menu("Texturepack Helper Textures", 1);//<editor-fold defaultstate="collapsed">
        menus.add(menu);
        menu.add(new Menu("New Texturepack Button", 2, 400, 40, "/texturepackHelper/mainMenu/new.png", null, null, null, -1, -1, null, null));
        menu.add(new Menu("Load Texturepack Button", 2, 400, 40, "/texturepackHelper/mainMenu/load.png", null, null, null, -1, -1, null, null));
        menu.add(new Menu("Back Button", 2, 400, 40, "/texturepackHelper/mainMenu/back.png", null, null, null, -1, -1, null, null));
        menu.add(new Menu("First Layer Menu Button", 2, 300, 500, "/texturepackHelper/menu1.png", null, null, null, -1, -1, null, null));
        menu.add(new Menu("Second Layer Menu Button", 2, 290, 500, "/texturepackHelper/menu2.png", null, null, null, -1, -1, null, null));
        menu.add(new Menu("Third Layer Menu Button", 2, 280, 500, "/texturepackHelper/menu3.png", null, null, null, -1, -1, null, null));
        menu.add(new Menu("Fourth Layer Menu Button", 2, 270, 500, "/texturepackHelper/menu4.png", null, null, null, -1, -1, null, null));
        menu.add(new Menu("Fifth Layer Menu Button", 2, 260, 500, "/texturepackHelper/menu5.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menu = new Menu("Tutorial Textures", 1);//<editor-fold defaultstate="collapsed">
        menus.add(menu);
        menu.add(new Menu("Dialog Box", 2, 1400, 650, "/textbox_1400_650.png", null, null, null, -1, -1, null, null));
        menuChild = menu.add(new Menu("Plot Pointouts", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Top Left", 2, 50, 50, "/plotPointout/topLeft.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Top", 2, 50, 50, "/plotPointout/top.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Top Right", 2, 50, 50, "/plotPointout/topRight.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Left", 2, 50, 50, "/plotPointout/left.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Right", 2, 50, 50, "/plotPointout/right.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Bottom Left", 2, 50, 50, "/plotPointout/bottomLeft.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Bottom", 2, 50, 50, "/plotPointout/bottom.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Bottom Right", 2, 50, 50, "/plotPointout/bottomRight.png", null, null, null, -1, -1, null, null));//</editor-fold>
        //</editor-fold>
        menu = new Menu("Timer Display Background", 2, 1000, 250, "/textbox_1000_250.png", null, null, null, -1, -1, null, null);//<editor-fold defaultstate="collapsed">
        menus.add(menu);//</editor-fold>
        menu = new Menu("Auto-Tasking System Textures", 1);//<editor-fold defaultstate="collapsed">
        menus.add(menu);
        menu.add(new Menu("Background", 2, 1600, 1000, "/plotList/background.png", null, null, null, -1, -1, null, null));
        menu.add(new Menu("Overlay", 2, 1600, 1000, "/plotList/overlay.png", null, null, null, -1, -1, null, null));
        menu.add(new Menu("Buttons", 2, 400, 35, "/plotList/button.png", null, null, null, -1, -1, null, null));
        menuChild = menu.add(new Menu("List Objects", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Background (Not selected)", 2, 800, 100, "/plotList/listBackground.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Background (Selected)", 2, 800, 100, "/plotList/listBackgroundSelected.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChild = menu.add(new Menu("Plot Overlays", 1));//<editor-fold defaultstate="collapsed">
        menuChild.add(new Menu("Selected Plot", 2, 50, 50, "/Plots/overlay/selected.png", null, null, null, -1, -1, null, null));
        menuChild.add(new Menu("Unselectable Plot", 2, 50, 50, "/Plots/overlay/unselectable.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChild = menu.add(new Menu("Possible plot tasks", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild = menuChild.add(new Menu("List Objects", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild.add(new Menu("Background (Not selected)", 2, 800, 100, "/plotList/possibleTasks/taskBackground.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Background (Selected)", 2, 800, 100, "/plotList/possibleTasks/taskBackgroundSelected.png", null, null, null, -1, -1, null, null));//</editor-fold>
        menuChildChild = menuChild.add(new Menu("Number Selection", 1));//<editor-fold defaultstate="collapsed">
        menuChildChild.add(new Menu("Increase Number button", 2, 800, 40, "/plotList/possibleTasks/spinner/upButton.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Number Background", 2, 800, 40, "/plotList/possibleTasks/spinner/field.png", null, null, null, -1, -1, null, null));
        menuChildChild.add(new Menu("Decrease Number button", 2, 800, 40, "/plotList/possibleTasks/spinner/downButton.png", null, null, null, -1, -1, null, null));//</editor-fold>
        //</editor-fold>
        //</editor-fold>
        menu = new Menu("Import Textures", 5);//<editor-fold defaultstate="collapsed">
        menus.add(menu);//</editor-fold>
        menu = new Menu("Save", 0);
        menus.add(menu);
        menu = new Menu("Export", 0);
        menus.add(menu);
        menu = new Menu("Close", 0);
        menus.add(menu);
    }
    private static class Menu{
        //<editor-fold defaultstate="collapsed" desc="Variables">
        private final int type;
        private final String title;
        private final ArrayList<Menu> children = new ArrayList<>();
        private int collapsedStatus = 1;
        private int currentHeight;
        protected Menu parent;
        private int targetHeight;
        private String image;
        private boolean completed;
        private final int[] specs;
        private int tpf = 1;
        private final String location;
        private TexturePack texturepack;
        private final String animationInfoClassName;
        private final String frameCountVariableName;
        private final String TPFVariableName;
        private final int indexInArrays;
        private final int secondIndexInArrays;
        private final Object variableObject;
        private final String key;
        private int value = -1;
        //</editor-fold>
        /**
         * Type = 0 = Direct operation button
         * Type = 1 = Expandable menu
         * Type = 2 = Static Image obtain
         * Type = 3 = Animation obtain
         * Type = 4 = Animation frame get
         * Type = 5 = Texture import
         * Type = 6 = Plot get
         * Type = 7 = Farm get
         * Type = 8 = Mod info import
         * Type = 9 = Plot level get
         * Type = 10 = Farm level get
         * Type = 11 = Plot Costs Get
         * Type = 12 = Number Get
         */
        private Menu(String title, int type){
            this(title, type, -1, -1, null, null, null, null, -1, -1, null, null);
        }
        private Menu(String title, int type, int x, int y, String location, String animationInfoClassName, String frameCountVariableName, String TPFVariableName, int indexInArrays, int secondIndexInArrays, Object variableObject, String key){
            this.title = title;
            this.type = type;
            this.specs = new int[]{x, y};
            this.location = location;
            this.animationInfoClassName = animationInfoClassName;
            this.frameCountVariableName = frameCountVariableName;
            this.TPFVariableName = TPFVariableName;
            this.indexInArrays = indexInArrays;
            this.secondIndexInArrays = secondIndexInArrays;
            this.variableObject = variableObject;
            this.key = key;
            PlotType atype = null;
            switch(type){
                case 0://static action button
                    break;
                case 1://expandable menu
                    add(new Menu("Import Textures", 5));
                    break;
                case 2://static image get
                    add(new Menu("View Image", 0));
                    add(new Menu("Get Image Specs", 0));
                    add(new Menu("Load Image", 0));
                    add(new Menu("Generate standard image", 0));
                    add(new Menu("Generate high-res image", 0));
                    add(new Menu("Import Textures", 5));
                    add(new Menu("Clear Image", 0));
                    break;
                case 3://animation get
                    add(new Menu("Add a Frame", 0));
                    add(new Menu("Remove a Frame", 0));
                    add(new Menu("Speed up the animation", 0));
                    add(new Menu("Slow down the animation", 0));
                    add(new Menu("Import Textures", 5));
                    add(new Menu("Preview Animation", 0));
                    break;
                case 4://animation frame get
                    add(new Menu("View Frame", 0));
                    add(new Menu("Get Animation Specs", 0));
                    add(new Menu("Load Frame", 0));
                    add(new Menu("Generate Blank Frame", 0));
                    add(new Menu("Import Textures", 5));
                    add(new Menu("Clear Frame", 0));
                    break;
                case 5://Import textures
                    for(String name : TexturePackManager.instance.listTexturePacks()){
                        add(new Menu(name, 0));
                    }
                    break;
                case 6://Plot get
                    add(new Menu("Import Mod Info", 8));
                    add(new Menu("Import Textures", 5));
                    add(new Menu("Add Level", 0));
                    add(new Menu("Remove Level", 0));
                    break;
                case 7://Farm get
                    add(new Menu("Import Mod Info", 8));
                    add(new Menu("Import Textures", 5));
                    add(new Menu("Add Level", 0));
                    add(new Menu("Remove Level", 0));
                    break;
                case 8://Import mod info
                    for(String name : main.modpackManager.listTexturePacks()){
                        add(new Menu(name, 0));
                    }
                    break;
                case 9://Plot level get
                    for(PlotType btype : PlotType.values()){
                        if(btype.name.equals(location)){
                            atype = btype;
                        }
                    }
                    add(new Menu("Import Mod Info", 8));
                    add(new Menu("Import Textures", 5));
                    add(new Menu("Texture", 3, 50, 50, "/Plots/"+atype.name()+"/Level "+title.substring(6)+"_<FRAME>.png", "CityPopulization.PlotType", "frames", "tpf", Integer.parseInt(title.substring(6))-1, 0, atype, atype+" level "+title.substring(6)));
                    add(new Menu("Costs", 11, 0, 0, atype.name, title, null, null, -1, -1, null, null));
                    break;
                case 10://Farm level get
                    for(PlotType btype : PlotType.values()){
                        if(btype.name.equals(location)){
                            atype = btype;
                        }
                    }
                    add(new Menu("Import Mod Info", 8));
                    add(new Menu("Import Textures", 5));
                    add(new Menu("Idle Texture", 3, 50, 50, "/Plots/"+atype.name()+"/Level "+title.substring(6)+"_<FRAME>.png", "CityPopulization.PlotType", "frames", "tpf", Integer.parseInt(title.substring(6))-1, 0, type, type+" (Idle) level "+title.substring(6)));
                    add(new Menu("Pending Harvesting Texture", 3, 50, 50, "/Plots/"+atype.name()+"/Pending Harvest/Level "+title.substring(6)+"_<FRAME>.png", "CityPopulization.PlotType", "frames", "tpf", Integer.parseInt(title.substring(6))-1, 1, type, type+" (Pending Harvesting) level "+title.substring(6)));
                    add(new Menu("Harvesting Texture", 3, 50, 50, "/Plots/"+atype.name()+"/Harvesting/Level "+title.substring(6)+"_<FRAME>.png", "CityPopulization.PlotType", "frames", "tpf", Integer.parseInt(title.substring(6))-1, 2, type, type+" (Harvesting) level "+title.substring(6)));
                    add(new Menu("Costs", 11, 0, 0, atype.name, title, null, null, -1, -1, null, null));
                    break;
                case 11://Plot Costs Get
                    for(PlotType btype : PlotType.values()){
                        if(btype.name.equals(location)){
                            atype = btype;
                        }
                    }
                    add(new Menu("Import Mod Info", 8));
                    add(new Menu("Cash", 12, 0, 0, null, "How much cash should "+atype.name+" "+animationInfoClassName+" cost?", "Cash Cost- "+atype.name+" "+animationInfoClassName, null, -1, -1, null, null));
                    add(new Menu("Dirt", 12, 0, 0, null, "How much dirt should "+atype.name+" "+animationInfoClassName+" cost?", "Dirt Cost- "+atype.name+" "+animationInfoClassName, null, -1, -1, null, null));
                    add(new Menu("Coal", 12, 0, 0, null, "How much coal should "+atype.name+" "+animationInfoClassName+" cost?", "Coal Cost- "+atype.name+" "+animationInfoClassName, null, -1, -1, null, null));
                    add(new Menu("Oil", 12, 0, 0, null, "How much oil should "+atype.name+" "+animationInfoClassName+" cost?", "Oil Cost- "+atype.name+" "+animationInfoClassName, null, -1, -1, null, null));
                    add(new Menu("Wood", 12, 0, 0, null, "How much wood should "+atype.name+" "+animationInfoClassName+" cost?", "Wood Cost- "+atype.name+" "+animationInfoClassName, null, -1, -1, null, null));
                    add(new Menu("Stone", 12, 0, 0, null, "How much stone should "+atype.name+" "+animationInfoClassName+" cost?", "Stone Cost- "+atype.name+" "+animationInfoClassName, null, -1, -1, null, null));
                    add(new Menu("Iron", 12, 0, 0, null, "How much iron should "+atype.name+" "+animationInfoClassName+" cost?", "Iron Cost- "+atype.name+" "+animationInfoClassName, null, -1, -1, null, null));
                    add(new Menu("Sand", 12, 0, 0, null, "How much sand should "+atype.name+" "+animationInfoClassName+" cost?", "Sand Cost- "+atype.name+" "+animationInfoClassName, null, -1, -1, null, null));
                    add(new Menu("Clay", 12, 0, 0, null, "How much clay should "+atype.name+" "+animationInfoClassName+" cost?", "Clay Cost- "+atype.name+" "+animationInfoClassName, null, -1, -1, null, null));
                    add(new Menu("Gold", 12, 0, 0, null, "How much gold should "+atype.name+" "+animationInfoClassName+" cost?", "Gold Cost- "+atype.name+" "+animationInfoClassName, null, -1, -1, null, null));
                    break;
                case 12://Number Get
                    break;
                default:
                    throw new IllegalArgumentException("Illegal type- "+type);
            }
        }
        private int draw(int distanceDown, int layer){
            if(currentHeight<=0){
                return 0;
            }
            int width = Display.getWidth(), height = Display.getHeight();
            if(completed){
                GL11.glColor4f(0, 1, 0, 1);
            }else{
                GL11.glColor4f(1, 1, 1, 1);
            }
            drawScaledRect(width, height, 800, 500, layer*10, distanceDown, 300, distanceDown+currentHeight, ImageStash.instance.getTexture("/texturepackHelper/menu"+(layer+1)+".png"));
            drawScaledText(width, height, 800, 500, layer*10, distanceDown, 300, distanceDown+Math.min(currentHeight, 20), title+((type==12&&value>=0)?(" = "+value):""));
            int childrenDistanceDown = Math.min(currentHeight, 20);
            for(Menu menu : children){
                childrenDistanceDown+=menu.draw(distanceDown+childrenDistanceDown, layer+1);
            }
            return currentHeight;
        }
        private Menu add(Menu child){
            children.add(child);
            if(collapsedStatus==0){
                child.collapsedStatus = 1;
            }else if(collapsedStatus==1){
                child.collapsedStatus = 2;
            }else if(collapsedStatus==2){
                child.collapsedStatus = 2;
            }
            child.parent = this;
            return child;
        }
        private int update(int height){
            if(currentHeight>height){
                currentHeight = height;
            }else if(currentHeight<targetHeight&&currentHeight<height){
                for(int i = 0; i<20&&currentHeight<targetHeight&&currentHeight<height; i++){
                    currentHeight++;
                }
            }else if(currentHeight>targetHeight){
                currentHeight-=currentHeight<=20?1:((type==1||type==3||type==6||type==7||type==9||type==11)?Math.min(Math.min(currentHeight-targetHeight, 20), currentHeight-20):1);
            }else if(collapsedStatus==2&&currentHeight>0){
                currentHeight--;
            }else if(collapsedStatus==1&&currentHeight>20){
                currentHeight--;
            }
            if(currentHeight<0){
                currentHeight = 0;
            }
            completed = false;
            int maxHeight = currentHeight-20;
            if(type==1||(type==3&&children.size()>6)||type==11||type==9||type==10||((type==6||type==7)&&children.size()>4)){
                completed = true;
            }
            for(Menu menu : children){
                maxHeight-=menu.update(maxHeight+5);
                if((type==1||type==12)&&!menu.completed){
                    completed = false;
                }else if(type==3&&!menu.completed&&completed&&children.indexOf(menu)>=6){
                    completed = false;
                }else if((type==9||type==10||type==6||type==7)&&!menu.completed&&completed){
                    completed = false;
                }
            }
            int diff = (currentHeight-20)-maxHeight;
            targetHeight = 20+diff;
            if(collapsedStatus==2&&(type==0||targetHeight==20)){
                targetHeight = 0;
            }else if(collapsedStatus==1&&type!=1){
                targetHeight = 20;
            }
            if((type==2||type==4)){
                completed = image!=null;
            }else if(type==12){
                completed = value>=0;
            }else if(type==5||type==8||type==0){
                completed = true;
            }
            return currentHeight;
        }
        private int onClick(int distanceDown, int mouseX, int mouseY, int layer, int shift){
            if(isScaledClickWithinBounds(Display.getWidth(), Display.getHeight(), 800, 500, mouseX, mouseY, layer*10, distanceDown-shift, 300, distanceDown+currentHeight-shift)){
                if(isScaledClickWithinBounds(Display.getWidth(), Display.getHeight(), 800, 500, mouseX, mouseY, layer*10, distanceDown-shift, 300, distanceDown+Math.min(currentHeight, 20)-shift-1)){
                    if(type==0){
                        onStaticClick(this);
                    }else if(type==12){
                        onNumberClick(this);
                    }else if(!children.isEmpty()){
                        if(collapsedStatus==0){
                            collapse();
                        }else if(collapsedStatus==1){
                            expand();
                        }else if(collapsedStatus==2){
                            expand();
                        }
                    }
                }else{
                    distanceDown+=20;
                    for(Menu menu : children){
                        distanceDown+=menu.onClick(distanceDown, mouseX, mouseY, layer+1, shift);
                    }
                }
            }
            return currentHeight;
        }
        private void collapse(){
            collapsedStatus = 1;
            for(Menu child : children){
                child.collapseCompletely();
            }
        }
        private void collapseCompletely(){
            collapsedStatus = 2;
            for(Menu child : children){
                child.collapseCompletely();
            }
        }
        private void expand(){
            if(parent!=null){
                parent.expand();
            }
            for(Menu menu : children){
                menu.onParentExpand();
            }
            collapsedStatus = 0;
        }
        private void onParentExpand(){
            if(collapsedStatus==2){
                collapsedStatus = 1;
            };
        }
        private String getName(){
            if(parent!=null){
                return parent.getName()+"."+title;
            }else{
                return title;
            }
        }
        private void expandAll(){
            expand();
            for(Menu menu : children){
                menu.expandAll();
            }
        }
        private String[] getAnimationFrames(){
            if(type!=3){
                return null;
            }else{
                ArrayList<String> lst = new ArrayList<>();
                for(int i = 6; i<children.size(); i++){
                    lst.add(children.get(i).image);
                }
                return lst.toArray(new String[lst.size()]);
            }
        }
        private TexturePack[] getAnimationTexturepacks(){
            if(type!=3){
                return null;
            }else{
                ArrayList<TexturePack> lst = new ArrayList<>();
                for(int i = 6; i<children.size(); i++){
                    lst.add(children.get(i).texturepack);
                }
                return lst.toArray(new TexturePack[lst.size()]);
            }
        }
        private Menu setFrameCount(int maxWorkerFrames){
            while(children.size()>maxWorkerFrames+6){
                children.remove(children.size()-1);
            }
            while(children.size()<maxWorkerFrames+6){
                add(new Menu("Frame "+(children.size()-5), 4, specs[0], specs[1], location.replace("<FRAME>", (children.size()-5)+""), null, null, null, -1, -1, null, null));
            }
            return this;
        }
    }
}