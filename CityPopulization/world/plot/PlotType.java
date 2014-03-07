package CityPopulization.world.plot;
import CityPopulization.render.AirportRenderer;
import CityPopulization.render.CubeRenderer;
import CityPopulization.render.ForestRenderer;
import CityPopulization.render.NonRenderer;
import CityPopulization.render.PlotRenderer;
import CityPopulization.render.PumpingStationRenderer;
import CityPopulization.render.RoadRenderer;
import CityPopulization.render.Side;
import CityPopulization.render.StoreRenderer;
import CityPopulization.render.WarehouseRenderer;
import CityPopulization.render.WorkshopRenderer;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
public enum PlotType{
    Grass("Grass", "grass", new ResourceList(Resource.Dirt, 5000), new CubeRenderer(), 1, true, true, Side.values()),
    Dirt("Dirt", "dirt", new ResourceList(Resource.Dirt, 5000), new CubeRenderer(), 1, true, true, Side.values()),
    Road("Road", "road", new ResourceList(), new RoadRenderer(), 10, false, false, Side.values()),
    Warehouse("Warehouse", "warehouse", new ResourceList(), new WarehouseRenderer(), 10, false, true, new Side[]{Side.FRONT}),
    Workshop("Workshop", "workshop", new ResourceList(), new WorkshopRenderer(), 10, false, true, new Side[]{Side.FRONT}),
    Store("Store", "store", new ResourceList(), new StoreRenderer(), 1, false, true, new Side[]{Side.FRONT}),
    CoalDeposit("Coal", "coal", new ResourceList(Resource.Coal, 5000), new CubeRenderer(), 1, true, true, Side.values()),
    Air("Air", "air", new ResourceList(), new NonRenderer(), 1, false, false, Side.values()),
    OilDeposit("Oil", "oil", new ResourceList(Resource.Oil, 5000), new CubeRenderer(), 1, true, true, new Side[0]),
    PumpingStation("Pumping Station", "pump", new ResourceList(), new PumpingStationRenderer(), 1, false, true, Side.values()),
    Woods("Forest", "woods", new ResourceList(Resource.Wood, 5000), new ForestRenderer(), 1, false, true, Side.values()),
    Stone("Stone", "stone", new ResourceList(Resource.Stone, 5000), new CubeRenderer(), 1, true, true, Side.values()),
    IronDeposit("Iron", "iron", new ResourceList(Resource.Iron, 5000), new CubeRenderer(), 1, true, true, Side.values()),
    Sand("Sand", "sand", new ResourceList(Resource.Sand, 5000), new CubeRenderer(), 1, true, true, Side.values()),
    ClayDeposit("Clay", "clay", new ResourceList(Resource.Clay, 5000), new CubeRenderer(), 1, true, true, Side.values()),
    GoldDeposit("Gold", "gold", new ResourceList(Resource.Gold, 5000), new CubeRenderer(), 1, true, true, Side.values()),
    AirportEntrance("Airport Entrance", "airport/entrance", new ResourceList(), new AirportRenderer(AirportRenderer.ENTRANCE), 1, false, true, new Side[]{Side.FRONT}),
    AirportTerminal("Airport Terminal", "airport/terminal", new ResourceList(), new AirportRenderer(AirportRenderer.TERMINAL), 1, false, false, Side.values()),
    AirportJetway("Airport Jetway", "airport/jetway", new ResourceList(), new AirportRenderer(AirportRenderer.JETWAY), 1, false, false, Side.values()),
    AirportRunway("Airport Runway", "airport/runway", new ResourceList(), new AirportRenderer(AirportRenderer.RUNWAY), 1, false, false, Side.values());
    private final String name;
    private String textureFolder;
    private ResourceList resourceHarvested;
    private PlotRenderer renderer;
    private int highestLevel;
    private boolean isOpaque;
    private int[] frameCaps;
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
    private int[] findFrameCaps(){
        int[] frameCaps = new int[highestLevel];
        for(int i = 1; i<highestLevel+1; i++){
            for(int j = 1; frameCaps[i-1]==0; j++){
                String path = "/textures/plots/"+textureFolder+"/level "+i+"/frame "+j+".png";
                try(InputStream in = PlotType.class.getResourceAsStream(path)){
                    if(in==null){
                        frameCaps[i-1] = j-1;
                    }
                }catch(IOException ex){}
            }
        }
        return frameCaps;
    }
    public int getFrameCap(int level){
        return frameCaps[level-1];
    }
    public boolean causesAirlineCrash(){
        return causesAirlineCrash;
    }
    public ArrayList<Side> getPathableSides(){
        return new ArrayList<Side>(Arrays.asList(pathableSides));
    }
}
