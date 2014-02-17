package CityPopulization.world.plot;
import CityPopulization.world.World;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.plot.PlotType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
public enum Template{
    FLAT("Flat"){
        @Override
        public void onPlotGenerated(World world, int x, int y, int z){
            if(z>0){
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
                airChance = rand.nextGaussian()*10;
                woodChance = rand.nextGaussian();
            }else{
                coalChance = rand.nextGaussian()*3/2;
                oilChance = rand.nextGaussian()*3/2;
                stoneChance = rand.nextGaussian()*10;
                ironChance = rand.nextGaussian();
                sandChance = rand.nextGaussian()*2;
                clayChance = rand.nextGaussian();
                goldChance = rand.nextGaussian()/5;
            }
            HashMap<Double, Resource> chances = new HashMap<>();
            chances.put(Math.abs(dirtChance), Resource.Dirt);
            chances.put(Math.abs(coalChance), Resource.Coal);
            chances.put(Math.abs(oilChance), Resource.Oil);
            chances.put(Math.abs(woodChance), Resource.Wood);
            chances.put(Math.abs(stoneChance), Resource.Stone);
            chances.put(Math.abs(ironChance), Resource.Iron);
            chances.put(Math.abs(sandChance), Resource.Sand);
            chances.put(Math.abs(clayChance), Resource.Clay);
            chances.put(Math.abs(goldChance), Resource.Gold);
            ArrayList<Double> lst = new ArrayList(chances.keySet());
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
                    world.getPlot(x, y, z).setType(PlotType.Woods);
                    break;
                case Stone:
                    world.getPlot(x, y, z).setType(PlotType.Stone);
                    break;
                case Iron:
                    world.getPlot(x, y, z).setType(PlotType.IronDeposit);
                    break;
                case Sand:
                    world.getPlot(x, y, z).setType(PlotType.Sand);
                    break;
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
