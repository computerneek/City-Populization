package city.populization.world.plot.entity;
import city.populization.world.civilian.Life;
import city.populization.world.resource.Goods;
import city.populization.world.resource.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import simplelibrary.config2.Config;
public class PlotEntityFarm extends PlotEntity{
    private double fertilizer;
    private double growingProgress;
    private ArrayList<Goods> goods = new ArrayList<>();
    private Resource crop;
    private Random rnd = new Random();
    private static HashMap<Resource, Integer> longevities = new HashMap<>();
    private static HashMap<Resource, Integer> growthTimes = new HashMap<>();
    private boolean rotted;
    private int rot;
    static{
        addCrop(Resource.Wheat, 20, 2);
        addCrop(Resource.SaladGreens, 3, 3);
        addCrop(Resource.Apples, 5, 10);
        addCrop(Resource.Grapes, 2, 2);
        addCrop(Resource.Sugar, 15, 4);
        longevities.put(Resource.Wheat, 20);
        longevities.put(Resource.SaladGreens, 2);
        longevities.put(Resource.Grapes, 3);
        longevities.put(Resource.Apples, 2);
        longevities.put(Resource.Sugar, 2);
    }//TODO hold full-time job opportunity:  1 farmer
    public static void addCrop(Resource resource, double longevity, double growthTime){
        longevities.put(resource, (int)(longevity*Life.ticksPerDay));
        growthTimes.put(resource, (int)(growthTime*Life.ticksPerDay));
    }
    @Override
    public Config save() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO save the farm!
    }
    @Override
    public void load(Config data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void tick() {
        if(!rotted&&rot>0){
            rot--;
        }
        if(crop!=null){
            growingProgress+=rnd.nextDouble()*fertilizer;
            fertilizer*=0.99;
            if(growingProgress>=getGrowthTime(crop)+Life.ticksPerDay){
                rotted = true;
            }
        }
        if(rotted){
            rot++;
            if(rot%(Life.ticksPerDay/24)==0){//Every hour- that makes every 50 real seconds
                stink(rot/(Life.ticksPerDay/24));
            }
        }
    }
    @Override
    public void construct() {
        growingProgress = 0;
        crop = null;
        if(world.getTime()<0){
            crop = new ArrayList<>(longevities.keySet()).get(rand.nextInt(longevities.size()));
            growingProgress = rand.nextInt(getGrowthTime(crop));
            fertilizer = rand.nextDouble()*10;
        }
    }
    public static int getGrowthTime(Resource r) {
        return growthTimes.containsKey(r)?growthTimes.get(r):-1;
    }
    private void stink(int stench) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO apply rotting stench to nearby structures
    }
}
