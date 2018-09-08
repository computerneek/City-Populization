package CityPopulization.menu;
import CityPopulization.Core;
import CityPopulization.world.WinningCondition;
import org.lwjgl.input.Keyboard;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentOptionButton;
public class MenuOpenServerGoal extends Menu{
    private final MenuOpenServer parent;
    private final MenuComponentOptionButton goalType;
    private final MenuComponentButton cancel;
    private final MenuComponentButton done;
    public MenuOpenServerGoal(GUI gui, MenuOpenServer parent){
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
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(key==Keyboard.KEY_F11&&pressed&&!repeat){
            Core.helper.setFullscreen(!Core.helper.isFullscreen());
        }
        super.keyboardEvent(character, key, pressed, repeat);
    }
}
