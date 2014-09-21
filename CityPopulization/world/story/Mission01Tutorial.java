package CityPopulization.world.story;
import CityPopulization.Core;
import CityPopulization.menu.ListComponentStory;
import CityPopulization.menu.MenuIngameRestricted;
import CityPopulization.world.GameDifficulty;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.civilian.Worker;
import CityPopulization.world.player.PlayerHuman;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.plot.Template;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.save.StorySaveLoader;
import java.io.File;
import simplelibrary.config2.Config;
import simplelibrary.opengl.gui.components.ListComponent;
public class Mission01Tutorial extends StoryMission {
    private final File file;
    private boolean isComplete;
    private String highScore = "High Score:  ----";
    private String lastScore = "Last Score:  ----";
    private int high = -1;
    public Mission01Tutorial(){
        file = new File(((StorySaveLoader)Core.getStorySaveLoader()).file, "01 Tutorial.cps");
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
        return "01:  Tutorial";
    }
    @Override
    public String difficulty(){
        return "Normal (4x)";
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
        speedMultiplier = 4;
        Core.world = this;
        for(int i = -7; i<4; i++){
            for(int j = -2; j<3; j++){
                for(int k = -12; k<-8; k++){
                    generatePlot(i, j, k).setType(PlotType.Stone);
                }
            }
        }
        for(int i = 0; i<2; i++){
            generateAndGetPlot(i, 1, -10).setType(PlotType.Road).setOwner(localPlayer);
        }
        for(int i = -5; i<0; i++){
            generateAndGetPlot(i, 1, -10).setType(PlotType.Dirt);
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
            generateAndGetPlot(i, 0, -10).setType(PlotType.House).setLevel(2);
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
        generateAndGetPlot(0, 0, -10).setType(PlotType.Warehouse).setOwner(localPlayer).setLevel(2);
        localPlayer.cash = 100_000;
        startMonitor();
    }
    private void startMonitor(){
        Thread thread = new Thread(){
            public void run(){
                MenuIngameRestricted rst = restrict();
                rst.restrictZoom(-5, -5, -10, 5, 5, -10);
                paused = true;
                conversation(Character.TRANSIT, "System error; jump terminated.",
                             Character.MAYOR, "Where did Aria go?",
                             "Location unknown.  System rebooting.", Character.TRANSIT,
                             "Great.  How long is it going to take?",
                             Character.TECH, "That is of little consequence if we can't find Aria to operate them.",
                             "You're a technician.  You can operate it.  Look at it and tell me how long it will take.",
                             "It will take a while for me to figure out how.",
                             "Do it anyways.",
                             "Yes ma'am.", Character.TECH,
                             "What do we have with us?",
                             Character.WORKER, "My shovel.  Computer, do we have anything else?",
                             Character.COMPUTER, "Query complete.  Jump was partially successful.  All resources and some structures were lost.  Location unknown.",
                             "So we're stuck here with nothing but a shovel.  Computer, off.", Character.COMPUTER,
                             "It certainly seems so.  This shovel isn't going to do anything against the stone ceiling.",
                             "Stone?  The ceiling is made of wood.",
                             "We're underground.  There's some dirt that we can dig out, though.  If we dug that out, we could try causing a cave-in in that area.",
                             "Why would we do that?", 
                             "A cave-in would loosen the overlaying rock layers and perhaps give us a path out to the surface.  The archeologists told me the surface should be somewhere near.",
                             "Make sure it doesn't cave in on anyone and go right on ahead.",
                             "Okay.",
                             Character.MAYOR, Character.WORKER, "This is an accelerated mission due to the tendency of workers to take a long time to harvest resources.",
                             "Most missions will not be accelerated.");
                paused = false;
                rst.allowButton(PlotType.Dirt, "Harvest Dirt");
                rst.addGoal(0, new GoalHarvest(Resource.Dirt, 2375, true));
                getPlot(0, 0, -10).resources.add(Resource.Tools, 1);
                waitFor(-1, 1, -10, PlotType.Air, 0);
                getPlot(-2, 0, -10).playerVisibilities.add(localPlayer);
                paused = true;
                conversation(Character.WORKER, "There's houses back here!",
                             Character.MAYOR, "There are?  What's their status?",
                             "The dirt goes right in front of them.  There's a lot more dirt than I expected.",
                             "Build roads to those houses, then.",
                             "Will do.");
                paused = false;
                rst.addGoal(1, new GoalConstruct(PlotType.Road, 0, 5, true));
                rst.allowButton(PlotType.Air, "Construct Road");
                for(int i = 0; i<4; i++){
                    waitFor(-i-2, 1, -10, PlotType.Road, 0);
                    waitFor(20);
                    getPlot(-i-2, 0, -10).setOwner(localPlayer);
                }
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
