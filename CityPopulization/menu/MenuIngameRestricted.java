package CityPopulization.menu;
import CityPopulization.Core;
import CityPopulization.menu.MenuIngame;
import CityPopulization.menu.buttons.Button;
import CityPopulization.menu.buttons.ButtonSet;
import CityPopulization.menu.buttons.MenuComponentButtonIngame;
import CityPopulization.world.World;
import CityPopulization.world.player.Player;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.resource.ResourceList;
import CityPopulization.world.story.Goal;
import java.util.ArrayList;
import java.util.HashMap;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuIngameRestricted extends MenuIngame{
    public World world = Core.world;
    public float screenBottom;
    private ButtonSet set;
    private int lastX;
    private int lastY;
    private double lastScreenWidth;
    private int lastButton;
    private Plot plot;
    private boolean needsUpdate;
    private int minX = Integer.MIN_VALUE;
    private int minY = Integer.MIN_VALUE;
    private int minZ = Integer.MIN_VALUE;
    private int maxX = Integer.MAX_VALUE;
    private int maxY = Integer.MAX_VALUE;
    private int maxZ = Integer.MAX_VALUE;
    private int minXPan = Integer.MIN_VALUE;
    private int minYPan = Integer.MIN_VALUE;
    private int minZPan = Integer.MIN_VALUE;
    private int maxXPan = Integer.MAX_VALUE;
    private int maxYPan = Integer.MAX_VALUE;
    private int maxZPan = Integer.MAX_VALUE;
    private ArrayList<String> allowedButtons = new ArrayList<>();
    private ArrayList<String> prohibitedButtons = new ArrayList<>();
    private HashMap<PlotType, ArrayList<String>> allowedTypeButtons = new HashMap<>();
    public MenuIngameRestricted(GUI gui, Menu menu){
        super(gui, menu);
    }
    @Override
    public void renderBackground(){
        super.screenBottom = screenBottom;
        Player player = Core.world.localPlayer;
        if(minXPan>maxXPan){
            minXPan = maxXPan = minXPan+(maxXPan-minXPan)/2;
        }
        if(minYPan>maxYPan){
            minYPan = maxYPan = minYPan+(maxYPan-minYPan)/2;
        }
        if(minZPan>maxZPan){
            minZPan = maxZPan = minZPan+(maxZPan-minZPan)/2;
        }
        if(player.cameraX<minXPan){
            player.cameraX = minXPan;
        }
        if(player.cameraX>maxXPan){
            player.cameraX = maxXPan;
        }
        if(player.cameraY<minYPan){
            player.cameraY = minYPan;
        }
        if(player.cameraY>maxYPan){
            player.cameraY = maxYPan;
        }
        if(player.cameraZ<minZPan){
            player.cameraZ = minZPan;
        }
        if(player.cameraZ>maxZPan){
            player.cameraZ = maxZPan;
        }
        Core.world.render();
        GL11.glLoadIdentity();
        GL11.glColor4f(1, 1, 1, 1);
        if(lastScreenWidth!=(double)org.lwjgl.opengl.Display.getWidth()/org.lwjgl.opengl.Display.getHeight()*gui.helper.guiScale){
            lastScreenWidth = (double)org.lwjgl.opengl.Display.getWidth()/org.lwjgl.opengl.Display.getHeight()*gui.helper.guiScale;
            if(set!=null){
                set.display(plot==null?PlotType.Air:plot.getType(), set.buttonIndex, this);
            }
        }
        screenBottom = gui.helper.guiScale;
        ResourceList lst = new ResourceList();
        int space = 0;
        for(Plot plot : Core.world.getLocalPlayer().resourceStructures){
            lst.addAll(plot.resources);
            space += (plot.getLevel()+1)*Core.world.getLocalPlayer().getResourcesPerWarehouse()-plot.coming-plot.readyResources.count();
        }
        space-= lst.count();
        drawCenteredText(-lastScreenWidth, -screenBottom, lastScreenWidth, -screenBottom+0.1, (Core.world.getLocalPlayer().sandbox?"":"$"+Core.world.getLocalPlayer().cash+"; ")+lst.toString()+"; "+space+" space");
        for(int i = 0; i<5&&i<world.goals.size(); i++){
            drawText(-lastScreenWidth, screenBottom-0.25+0.05*i, -lastScreenWidth+0.75, screenBottom-0.2+0.05*i, world.goals.get(i).getText());
        }
    }
    @Override
    public void render(int millisSinceLastTick){
        if(needsUpdate){
            Core.world.getLocalPlayer().onPlotClicked(lastX, lastY, this, lastButton);
            needsUpdate = false;
        }
        renderBackground();
        for(MenuComponent component : components){
            component.render(millisSinceLastTick);
        }
    }
    public boolean approve(PlotType type, Button button){
        String label = button.text[0];
        for(int i = 1; i<button.text.length; i++){
            label += " "+button.text[i];
        }
        return allowedButtons.contains(label)||(allowedButtons.contains("*")&&!prohibitedButtons.contains(label))||(allowedTypeButtons.containsKey(type)&&allowedTypeButtons.get(type).contains(label));
    }
    @Override
    public void mouseEvent(int button, boolean pressed, float x, float y, float xChange, float yChange, int wheelChange){
        for(MenuComponent component : components){
            if(isClickWithinBounds(x, y, component.x, component.y, component.x+component.width, component.y+component.height)){
                component.mouseover(x-component.x, y-component.y, true);
                if(button==0&&gui.mouseWereDown.contains(button)!=pressed){
                    selected = component;
                    component.mouseEvent(x-component.x, y-component.y, 0, pressed);
                }else if(button==-1&&Mouse.isButtonDown(0)){
                    component.mouseDragged(x-component.x, y-component.y, 0);
                }
                if(button==1&&gui.mouseWereDown.contains(button)!=pressed){
                    selected = component;
                    component.mouseEvent(x-component.x, y-component.y, 1, pressed);
                }else if(button==-1&&Mouse.isButtonDown(1)){
                    component.mouseDragged(x-component.x, y-component.y, 1);
                }
                if(button==2&&gui.mouseWereDown.contains(button)!=pressed){
                    selected = component;
                    component.mouseEvent(x-component.x, y-component.y, 2, pressed);
                }else if(button==-1&&Mouse.isButtonDown(2)){
                    component.mouseDragged(x-component.x, y-component.y, 2);
                }
            }else{
                component.mouseover(-1, -1, false);
            }
            if(selected==component){
                component.isSelected = true;
            }else{
                component.isSelected = false;
            }
        }
        if(selected!=null&&wheelChange!=0){
            selected.mouseWheelChange(wheelChange);
        }
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
            onPlotClicked(plotX, plotY, this, button);
        }
        if(wheelChange!=0){
            player.mousewheel(wheelChange/120);
        }
    }
    public void setButtonSet(ButtonSet set){
        lastScreenWidth = (double)org.lwjgl.opengl.Display.getWidth()/org.lwjgl.opengl.Display.getHeight()*gui.helper.guiScale;
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
        if(selected!=null){
            selected.processKeyboard(character, key, pressed, repeat);
        }
    }
    private void onPlotClicked(int plotX, int plotY, MenuIngameRestricted aThis, int button){
        if(plotX<minX||plotY<minY||plotX>maxX||plotY>maxY||Core.world.localPlayer.cameraZ<minZ||Core.world.localPlayer.cameraZ>maxZ){
            return;
        }
        Core.world.localPlayer.onPlotClicked(plotX, plotY, this, button);
    }
    public MenuIngameRestricted allowButton(String text){
        allowedButtons.add(text);
        needsUpdate = true;
        return this;
    }
    public MenuIngameRestricted disallowButton(String text){
        allowedButtons.remove(text);
        needsUpdate = true;
        return this;
    }
    public MenuIngameRestricted prohibitButton(String text){
        prohibitedButtons.add(text);
        needsUpdate = true;
        return this;
    }
    public MenuIngameRestricted unprohibitButton(String text){
        prohibitedButtons.remove(text);
        needsUpdate = true;
        return this;
    }
    public MenuIngameRestricted restrictZoom(int minX, int minY, int minZ, int maxX, int maxY, int maxZ){
        minXPan = minX;
        maxXPan = maxX;
        minYPan = minY;
        maxYPan = maxY;
        minZPan = minZ;
        maxZPan = maxZ;
        return this;
    }
    public MenuIngameRestricted restrictPlot(int minX, int minY, int minZ, int maxX, int maxY, int maxZ){
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        return this;
    }
    public MenuIngameRestricted addGoal(int index, Goal goal){
        world.goals.add(index, goal);
        return this;
    }
    public MenuIngameRestricted updateGoal(int index, Goal goal){
        world.goals.remove(index);
        world.goals.add(index, goal);
        return this;
    }
    public MenuIngameRestricted removeGoal(int index){
        world.goals.remove(index);
        return this;
    }
    public MenuIngameRestricted allowButton(PlotType type, String text){
        if(!allowedTypeButtons.containsKey(type)){
            allowedTypeButtons.put(type, new ArrayList<String>());
        }
        allowedTypeButtons.get(type).add(text);
        return this;
    }
    public MenuIngameRestricted disallowButton(PlotType type, String text){
        if(!allowedTypeButtons.containsKey(type)){
            allowedTypeButtons.put(type, new ArrayList<String>());
        }
        allowedTypeButtons.get(type).add(text);
        return this;
    }
}
