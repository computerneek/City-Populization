package CityPopulization.world.aircraft.cargo;
import CityPopulization.world.resource.Resource;
public class AircraftCargoResource extends AircraftCargo{
    private Resource resource;
    public AircraftCargoResource(Resource resource){
        this.resource = resource;
    }
    @Override
    public int getSpaceOccupied(){
        return 1;
    }
}
