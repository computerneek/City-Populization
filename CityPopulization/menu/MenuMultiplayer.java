package CityPopulization.menu;
import CityPopulization.Core;
import CityPopulization.VersionManager;
import java.io.IOException;
import org.lwjgl.input.Keyboard;
import simplelibrary.net.ConnectionManager;
import simplelibrary.net.packet.Packet;
import simplelibrary.net.packet.PacketString;
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
        join.enabled = false;
        open.enabled = false;
        new Thread(){
            public void run(){
                try{
                    ConnectionManager connection = ConnectionManager.createClientSide(IP.text, 25565, 1000, ConnectionManager.TYPE_PACKET);
                    connection.send(new PacketString("City Populization"));
                    connection.send(new PacketString(VersionManager.currentVersion));
                    String str = getString(connection.receive());
                    if(!"City Populization".equals(str)){
                        connection.close();
                        return;
                    }
                    int ID = VersionManager.getVersionID(getString(connection.receive()));
                    if(ID<0){
                        connection.send(new PacketString("client outdated"));
                        connection.close();
                        return;
                    }
                    gui.open(new MenuOpenServer(gui, MenuMultiplayer.this, connection));
                }catch(IOException ex){
                    join.enabled = true;
                    open.enabled = true;
                }
            }
        }.start();
    }
    private String getString(Packet packet){
        if(packet!=null&&packet instanceof PacketString){
            return ((PacketString)packet).value;
        }
        return null;
    }
    private void open(){
        MenuOpenServer menu = new MenuOpenServer(gui, this, null);
        gui.open(menu);
    }
    private void back(){
        gui.open(parent);
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(key==Keyboard.KEY_F11&&pressed&&!repeat){
            Core.helper.setFullscreen(!Core.helper.isFullscreen());
        }
        super.keyboardEvent(character, key, pressed, repeat);
    }
}
