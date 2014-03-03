package CityPopulization.world.aircraft;
import CityPopulization.world.player.Player;
public abstract class Helicopter extends Aircraft{
    public static final int topSpeed = 5;
    public Helicopter(Player player){
        super(player);
    }
    @Override
    public int getRequiredRunwayLength(){
        return 1;
    }
}
