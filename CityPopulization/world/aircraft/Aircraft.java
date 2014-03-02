package CityPopulization.world.aircraft;
import CityPopulization.world.aircraft.cargo.AircraftCargo;
import CityPopulization.world.aircraft.passenger.AircraftPassenger;
import CityPopulization.world.player.Player;
import java.util.ArrayList;
import java.util.Iterator;
public abstract class Aircraft{
    public final Player player;
    private ArrayList<AircraftPassenger> passengers = new ArrayList<>();
    private ArrayList<AircraftCargo> cargo = new ArrayList<>();
    protected int passengerCapacity;
    protected int cargoCapacity;
    private int departureTime;
    private int cargoOccupied;
    public Aircraft(Player player){
        this.player = player;
    }
    public Aircraft loadPassengers(ArrayList<AircraftPassenger> passengers){
        for(Iterator<AircraftPassenger> it=passengers.iterator(); it.hasNext();){
            AircraftPassenger passenger=it.next();
            if(this.passengerCapacity>this.passengers.size()){
                this.passengers.add(passenger);
                it.remove();
            }
        }
        return this;
    }
    public Aircraft setDepartureTime(int departureTime){
        this.departureTime = departureTime;
        return this;
    }
    public Aircraft loadCargo(ArrayList<AircraftCargo> resources){
        for(Iterator<AircraftCargo> it=resources.iterator(); it.hasNext();){
            AircraftCargo cargo=it.next();
            if(cargoCapacity-cargoOccupied>=cargo.getSpaceOccupied()){
                this.cargo.add(cargo);
                cargoOccupied+=cargo.getSpaceOccupied();
                it.remove();
            }
        }
        return this;
    }
}
