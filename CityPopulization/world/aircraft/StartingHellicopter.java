package CityPopulization.world.aircraft;
import CityPopulization.world.aircraft.landingSequence.LSEMovement;
import CityPopulization.world.aircraft.landingSequence.LSEPitch;
import CityPopulization.world.aircraft.landingSequence.LSEStartingPoint;
import CityPopulization.world.aircraft.landingSequence.LandingSequenceEvent;
import CityPopulization.world.player.Player;
import java.util.ArrayList;
public class StartingHellicopter extends Helicopter{
    public StartingHellicopter(Player player){
        super(player, "initial");
        passengerCapacity = 4;
        cargoCapacity = 3000;
        maxFuelLevel = 49;
    }
    @Override
    public ArrayList<LandingSequenceEvent> getLandingSequence(){
        passengerCapacity = 2;
        ArrayList<LandingSequenceEvent> lst = new ArrayList<>();
        lst.add(new LSEStartingPoint(7, 4, topSpeed));
        lst.add(new LSEMovement(131, 1));
        lst.add(new LSEPitch(-90));
        lst.add(new LSEMovement(191, 1));
        lst.add(new LSEPitch(0));
        lst.add(new LSEMovement(30, 1));
        lst.add(new LSEMovement(20, 0));
        lst.add(new LSEStartingPoint(0, 0, 0));
        return lst;
    }
    @Override
    public ArrayList<LandingSequenceEvent> getTakeoffSequence(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
