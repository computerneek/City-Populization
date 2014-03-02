package CityPopulization.world.aircraft.cargo;
import CityPopulization.world.resource.Resource;
import java.util.ArrayList;
public abstract class AircraftCargo {
    public static ArrayList<AircraftCargo> resource(Resource resource, int quantity){
        ArrayList<AircraftCargo> lst = new ArrayList<>(quantity);
        for(int i = 0; i<quantity; i++){
            lst.add(new AircraftCargoResource(resource));
        }
        return lst;
    }
    public abstract int getSpaceOccupied();
}
