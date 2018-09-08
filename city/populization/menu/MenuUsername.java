package city.populization.menu;
import city.populization.core.ClientSide;
import city.populization.core.Core;
import city.populization.core.Settings;
import city.populization.world.WorldData;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentTextBox;
public class MenuUsername extends Menu {
    private final MenuComponentTextBox input;
    private final MenuComponentButton ok;
    private final MenuComponentButton exit;
    private final ClientSide client;
    private String thisUsername;
    public MenuUsername(ClientSide client, Menu parent) {
        super(client.gui, parent);
        this.client = client;
        input = add(new MenuComponentTextBox(-0.8, -0.28, 1.6, 0.16, Settings.username==null?"Player"+(System.currentTimeMillis()%1000):Settings.username, true));
        ok = add(new MenuComponentButton(-0.8, -0.08, 1.6, 0.16, "Enter Game", true));
        exit = add(new MenuComponentButton(-0.8, 0.12, 1.6, 0.16, "Exit", true));
    }
    @Override
    public void renderBackground(){
        drawText(-0.8, -0.38, 0.8, -0.3, "What game name will you use?");
        ok.enabled = !input.text.isEmpty();
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==ok){
            login();
        }else if(button==exit){
            exit();
        }
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(key==Keyboard.KEY_F11&&pressed&&!repeat){
            Core.helper.setFullscreen(!Core.helper.isFullscreen());
        }
        super.keyboardEvent(character, key, pressed, repeat);
    }
    private synchronized void login(){
        if(input.text.isEmpty()){
            return;
        }
        gui.open(new MenuAuthenticating(client, new MenuMain(client, null)));
        thisUsername = input.text;
        Settings.username = thisUsername;
        Settings.save();
        notifyAll();
    }
    private void exit(){
        gui.helper.running = false;
    }
    public synchronized String getUsername() {
        while(thisUsername==null&&gui.helper.running){
            try {
                wait(1000);
            } catch (InterruptedException ex) {}
        }
        return thisUsername;
    }
}
