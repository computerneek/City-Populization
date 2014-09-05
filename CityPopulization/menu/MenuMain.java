package CityPopulization.menu;
import CityPopulization.Core;
import org.lwjgl.input.Keyboard;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuMain extends Menu{
    private final MenuComponentButton story;
    private final MenuComponentButton multiplayer;
    private final MenuComponentButton quickmap;
    private final MenuComponentButton texturepacks;
    private final MenuComponentButton options;
    private final MenuComponentButton exit;
    public MenuMain(GUI gui, Menu parent){
        super(gui, parent);
        story = add(new MenuComponentButton(-0.8f, -0.58f, 1.6f, 0.16f, "Story", true));
        multiplayer = add(new MenuComponentButton(-0.8f, -0.38f, 1.6f, 0.16f, "Multiplayer (NYI)", true));
        quickmap = add(new MenuComponentButton(-0.8f, -0.18f, 1.6f, 0.16f, "Empire Mode", true));
        texturepacks = add(new MenuComponentButton(-0.8f, 0.02f, 1.6f, 0.16f, "Texturepacks", true));
        options = add(new MenuComponentButton(-0.8f, 0.22f, 1.6f, 0.16f, "Options (NYI)", false));
        exit = add(new MenuComponentButton(-0.8f, 0.42f, 1.6f, 0.16f, "Exit", true));
    }
    @Override
    public void renderBackground(){
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==story){
            story();
        }else if(button==multiplayer){
            multiplayer();
        }else if(button==quickmap){
            quickmaps();
        }else if(button==texturepacks){
            texturepacks();
        }else if(button==options){
            options();
        }else if(button==exit){
            exit();
        }
    }
    private void story(){
        gui.open(new MenuStory(gui, this));
    }
    private void quickmaps(){
        gui.open(new MenuEmpireMode(gui, this));
    }
    private void multiplayer(){
        gui.open(new MenuMultiplayer(gui, this));
    }
    private void texturepacks(){
        gui.open(new MenuTexturepacks(gui, this));
    }
    private void options(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void exit(){
        Core.helper.running = false;
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(key==Keyboard.KEY_F11&&pressed&&!repeat){
            Core.helper.setFullscreen(!Core.helper.isFullscreen());
        }
        super.keyboardEvent(character, key, pressed, repeat);
    }
}
