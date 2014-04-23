package CityPopulization.world.story;
import CityPopulization.world.World;
public abstract class Goal{
    public abstract void update(World world);
    public abstract String getText();
    public abstract boolean isComplete();
}
