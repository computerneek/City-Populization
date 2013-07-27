package CityPopulization;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import multilib.error.ErrorCategory;
import multilib.error.ErrorLevel;
import multilib.error.Sys;
import multilib.game.GameHelper;
import multilib.game.KeyList;
import multilib.game.KeyboardStatus;
import multilib.game.MouseStatus;
import multilib.gui.ImageStash;
import multilib.gui.WindowControl;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
public class EscapeMenu{
    private static boolean isOpen;
    private static String menu = "main";
    private static boolean boxSelected = false;
    private static String enteredString = "";
    private static int tick;
    private static boolean saving;
    private static String[] names;
    private static int whichName;
    public static void toggleState(){
        if(isOpen){
            isOpen = false;
            menu = "main";
        }else{
            isOpen = true;
        }
    }
    public static boolean isOpen(){
        return isOpen;
    }
    public static void tick(){
        tick++;
        if(!isOpen){
            return;
        }
        GameHelper helper = main.helper;
        KeyboardStatus keyboard = helper.keyboard;
        KeyList keyList = keyboard.getDownKeys();
        String[] keys = keyList.getKeys();
        boolean isShiftDown = false;
        boolean isReturnDown = false;
        for(String key : keys){
            switch(key){
                case "LSHIFT":
                case "RSHIFT":
                    isShiftDown = true;
                    break;
                case "RETURN":
                    isReturnDown = true;
                    break;
            }
        }
        keyList = keyboard.getPressedKeys();
        keys = keyList.getKeys();
        String[] characters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", " ", "_", "."};
        String charactersTyped = "";
        for(int i = 0; i<keys.length; i++){
            String tag = keys[i];
            for(int j = 0; j<characters.length; j++){
                if(tag.equals(characters[j])){
                    if(charactersTyped==null){
                        charactersTyped = "";
                        break;
                    }
                    if(isShiftDown){
                        charactersTyped+=characters[j];
                    }else{
                        charactersTyped+=characters[j].toLowerCase();
                    }
                    break;
                }
            }
            if(tag.equals("BACKSPACE")&&charactersTyped.isEmpty()){
                charactersTyped = null;
            }else if(tag.equals("BACKSPACE")){
                charactersTyped = charactersTyped.substring(0, charactersTyped.length()-1);
            }
        }
        MouseStatus mouse = helper.mouse;
        switch(menu){
            case "main":
                if(mouse.leftButtonPressed){
                    int x = mouse.posX, y = mouse.posY;
                    if(Renderer.isScaledClickWithinBounds(x, y, 400, 305, 1200, 395)){
                        menu = "Save Game";
                    }else if(Renderer.isScaledClickWithinBounds(x, y, 400, 405, 1200, 495)){
                        menu = "Load Game";
                        
                        File savesDir = new File(main.getAppdataRoot()+"\\Saves");
                        File[] files = savesDir.listFiles();
                        ArrayList<String> lst = new ArrayList<>();
                        for(File file : files){
                            if(file.isFile()&&file.getName().endsWith(".cps")){
                                lst.add(file.getName().substring(0, file.getName().length()-4));
                            }
                        }
                        if(lst.isEmpty()){
                            menu = "main";
                        }
                        names = lst.toArray(new String[lst.size()]);
                        whichName = 0;
                    }else if(Renderer.isScaledClickWithinBounds(x, y, 400, 505, 1200, 595)){
                        helper.running = false;
                    }else if(Renderer.isScaledClickWithinBounds(x, y, 400, 605, 1200, 695)){
                        isOpen = false;
                    }
                }
                break;
            case "Save Game":
                if(mouse.leftButtonPressed){
                    int x = mouse.posX, y = mouse.posY;
                    boxSelected = Renderer.isScaledClickWithinBounds(x, y, 400, 355, 1200, 445);
                    if(Renderer.isScaledClickWithinBounds(x, y, 400, 455, 1200, 545)){
                        saveGame();
                    }else if(Renderer.isScaledClickWithinBounds(x, y, 400, 555, 1200, 645)){
                        menu = "main";
                    }
                }
                if(charactersTyped==null&&!enteredString.isEmpty()&&boxSelected){
                    enteredString = enteredString.substring(0, enteredString.length()-1);
                }else if(charactersTyped!=null&&boxSelected){
                    enteredString+=charactersTyped;
                }
                break;
            case "Load Game":
                if(mouse.leftButtonPressed){
                    int x = mouse.posX, y = mouse.posY;
                    if(Renderer.isScaledClickWithinBounds(x, y, 400, 255, 1200, 345)&&whichName>0){
                        whichName--;
                    }else if(Renderer.isScaledClickWithinBounds(x, y, 400, 455, 1200, 545)&&whichName<names.length-1){
                        whichName++;
                    }else if(Renderer.isScaledClickWithinBounds(x, y, 400, 555, 1200, 645)){
                        loadGame();
                    }else if(Renderer.isScaledClickWithinBounds(x, y, 400, 655, 1200, 745)){
                        menu = "main";
                    }
                }
                break;
            default:
                menu = "main";
                throw new UnsupportedOperationException("Not yet implemented- "+menu);
        }
    }
    public static void render(){
        if(!isOpen){
            return;
        }
        switch(menu){
            case "main":
                Renderer.drawScaledRect(400, 305, 1200, 395, ImageStash.instance.getTexture("/gui/button.png"));
                Renderer.drawScaledRect(400, 405, 1200, 495, ImageStash.instance.getTexture("/gui/button.png"));
                Renderer.drawScaledRect(400, 505, 1200, 595, ImageStash.instance.getTexture("/gui/button.png"));
                Renderer.drawScaledRect(400, 605, 1200, 695, ImageStash.instance.getTexture("/gui/button.png"));
                Renderer.drawCenteredScaledText(410, 315, 1190, 385, "Save Game");
                Renderer.drawCenteredScaledText(410, 415, 1190, 485, "Load Game");
                Renderer.drawCenteredScaledText(410, 515, 1190, 585, "Exit");
                Renderer.drawCenteredScaledText(410, 615, 1190, 685, "Return to Game");
                break;
            case "Save Game":
                Renderer.drawScaledRect(400, 355, 1200, 445, ImageStash.instance.getTexture("/gui/textBox.png"));
                Renderer.drawScaledRect(400, 455, 1200, 545, ImageStash.instance.getTexture("/gui/button.png"));
                Renderer.drawScaledRect(400, 555, 1200, 645, ImageStash.instance.getTexture("/gui/button.png"));
                Renderer.drawScaledText(410, 365, 1190, 435, enteredString+(((tick&20)<10&&boxSelected)?"_":""));
                Renderer.drawCenteredScaledText(410, 465, 1190, 535, saving?"Saving...":"Save");
                Renderer.drawCenteredScaledText(410, 565, 1190, 635, "Back");
                break;
            case "Load Game":
                for(int i = 0; i<names.length; i++){
                    Renderer.drawScaledRect(400, 375+(i-whichName)*60, 1200, 425+(i-whichName)*60, ImageStash.instance.getTexture("/gui/textBox.png"));
                    Renderer.drawCenteredScaledText(410, 385+(i-whichName)*60, 1200, 425+(i-whichName)*60, names[i]);
                }
                Renderer.drawScaledRect(400, 255, 1200, 345, ImageStash.instance.getTexture("/gui/button.png"));
                Renderer.drawScaledRect(400, 455, 1200, 545, ImageStash.instance.getTexture("/gui/button.png"));
                Renderer.drawScaledRect(400, 555, 1200, 645, ImageStash.instance.getTexture("/gui/button.png"));
                Renderer.drawScaledRect(400, 655, 1200, 745, ImageStash.instance.getTexture("/gui/button.png"));
                Renderer.drawCenteredScaledText(410, 265, 1190, 335, "/\\ /\\ /\\");
                Renderer.drawCenteredScaledText(410, 465, 1190, 535, "\\/ \\/ \\/");
                Renderer.drawCenteredScaledText(410, 565, 1190, 635, saving?"Loading...":"Load");
                Renderer.drawCenteredScaledText(410, 665, 1190, 735, "Back");
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented- "+menu);
        }
    }
    private static void saveGame(){
        String name = enteredString;
        if(name==null||name.isEmpty()){
            return;
        }
        saving = true;
        main.forceRender = true;
        try{
            main.helper.render();
        }catch(Throwable ex){
            Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.other);
        }
        try{
            main.saveGame(name);
        }catch(Throwable ex){
            Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.other);
        }
        saving = false;
        menu = "main";
        enteredString = "";
        boxSelected = false;
    }
    private static void loadGame(){
        String saveName = names[whichName];
        if(saveName==null||saveName.isEmpty()){
            return;
        }
        saving = true;
        main.forceRender = true;
        try{
            main.helper.render();
        }catch(Throwable ex){
            Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.other);
        }
        try{
            main.loadGame(saveName);
        }catch(Throwable ex){
            Sys.error(ErrorLevel.severe, null, ex, ErrorCategory.other);
        }
        saving = false;
        menu = "main";
        isOpen = false;
    }
}
