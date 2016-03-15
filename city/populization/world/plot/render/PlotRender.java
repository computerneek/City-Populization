package city.populization.world.plot.render;
import city.populization.world.Chunk;
import city.populization.world.Direction;
import city.populization.world.plot.PlotPos;
public abstract class PlotRender {
    public abstract void render(Chunk world, PlotPos pos);
    public Direction getFront(Chunk world, PlotPos pos, Direction heading){
        return heading;
    }
}
