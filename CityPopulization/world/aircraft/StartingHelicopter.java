package CityPopulization.world.aircraft;
import CityPopulization.world.aircraft.landingSequence.LandingSequenceEvent;
import CityPopulization.world.player.Player;
import java.util.ArrayList;
public class StartingHelicopter extends Helicopter{
    public StartingHelicopter(Player player){
        super(player, "initial");
        passengerCapacity = 4;
        cargoCapacity = 2000;
        maxFuelLevel = 9;
    }
    @Override
    public ArrayList<LandingSequenceEvent> getLandingSequence(){
        passengerCapacity = 0;
        return super.getLandingSequence();
    }
}
