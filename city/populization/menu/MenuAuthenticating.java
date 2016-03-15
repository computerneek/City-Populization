package city.populization.menu;
import city.populization.core.ClientSide;
import city.populization.core.Core;
import java.io.IOException;
import org.lwjgl.input.Keyboard;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuAuthenticating extends Menu {
    private final MenuComponentButton cancel;
    private final ClientSide client;
    public MenuAuthenticating(ClientSide client, Menu parent) {
        super(client.gui, parent);
        this.client = client;
        cancel = add(new MenuComponentButton(-0.8, -0.08, 1.6, 0.16, parent instanceof MenuMain?"Exit":"Cancel", true));
    }
    @Override
    public void renderBackground(){
        drawCenteredText(-0.8, -0.18, 0.8, -0.1, parent instanceof MenuMain?"Using game name...":"Logging In....");
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==cancel){
            cancel();
        }
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(key==Keyboard.KEY_F11&&pressed&&!repeat){
            Core.helper.setFullscreen(!Core.helper.isFullscreen());
        }
        super.keyboardEvent(character, key, pressed, repeat);
    }
    private void cancel(){
        try {
            client.connection.connection.close();
        } catch (IOException ex) {}
        if(parent instanceof MenuMain) gui.helper.running = false;
        else gui.open(parent);
    }
}
