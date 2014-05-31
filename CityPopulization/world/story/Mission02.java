package CityPopulization.world.story;
import CityPopulization.Core;
import CityPopulization.menu.ListComponentStory;
import CityPopulization.menu.MenuIngameRestricted;
import CityPopulization.world.GameDifficulty;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.civilian.Worker;
import CityPopulization.world.civilian.WorkerTask;
import CityPopulization.world.player.PlayerHuman;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.plot.Template;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import CityPopulization.world.save.StorySaveLoader;
import java.io.File;
import simplelibrary.config2.Config;
import simplelibrary.opengl.gui.components.ListComponent;
public class Mission02 extends StoryMission {
    private final File file;
    private boolean isComplete;
    private String highScore = "High Score:  ----";
    private String lastScore = "Last Score:  ----";
    private int high = -1;
    public Mission02(){
        file = new File(((StorySaveLoader)Core.getStorySaveLoader()).file, "02 .cps");
        Config config = Config.newConfig(file).load();
        localPlayer = new PlayerHuman(this);
        template = Template.FLAT;
        goal = null;
        difficulty = GameDifficulty.NORMAL;
        seed = "Story Location 01".hashCode();
        if(config==null){
            return;
        }
        isComplete = config.get("complete");
        if(isComplete){
            highScore = config.get("highscore");
            lastScore = config.get("lastscore");
            high = config.get("high");
        }
    }
    @Override
    public ListComponent getComponent(){
        return new ListComponentStory(this);
    }
    @Override
    public boolean isComplete(){
        return isComplete;
    }
    @Override
    public String name(){
        return "02:  ???";
    }
    @Override
    public String difficulty(){
        return "Normal (2x)";
    }
    @Override
    public String lastScore(){
        return lastScore;
    }
    @Override
    public String highScore(){
        return highScore;
    }
    @Override
    public void setup(){
        speedMultiplier = 2;
        for(int i = -7; i<4; i++){
            for(int j = -2; j<3; j++){
                for(int k = -12; k<-8; k++){
                    generatePlot(i, j, k).setType(PlotType.Stone);
                }
            }
        }
        for(int i = -5; i<2; i++){
            generateAndGetPlot(i, 1, -10).setType(PlotType.Road).setOwner(localPlayer);
        }
        for(int i = 1; i<2; i++){
            generateAndGetPlot(i, 0, -10).setType(PlotType.House).setLevel(3).setOwner(localPlayer);
            for(int j = 0; j<8; j++){
                Worker worker = new Worker();
                worker.homePlot = getPlot(i, 0, -10);
                worker.player = localPlayer;
                worker.homePlot.workers.add(worker);
                worker.homePlot.workersPresent.add(worker);
                worker.x = i;
                worker.z= -10;
                schedulePlotUpdate(worker.homePlot);
            }
            for(int j = 0; j<8; j++){
                Civilian worker = new Civilian();
                worker.homePlot = getPlot(i, 0, -10);
                worker.player = localPlayer;
                worker.homePlot.civilians.add(worker);
                worker.homePlot.civiliansPresent.add(worker);
                worker.x = i;
                worker.z= -10;
                schedulePlotUpdate(worker.homePlot);
            }
        }
        for(int i = -5; i<-1; i++){
            generateAndGetPlot(i, 0, -10).setType(PlotType.House).setLevel(2).setOwner(localPlayer);
            for(int j = 0; j<4; j++){
                Worker worker = new Worker();
                worker.homePlot = getPlot(i, 0, -10);
                worker.player = localPlayer;
                worker.homePlot.workers.add(worker);
                worker.homePlot.workersPresent.add(worker);
                worker.x = i;
                worker.z= -10;
                schedulePlotUpdate(worker.homePlot);
            }
            for(int j = 0; j<5; j++){
                Civilian worker = new Civilian();
                worker.homePlot = getPlot(i, 0, -10);
                worker.player = localPlayer;
                worker.homePlot.civilians.add(worker);
                worker.homePlot.civiliansPresent.add(worker);
                worker.x = i;
                worker.z= -10;
                schedulePlotUpdate(worker.homePlot);
            }
        }
        generateAndGetPlot(0, 0, -10).setType(PlotType.Warehouse).setOwner(localPlayer).setLevel(3).resources.add(Resource.Tools, 1).add(Resource.Dirt, 2375);
        localPlayer.cash = 100_000;
        startMonitor();
    }
    private void startMonitor(){
        Thread thread = new Thread(){
            public void run(){
                MenuIngameRestricted rst = restrict();
                rst.restrictZoom(-5, -5, -10, 5, 5, -10);
                rst.allowButton(PlotType.Dirt, "Harvest Dirt");
                rst.allowButton(PlotType.Air, "Construct Road");
                paused = true;
                conversation(Character.COMPUTER, "Warning:  Seismic activity detected.");
                paused = false;
                getPlot(-1, 0, -10).setType(PlotType.IronDeposit);
                for(int i = -9; i<1; i++){
                    if(generateAndGetPlot(-1, 0, i).getType()!=PlotType.Air){
                        getPlot(-1, 0, i).setType(i%2==-1?PlotType.Sand:PlotType.Air);
                    }
                }
                getPlot(-1, 0, -10).setType(PlotType.Air);
                waitFor(100);
                WorkerTask task = new WorkerTask()
                        .setPlot(getPlot(-1, 0, -10))
                        .setCash(0)
                        .setCost(new ResourceList())
                        .setRevenue(PlotType.IronDeposit.resourceHarvested)
                        .setOwner(localPlayer);
                task.cost.remove(Resource.Tools, 1);
                task.revenue.remove(Resource.Tools, 1);
                task.prepare();
                task.segments.remove(0);
                getPlot(-1, 0, -10).task = task;
                paused = true;
                conversation(Character.COMPUTER, "Seismic activity has ceased.",
                             Character.COMPUTER, Character.WORKER, "A wall collapsed!",
                             Character.MAYOR, "A wall?  Which one?",
                             "The one right in the middle of the town- the one that separates the two halves of town.",
                             "Is there anything useful there?",//mayor
                             "Yes.  As it collapsed, it turned out to have an iron core, and sand crushed it from above.",//worker
                             "Can we collect that iron?",//mayor
                             "We should be able to.  From what I could see as it collapsed, it's in small enough pieces for us to bring in with no tools.",//worker
                             "What about the sand?",//mayor
                             Character.CIVILIAN, "I got some dynamite you can use.  It'll destroy most of the sand, and you'll still have to haul off about one tenth of it.",//civilian
                             "That'll keep our warehouse from filling up with sand as we dig through it.",//worker
                             "Okay, get to work- maybe this will go up to the top!");//mayor
                paused = false;
                rst.allowButton(PlotType.Sand, "Destroy");
                rst.allowButton(PlotType.Dirt, "Destroy");
                rst.allowButton(PlotType.Sand, "Harvest Sand");
                rst.restrictZoom(-5, -5, -10, 5, 5, 0);
                while(getPlot(-1, 0, -9).getType()==PlotType.Sand||getPlot(-1, 0, -10).getType()==PlotType.Sand){
                    waitFor(-1, 0, -10, PlotType.Sand, 0);
                    waitFor(-1, 0, -10, PlotType.Air, 0);
                }
                paused = true;
                conversation(Character.WORKER, "I see the sun!",
                             Character.MAYOR, "The sun?  We've found the surface!  How do we reach it?",
                             "We should be able to use this iron to build elevators to reach the surface.",
                             "What are you waiting for?  Do it!");
                paused = false;
                rst.addGoal(0, new GoalConstruct(PlotType.Elevator, 0, 11, true));
                rst.allowButton(PlotType.Air, "Construct Elevator");
                win();
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
    @Override
    public void update(){}
    @Override
    public void victory(){
        isComplete = true;
        int newScore = Math.max(0, 10000-age/4);
        if(newScore>high){
            highScore = "High Score:  "+newScore;
            high = newScore;
        }
        lastScore = "Last Score:  "+newScore;
        Config config = Config.newConfig(file);
        config.set("complete", isComplete);
        config.set("highscore", highScore);
        config.set("lastscore", lastScore);
        config.set("high", high);
        config.save();
        super.victory();
    }
}
