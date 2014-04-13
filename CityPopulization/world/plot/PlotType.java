package CityPopulization.world.plot;
import CityPopulization.render.AirportRenderer;
import CityPopulization.render.BankRenderer;
import CityPopulization.render.CubeRenderer;
import CityPopulization.render.ElevatorRenderer;
import CityPopulization.render.ForestRenderer;
import CityPopulization.render.HouseRenderer;
import CityPopulization.render.NonRenderer;
import CityPopulization.render.PlotRenderer;
import CityPopulization.render.RoadRenderer;
import CityPopulization.render.Side;
import CityPopulization.render.StoreRenderer;
import CityPopulization.render.WarehouseRenderer;
import CityPopulization.render.WorkshopRenderer;
import CityPopulization.texturepack.Texture;
import CityPopulization.texturepack.TexturepackCreator;
import CityPopulization.world.player.Race;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import CityPopulization.world.resource.ResourceListList;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
public enum PlotType{
    Grass("Grass", "grass", new ResourceList(Resource.Dirt, 5000), new CubeRenderer(), 1, true, true, Side.values()),
    Dirt("Dirt", "dirt", new ResourceList(Resource.Dirt, 5000), new CubeRenderer(), 1, true, true, Side.values()),
    Road("Road", "road", new ResourceList(), new RoadRenderer(), 10, false, false, new Side[]{Side.FRONT, Side.LEFT, Side.RIGHT, Side.BACK}),
    Warehouse("Warehouse", "warehouse", new ResourceList(), new WarehouseRenderer(), 10, false, true, new Side[]{Side.FRONT}),
    Workshop("Workshop", "workshop", new ResourceList(), new WorkshopRenderer(), 10, false, true, new Side[]{Side.FRONT}),
    Store("Store", "store", new ResourceList(), new StoreRenderer(), 1, false, true, new Side[]{Side.FRONT}),
    CoalDeposit("Coal", "coal", new ResourceList(Resource.Coal, 5000), new CubeRenderer(), 1, true, true, Side.values()),
    Air("Air", "air", new ResourceList(), new NonRenderer(), 1, false, false, Side.values()),
    OilDeposit("Oil", "oil", new ResourceList(Resource.Oil, 5000), new CubeRenderer(), 1, true, true, new Side[0]),
    Woods("Forest", "woods", new ResourceList(Resource.Wood, 5000), new ForestRenderer(), 1, false, true, Side.values()),
    Stone("Stone", "stone", new ResourceList(Resource.Stone, 5000), new CubeRenderer(), 1, true, true, Side.values()),
    IronDeposit("Iron", "iron", new ResourceList(Resource.Iron, 5000), new CubeRenderer(), 1, true, true, Side.values()),
    Sand("Sand", "sand", new ResourceList(Resource.Sand, 5000), new CubeRenderer(), 1, true, true, Side.values()),
    ClayDeposit("Clay", "clay", new ResourceList(Resource.Clay, 5000), new CubeRenderer(), 1, true, true, Side.values()),
    GoldDeposit("Gold", "gold", new ResourceList(Resource.Gold, 5000), new CubeRenderer(), 1, true, true, Side.values()),
    AirportEntrance("Airport Entrance", "airport/entrance", new ResourceList(), new AirportRenderer(AirportRenderer.ENTRANCE), 1, false, true, new Side[]{Side.FRONT}),
    AirportTerminal("Airport Terminal", "airport/terminal", new ResourceList(), new AirportRenderer(AirportRenderer.TERMINAL), 1, false, false, new Side[]{Side.FRONT, Side.LEFT, Side.RIGHT, Side.BACK}),
    AirportJetway("Airport Jetway", "airport/jetway", new ResourceList(), new AirportRenderer(AirportRenderer.JETWAY), 1, false, false, new Side[]{Side.FRONT, Side.LEFT, Side.RIGHT, Side.BACK}),
    AirportRunway("Airport Runway", "airport/runway", new ResourceList(), new AirportRenderer(AirportRenderer.RUNWAY), 1, false, false, new Side[]{Side.FRONT, Side.LEFT, Side.RIGHT, Side.BACK}),
    Bank("Bank", "bank", new ResourceList(), new BankRenderer(), 10, false, true, new Side[]{Side.FRONT}),
    House("House", "house", new ResourceList(), new HouseRenderer(), 10, false, true, new Side[]{Side.FRONT}),
    Elevator("Elevator", "elevator", new ResourceList(), new ElevatorRenderer(), 10, false, true, Side.values());
    public final String name;
    public final String textureFolder;
    public ResourceList resourceHarvested;
    private PlotRenderer renderer;
    private int highestLevel;
    private boolean isOpaque;
    private int[][] frameCaps;
    private final boolean causesAirlineCrash;
    private Side[] pathableSides;
    PlotType(String name, String textureFolder, ResourceList resourceHarvested, PlotRenderer renderer, int highestLevel, boolean isOpaque, boolean causesAirlineCrash, Side[] pathableSides){
        this.name = name;
        this.textureFolder = textureFolder;
        this.resourceHarvested = resourceHarvested;
        this.renderer = renderer;
        this.highestLevel = highestLevel;
        this.isOpaque = isOpaque;
        this.causesAirlineCrash=causesAirlineCrash;
        frameCaps = findFrameCaps();
        this.pathableSides = pathableSides;
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
                String[] paths = renderer.getPaths(1, textureFolder);
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
    public ArrayList<Side> getPathableSides(){
        return new ArrayList<Side>(Arrays.asList(pathableSides));
    }
    public ResourceList getConstructionCost(Race race){
        return getCost(0, race);
    }
    private static HashMap<Race, HashMap<PlotType, ResourceListList>> constructionCosts = new HashMap<>();
    static{
        HashMap<PlotType, ResourceListList> costs = new HashMap<>();
        constructionCosts.put(Race.HUMAN, costs);
        costs.put(Road, new ResourceListList(new ResourceList(Resource.Dirt, 25)));
        costs.put(House, new ResourceListList(new ResourceList(Resource.Wood, 50), new ResourceList(Resource.Wood, 100)));
        costs.put(Warehouse, new ResourceListList(new ResourceList(Resource.Wood, 50)));
        costs.put(Elevator, new ResourceListList(new ResourceList(Resource.Iron, 100)));
    }
    public void loadAllTextures(){
        for(String texture : renderer.getPaths(highestLevel, textureFolder)){
            TexturepackCreator.addTexture(new Texture(texture));
        }
    }
    public void loadAllSounds(){
        for(PlotType type : values()){
            
        }
    }
    public int getTextureIndex(String string){
        return new ArrayList<String>(Arrays.asList(renderer.getPaths(1, textureFolder))).indexOf(string);
    }
    public ResourceList getCost(int level, Race race){
        HashMap<PlotType, ResourceListList> costs = constructionCosts.get(race);
        if(costs==null){
            return null;
        }
        return (costs.get(this)!=null&&costs.get(this).get(level)!=null)?new ResourceList().addAll(costs.get(this).get(level)):null;
    }
}
