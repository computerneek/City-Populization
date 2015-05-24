package CityPopulization.world.plot;
import CityPopulization.render.AirportRenderer;
import CityPopulization.render.BankRenderer;
import CityPopulization.render.CubeRenderer;
import CityPopulization.render.DebrisRenderer;
import CityPopulization.render.ElevatorRenderer;
import CityPopulization.render.ForestRenderer;
import CityPopulization.render.HouseRenderer;
import CityPopulization.render.NonRenderer;
import CityPopulization.render.PlotRenderer;
import CityPopulization.render.RoadRenderer;
import CityPopulization.render.Side;
import CityPopulization.render.SkyscraperRenderer;
import CityPopulization.render.StoreRenderer;
import CityPopulization.render.WarehouseRenderer;
import CityPopulization.render.WorkshopRenderer;
import CityPopulization.world.player.Race;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import CityPopulization.world.resource.ResourceListList;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.lwjgl.input.Keyboard;
public enum PlotType{
    Air("Air", "air", new ResourceList(), new NonRenderer(), 1, false, false, Side.values(), false, false, 0, 0, null, false),
    Grass("Grass", "grass", new ResourceList(Resource.Dirt, 500), new CubeRenderer(), 10, true, true, Side.values(), false, true, 0, 0, null, false),
    Dirt("Dirt", "dirt", new ResourceList(Resource.Dirt, 500), new CubeRenderer(), 10, true, true, Side.values(), false, true, 0, 0, null, false),
    Road("Road", "road", new ResourceList(), new RoadRenderer(), 10, false, false, new Side[]{Side.FRONT, Side.LEFT, Side.RIGHT, Side.BACK}, true, false, Keyboard.KEY_R, 0, null, false),
    Warehouse("Warehouse", "warehouse", new ResourceList(), new WarehouseRenderer(), 10, false, true, new Side[]{Side.FRONT}, true, false, Keyboard.KEY_W, 0, null, false),
    Workshop("Workshop", "workshop", new ResourceList(), new WorkshopRenderer(), 10, false, true, new Side[]{Side.FRONT}, true, false, Keyboard.KEY_O, 0, null, false),
    Store("Store", "store", new ResourceList(), new StoreRenderer(), 1, false, true, new Side[]{Side.FRONT}, true, false, Keyboard.KEY_S, 0, null, false),
    CoalDeposit("Coal", "coal", new ResourceList(Resource.Coal, 500), new CubeRenderer(), 10, true, true, Side.values(), false, true, 0, 0, null, false),
    OilDeposit("Oil", "oil", new ResourceList(Resource.Oil, 500), new CubeRenderer(), 10, true, true, Side.values(), true, false, 0, 0, null, false),
    Woods("Forest", "woods", new ResourceList(Resource.Wood, 500), new ForestRenderer(), 10, false, true, Side.values(), true, false, 0, 0, null, false),
    Stone("Stone", "stone", new ResourceList(Resource.Stone, 500), new CubeRenderer(), 10, true, true, Side.values(), false, true, 0, 0, null, false),
    IronDeposit("Iron", "iron", new ResourceList(Resource.Iron, 500), new CubeRenderer(), 10, true, true, Side.values(), false, true, 0, 0, null, false),
    Sand("Sand", "sand", new ResourceList(Resource.Sand, 500), new CubeRenderer(), 10, true, true, Side.values(), true, true, 0, 0, null, false),
    ClayDeposit("Clay", "clay", new ResourceList(Resource.Clay, 500), new CubeRenderer(), 10, true, true, Side.values(), false, true, 0, 0, null, false),
    GoldDeposit("Gold", "gold", new ResourceList(Resource.Gold, 500), new CubeRenderer(), 10, true, true, Side.values(), false, true, 0, 0, null, false),
    AirportEntrance("Airport", "airport/entrance", new ResourceList(), new AirportRenderer(AirportRenderer.ENTRANCE), 1, false, true, new Side[]{Side.FRONT}, true, false, Keyboard.KEY_E, 0, null, false),
    AirportTerminal("Terminal", "airport/terminal", new ResourceList(), new AirportRenderer(AirportRenderer.TERMINAL), 1, false, false, new Side[]{Side.FRONT, Side.LEFT, Side.RIGHT, Side.BACK}, true, false, Keyboard.KEY_T, 0, null, false),
    AirportJetway("Jetway", "airport/jetway", new ResourceList(), new AirportRenderer(AirportRenderer.JETWAY), 1, false, false, new Side[]{Side.FRONT, Side.LEFT, Side.RIGHT, Side.BACK}, true, false, Keyboard.KEY_J, 0, null, false),
    AirportRunway("Runway", "airport/runway", new ResourceList(), new AirportRenderer(AirportRenderer.RUNWAY), 1, false, false, new Side[]{Side.FRONT, Side.LEFT, Side.RIGHT, Side.BACK}, true, false, Keyboard.KEY_N, 0, null, false),
    Bank("Bank", "bank", new ResourceList(), new BankRenderer(), 10, false, true, new Side[]{Side.FRONT}, true, false, Keyboard.KEY_B, 0, null, false),
    House("House", "house", new ResourceList(), new HouseRenderer(), 10, false, true, new Side[]{Side.FRONT}, true, false, Keyboard.KEY_H, 0, null, false),
    Elevator("Elevator", "elevator", new ResourceList(), new ElevatorRenderer(), 10, false, true, Side.values(), false, false, Keyboard.KEY_L, 0, null, false),
    Support("Support", "support", new ResourceList(), new CubeRenderer(), 1, true, true, Side.values(), false, true, Keyboard.KEY_U, 0, null, false),
    SkyscraperBase("Skyscraper", "skyscraper/base", new ResourceList(), new SkyscraperRenderer(), 10, false, true, new Side[]{Side.FRONT, Side.LEFT, Side.RIGHT, Side.BACK}, true, true, Keyboard.KEY_K, 10, null, false),
    SkyscraperFloor("Skyscraper Floor", "skyscraper/upperFloor", new ResourceList(), new SkyscraperRenderer(), SkyscraperBase.highestLevel, false, true, new Side[0], true, true, 0, 10, SkyscraperBase, false),
    Debris("Debris", "debris", new ResourceList(), new DebrisRenderer(), 1, false, false, Side.values(), true, false, 0, 0, null, false);
    public final String name;
    public final String textureFolder;
    public ResourceList resourceHarvested;
    private PlotRenderer renderer;
    private int highestLevel;
    private boolean isOpaque;
    private int[][] frameCaps;
    private final boolean causesAirlineCrash;
    private Side[] pathableSides;
    private boolean falls;
    private PlotType fallType;
    private boolean supports;
    private int hotkey;
    private int skyscraperLevels;
    public PlotType skyscraperFloorType;
    public PlotType skyscraperBaseType;
    private final boolean alwaysVisible;
    private PlotType(String name, String textureFolder, ResourceList resourceHarvested, PlotRenderer renderer, int highestLevel, boolean isOpaque, boolean causesAirlineCrash, Side[] pathableSides, boolean falls, boolean supports, int hotkey, int skyscraperLevels, PlotType skyscraperBaseType, boolean alwaysVisible){
        this.name = name;
        this.textureFolder = textureFolder;
        this.resourceHarvested = resourceHarvested;
        this.renderer = renderer;
        this.highestLevel = highestLevel;
        this.isOpaque = isOpaque;
        this.causesAirlineCrash=causesAirlineCrash;
        frameCaps = findFrameCaps();
        this.pathableSides = pathableSides;
        this.falls = falls;
        this.supports = supports;
        this.hotkey = hotkey;
        this.skyscraperLevels=skyscraperLevels;
        if(skyscraperBaseType!=null){
            skyscraperBaseType.skyscraperFloorType=this;
        }
        this.skyscraperBaseType = skyscraperBaseType;
        this.alwaysVisible=alwaysVisible;
    }
    boolean isAlwaysVisible(){
        return alwaysVisible;
    }
    public int getSkyscraperHeight(){
        return skyscraperLevels;
    }
    public int getMaximumLevel(){
        return highestLevel;
    }
    public boolean isOpaque(){
        return isOpaque;
    }
    public void render(Plot plot){
        renderer.render(plot, textureFolder);
    }
    private int[][] findFrameCaps(){
        int[][] frameCaps = new int[highestLevel][];
        for(int i = 1; i<highestLevel+1; i++){
            for(int j = 1; true; j++){
                boolean skip = true;
                String[] paths = renderer.getPaths(this, 1, textureFolder);
                frameCaps[i-1] = new int[paths.length];
                for(int k = 0; k<paths.length; k++){
                    String path = paths[k].substring(2).replaceAll("<LEVEL>", ""+i).replaceAll("<FRAME>", ""+j);
                    try(InputStream in = PlotType.class.getResourceAsStream(path)){
                        if(in==null){
                            frameCaps[i-1][k] = j-1;
                        }
                    }catch(IOException ex){}
                    if(frameCaps[i-1][k]<1){
                        skip = false;
                    }
                }
                if(skip){
                    break;
                }
            }
        }
        return frameCaps;
    }
    public int getFrameCap(int level, int texture){
        return frameCaps[level-1][texture];
    }
    public boolean causesAirlineCrash(){
        return causesAirlineCrash;
    }
    public ArrayList<Side> getPathableSides(boolean isWorker){
        if(this==House&&isWorker){
            return new ArrayList<>(Arrays.asList(new Side[]{Side.FRONT, Side.LEFT, Side.RIGHT, Side.BACK}));
        }else if(falls){
            ArrayList<Side> lst = new ArrayList<>(Arrays.asList(pathableSides));
            lst.remove(Side.DOWN);
            return lst;
        }
        return new ArrayList<>(Arrays.asList(pathableSides));
    }
    public ResourceList getConstructionCost(Race race){
        return getCost(0, race);
    }
    private static HashMap<Race, HashMap<PlotType, ResourceListList>> constructionCosts = new HashMap<>();
    static{
        HashMap<PlotType, ResourceListList> costs = new HashMap<>();
        constructionCosts.put(Race.HUMAN, costs);
        costs.put(Road, new ResourceListList(new ResourceList(Resource.Dirt, 25), new ResourceList(Resource.Dirt, 25), new ResourceList(Resource.Stone, 25), new ResourceList(Resource.Stone, 25)));
        costs.put(House, new ResourceListList(new ResourceList(Resource.Wood, 50), new ResourceList(Resource.Wood, 50), new ResourceList(Resource.Wood, 50, Resource.Coal, 5), new ResourceList(Resource.Wood, 25, Resource.Coal, 5, Resource.Sand, 25), new ResourceList(Resource.Wood, 25, Resource.Coal, 5, Resource.Sand, 25, Resource.Oil, 25), new ResourceList(Resource.Wood, 25, Resource.Coal, 5, Resource.Oil, 50), new ResourceList(Resource.Wood, 25, Resource.Coal, 5, Resource.Oil, 50), new ResourceList(Resource.Wood, 100, Resource.Coal, 5), new ResourceList(Resource.Wood, 100, Resource.Coal, 5)));
        costs.put(Warehouse, new ResourceListList(new ResourceList(Resource.Wood, 50), new ResourceList(Resource.Stone, 50), new ResourceList(Resource.Iron, 100), new ResourceList(Resource.Iron, 100), new ResourceList(Resource.Iron, 100), new ResourceList(Resource.Iron, 100), new ResourceList(Resource.Iron, 100), new ResourceList(Resource.Iron, 100), new ResourceList(Resource.Iron, 100), new ResourceList(Resource.Iron, 100)));
        costs.put(Elevator, new ResourceListList(new ResourceList(Resource.Iron, 20), new ResourceList(Resource.Iron, 20), new ResourceList(Resource.Iron, 20), new ResourceList(Resource.Iron, 20)));
        costs.put(Workshop, new ResourceListList(new ResourceList(Resource.Wood, 50, Resource.Tools, 5)));
        costs.put(AirportRunway, new ResourceListList(new ResourceList(Resource.Stone, 100, Resource.Iron, 25)));
        costs.put(AirportJetway, new ResourceListList(new ResourceList(Resource.Stone, 50)));
        costs.put(AirportTerminal, new ResourceListList(new ResourceList(Resource.Wood, 100, Resource.Stone, 50, Resource.Iron, 50, Resource.Coal, 5, Resource.Clay, 50, Resource.Oil, 25)));
        costs.put(AirportEntrance, new ResourceListList(new ResourceList(Resource.Wood, 100, Resource.Stone, 50, Resource.Iron, 50, Resource.Coal, 5, Resource.Clay, 50, Resource.Oil, 25)));
        costs.put(Support, new ResourceListList(new ResourceList(Resource.Iron, 100)));
//        costs.put(SkyscraperBase, new ResourceListList());
        for(int i = 0; i<10; i++){
//            costs.get(SkyscraperBase).add(new ResourceList(Resource.Iron, 30, Resource.Wood, 50, Resource.Sand, 50, Resource.Clay, 25, Resource.Oil, 75));
        }
    }
    public int getTextureIndex(String string){
        return new ArrayList<String>(Arrays.asList(renderer.getPaths(this, 1, textureFolder))).indexOf(string);
    }
    public ResourceList getCost(int level, Race race){
        HashMap<PlotType, ResourceListList> costs = constructionCosts.get(race);
        if(costs==null){
            return null;
        }
        if(costs.get(this)!=null&&costs.get(this).get(level)==null){
            return new ResourceList();
        }
        return (costs.get(this)!=null&&costs.get(this).get(level)!=null)?new ResourceList().addAll(costs.get(this).get(level)):null;
    }
    public boolean falls(){
        return falls;
    }
    public boolean supports(){
        return supports;
    }
    public PlotType getFallenType(){
        if((supports()||this==PlotType.OilDeposit)&&skyscraperBaseType==null&&skyscraperFloorType==null){
            return this;
        }else{
            return Debris;
        }
    }
    public int getHotkey(){
        return hotkey;
    }
}
