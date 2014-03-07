package CityPopulization.menu;
import CityPopulization.Core;
import CityPopulization.world.player.Player;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuIngame extends Menu{
    private float screenBottom;
    public MenuIngame(GUI gui, Menu parent){
        super(gui, parent);
    }
    @Override
    public void renderBackground(){
        Core.world.render();
        GL11.glLoadIdentity();
        GL11.glTranslatef(0, 0, -1);
        GL11.glScalef(1, -1, 1);
        screenBottom = gui.helper.guiScale;
    }
    @Override
    public void tick(){}
    @Override
    public void render(int millisSinceLastTick){
        renderBackground();
        for(MenuComponent component : components){
            component.draw();
        }
    }
    @Override
    public void mouseEvent(int button, boolean pressed, float x, float y, float xChange, float yChange, int wheelChange){
        super.mouseEvent(button, pressed, x, y, xChange, yChange, wheelChange);
        if(y<screenBottom-0.25f&&pressed){
            Player player = Core.world.getLocalPlayer();
            x*=4;
            y*=4;
            x-=player.getCameraX();
            y-=player.getCameraY();
            int plotX = (int)Math.round(Math.floor(x));
            int plotY = (int)Math.round(Math.floor(y));
            player.onPlotClicked(plotX, plotY, this, button);
        }
    }
}
