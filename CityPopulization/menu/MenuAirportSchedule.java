package CityPopulization.menu;
import CityPopulization.Core;
import CityPopulization.world.aircraft.schedule.AircraftSchedule;
import CityPopulization.world.aircraft.schedule.ScheduleElement;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentMulticolumnList;
public class MenuAirportSchedule extends Menu{
    private double screenWidth;
    private float screenHeight;
    private MenuComponentMulticolumnList list;
    private MenuComponentButton add;
    private MenuComponentButton remove;
    private MenuComponentButton done;
    private MenuComponentButton cancel;
    public final AircraftSchedule schedule;
    private final AircraftSchedule originalSchedule;
    public MenuAirportSchedule(GUI gui, Menu menu, AircraftSchedule schedule){
        super(gui, menu);
        this.schedule = schedule.copy();
        originalSchedule = schedule;
        screenHeight = gui.helper.guiScale;
        list = add(new MenuComponentMulticolumnList(-1.0, -screenHeight, 2, screenHeight*2-0.2, 1, 0.2));
        add = add(new MenuComponentButton(-1, screenHeight-0.18, 0.98, 0.06, "Add Aircraft", true));
        remove = add(new MenuComponentButton(0, screenHeight-0.18, 0.98, 0.06, "Remove Aircraft", false));
        done = add(new MenuComponentButton(-1, screenHeight-0.08, 0.98, 0.06, "Done", true));
        cancel = add(new MenuComponentButton(0, screenHeight-0.08, 0.98, 0.06, "Cancel", true));
        refresh();
    }
    @Override
    public void render(int millisSinceLastTick){
        remove.enabled = list.selectedIndex>-1;
        parent.render(millisSinceLastTick);
        screenWidth = (double)Display.getWidth()/Display.getHeight()*gui.helper.guiScale;
        screenHeight = gui.helper.guiScale;
        GL11.glColor4d(0, 0, 0, 0.2);
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
        gui.open(new MenuAirportSchedule_NewAircraft(gui, this));
    }
    private void remove(){
        schedule.elements.remove(list.selectedIndex);
        refresh();
    }
    private void done(){
        originalSchedule.elements.clear();
        originalSchedule.elements.addAll(schedule.elements);
        gui.open(parent);
    }
    private void cancel(){
        gui.open(parent);
    }
    public MenuAirportSchedule refresh(){
        list.components.clear();
        for(ScheduleElement element : schedule.elements){
            list.add(new ListComponentAircraftSchedule(element));
        }
        return this;
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(key==Keyboard.KEY_F11&&pressed&&!repeat){
            Core.helper.setFullscreen(!Core.helper.isFullscreen());
        }
        super.keyboardEvent(character, key, pressed, repeat);
    }
}
