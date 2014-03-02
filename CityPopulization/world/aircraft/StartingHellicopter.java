package CityPopulization.world.aircraft;
import CityPopulization.world.player.Player;
public class StartingHellicopter extends Helicopter{
    public StartingHellicopter(Player player){
        super(player);
        passengerCapacity = 4;
        cargoCapacity = 2000;
    }
}
