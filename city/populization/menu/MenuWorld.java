package city.populization.menu;
import city.populization.menu.ingame.MenuSelection;
import city.populization.core.ClientSide;
import city.populization.world.World;
import city.populization.world.plot.PlotPos;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import simplelibrary.Queue;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.Menu;
public class MenuWorld extends Menu {
    private final ClientSide client;
    private PlotPos selection;
//    private int lastX;
//    private int lastY;
//    private double lastScreenWidth;
    public MenuWorld(ClientSide client, Menu parent) {
        super(client.gui, parent);
        this.client = client;
    }
    @Override
    public void renderBackground(){
        if(client.world!=null){
            long time = client.world.getTime();
            if(time<0){
                time = -time;
                GL11.glPushMatrix();
                GL11.glColor4f(1, 1, 1, 1);
                drawCenteredText(-1, -0.6, 1, 0.6, time/20+1+"");
                ImageStash.instance.bindTexture(0);
                GL11.glBegin(GL11.GL_QUADS);
                for(int i = 0; i<time; i++){
                    GL11.glColor4f(1, 1, 1, 1-((Math.min(19, time-i))%20)/25f);
                    double x = -Math.sin(-Math.PI/10*(i+0.1));
                    double y = -Math.cos(-Math.PI/10*(i+0.1));
                    double x2 = -Math.sin(-Math.PI/10*(i+0.9));
                    double y2 = -Math.cos(-Math.PI/10*(i+0.9));
                    GL11.glVertex2d(x*0.7f, y*0.7f);
                    GL11.glVertex2d(x*0.9f, y*0.9f);
                    GL11.glVertex2d(x2*0.9f, y2*0.9f);
                    GL11.glVertex2d(x2*0.7f, y2*0.7f);
                }
                GL11.glEnd();
                GL11.glPopMatrix();
                World.timerClick.play();
            }else if(time<20){
                World.timerDone.play();
            }
        }
//        if(lastScreenWidth!=(double)Display.getWidth()/Display.getHeight()*gui.helper.guiScale){
//            lastScreenWidth = (double)Display.getWidth()/Display.getHeight()*gui.helper.guiScale;
//        }
//        screenBottom = gui.helper.guiScale;
//        if(Core.world.getLocalPlayer().gamemode.finiteCash||Core.world.getLocalPlayer().gamemode.finiteResources){
//            ResourceList lst = new ResourceList();
//            int space = 0;
//            for(Plot plot : Core.world.getLocalPlayer().resourceStructures){
//                lst.addAll(plot.resources);
//                space += (plot.getLevel()+1)*Core.world.getLocalPlayer().getResourcesPerWarehouse()-plot.coming-plot.readyResources.count();
//            }
//            space-= lst.count();
//            drawCenteredText(-lastScreenWidth, -screenBottom, lastScreenWidth, -screenBottom+0.06, "$"+Core.world.getLocalPlayer().cash+"; "+lst.toString()+"; "+space+" space");
//        }
    }
    @Override
    public synchronized void render(int millisSinceLastTick){
        renderBackground();
    }
    @Override
    public void mouseEvent(int button, boolean pressed, float x, float y, float xChange, float yChange, int wheelChange){
        super.mouseEvent(button, pressed, x, y, xChange, yChange, wheelChange);
//        Player player = Core.world.getLocalPlayer();
        float screenBottom = gui.helper.guiScale;
        Viewport port = client.getWorld().getViewport();
        if(/*y<screenBottom-0.25f&&*/pressed){
            x*=4;
            y*=-4;
            x+=port.x;
            y+=port.y;
            int plotX = (int)Math.round(Math.floor(x));
            int plotY = (int)Math.round(Math.floor(y));
            if(button==0){//Left click
                makeSelection(plotX, plotY, port.z);
            }else{
                clearSelection();
            }
        }
        if(wheelChange!=0){
            port.z-=wheelChange/120;
        }
    }
//    public void setButtonSet(ButtonSet set){
//        lastScreenWidth = (double)Display.getWidth()/Display.getHeight()*gui.helper.guiScale;
//        screenBottom = gui.helper.guiScale;
//        set.display(plot==null?PlotType.Air:plot.getType(), this.set==null?0:this.set.buttonIndex, this);
//        this.set = set;
//    }
//    @Override
//    public void buttonClicked(MenuComponentButton button){
//        ((MenuComponentButtonIngame)button).listener.actionPerformed(null);
//        needsUpdate = true;
//    }
//    public void onPlotUpdate(){
//        Core.world.getLocalPlayer().onPlotClicked(lastX, lastY, this, lastButton);
//    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(key==Keyboard.KEY_PAUSE&&pressed&&!repeat){
            client.requestWorldPause();
        }
        if(key==Keyboard.KEY_G&&pressed&&!repeat){
            client.getWorld().getViewport().x = 0.5f;
            client.getWorld().getViewport().y = 0.5f;
            client.getWorld().getViewport().z = 0;
        }
//        for(MenuComponent cmpt : components){
//            MenuComponentButtonIngame button = (MenuComponentButtonIngame)cmpt;
//            if(button.getHotkey()==key){
//                buttonClicked(button);
//            }
//        }
        if(key==Keyboard.KEY_F11&&pressed&&!repeat){
            gui.helper.setFullscreen(!gui.helper.isFullscreen());
        }
        super.keyboardEvent(character, key, pressed, repeat);
    }
    private void clearSelection() {
        selection = null;
        gui.open(this);
    }
    private void makeSelection(int x, int y, int z) {
        selection = new PlotPos(x, y, z);
        gui.open(MenuSelection.onSelected(client, this, client.world, selection, client.localPlayer));
    }
    @Override
    public void tick() {
        if(client!=null&&client.getWorld()!=null){
            if(Mouse.getX()<30){
                client.getWorld().getViewport().x-=0.1+(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)?0.9:0);
            }else if(Mouse.getX()>Display.getWidth()-30){
                client.getWorld().getViewport().x+=0.1+(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)?0.9:0);
            }
            if(Mouse.getY()<30){
                client.getWorld().getViewport().y-=0.1+(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)?0.9:0);
            }else if(Mouse.getY()>Display.getHeight()-30){
                client.getWorld().getViewport().y+=0.1+(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)?0.9:0);
            }
        }
    }
}
