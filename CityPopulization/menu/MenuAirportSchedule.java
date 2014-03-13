package CityPopulization.menu;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuAirportSchedule extends Menu{
    private double screenWidth;
    private float screenHeight;
    public MenuAirportSchedule(GUI gui, Menu menu){
        super(gui, menu);
    }
    @Override
    public void render(int millisSinceLastTick){
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
}
