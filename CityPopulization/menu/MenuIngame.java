package CityPopulization.menu;
import CityPopulization.Core;
import CityPopulization.menu.buttons.ButtonSet;
import CityPopulization.menu.buttons.MenuComponentButtonIngame;
import CityPopulization.world.player.Player;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.resource.ResourceList;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuIngame extends Menu{
    public float screenBottom;
    private ButtonSet set;
    private int lastX;
    private int lastY;
    private double lastScreenWidth;
    public MenuIngame(GUI gui, Menu parent){
        super(gui, parent);
    }
    @Override
    public void renderBackground(){
        Core.world.render();
        GL11.glLoadIdentity();
        GL11.glTranslatef(0, 0, -1);
        GL11.glScalef(1, -1, 1);
        GL11.glColor4f(1, 1, 1, 1);
        if(lastScreenWidth!=(double)Display.getWidth()/Display.getHeight()*gui.helper.guiScale){
            lastScreenWidth = (double)Display.getWidth()/Display.getHeight()*gui.helper.guiScale;
            if(set!=null){
                set.display(set.buttonIndex, this);
            }
        }
        screenBottom = gui.helper.guiScale;
        ResourceList lst = new ResourceList();
        for(Plot plot : Core.world.getLocalPlayer().resourceStructures){
            lst.addAll(plot.resources);
        }
        drawCenteredText(-lastScreenWidth, -screenBottom, lastScreenWidth, -screenBottom+0.08, lst.toString());
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
        if(y<screenBottom-0.5f&&pressed){
            Player player = Core.world.getLocalPlayer();
            x*=4;
            y*=4;
            x-=player.getCameraX();
            y-=player.getCameraY();
            int plotX = (int)Math.round(Math.floor(x));
            int plotY = (int)Math.round(Math.floor(y));
            lastX = plotX;
            lastY = plotY;
            player.onPlotClicked(plotX, plotY, this, button);
        }
    }
    public void setButtonSet(ButtonSet set){
        lastScreenWidth = (double)Display.getWidth()/Display.getHeight()*gui.helper.guiScale;
        set.display(this.set==null?0:this.set.buttonIndex, this);
        this.set = set;
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        ((MenuComponentButtonIngame)button).listener.actionPerformed(null);
        Core.world.getLocalPlayer().onPlotClicked(lastX, lastY, this, 0);
    }
}
