package CityPopulization.menu;
import CityPopulization.Core;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
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
}
