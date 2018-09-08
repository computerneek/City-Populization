package city.populization.menu;
import city.populization.core.ClientSide;
import city.populization.core.Core;
import city.populization.core.Settings;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;

import static simplelibrary.opengl.Renderer2D.drawRect;
public class MenuMain extends Menu {
    private final ClientSide client;
    private final MenuComponentButton story;
    private final MenuComponentButton singleplayer;
    private final MenuComponentButton multiplayer;
    private final MenuComponentButton hostServer;
    private final MenuComponentButton texturepacks;
    private final MenuComponentButton options;
    private final MenuComponentButton exit;
    public MenuMain(ClientSide client, Menu parent) {
        super(client.gui, parent);
        this.client = client;
        story = add(new MenuComponentButton(-0.8f, -0.68f, 1.6f, 0.16f, "Story (NYI)", false));
        singleplayer = add(new MenuComponentButton(-0.8f, -0.48f, 1.6f, 0.16f, "Lone Worlds (NYI)", true));
        multiplayer = add(new MenuComponentButton(-0.8f, -0.28f, 1.6f, 0.16f, "Find Worlds (NYI)", false));
        hostServer = add(new MenuComponentButton(-0.8f, -0.08f, 1.6f, 0.16f, "Shared Worlds (NYI)", false));
        texturepacks = add(new MenuComponentButton(-0.8f, 0.12f, 1.6f, 0.16f, "Texturepacks (NYI)", false));
        options = add(new MenuComponentButton(-0.8f, 0.32f, 1.6f, 0.16f, "Options (NYI)", false));
        exit = add(new MenuComponentButton(-0.8f, 0.52f, 1.6f, 0.16f, "Exit", true));
    }
    @Override
    public void renderBackground(){
        if(Keyboard.isKeyDown(Keyboard.KEY_F1)){
            float screenLeft = -Display.getWidth()/2f/client.gui.helper.guiScale*0.004f;
            drawText(screenLeft, 1, screenLeft+1, 0.9, "Port:  "+Settings.port);
        }
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==story){
            story();
        }else if(button==singleplayer){
            singleplayer();
        }else if(button==multiplayer){
            multiplayer();
        }else if(button==hostServer){
            hostServer();
        }else if(button==texturepacks){
            texturepacks();
        }else if(button==options){
            options();
        }else if(button==exit){
            exit();
        }
    }
    private void story(){
//        gui.open(new MenuStory(client, this));
        throw new UnsupportedOperationException("Not yet implemented");//TODO Story mode
    }
    private void singleplayer(){
        gui.open(new MenuSingleplayer(client, this));
    }
    private void multiplayer(){
//        gui.open(new MenuMultiplayer(client, this));
        throw new UnsupportedOperationException("Not yet implemented");//TODO Multiplayer mode
    }
    private void hostServer(){
//        gui.open(new MenuHostServer(client, this));
        throw new UnsupportedOperationException("Not yet implemented");//TODO Server host mode
    }
    private void texturepacks(){
//        gui.open(new MenuTexturepacks(client, this));;
        throw new UnsupportedOperationException("Not yet implemented");//TODO Texturepacks
    }
    private void options(){
//        gui.open(new MenuOptions(client, this));
        throw new UnsupportedOperationException("Not yet implemented");//TODO Options
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
