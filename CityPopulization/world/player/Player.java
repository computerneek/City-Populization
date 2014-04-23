package CityPopulization.world.player;
import CityPopulization.Core;
import CityPopulization.menu.MenuIngame;
import CityPopulization.world.World;
import CityPopulization.world.plot.Plot;
import java.util.ArrayList;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import simplelibrary.config2.Config;
public abstract class Player{
    public Race race;
    public double cameraX;
    public double cameraY;
    public int cameraZ;
    public final World world;
    public ArrayList<Plot> resourceStructures = new ArrayList<>();
    public boolean sandbox;
    public long cash;
    public Player(World world){
        this.world = world;
    }
    public void setRace(Race race){
        this.race = race;
    }
    public double getCameraX(){
        return cameraX;
    }
    public double getCameraY(){
        return cameraY;
    }
    public int getCameraZ(){
        return cameraZ;
    }
    public abstract void summonInitialWorkers();
    public abstract Player createNew(World world);
    public abstract void onPlotClicked(int plotX, int plotY, MenuIngame menu, int button);
    public void setSandbox(boolean sandbox){
        this.sandbox = sandbox;
    }
    public void update(){
        if(sandbox){
            cash = Long.MAX_VALUE;
        }
    }
    public void motion(){
        if(world.getLocalPlayer()==this&&Core.gui.menu instanceof MenuIngame){
            if(Mouse.getX()<=30){
                cameraX+=0.1;
            }else if(Mouse.getX()>=Display.getWidth()-30){
                cameraX-=0.1;
            }
            if(Mouse.getY()<=30){
                cameraY+=0.1;
            }else if(Mouse.getY()>=Display.getHeight()-30){
                cameraY-=0.1;
            }
        }
    }
    public int getResourcesPerWarehouse(){
        return (int)(1000*world.difficulty.incomeModifier);
    }
    public void render(){}
    public void mousewheel(int dist){
        cameraZ-=dist;
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("race", race.getName());
        config.set("x", (int)cameraX);
        config.set("y", (int)cameraY);
        config.set("z", cameraZ);
        config.set("sandbox", sandbox);
        config.set("cash", ""+cash);
        return config;
    }
    public static Player load(Config get){
        Race race = Race.getByName((String)get.get("race"));
        Player player = race.createPlayer(Core.loadingWorld);
        player.cameraX = (int)get.get("x");
        player.cameraY = (int)get.get("y");
        player.cameraZ = get.get("z");
        player.sandbox = get.get("sandbox");
        if(get.get("cash") instanceof String){
            player.cash = Long.parseLong((String)get.get("cash"));
        }else{
            player.cash = (long)(float)get.get("cash");
        }
        return player;
    }
}
