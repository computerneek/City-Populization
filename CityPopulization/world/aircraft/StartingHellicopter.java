package CityPopulization.world.aircraft;
import CityPopulization.world.aircraft.landingSequence.LSEMovement;
import CityPopulization.world.aircraft.landingSequence.LSEPitch;
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
        lst.add(new LSEStartingPoint(6, 4, topSpeed));
        lst.add(new LSEMovement(89, 1));
        lst.add(new LSEPitch(-90));
        lst.add(new LSEMovement(141, 1));
        lst.add(new LSEPitch(0));
        lst.add(new LSEMovement(30, 1));
        lst.add(new LSEMovement(20, 0));
        return lst;
    }
}
