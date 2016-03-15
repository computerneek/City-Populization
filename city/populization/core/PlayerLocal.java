package city.populization.core;
import city.populization.world.Player;
import city.populization.world.World;
public class PlayerLocal extends Player {
    public PlayerLocal(World world) {
        super(world);
    }
    @Override
    public void setCash(long cash) {
        this.cash = cash;
    }
}
