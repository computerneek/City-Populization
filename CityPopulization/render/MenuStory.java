package CityPopulization.render;
import CityPopulization.world.story.StoryManager;
import CityPopulization.world.story.StoryMission;
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
        missionList.selectedIndex = missionList.components.size()-1;
        boolean isVisible = true;
        StoryMission mission;
        for(int i = 0; (mission = StoryManager.getMission(i))!=null&&isVisible; i++){
            isVisible = mission.isComplete();
            missionList.add(mission.getComponent());
        }
        playMission.enabled = missionList.selectedIndex>=0;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void back(){
        gui.open(parent);
    }
}
