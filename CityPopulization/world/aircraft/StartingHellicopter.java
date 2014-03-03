package CityPopulization.world.aircraft;
import CityPopulization.world.aircraft.landingSequence.LSEMovement;
import CityPopulization.world.aircraft.landingSequence.LSEStartingPoint;
import CityPopulization.world.aircraft.landingSequence.LandingSequenceEvent;
import CityPopulization.world.player.Player;
import java.util.ArrayList;
public class StartingHellicopter extends Helicopter{
    public StartingHellicopter(Player player){
        super(player);
        passengerCapacity = 4;
        cargoCapacity = 2000;
    }
    @Override
    public ArrayList<LandingSequenceEvent> getLandingSequence(){
        ArrayList<LandingSequenceEvent> lst = new ArrayList<>();
        lst.add(new LSEStartingPoint(3, 4, topSpeed));
        lst.add(new LSEMovement(0, 4, 1));
        lst.add(new LSEMovement(0, 0, 1));
        return lst;
    }
}
