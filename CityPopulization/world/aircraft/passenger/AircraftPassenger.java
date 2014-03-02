package CityPopulization.world.aircraft.passenger;
import java.util.ArrayList;
public class AircraftPassenger {
    public static ArrayList<AircraftPassenger> workers(int quantity){
        ArrayList<AircraftPassenger> lst = new ArrayList<>(quantity);
        for(int i = 0; i<quantity; i++){
            lst.add(new AircraftPassengerWorker());
        }
        return lst;
    }
    public static ArrayList<AircraftPassenger> civillians(int quantity){
        ArrayList<AircraftPassenger> lst = new ArrayList<>(quantity);
        for(int i = 0; i<quantity; i++){
            lst.add(new AircraftPassengerCivillian());
        }
        return lst;
    }
}
