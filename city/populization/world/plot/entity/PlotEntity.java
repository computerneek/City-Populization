package city.populization.world.plot.entity;
import city.populization.world.World;
import city.populization.world.plot.PlotPos;
import java.util.Random;
import simplelibrary.config2.Config;
public abstract class PlotEntity {
    public World world;
    public PlotPos pos;
    public Random rand;
    public abstract Config save();
    /**
     * Called when loading a structure, this must load any data necessary to complete the PlotEntity
     */
    public abstract void load(Config data);
    public abstract void tick();
    /**
     * Called when spawning or constructing a structure, this must generate any data necessary to complete the PlotEntity
     */
    public abstract void construct();
}
