package CityPopulization.world.player;
import CityPopulization.Core;
import CityPopulization.menu.Client;
import CityPopulization.menu.MenuIngame;
import CityPopulization.packets.PacketPlot;
import CityPopulization.world.World;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.plot.Plot;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import simplelibrary.config2.Config;
import simplelibrary.net.packet.PacketConfig;
import simplelibrary.net.packet.PacketString;
public abstract class Player{
    public Race race;
    public double cameraX;
    public double cameraY;
    public int cameraZ;
    public final World world;
    public ArrayList<Plot> resourceStructures = new ArrayList<>();
    public ArrayList<Civilian> civilians = new ArrayList<>();
    public boolean sandbox;
    public long cash;
    public Client client;
    public int offsetX;
    public int offsetY;
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
    public abstract void summonInitialWorkers(int workers);
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
                cameraX+=0.1+(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)?0.9:0);
            }else if(Mouse.getX()>=Display.getWidth()-30){
                cameraX-=0.1+(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)?0.9:0);
            }
            if(Mouse.getY()<=30){
                cameraY+=0.1+(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)?0.9:0);
            }else if(Mouse.getY()>=Display.getHeight()-30){
                cameraY-=0.1+(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)?0.9:0);
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
    public void reset(){
        cameraX = 0;
        cameraY = 0;
        cameraZ = 0;
        resourceStructures.clear();
        cash = 0;
    }
    public void sendPlot(int x, int y, int z){
        Plot plot = world.getPlot(x+offsetX, y+offsetY, z);
        if(plot!=null){
            client.client.send(new PacketPlot(x, y, z, plot));
        }
    }
    public void generatePlot(int x, int y, int z){
        Plot plot = world.generatePlot(x+offsetX, y+offsetY, z);
        if(plot!=null){
            client.client.send(new PacketPlot(x, y, z, plot));
        }
    }
    public Plot getPlot(int x, int y, int z){
        return world.getPlot(x+offsetX, y+offsetY, z);
    }
    public void plotDisappear(int x, int y, int z){
        if(client==null){
            return;
        }
        sendPlot(x, y, z);
    }
    public void plotAppear(int x, int y, int z){
        if(client==null){
            return;
        }
        sendPlot(x, y, z);
    }
    public void civilianRemoved(Civilian civil){
        if(client!=null&&civilians.contains(civil)){
            client.client.send(new PacketString("CIVILIAN_REMOVED:"+civilians.indexOf(civil)));
            civilians.remove(civil);
        }
    }
    public void civilianTrained(Civilian civil, Civilian work){
        if(client!=null&&civilians.contains(civil)){
            client.client.send(new PacketString("CIVILIAN_TRAINED:"+civilians.indexOf(civil)));
            civilians.set(civilians.indexOf(civil), work);
        }
    }
    public void civilianAdded(Civilian civil){
        if(client!=null&&(civil.player==this||getPlot(Math.round(civil.x), Math.round(civil.y), Math.round(civil.z)).playerVisibilities.contains(this))){
            civilians.add(civil);
            client.client.send(new PacketString("CIVILIAN_ADDED:"+civilians.indexOf(civil)));
            client.client.send(new PacketConfig(civil.save()));
        }
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
