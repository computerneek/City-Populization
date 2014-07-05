package CityPopulization.menu;
import CityPopulization.Core;
import CityPopulization.menu.buttons.ButtonSet;
import CityPopulization.menu.buttons.MenuComponentButtonIngame;
import CityPopulization.world.player.Player;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.resource.ResourceList;
import org.lwjgl.input.Keyboard;
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
    private int lastButton;
    private Plot plot;
    private boolean needsUpdate;
    public MenuIngame(GUI gui, Menu parent){
        super(gui, parent);
    }
    @Override
    public void renderBackground(){
        Core.world.render();
        GL11.glLoadIdentity();
        GL11.glTranslatef(0, 0, -gui.distBack);
        GL11.glScalef(1, -1, 1);
        GL11.glColor4f(1, 1, 1, 1);
        if(lastScreenWidth!=(double)Display.getWidth()/Display.getHeight()*gui.helper.guiScale){
            lastScreenWidth = (double)Display.getWidth()/Display.getHeight()*gui.helper.guiScale;
            if(set!=null){
                set.display(plot==null?PlotType.Air:plot.getType(), set.buttonIndex, this);
            }
        }
        screenBottom = gui.helper.guiScale;
        if(!Core.world.getLocalPlayer().sandbox){
            ResourceList lst = new ResourceList();
            int space = 0;
            for(Plot plot : Core.world.getLocalPlayer().resourceStructures){
                lst.addAll(plot.resources);
                space += (plot.getLevel()+1)*Core.world.getLocalPlayer().getResourcesPerWarehouse()-plot.coming-plot.readyResources.count();
            }
            space-= lst.count();
            drawCenteredText(-lastScreenWidth, -screenBottom, lastScreenWidth, -screenBottom+0.06, "$"+Core.world.getLocalPlayer().cash+"; "+lst.toString()+"; "+space+" space");
        }
    }
    @Override
    public void tick(){}
    @Override
    public void render(int millisSinceLastTick){
        if(needsUpdate){
            Core.world.getLocalPlayer().onPlotClicked(lastX, lastY, this, lastButton);
            needsUpdate = false;
        }
        renderBackground();
        String buttonInfo = "";
        for(MenuComponent component : components){
            component.draw();
            if(component instanceof MenuComponentButtonIngame&&component.isMouseOver){
                buttonInfo = ((MenuComponentButtonIngame)component).getInfo();
            }
        }
        drawCenteredText(-lastScreenWidth, screenBottom-0.31, lastScreenWidth, screenBottom-0.25, buttonInfo);
    }
    @Override
    public void mouseEvent(int button, boolean pressed, float x, float y, float xChange, float yChange, int wheelChange){
        super.mouseEvent(button, pressed, x, y, xChange, yChange, wheelChange);
        Player player = Core.world.getLocalPlayer();
        if(y<screenBottom-0.25f&&pressed){
            x*=4;
            y*=4;
            x-=player.getCameraX();
            y+=player.getCameraY();
            int plotX = (int)Math.round(Math.floor(x));
            int plotY = (int)Math.round(Math.floor(y));
            if(plot!=null){
                plot.unselect();
            }
            lastX = plotX;
            lastY = plotY;
            lastButton = button;
            plot = null;
            if(button==0){
                plot = player.world.getPlot(plotX, plotY, player.cameraZ);
            }
            if(plot!=null){
                plot.select(this);
            }
            player.onPlotClicked(plotX, plotY, this, button);
        }
        if(wheelChange!=0){
            player.mousewheel(wheelChange/120);
        }
    }
    public void setButtonSet(ButtonSet set){
        lastScreenWidth = (double)Display.getWidth()/Display.getHeight()*gui.helper.guiScale;
        screenBottom = gui.helper.guiScale;
        set.display(plot==null?PlotType.Air:plot.getType(), this.set==null?0:this.set.buttonIndex, this);
        this.set = set;
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        ((MenuComponentButtonIngame)button).listener.actionPerformed(null);
        needsUpdate = true;
    }
    public void onPlotUpdate(){
        Core.world.getLocalPlayer().onPlotClicked(lastX, lastY, this, lastButton);
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(key==Keyboard.KEY_PAUSE&&pressed&&!repeat){
            Core.world.isPaused = !Core.world.isPaused;
        }
        if(key==Keyboard.KEY_G&&pressed&&!repeat){
            Core.world.getLocalPlayer().cameraX = 0;
            Core.world.getLocalPlayer().cameraY = 0;
            Core.world.getLocalPlayer().cameraZ = 0;
        }
        for(MenuComponent cmpt : components){
            MenuComponentButtonIngame button = (MenuComponentButtonIngame)cmpt;
            if(button.getHotkey()==key){
                buttonClicked(button);
            }
        }
        if(key==Keyboard.KEY_F11&&pressed&&!repeat){
            Core.helper.setFullscreen(!Core.helper.isFullscreen());
        }
        super.keyboardEvent(character, key, pressed, repeat);
    }
}
