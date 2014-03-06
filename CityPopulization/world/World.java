package CityPopulization.world;
import CityPopulization.world.aircraft.Aircraft;
import CityPopulization.world.player.Player;
import CityPopulization.world.player.Race;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.Template;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import org.lwjgl.opengl.GL11;
public class World{
    public long seed = new Random().nextLong();
    private Player localPlayer;
    private ArrayList<Player> otherPlayers = new ArrayList<Player>();
    private Template template;
    private int speedMultiplier;
    private WinningCondition goal;
    public int age;
    private boolean isPaused;
    private GameDifficulty difficulty;
    private HashMap<Integer, HashMap<Integer, HashMap<Integer, Plot>>> plots = new HashMap<>();
    private HashMap<Integer, ArrayList<Plot>> plotsNeedingUpdate = new HashMap<>();
    public ArrayList<Aircraft> aircraft = new ArrayList<>();
    public World(){}
    public void tick(){
        if(isPaused){
            return;
        }
        for(int i = 0; i<speedMultiplier; i++){
            age++;
            ArrayList<Plot> plots = plotsNeedingUpdate.remove(age);
            if(plots!=null){
                for(Plot plot : plots){
                    plot.update();
                }
            }
            for(Aircraft aircraft : (ArrayList<Aircraft>)this.aircraft.clone()){
                aircraft.update();
            }
        }
    }
    public Player getLocalPlayer(){
        return localPlayer;
    }
    public void setTemplate(Template template){
        this.template = template;
    }
    public void setGameSpeed(int speedMultiplier){
        this.speedMultiplier = speedMultiplier;
    }
    public void setGoal(WinningCondition goal){
        this.goal = goal;
    }
    public void setAge(int worldAge){
        this.age = worldAge;
    }
    public void setPaused(boolean isPaused){
        this.isPaused = isPaused;
    }
    public void setDifficulty(GameDifficulty difficulty){
        this.difficulty = difficulty;
    }
    public Plot generateAndGetPlot(int x, int y, int z){
        for(int i = -1; i<2; i++){
            for(int j = -1; j<2; j++){
                for(int k = -1; k<2; k++){
                    generatePlot(x+i, y+j, z+k);
                }
            }
        }
        return getPlot(x, y, z);
    }
    public Plot getPlot(int x, int y, int z){
        HashMap<Integer, HashMap<Integer, Plot>> plots2 = plots.get(x);
        if(plots2==null){
            return null;
        }
        HashMap<Integer, Plot> plots3 = plots2.get(y);
        if(plots3==null){
            return null;
        }
        return plots3.get(z);
    }
    public void summonInitialWorker(){
        localPlayer.summonInitialWorkers();
    }
    public Plot generatePlot(int x, int y, int z){
        HashMap<Integer, HashMap<Integer, Plot>> plots2 = plots.get(x);
        if(plots2==null){
            plots2 = new HashMap<>();
            plots.put(x, plots2);
        }
        HashMap<Integer, Plot> plots3 = plots2.get(y);
        if(plots3==null){
            plots3 = new HashMap<>();
            plots2.put(y, plots3);
        }
        Plot plot = plots3.get(z);
        if(plot==null){
            plot = new Plot(this, x, y, z);
            plots3.put(z, plot);
            template.onPlotGenerated(this, x, y, z);
        }
        return plot;
    }
    public ArrayList<Player> listPlayers(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(localPlayer);
        players.addAll(otherPlayers);
        return players;
    }
    public void schedulePlotUpdate(Plot plot){
        schedulePlotUpdate(plot, 1);
    }
    public void schedulePlotUpdate(Plot plot, int tick){
        if(tick<1){
            tick = 1;
        }
        tick+=this.age;
        if(!plotsNeedingUpdate.containsKey(tick)){
            plotsNeedingUpdate.put(tick, new ArrayList<Plot>());
        }
        plotsNeedingUpdate.get(tick).add(plot);
    }
    public void render(){
        GL11.glLoadIdentity();
        GL11.glTranslated(localPlayer.getCameraX(), localPlayer.getCameraY(), -3);
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
        int x = -(int)localPlayer.getCameraX();
        int y = (int)localPlayer.getCameraY();
        int z = (int)localPlayer.getCameraZ();
        for(int i = -10; i<4; i++){
            GL11.glColor4d(1, 1, 1, i>0?0.2:1);
            HashMap<Float, ArrayList<Plot>> map = new HashMap<>();
            for(int j=-5; j<6; j++){
                for(int k = -5; k<6; k++){
                    float dist = (float)Math.sqrt(j*j+k*k);
                    if(!map.containsKey(dist)){
                        map.put(dist, new ArrayList<Plot>());
                    }
                    map.get(dist).add(getPlot(x+j, y+k, z+i));
                }
            }
            ArrayList<Float> dists = new ArrayList<>(map.keySet());
            Collections.sort(dists);
            while(!dists.isEmpty()){
                for(Plot plot : map.get(dists.remove(dists.size()-1))){
                    if(plot!=null){
                        plot.render(localPlayer);
                    }
                }
            }
        }
        for(Aircraft aircraft : this.aircraft){
            aircraft.render(localPlayer);
        }
//        for(int i = -6; i<7; i++){
//            HashMap<Integer, HashMap<Integer, Plot>> plots2 = plots.get(x+i);
//            if(plots2==null){
//                continue;
//            }
//            for(int j = -6; j<7; j++){
//                HashMap<Integer, Plot> plots3 = plots2.get(y+j);
//                if(plots3==null){
//                    continue;
//                }
//                for(int k = -10; k<4; k++){
//                    Plot plot = plots3.get(z+k);
//                    if(plot==null){
//                        continue;
//                    }
//                    GL11.glColor4d(1, 1, 1, k>0?0.2:1);
//                    plot.render(localPlayer);
//                }
//            }
//        }
//        GL11.glDisable(GL11.GL_DEPTH_TEST);
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public void setRace(Race race){
        localPlayer = race.createPlayer(this);
    }
}
