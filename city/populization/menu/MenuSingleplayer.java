package city.populization.menu;
import city.populization.core.ClientSide;
import city.populization.core.Core;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentMulticolumnList;
public class MenuSingleplayer extends Menu {
    private final ClientSide client;
    public final MenuComponentMulticolumnList missionList;
    private final MenuComponentButton play;
    private final MenuComponentButton delete;
    private final MenuComponentButton newMission;
    private final MenuComponentButton back;
    public MenuSingleplayer(ClientSide client, Menu parent) {
        super(client.gui, parent);
        this.client = client;
        missionList = add(new MenuComponentMulticolumnList(-1.6, -1, 3.2, 1.6, 0.8, 0.4));
        play = add(new MenuComponentButton(-1.56, 0.62, 1.56, 0.16, "Play", false));
        delete = add(new MenuComponentButton(-1.56, 0.82, 1.56, 0.16, "Delete", false));
        newMission = add(new MenuComponentButton(0.02, 0.62, 1.56, 0.16, "New", true));
        back = add(new MenuComponentButton(0.02, 0.82, 1.56, 0.16, "Back", true));
    }
    @Override
    public void renderBackground(){
        play.enabled = delete.enabled = missionList.selectedIndex>=0&&missionList.selectedIndex<missionList.components.size();
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==play){
            play();
        }else if(button==delete){
            delete();
        }else if(button==newMission){
            newMission();
        }else if(button==back){
            back();
        }
    }
    private void play(){
        client.playWorld((ListComponentWorld)missionList.components.get(missionList.selectedIndex));
    }
    private void delete(){
        client.deleteWorld((ListComponentWorld)missionList.components.get(missionList.selectedIndex));
        onGUIOpened();
    }
    private void newMission(){
        gui.open(new MenuNewSingleplayerWorld(client, this));
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
    @Override
    public void onGUIOpened() {
        missionList.components.clear();
        missionList.selectedIndex = -1;
        client.onMenuSingleplayer(this);
    }
}
