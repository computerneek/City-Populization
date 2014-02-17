package CityPopulization.world.plot;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import CityPopulization.render.RoadRenderer;
import CityPopulization.render.StoreRenderer;
import CityPopulization.render.WarehouseRenderer;
import CityPopulization.render.WorkshopRenderer;
import CityPopulization.render.AirportRenderer;
import CityPopulization.render.CubeRenderer;
import CityPopulization.render.ForestRenderer;
import CityPopulization.render.NonRenderer;
import CityPopulization.render.PlotRenderer;
import CityPopulization.render.PumpingStationRenderer;
public enum PlotType{
    Grass("Grass", "grass", new ResourceList(Resource.Dirt, 5000), new CubeRenderer(), 1, true),
    Dirt("Dirt", "dirt", new ResourceList(Resource.Dirt, 5000), new CubeRenderer(), 1, true),
    Road("Road", "road", new ResourceList(), new RoadRenderer(), 10, false),
    Warehouse("Warehouse", "warehouse", new ResourceList(), new WarehouseRenderer(), 10, false),
    Workshop("Workshop", "workshop", new ResourceList(), new WorkshopRenderer(), 10, false),
    Store("Store", "store", new ResourceList(), new StoreRenderer(), 1, false),
    CoalDeposit("Coal", "coal", new ResourceList(Resource.Coal, 5000), new CubeRenderer(), 1, true),
    Air("Air", "air", new ResourceList(), new NonRenderer(), 1, false),
    OilDeposit("Oil", "oil", new ResourceList(Resource.Oil, 5000), new CubeRenderer(), 1, true),
    PumpingStation("Pumping Station", "pump", new ResourceList(), new PumpingStationRenderer(), 1, false),
    Woods("Forest", "woods", new ResourceList(Resource.Wood, 5000), new ForestRenderer(), 1, false),
    Stone("Stone", "stone", new ResourceList(Resource.Stone, 5000), new CubeRenderer(), 1, true),
    IronDeposit("Iron", "iron", new ResourceList(Resource.Iron, 5000), new CubeRenderer(), 1, true),
    Sand("Sand", "sand", new ResourceList(Resource.Sand, 5000), new CubeRenderer(), 1, true),
    ClayDeposit("Clay", "clay", new ResourceList(Resource.Clay, 5000), new CubeRenderer(), 1, true),
    GoldDeposit("Gold", "gold", new ResourceList(Resource.Gold, 5000), new CubeRenderer(), 1, true),
    AirportEntrance("Airport Entrance", "airport/entrance", new ResourceList(), new AirportRenderer(AirportRenderer.ENTRANCE), 1, false),
    AirportTerminal("Airport Terminal", "airport/terminal", new ResourceList(), new AirportRenderer(AirportRenderer.TERMINAL), 1, false),
    AirportJetway("Airport Jetway", "airport/jetway", new ResourceList(), new AirportRenderer(AirportRenderer.JETWAY), 1, false),
    AirportRunway("Airport Runway", "airport/runway", new ResourceList(), new AirportRenderer(AirportRenderer.RUNWAY), 1, false);
    private final String name;
    private String textureFolder;
    private ResourceList resourceHarvested;
    private PlotRenderer renderer;
    private int highestLevel;
    private boolean isOpaque;
    PlotType(String name, String textureFolder, ResourceList resourceHarvested, PlotRenderer renderer, int highestLevel, boolean isOpaque){
        this.name = name;
        this.textureFolder = textureFolder;
        this.resourceHarvested = resourceHarvested;
        this.renderer = renderer;
        this.highestLevel = highestLevel;
        this.isOpaque = isOpaque;
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
}
