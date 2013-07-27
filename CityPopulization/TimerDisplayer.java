package CityPopulization;
import java.util.ArrayList;
import java.util.Arrays;
import multilib.gui.ImageStash;
public class TimerDisplayer {
    public static void render(){
        int civilliansComing = CivillianScheduler.plotsInNeedOfCivillians.size();
        int civilliansLeft = civilliansComing;
        int civillianLayers = civilliansComing>0?1:0;
        int covered = 100;
        int toCover = 200;
        while(covered<civilliansComing){
            civillianLayers++;
            civilliansLeft = civilliansComing-covered;
            covered+=toCover;
            toCover+=100;
        }
        int workersComing = CivillianScheduler.plotsInNeedOfWorkers.size();
        int workersLeft = workersComing;
        int workerLayers = workersComing>0?1:0;
        covered = 100;
        toCover = 200;
        while(covered<workersComing){
            workerLayers++;
            workersLeft = workersComing-covered;
            covered+=toCover;
            toCover+=100;
        }
        int civillianTime = (civillianLayers-1)*100+civilliansLeft/Math.max(civillianLayers, 1);
        int workerTime = (workerLayers-1)*100+workersLeft/Math.max(workerLayers, 1);
        civillianTime*=(Plot.getMainBase().getMaximumLevel()-Plot.getMainBase().getLevel())*5+1;
        ArrayList<Plot> lst = new ArrayList<>();
        lst.addAll(Arrays.asList(main.getPlotsOfType(PlotType.workshop)));
        Plot barracks = lst.size()>0?lst.get(0):null;
        if(barracks!=null){
            workerTime*=(barracks.getMaximumLevel()-barracks.getLevel())*200+1;
        }
        civillianTime-=CivillianScheduler.civillianTimeout/Math.max(1, civillianLayers);
        workerTime-=CivillianScheduler.workerTimeout/Math.max(1, workerLayers);
        double civillianSeconds = ((double)civillianTime)/20D;
        double workerSeconds = ((double)workerTime)/20D;
        int civillianMinutes = (int)((civillianSeconds-(civillianSeconds%60))/60);
        int workerMinutes = (int)((workerSeconds-(workerSeconds%60))/60);
        civillianSeconds-=civillianMinutes*60;
        workerSeconds-=workerMinutes*60;
        int civillianHours = (civillianMinutes-(civillianMinutes%60))/60;
        int workerHours = (workerMinutes-(workerMinutes%60))/60;
        civillianMinutes-=civillianHours*60;
        workerMinutes-=workerHours*60;
        civillianSeconds = Math.round(civillianSeconds*20)/20D;
        workerSeconds = Math.round(workerSeconds*20)/20D;
        Renderer.drawScaledRect(300, 350, 1300, 600, ImageStash.instance.getTexture("/textbox_1000_250.png"));
        Renderer.drawScaledText(325, 365, 1295, 385, "Civillians incoming:  "+CivillianScheduler.plotsInNeedOfCivillians.size());
        if(CivillianScheduler.plotsInNeedOfCivillians.size()>0){
            Renderer.drawScaledText(325, 385, 1295, 405, "Civillian tier:  "+civillianLayers);
            Renderer.drawScaledText(325, 405, 1295, 425, "Time:  "+civillianHours+":"+civillianMinutes+":"+civillianSeconds);
        }else{
            Renderer.drawScaledText(325, 385, 1295, 405, "Civillian tier:  0");
            Renderer.drawScaledText(325, 405, 1295, 425, "Time:  0");
        }
        if(main.lagTicks>=5){
            Renderer.drawScaledText(325, 445, 1295, 465, "Lag Detected- "+main.lagTicks+" ticks pending!");
            Renderer.drawScaledText(325, 485, 1295, 505, "Lag Detected- "+main.lagTicks+" ticks pending!");
        }
        Renderer.drawScaledText(325, 465, 1295, 485, "FPS:  "+(((float)main.FPStracker.size())/5F));
        Renderer.drawScaledText(325, 525, 1295, 545, "Workers incoming:  "+CivillianScheduler.plotsInNeedOfWorkers.size());
        if(barracks!=null&&CivillianScheduler.plotsInNeedOfWorkers.size()>0){
            Renderer.drawScaledText(325, 545, 1295, 565, "Worker tier:  "+workerLayers);
            Renderer.drawScaledText(325, 565, 1295, 585, "Time:  "+workerHours+":"+workerMinutes+":"+workerSeconds);
        }else if(barracks!=null){
            Renderer.drawScaledText(325, 545, 1295, 565, "Worker tier:  0");
            Renderer.drawScaledText(325, 565, 1295, 585, "Time:  0");
        }else{
            Renderer.drawScaledText(325, 545, 1295, 565, "Worker tier:  0");
            Renderer.drawScaledText(325, 565, 1295, 585, "Time:  Infinite (No Workshop)");
        }
    }
}
