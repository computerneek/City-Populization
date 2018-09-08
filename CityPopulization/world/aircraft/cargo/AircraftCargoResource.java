package CityPopulization.world.aircraft.cargo;
import CityPopulization.world.resource.Resource;
import simplelibrary.config2.Config;
public class AircraftCargoResource extends AircraftCargo{
    private Resource resource;
    public AircraftCargoResource(Resource resource){
        this.resource = resource;
    }
    @Override
    public int getSpaceOccupied(){
        return 1;
    }
    public Resource getResource(){
        return resource;
    }
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "resource");
        config.set("resource", resource.name());
        return config;
    }
    public int value(){
        return resource.getCost(1);
    }
}
