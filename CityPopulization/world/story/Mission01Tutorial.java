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
    public int getVisionDistance(){
        return 1;
    }
    @Override
    public boolean workersExpire(){
        return false;
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
        for(int i = -10; i<11; i++){
            for(int j= -10; j<11; j++){
                for(int k = -1; k<2; k++){
                    generatePlot(i, j, k).setType(PlotType.Stone);
                }
            }
        }
        for(int i = -9; i<10; i++){
            for(int j = -9; j<10; j++){
                getPlot(i, j, 0).setVisibilityOverride(VisibilityOverride.CAN_SEE_FROM);
            }
        }
        makeHouse(3, 1, 0, 2, 9, 4, localPlayer).resources.add(Resource.Tools, 1);
        getPlot(3, 2, 0).setType(PlotType.Road).setOwner(localPlayer);
        getPlot(4, 2, 0).setType(PlotType.Road).setOwner(localPlayer);
        getPlot(4, 1, 0).setType(PlotType.Warehouse).setLevel(2).setOwner(localPlayer);
        makeHouse(1, 1, 0, 1, 4, 2, null).resources.add(Resource.Tools, 1);
        makeHouse(-3, 5, 0, 1, 4, 2, null).resources.add(Resource.Tools, 1);
        makeHouse(-6, 3, 0, 1, 4, 2, null).resources.add(Resource.Tools, 1);
        makeHouse(5, -3, 0, 1, 4, 2, null).resources.add(Resource.Tools, 1);
        makeHouse(3, -7, 0, 1, 4, 2, null).resources.add(Resource.Tools, 1);
        makeHouse(-5, -7, 0, 1, 4, 2, null).resources.add(Resource.Tools, 1);
        makeHouse(-2, -5, 0, 1, 4, 2, null).resources.add(Resource.Tools, 1);
        setSpace(2, -2, 0, 2, 2, 0, PlotType.Dirt, 1, null);
        setSpace(-2, -2, 0, 5, -2, 0, PlotType.Dirt, 1, null);
        setSpace(3, -3, 0, 3, -6, 0, PlotType.Dirt, 1, null);
        setSpace(-2, -2, 0, -2, -4, 0, PlotType.Dirt, 1, null);
        setSpace(-5, -4, 0, -2, -4, 0, PlotType.Dirt, 1, null);
        setSpace(-5, -4, 0, -5, -6, 0, PlotType.Dirt, 1, null);
        setSpace(2, 2, 0, -2, 2, 0, PlotType.Dirt, 1, null);
        setSpace(-2, 2, 0, -2, 6, 0, PlotType.Dirt, 1, null);
        setSpace(-2, 4, 0, -6, 4, 0, PlotType.Dirt, 1, null);
        setSpace(-2, 6, 0, -3, 6, 0, PlotType.Dirt, 1, null);
        localPlayer.cash = 100;
        startMonitor();
    }
    private void startMonitor(){
        Thread thread = new Thread(){
            public void run(){
                MenuIngameRestricted rst = restrict();
                rst.restrictZoom(-4, -5, 0, 3, 6, 0);
                paused = true;
                conversation(Character.TRANSIT, "System error; jump terminated.",
                             Character.MAYOR, "What's that supposed to mean?",
                             "...", Character.TRANSIT,
                             null,
                             Character.TECH, "We're gonna have to fix it on our own!",
                             "You're a technician.  You can get it working, can't you?",
                             "I don't know about that, but I'll try.", Character.TECH,
                             null,
                             Character.WORKER, "There's a bunch of dynamite and a shovel over here.  We can use it to clean the dirt out to reach the other houses.",
                             "Where will we put it?",
                             "There should be enough space in the warehouse to contain it.  We can also use it to make roads between the houses.",
                             "Good thinking.  Each of the houses has another set of tools and some more workers, so let's get to it!", Character.MAYOR, Character.WORKER,
                             "This is an accelerated mission due to the tendency of workers to take a long time to do anything with resources.",
                             "Most missions will not be accelerated.",
                             "You can pan around the world by moving the cursor to the side of the screen (F11 toggles fullscreen, wich makes that much easier).",
                             "Each task costs money, which is charged when you assign the task.",
                             "Resources required for the task are charged later, and the task is completed once all requirements are fulfilled.",
                             "Once you destroy some dirt, click the empty plot to build a road.",
                             "Yes, destroy.  Destroying returns only a tenth of the resources.  Harvesting will be made available later in the story.");
                paused = false;
                rst.allowButton(PlotType.Dirt, "Destroy");
                rst.addGoal(0, new GoalHarvest(Resource.Dirt, 2700, true));
                rst.addGoal(1, new GoalConstruct(PlotType.Road, 0, 36, true));
                rst.allowButton(PlotType.Air, "Construct Road");
                int[][] locations = new int[][]{
                    {1, 1},
                    {-3, 5},
                    {-6, 3},
                    {5, -3},
                    {3, -7},
                    {-5, -7},
                    {-2, -5}
                };
                for(int[] loc : locations){
                    final int[] aloc = loc;
                    new Thread(){
                        public void run(){
                            waitFor(aloc[0], aloc[1]+1, 0, PlotType.Road, 0);
                            waitFor(20);
                            getPlot(aloc[0], aloc[1], 0).setOwner(localPlayer);
                        }
                    }.start();
                }
                rst.addGoal(2, new GoalConstruct(PlotType.House, 1, 7, false));
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
