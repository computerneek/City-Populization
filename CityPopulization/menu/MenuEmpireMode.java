package CityPopulization.menu;
import CityPopulization.Core;
import CityPopulization.world.World;
import CityPopulization.world.WorldInfo;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentMulticolumnList;
public class MenuEmpireMode extends Menu{
    private final MenuComponentMulticolumnList missionList;
    private final MenuComponentButton play;
    private final MenuComponentButton delete;
    private final MenuComponentButton newMission;
    private final MenuComponentButton back;
    public MenuEmpireMode(GUI gui, Menu parent){
        super(gui, parent);
        missionList = add(new MenuComponentMulticolumnList(-1.6, -1, 3.2, 1.6, 0.8, 0.4));
        play = add(new MenuComponentButton(-1.56, 0.62, 1.56, 0.16, "Play", false));
        delete = add(new MenuComponentButton(-1.56, 0.82, 1.56, 0.16, "Delete", false));
        newMission = add(new MenuComponentButton(0.02, 0.62, 1.56, 0.16, "New", true));
        back = add(new MenuComponentButton(0.02, 0.82, 1.56, 0.16, "Back", true));
        WorldInfo[] worlds = Core.getSingleplayerSaveLoader().listWorlds();
        for(WorldInfo info : worlds){
            missionList.add(new ListComponentWorld(info));
        }
    }
    @Override
    public void renderBackground(){
        play.enabled = delete.enabled = missionList.selectedIndex>=0;
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
        WorldInfo info = ((ListComponentWorld)missionList.components.get(missionList.selectedIndex)).info;
        World world = info.saveLoader.loadWorld(info);
        Core.playWorld(world);
    }
    private void delete(){
        WorldInfo info = ((ListComponentWorld)missionList.components.get(missionList.selectedIndex)).info;
        info.file.delete();
        gui.open(new MenuEmpireMode(gui, parent));
    }
    private void newMission(){
        gui.open(new MenuNewWorld(gui, this));
    }
    private void back(){
        gui.open(parent);
    }
}
