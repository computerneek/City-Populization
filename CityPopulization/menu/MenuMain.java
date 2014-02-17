package CityPopulization.menu;
import CityPopulization.Core;
import CityPopulization.render.MenuStory;
import CityPopulization.menu.MenuEmpireMode;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuMain extends Menu{
    private final MenuComponentButton story;
    private final MenuComponentButton customstory;
    private final MenuComponentButton multiplayer;
    private final MenuComponentButton quickmap;
    private final MenuComponentButton texturepacks;
    private final MenuComponentButton options;
    private final MenuComponentButton exit;
    public MenuMain(GUI gui, Menu parent){
        super(gui, parent);
        story = add(new MenuComponentButton(-0.8f, -0.68f, 1.6f, 0.16f, "Story (NYI)", false));
        customstory = add(new MenuComponentButton(-0.8f, -0.48f, 1.6f, 0.16f, "Custom Story (NYI)", false));
        multiplayer = add(new MenuComponentButton(-0.8f, -0.28f, 1.6f, 0.16f, "Multiplayer (NYI)", false));
        quickmap = add(new MenuComponentButton(-0.8f, -0.08f, 1.6f, 0.16f, "Empire Mode (NYI)", true));
        texturepacks = add(new MenuComponentButton(-0.8f, 0.12f, 1.6f, 0.16f, "Texturepacks (NYI)", false));
        options = add(new MenuComponentButton(-0.8f, 0.32f, 1.6f, 0.16f, "Options (NYI)", false));
        exit = add(new MenuComponentButton(-0.8f, 0.52f, 1.6f, 0.16f, "Exit", true));
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==story){
            story();
        }else if(button==customstory){
            customstory();
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
    private void customstory(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void quickmaps(){
        gui.open(new MenuEmpireMode(gui, this));
    }
    private void multiplayer(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void texturepacks(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void options(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void exit(){
        Core.helper.running = false;
    }
}
