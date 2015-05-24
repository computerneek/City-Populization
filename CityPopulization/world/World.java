package CityPopulization.world;
import CityPopulization.Core;
import CityPopulization.menu.MenuIngameVictory;
import CityPopulization.packets.PacketPlot;
import CityPopulization.packets.PacketPlotRequest;
import CityPopulization.world.aircraft.Aircraft;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.player.Player;
import CityPopulization.world.player.Race;
import CityPopulization.world.plot.ChunkSize;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.plot.SkyScraper;
import CityPopulization.world.plot.Template;
import CityPopulization.world.story.Goal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.lwjgl.opengl.GL11;
import simplelibrary.Sys;
import simplelibrary.config2.Config;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.net.ConnectionManager;
import simplelibrary.net.packet.Packet;
public class World{
    public long seed = new Random().nextLong();
    public Player localPlayer;
    public ArrayList<Player> otherPlayers = new ArrayList<>();
    public Template template;
    public int speedMultiplier = 1;
    public WinningCondition goal;
    public int age;
    public boolean isPaused;
    public boolean paused;
    public GameDifficulty difficulty;
    public HashMap<Integer, HashMap<Integer, HashMap<Integer, Plot>>> plots = new HashMap<>();
    public HashMap<Integer, ArrayList<Plot>> plotsNeedingUpdate = new HashMap<>();
    public ArrayList<Aircraft> aircraft = new ArrayList<>();
    public ArrayList<Civilian> civilians = new ArrayList<>();
    public WorldInfo info;
    public ArrayList<Goal> goals = new ArrayList<>();
    public boolean winFromGoals = false;
    public boolean remote;
    public ConnectionManager server;
    public synchronized void tick(){
        localPlayer.motion();
        for(int i = 0; i<speedMultiplier*difficulty.gameSpeedModifier; i++){
            if(isPaused||paused){
                return;
            }
            age++;
            ArrayList<Plot> plots = plotsNeedingUpdate.remove(age);
            if(plots!=null){
                for(Plot plot : plots){
                    try{
                        plot.update();
                    }catch(Throwable twbl){
                        Sys.error(ErrorLevel.severe, null, twbl, ErrorCategory.other);
                    }
                }
            }
            for(Aircraft aircraft : (ArrayList<Aircraft>)this.aircraft.clone()){
                aircraft.update();
            }
            for(Civilian civilian : (ArrayList<Civilian>)civilians.clone()){
                civilian.update();
            }
            for(Player player : otherPlayers){
                player.update();
            }
            localPlayer.update();
            boolean complete = true;
            for(Goal goal : goals){
                goal.update(this);
                complete &= goal.isComplete();
            }
            if(winFromGoals&&complete){
                winFromGoals = false;
                victory();
            }
        }
        if((age/speedMultiplier*difficulty.gameSpeedModifier)%6000==0){
            save();
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
    public synchronized Plot generateAndGetPlot(int x, int y, int z){
        for(int i = -1; i<Math.max(getVisionDistance(), 1); i++){
            for(int j = -1; j<Math.max(getVisionDistance(), 1); j++){
                for(int k = -1; k<Math.max(getVisionDistance(), 1); k++){
                    generatePlot(x+i, y+j, z+k);
                }
            }
        }
        return getPlot(x, y, z);
    }
    public synchronized Plot getPlot(int x, int y, int z){
        HashMap<Integer, HashMap<Integer, Plot>> plots2 = plots.get(x);
        if(plots2==null){
            if(remote){
                server.send(new PacketPlotRequest(x, y, z));
            }
            return null;
        }
        HashMap<Integer, Plot> plots3 = plots2.get(y);
        if(plots3==null){
            if(remote){
                server.send(new PacketPlotRequest(x, y, z));
            }
            return null;
        }
        if(remote&&!plots3.containsKey(z)){
            server.send(new PacketPlotRequest(x, y, z));
        }
        return plots3.get(z);
    }
    public void summonInitialWorker(int workers){
        localPlayer.summonInitialWorkers(workers);
    }
    public synchronized Plot generatePlot(int x, int y, int z){
        if(getPlot(x, y, z)!=null){
            return getPlot(x, y, z);
        }else if(remote){
            return makePlot(x, y, z);
        }
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
            ChunkSize cs = template.getChunkSize();
            if(cs.x==0&&cs.y==0&&cs.z==0){
                plot = new Plot(this, x, y, z);
                plots3.put(z, plot);
                template.onPlotGenerated(this, x, y, z);
            }else{
                generateChunk(x>>cs.x, y>>cs.y, z>>cs.z);
                return getPlot(x, y, z);
            }
        }
        return plot;
    }
    public int getVisionDistance(){
        return 2;
    }
    private synchronized void generateChunk(int chunkX, int chunkY, int chunkZ){
        ChunkSize cs = template.getChunkSize();
        int startX = chunkX<<cs.x, startY = chunkY<<cs.y, startZ = chunkZ<<cs.z;
        int xSize = 1<<cs.x, ySize = 1<<cs.y, zSize = 1<<cs.z;
        for(int x = 0; x<xSize; x++){
            if(!plots.containsKey(x)){
                plots.put(x, new HashMap<Integer, HashMap<Integer, Plot>>());
            }
            HashMap<Integer, HashMap<Integer, Plot>> yzPlane = plots.get(x);
            for(int y = 0; y<ySize; y++){
                if(!yzPlane.containsKey(y)){
                    yzPlane.put(y, new HashMap<Integer, Plot>());
                }
                HashMap<Integer, Plot> zRow = yzPlane.get(y);
                for(int z = 0; z<zSize; z++){
                    if(!zRow.containsKey(z)){
                        zRow.put(z, new Plot(this, x, y, z));
                    }
                    Plot plot = zRow.get(z);
                    template.onPlotGenerated(this, x, y, z);
                }
            }
        }
        template.onChunkGenerated(this, chunkX, chunkY, chunkZ);
    }
    public ArrayList<Player> listPlayers(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(localPlayer);
        players.addAll(otherPlayers);
        return players;
    }
    public synchronized void clearPlotUpdates(Plot plot){
        for(Map.Entry<Integer, ArrayList<Plot>> entry:plotsNeedingUpdate.entrySet()){
            Integer key=entry.getKey();
            ArrayList<Plot> value=entry.getValue();
            value.remove(plot);
        }
    }
    public synchronized void schedulePlotUpdate(Plot plot){
        schedulePlotUpdate(plot, 1);
    }
    public synchronized void schedulePlotUpdate(Plot plot, int tick){
        if(tick<1){
            tick = 1;
        }
        tick+=this.age;
        if(!plotsNeedingUpdate.containsKey(tick)){
            plotsNeedingUpdate.put(tick, new ArrayList<Plot>());
        }
        if(!plotsNeedingUpdate.get(tick).contains(plot)){
            plotsNeedingUpdate.get(tick).add(plot);
        }
    }
    public synchronized void render(){
//        renderWithDepthTesting();
        renderWithoutDepthTesting();
    }
    public void renderWithDepthTesting(){
        GL11.glLoadIdentity();
        GL11.glTranslated(localPlayer.getCameraX(), localPlayer.getCameraY(), -4*Core.gui.distBack);
        GL11.glScalef(1, 1, 0.25f);
        GL11.glTranslated(0, 0, -localPlayer.getCameraZ());
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        int x = -(int)localPlayer.getCameraX();
        int y = (int)localPlayer.getCameraY();
        int z = localPlayer.getCameraZ();
        int renderWidth = 10;
//        for(int i = -30; i<6; i++){
//            GL11.glColor4d(1, 1, 1, i>0?0.2:1*Math.pow(0.9, -i));
//            HashMap<Float, ArrayList<Plot>> map = new HashMap<>();
//            for(int j=-renderWidth; j<renderWidth+1; j++){
//                for(int k = -renderWidth; k<renderWidth+1; k++){
//                    float dist = (float)Math.sqrt(j*j+k*k);
//                    if(!map.containsKey(dist)){
//                        map.put(dist, new ArrayList<Plot>());
//                    }
//                    map.get(dist).add(getPlot(x+j, y+k, z+i));
//                }
//            }
//            ArrayList<Float> dists = new ArrayList<>(map.keySet());
//            Collections.sort(dists);
//            while(!dists.isEmpty()){
//                for(Plot plot : map.get(dists.remove(dists.size()-1))){
//                    if(plot!=null){
//                        plot.render(localPlayer);
//                    }
//                }
//            }
//        }
        for(int k = -50; k<6; k++){
            if(k==0){
                GL11.glDisable(GL11.GL_DEPTH_TEST);
            }else if(k==-50){
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
            for(int i = -renderWidth; i<renderWidth+1; i++){
                for(int j = -renderWidth; j<renderWidth+1; j++){
                    Plot plot = getPlot(x+i, y+j, z+k);
                    if(plot==null){
                        continue;
                    }
                    GL11.glColor4d(1, 1, 1, k>0?0.2:1);
                    plot.render(localPlayer);
                }
            }
        }
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
//        GL11.glDisable(GL11.GL_BLEND);
        for(Aircraft aircraft : this.aircraft){
            aircraft.render(localPlayer);
        }
        localPlayer.render();
        for(Player player : otherPlayers){
            player.render();
        }
        for(Civilian civilian : civilians){
            civilian.render(localPlayer);
        }
    }
    public void renderWithoutDepthTesting(){
        GL11.glLoadIdentity();
        GL11.glTranslated(localPlayer.getCameraX(), localPlayer.getCameraY(), -4*Core.gui.distBack);
        GL11.glScalef(1, 1, 0.25f);
        GL11.glTranslated(0, 0, -localPlayer.getCameraZ());
//        GL11.glEnable(GL11.GL_DEPTH_TEST);
        int x = -(int)localPlayer.getCameraX();
        int y = (int)localPlayer.getCameraY();
        int z = localPlayer.getCameraZ();
        int renderWidth = 10;
        for(int i = -30; i<6; i++){
            GL11.glColor4d(1, 1, 1, i>0?0.1:1*Math.pow(0.9, -i));
            HashMap<Float, ArrayList<Plot>> map = new HashMap<>();
            for(int j=-renderWidth; j<renderWidth+1; j++){
                for(int k = -renderWidth; k<renderWidth+1; k++){
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
                        if(i==1&&plot.fallProgress>0){
                            GL11.glColor4d(1, 1, 1, 1);
                            plot.render(localPlayer);
                            GL11.glColor4d(1, 1, 1, 0.1);
                        }else{
                            plot.render(localPlayer);
                        }
                    }
                }
            }
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
        for(Aircraft aircraft : this.aircraft){
            aircraft.render(localPlayer);
        }
        localPlayer.render();
        for(Player player : otherPlayers){
            player.render();
        }
        for(Civilian civilian : civilians){
            civilian.render(localPlayer);
        }
    }
    public void setRace(Race race){
        localPlayer = race.createPlayer(this);
    }
    public void save(){
        if(info.saveLoader!=null){
            info.saveLoader.saveWorld(this);
        }
    }
    public String size(){
        int count = 0;
        for(Integer key : plots.keySet()){
            HashMap<Integer, HashMap<Integer, Plot>> plots = this.plots.get(key);
            for(Integer key2 : plots.keySet()){
                count+=plots.get(key2).size();
            }
        }
        return count+" plots";
    }
    public synchronized void save(Config config){
        config.set("seed", ""+seed);
        config.set("localPlayer", localPlayer.save());
        Config two = Config.newConfig();
        two.set("count", otherPlayers.size());
        for(int i = 0; i<otherPlayers.size(); i++){
            two.set(i+"", otherPlayers.get(i).save());
        }
        config.set("otherPlayers", two);
        config.set("template", template.name());
        config.set("speed", speedMultiplier);
        config.set("goal", goal.save());
        config.set("paused", isPaused);
        config.set("difficulty",difficulty.name());
        ArrayList<Plot> lst = new ArrayList<Plot>();
        for(Integer key : plots.keySet()){
            HashMap<Integer, HashMap<Integer, Plot>> plots = this.plots.get(key);
            for(Integer key2 : plots.keySet()){
                lst.addAll(plots.get(key2).values());
            }
        }
        two = Config.newConfig();
        two.set("count", lst.size());
        for(int i = 0; i<lst.size(); i++){
            two.set(i+"",lst.get(i).save());
        }
        config.set("plots", two);
        two = Config.newConfig();
        two.set("count", 0);
        for(Integer key : plotsNeedingUpdate.keySet()){
            if(key>age){
                Config three = Config.newConfig();
                ArrayList<Plot> plts = plotsNeedingUpdate.get(key);
                three.set("time", key);
                three.set("count", plts.size());
                for(int i = 0; i<plts.size(); i++){
                    Plot plot = plts.get(i);
                    three.set(i+"x", plot.x);
                    three.set(i+"y", plot.y);
                    three.set(i+"z", plot.z);
                }
                two.set(two.get("count")+"",three);
                two.set("count", (int)two.get("count")+1);
            }
        }
        config.set("updates",two);
        two = Config.newConfig();
        two.set("count", aircraft.size());
        for(int i = 0; i<aircraft.size(); i++){
            two.set(i+"",aircraft.get(i).save());
        }
        config.set("aircraft",two);
        two = Config.newConfig();
        two.set("count", civilians.size());
        for(int i = 0; i<civilians.size(); i++){
            two.set(i+"",civilians.get(i).save());
        }
        config.set("civilians",two);
    }
    public synchronized void load(Config config){
        Core.loadingWorld = this;
        seed = Long.parseLong((String)config.get("seed"));
        localPlayer = Player.load((Config)config.get("localPlayer"));
        Config two = config.get("otherPlayers");
        for(int i = 0; i<(int)two.get("count"); i++){
            otherPlayers.add(Player.load((Config)two.get(""+i)));
        }
        template = Template.valueOf((String)config.get("template"));
        speedMultiplier = config.get("speed");
        goal = WinningCondition.load((Config)config.get("goal"));
        isPaused = config.get("paused");
        String diff = config.get("difficulty");
        difficulty = GameDifficulty.valueOf((String)config.get("difficulty"));
        two = config.get("plots");
        for(int i = 0; i<(int)two.get("count"); i++){
            Config three = two.get(i+"");
            generatePlot((int)three.get("x"), (int)three.get("y"), (int)three.get("z")).load(three);
        }
        for(int i = 0; i<(int)two.get("count"); i++){
            Config three = two.get(i+"");
            getPlot((int)three.get("x"), (int)three.get("y"), (int)three.get("z")).terminal.load((Config)three.get("terminal"));
            if(three.hasProperty("skyscraper")){
                SkyScraper.load((Config)three.get("skyscraper"), this);
            }
        }
        two = config.get("updates");
        for(int i = 0; i<(int)two.get("count"); i++){
            Config three = two.get(i+"");
            int time = three.get("time");
            ArrayList<Plot> plts = plotsNeedingUpdate.containsKey(time)?plotsNeedingUpdate.get(time):new ArrayList<Plot>();
            plotsNeedingUpdate.put(time, plts);
            for(int j = 0; j<(int)three.get("count"); j++){
                plts.add(getPlot((int)three.get(j+"x"), (int)three.get(j+"y"), (int)three.get(j+"z")));
            }
        }
        two = config.get("aircraft");
        for(int i = 0; i<(int)two.get("count"); i++){
            aircraft.add(Aircraft.load((Config)two.get(i+"")));
        }
        two = config.get("civilians");
        for(int i = 0; i<(int)two.get("count"); i++){
            Civilian civil = Civilian.load((Config)two.get(i+""));
            civilians.add(civil);
            if(civil.worker){
                civil.homePlot.workers.add(civil);
            }else{
                civil.homePlot.civilians.add(civil);
            }
        }
        for(HashMap<Integer, HashMap<Integer, Plot>> plots : this.plots.values()){
            for(HashMap<Integer, Plot> plts : plots.values()){
                for(Plot plot : plts.values()){
                    plot.updateVisibility();
                    if(!plot.workers.isEmpty()||!plot.civilians.isEmpty()||plot.getType()==PlotType.AirportEntrance||plot.getType()==PlotType.Warehouse||plot.getType()==PlotType.AirportTerminal){
                        schedulePlotUpdate(plot);
                    }
                }
            }
        }
    }
    public void victory(){
        speedMultiplier = 1;
        Core.gui.open(new MenuIngameVictory());
    }
    public void reset(){
        otherPlayers.clear();
        localPlayer.reset();
        age = 0;
        isPaused = false;
        paused = false;
        plots.clear();
        plotsNeedingUpdate.clear();
        aircraft.clear();
        civilians.clear();
        goals.clear();
        winFromGoals = false;
    }
    public void setRemote(ConnectionManager connection){
        remote = true;
        server = connection;
        new Thread(){
            public void run(){
                doThread();
            }
        }.start();
    }
    public void onCivilianAdded(Civilian civilian){
        for(Player player : otherPlayers){
            player.civilianAdded(civilian);
        }
    }
    private void doThread(){
        while(!server.isClosed()){
            while(!server.inboundPackets.isEmpty()){
                processPacket(server.inboundPackets.remove(0));
            }
            try{
                Thread.sleep(50);
            }catch(InterruptedException ex){}
        }
    }
    private void processPacket(Packet packet){
        if(packet.getClass()==PacketPlot.class){
            PacketPlot pkt = (PacketPlot)packet;
            Plot plot = findPlot((int)pkt.value.get("x"), (int)pkt.value.get("y"), (int)pkt.value.get("z"));
            if(plot==null){
                plot = makePlot((int)pkt.value.get("x"), (int)pkt.value.get("y"), (int)pkt.value.get("z"));
            }
            plot.eraseAndLoad(pkt.value);
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public synchronized Plot findPlot(int x, int y, int z){
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
    private synchronized Plot makePlot(int x, int y, int z){
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
}
