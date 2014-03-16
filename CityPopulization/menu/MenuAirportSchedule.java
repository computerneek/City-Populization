package CityPopulization.menu;
import CityPopulization.world.aircraft.schedule.AircraftSchedule;
import CityPopulization.world.aircraft.schedule.ScheduleElement;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentList;
import simplelibrary.opengl.gui.components.MenuComponentMulticolumnList;
public class MenuAirportSchedule extends Menu{
    private double screenWidth;
    private float screenHeight;
    private final MenuComponentList list;
    private final MenuComponentButton add;
    private final MenuComponentButton remove;
    private final MenuComponentButton done;
    private final MenuComponentButton cancel;
    private AircraftSchedule schedule;
    public MenuAirportSchedule(GUI gui, Menu menu, AircraftSchedule schedule){
        super(gui, menu);
        this.schedule = schedule.copy();
        screenHeight = gui.helper.guiScale;
        list = add(new MenuComponentMulticolumnList(-1.0, -screenHeight, 2, screenHeight*2-0.2, 1, 0.2));
        add = add(new MenuComponentButton(-1, screenHeight-0.18, 0.98, 0.06, "Add Aircraft", true));
        remove = add(new MenuComponentButton(0, screenHeight-0.18, 0.98, 0.06, "Remove Aircraft", false));
        done = add(new MenuComponentButton(-1, screenHeight-0.08, 0.98, 0.06, "Done", true));
        cancel = add(new MenuComponentButton(0, screenHeight-0.08, 0.98, 0.06, "Cancel", true));
        for(ScheduleElement element : schedule.elements){
            list.add(new ListComponentAircraftSchedule(element));
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        remove.enabled = list.selectedIndex>=0;
        parent.render(millisSinceLastTick);
        GL11.glColor4d(0, 0, 0, 0.2);
        screenWidth = (double)Display.getWidth()/Display.getHeight()*gui.helper.guiScale;
        screenHeight = gui.helper.guiScale;
        drawRect(-screenWidth, -screenHeight, screenWidth, screenHeight, 0);
        GL11.glColor4d(1, 1, 1, 1);
        for(MenuComponent component : components){
            component.draw();
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            gui.open(parent);
        }
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==add){
            add();
        }else if(button==remove){
            remove();
        }else if(button==done){
            done();
        }else if(button==cancel){
            cancel();
        }
    }
    private void add(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void remove(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void done(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void cancel(){
        gui.open(parent);
    }
}
