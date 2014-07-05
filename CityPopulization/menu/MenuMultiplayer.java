package CityPopulization.menu;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentTextBox;
public class MenuMultiplayer extends Menu {
    private final MenuComponentTextBox IP;
    private final MenuComponentButton join;
    private final MenuComponentButton open;
    private final MenuComponentButton back;
    public MenuMultiplayer(GUI gui, MenuMain parent){
        super(gui, parent);
        IP = add(new MenuComponentTextBox(-0.8f, -0.38f, 1.6f, 0.16f, "", true));
        join = add(new MenuComponentButton(-0.8f, -0.18f, 1.6f, 0.16f, "Join", false));
        open = add(new MenuComponentButton(-0.8f, 0.02f, 1.6f, 0.16f, "Create", true));
        back = add(new MenuComponentButton(-0.8f, 0.22f, 1.6f, 0.16f, "Back", true));
    }
    @Override
    public void renderBackground(){
        join.enabled = !IP.text.isEmpty();
        super.renderBackground();
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==join){
            join();
        }else if(button==open){
            open();
        }else if(button==back){
            back();
        }
    }
    private void join(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void open(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void back(){
        gui.open(parent);
    }
}
