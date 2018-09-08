package CityPopulization.world.aircraft.passenger;
import CityPopulization.world.civilian.Civilian;
import java.util.ArrayList;
import simplelibrary.config2.Config;
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
    public static AircraftPassenger load(Config config){
        switch((String)config.get("type")){
            case "civilian":
                return new AircraftPassengerCivilian();
            case "worker":
                return new AircraftPassengerWorker();
            default:
                throw new AssertionError((String)config.get("type"));
        }
    }
    public abstract Civilian createCivilian();
    public abstract Config save();
}
