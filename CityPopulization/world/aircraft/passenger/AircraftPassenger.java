package CityPopulization.world.aircraft.passenger;
import CityPopulization.world.civilian.Civilian;
import java.util.ArrayList;
public abstract class AircraftPassenger {
    public static ArrayList<AircraftPassenger> workers(int quantity){
        ArrayList<AircraftPassenger> lst = new ArrayList<>(quantity);
        for(int i = 0; i<quantity; i++){
            lst.add(new AircraftPassengerWorker());
        }
        return lst;
    }
    public static ArrayList<AircraftPassenger> civilians(int quantity){
        ArrayList<AircraftPassenger> lst = new ArrayList<>(quantity);
        for(int i = 0; i<quantity; i++){
            lst.add(new AircraftPassengerCivilian());
        }
        return lst;
    }
    public abstract Civilian createCivilian();
}
