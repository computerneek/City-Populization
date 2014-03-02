package CityPopulization.menu;
import CityPopulization.world.WinningCondition;
import CityPopulization.menu.MenuNewWorld;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentOptionButton;
public class MenuNewWorldGoal extends Menu{
    private final MenuNewWorld parent;
    private final MenuComponentOptionButton goalType;
    private final MenuComponentButton cancel;
    private final MenuComponentButton done;
    public MenuNewWorldGoal(GUI gui, MenuNewWorld parent){
        super(gui, parent);
        this.parent = parent;
        goalType = add(new MenuComponentOptionButton(-0.8f, -0.78, 1.6, 0.16, "Type", true, parent.theGoal.getGoalType(), WinningCondition.listTypes()));
        cancel = add(new MenuComponentButton(-0.8f, 0.62, 0.78, 0.16, "Cancel", true));
        done = add(new MenuComponentButton(0.02, 0.62, 0.78, 0.16, "Done", true));
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==cancel){
            gui.open(parent);
        }else if(button==done){
            save();
            gui.open(parent);
        }
    }
    private void save(){
        parent.theGoal = WinningCondition.getGoal(goalType.getIndex());
    }
    @Override
    public void renderBackground(){}
}
