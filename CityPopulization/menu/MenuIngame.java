package CityPopulization.menu;
import CityPopulization.Core;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuIngame extends Menu{
    public MenuIngame(GUI gui, Menu parent){
        super(gui, parent);
    }
    @Override
    public void renderBackground(){
        Core.world.render();
        GL11.glLoadIdentity();
        GL11.glTranslatef(0, 0, -1);
        GL11.glScalef(1, -1, 1);
    }
    @Override
    public void tick(){
    }
    @Override
    public void render(int millisSinceLastTick){
        renderBackground();
        for(MenuComponent component : components){
            component.draw();
        }
    }
}
