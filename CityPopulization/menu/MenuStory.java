package CityPopulization.menu;
import CityPopulization.Core;
import CityPopulization.world.story.StoryManager;
import CityPopulization.world.story.StoryMission;
import org.lwjgl.input.Keyboard;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentMulticolumnList;
public class MenuStory extends Menu{
    private final MenuComponentMulticolumnList missionList;
    private final MenuComponentButton playMission;
    private final MenuComponentButton back;
    public MenuStory(GUI gui, Menu parent){
        super(gui, parent);
        missionList = add(new MenuComponentMulticolumnList(-1.6f, -0.8f, 3.2f, 1.6f, 0.8f, 0.4f));
        playMission = add(new MenuComponentButton(-1.56f, 0.82f, 1.56f, 0.16f, "Start Mission", false));
        back = add(new MenuComponentButton(0.02f, 0.82f, 1.56f, 0.16f, "Back", true));
    }
    @Override
    public void onGUIOpened(){
        boolean isVisible = true;
        StoryMission mission;
        missionList.components.clear();
        for(int i = 0; (mission = StoryManager.getMission(i))!=null&&isVisible; i++){
            isVisible = mission.isComplete();
            missionList.add(mission.getComponent());
        }
        missionList.setSelectedIndex(missionList.components.size()-1);
        missionList.components.get(missionList.getSelectedIndex()).isSelected = true;
        playMission.enabled = missionList.getSelectedIndex()>=0;
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==playMission){
            play();
        }else if(button==back){
            back();
        }
    }
    private void play(){
        ((ListComponentStory)missionList.components.get(missionList.getSelectedIndex())).mission.play();
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
