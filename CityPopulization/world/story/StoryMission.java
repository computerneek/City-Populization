package CityPopulization.world.story;
import CityPopulization.Core;
import CityPopulization.menu.MenuIngame;
import CityPopulization.world.World;
import CityPopulization.world.player.Player;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import simplelibrary.opengl.gui.components.ListComponent;
public abstract class StoryMission extends World{
    public abstract boolean isComplete();
    public abstract ListComponent getComponent();
    public abstract String name();
    public abstract String difficulty();
    public abstract String lastScore();
    public abstract String highScore();
    public void play(){
        setup();
        Core.playWorld(this);
    }
    public abstract void setup();
    @Override
    public void tick(){
        super.tick();
        update();
    }
    public abstract void update();
    @Override
    public void save(){}
    public void display(Display menu){
        Core.gui.open(menu);
        while(Core.gui.menu==menu);
    }
    public void waitUntil(int tick){
        while(age<tick){
            try{
                Thread.sleep(1);
            }catch(InterruptedException ex){
                throw new RuntimeException(ex);
            }
        }
    }
    public void waitFor(int ticks){
        waitUntil(age+ticks);
    }
    public void waitFor(int x, int y, int z, PlotType type, int level){
        Plot plot = getPlot(x, y, z);
        while(plot.getType()!=type||plot.getLevel()!=level){
            waitFor(1);
        }
    }
    public void zoom(int x, int y, int z){
        Player player = Core.world.localPlayer;
        while(player.cameraX!=x&&player.cameraY!=y&&player.cameraZ!=z);
    }
    public MenuIngameRestricted restrict(){
        if(!(Core.gui.menu instanceof MenuIngameRestricted)){
            Core.gui.open(new MenuIngameRestricted(Core.gui, Core.gui.menu));
        }
        return (MenuIngameRestricted)Core.gui.menu;
    }
    public MenuIngame unrestrict(){
        if(!(Core.gui.menu instanceof MenuIngameRestricted)){
            Core.gui.open(Core.gui.menu.parent);
        }
        return (MenuIngame)Core.gui.menu;
    }
    public void win(){
        winFromGoals = true;
    }
    public void conversation(Object... objs){
        display(new Conversation(objs));
    }
}
