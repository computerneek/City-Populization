package CityPopulization.world.plot;
import CityPopulization.Core;
import CityPopulization.world.World;
import CityPopulization.world.resource.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
public enum Template{
    FLAT("Flat"){
        @Override
        public void onPlotGenerated(World world, int x, int y, int z){
            if(z>0||Core.world!=world){
                return;
            }
            Random rand = world.getPlot(x, y, z).rand;
            double airChance = 0;
            double dirtChance = 0;
            double coalChance = 0;
            double oilChance = 0;
            double woodChance = 0;
            double stoneChance = 0;
            double ironChance = 0;
            double sandChance = 0;
            double clayChance = 0;
            double goldChance = 0;
            if(z==0){
                airChance = rand.nextGaussian()*20;
                woodChance = rand.nextGaussian();
            }else{
                dirtChance = rand.nextGaussian()*5*(z==-1?10:1);
                if(z<-1){
                    coalChance = rand.nextGaussian()*3;
                    oilChance = rand.nextGaussian()*3;
                    stoneChance = rand.nextGaussian()*10;
                    ironChance = rand.nextGaussian()*2;
                    sandChance = rand.nextGaussian()*2;
                    clayChance = rand.nextGaussian()*4;
                    goldChance = rand.nextGaussian();
                }
            }
            HashMap<Double, Resource> chances = new HashMap<>();
            chances.put(Math.abs(airChance), null);
            chances.put(Math.abs(dirtChance), Resource.Dirt);
            chances.put(Math.abs(coalChance), Resource.Coal);
            chances.put(Math.abs(oilChance), Resource.Oil);
            chances.put(Math.abs(woodChance), Resource.Wood);
            chances.put(Math.abs(stoneChance), Resource.Stone);
            chances.put(Math.abs(ironChance), Resource.Iron);
            chances.put(Math.abs(sandChance), Resource.Sand);
            chances.put(Math.abs(clayChance), Resource.Clay);
            chances.put(Math.abs(goldChance), Resource.Gold);
            ArrayList<Double> lst = new ArrayList<>(chances.keySet());
            Collections.sort(lst);
            Resource resource = chances.get(lst.get(lst.size()-1));
            if(resource==null){
                world.getPlot(x, y, z).setType(PlotType.Air);
                return;
            }
            switch(resource){
                case Oil:
                    world.getPlot(x, y, z).setType(PlotType.OilDeposit);
                    break;
                case Dirt:
                    world.getPlot(x, y, z).setType(z==-1?PlotType.Grass:PlotType.Dirt);
                    break;
                case Coal:
                    world.getPlot(x, y, z).setType(PlotType.CoalDeposit);
                    break;
                case Wood:
                {   world.getPlot(x, y, z).setType(PlotType.Woods);
                    int dist = rand.nextInt(7);
                    for(int i = -dist+1; i<dist; i++){
                        for(int j = -dist+1+Math.abs(i); j<dist-Math.abs(i); j++){
                            if((world.getPlot(x+i, y+j, z)==null||(world.getPlot(x+i, y+j, z).getType()==PlotType.Air&&world.getPlot(x+i, y+j, z).task==null))&&rand.nextGaussian()<2){
                                world.generateAndGetPlot(x+i, y+j, z).setType(PlotType.Woods);
                            }
                        }
                    }
                    break;
                }
                case Stone:
                    world.getPlot(x, y, z).setType(PlotType.Stone);
                    break;
                case Iron:
                    world.getPlot(x, y, z).setType(PlotType.IronDeposit);
                    break;
                case Sand:
                {
                    world.getPlot(x, y, z).setType(PlotType.Sand);
                    int dist = rand.nextInt(5);
                    for(int i = -dist+1; i<dist; i++){
                        int dist2 = dist-Math.abs(i);
                        for(int j = -dist2+1; j<dist2; j++){
                            int dist3 = dist2-Math.abs(j);
                            for(int k = -dist3+1; k<dist3; k++){
                                if(z>-2&&world.getPlot(x+i, y+j, z+k)==null&&rand.nextGaussian()<5){
                                    world.generateAndGetPlot(x+i, y+j, z+k).setType(PlotType.Sand);
                                }
                            }
                        }
                    }
                    break;
                }
                case Clay:
                    world.getPlot(x, y, z).setType(PlotType.ClayDeposit);
                    break;
                case Gold:
                    world.getPlot(x, y, z).setType(PlotType.GoldDeposit);
                    break;
                default:
                    throw new AssertionError(resource.name());
            }
        }
    };
    private final String name;
    Template(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    public abstract void onPlotGenerated(World world, int x, int y, int z);
}
