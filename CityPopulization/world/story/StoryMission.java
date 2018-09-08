package CityPopulization.world.story;
import CityPopulization.menu.MenuIngameRestricted;
import CityPopulization.Core;
import CityPopulization.menu.MenuIngame;
import CityPopulization.menu.MenuIngameVictory;
import CityPopulization.world.World;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.player.Player;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public boolean workersExpire(){
        return true;
    }
    public abstract void update();
    @Override
    public void save(){}
    public void display(Display menu){
        Core.gui.open(menu);
        while(Core.gui.menu==menu){
            synchronized(Core.gui){
                try{
                    Core.gui.wait(1);
                }catch(InterruptedException ex){
                    Logger.getLogger(StoryMission.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
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
    public MenuIngameRestricted restrict(){
        if(!(Core.gui.menu instanceof MenuIngameRestricted)){
            Core.gui.open(new MenuIngameRestricted(Core.gui, Core.gui.menu));
        }
        return (MenuIngameRestricted)Core.gui.menu;
    }
    public MenuIngame unrestrict(){
        if(Core.gui.menu instanceof MenuIngameRestricted){
            Core.gui.open(Core.gui.menu.parent);
        }
        return (MenuIngame)Core.gui.menu;
    }
    public void win(){
        winFromGoals = true;
        MenuIngameVictory.init();
    }
    public void conversation(Object... objs){
        display(new Conversation(objs));
    }
    public Plot makeHouse(int x, int y, int z, int level, int fill, int workers, Player owner){
        fill = Math.min(fill, Math.max(1, (int)((level+1)*(level+1)*difficulty.homeOccupantModifier)));
        workers = Math.min(workers, fill);
        Plot plot;
        (plot = generateAndGetPlot(x, y, z)).setType(PlotType.House).setLevel(level).setOwner(owner);
        for(int i = 0; i<workers; i++){
            Civilian worker = new Civilian().upgradeToWorker();
            worker.homePlot = plot;
            worker.player = owner;
            plot.workers.add(worker);
            plot.workersPresent.add(worker);
            worker.x = x;
            worker.y = y;
            worker.z = z;
        }
        for(int i = 0; i<fill-workers; i++){
            Civilian worker = new Civilian();
            worker.homePlot = plot;
            worker.player = owner;
            plot.civilians.add(worker);
            plot.civiliansPresent.add(worker);
            worker.x = x;
            worker.y = y;
            worker.z = z;
        }
        if(fill>0){
            schedulePlotUpdate(plot);
        }
        return plot;
    }
    public void setSpace(int x, int y, int z, int X, int Y, int Z, PlotType type, int level, Player owner){
        int coord = 0;
        if(X<x){
            coord = X;
            X = x;
            x = coord;
        }
        if(Y<y){
            coord = Y;
            Y = y;
            y = coord;
        }
        if(Z<z){
            coord = Z;
            Z = z;
            z = coord;
        }
        for(int i = x; i<=X; i++){
            for(int j = y; j<=Y; j++){
                for(int k = z; k<=Z; k++){
                    generateAndGetPlot(i, j, k).setType(type).setLevel(level).setOwner(owner);
                }
            }
        }
    }
    public void setHouses(int x, int y, int z, int X, int Y, int Z, PlotType type, int level, Player owner, int fill, int workers){
        int coord = 0;
        if(X<x){
            coord = X;
            X = x;
            x = coord;
        }
        if(Y<y){
            coord = Y;
            Y = y;
            y = coord;
        }
        if(Z<z){
            coord = Z;
            Z = z;
            z = coord;
        }
        fill = Math.min(fill, Math.max(1, (int)((level+1)*(level+1)*difficulty.homeOccupantModifier)));
        workers = Math.min(workers, fill);
        Plot plot;
        for(int i = x; i<=X; i++){
            for(int j = y; j<=Y; j++){
                for(int k = z; k<=Z; k++){
                    plot = generateAndGetPlot(i, j, k).setType(type).setLevel(level).setOwner(owner);
                    for(int l = 0; l<workers; l++){
                        Civilian worker = new Civilian().upgradeToWorker();
                        worker.homePlot = plot;
                        worker.player = owner;
                        plot.workers.add(worker);
                        plot.workersPresent.add(worker);
                        worker.x = x;
                        worker.y = y;
                        worker.z = z;
                    }
                    for(int l = 0; l<fill-workers; l++){
                        Civilian worker = new Civilian();
                        worker.homePlot = plot;
                        worker.player = owner;
                        plot.civilians.add(worker);
                        plot.civiliansPresent.add(worker);
                        worker.x = x;
                        worker.y = y;
                        worker.z = z;
                    }
                    if(fill>0){
                        schedulePlotUpdate(plot);
                    }
                }
            }
        }
    }
}
