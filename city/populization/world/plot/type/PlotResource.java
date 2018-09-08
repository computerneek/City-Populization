package city.populization.world.plot.type;
import city.populization.world.Direction;
import city.populization.world.plot.Plot;
import city.populization.world.resource.Resource;
import city.populization.world.plot.render.PlotRender;
import java.util.Arrays;
import java.util.Collections;
public class PlotResource extends Plot{
    public final Resource resourceProduced;
    public PlotResource(String name, int crushResistance, int friction, int weight, int tension, PlotRender model, Resource resourceProduced) {
        super(name, crushResistance, friction, weight, tension, model);
        this.resourceProduced = resourceProduced;
        roadConnections = Collections.unmodifiableList(Arrays.asList(Direction.straight));
    }
}
