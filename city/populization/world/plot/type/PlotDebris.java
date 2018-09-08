package city.populization.world.plot.type;
import city.populization.world.Direction;
import city.populization.world.plot.Plot;
import city.populization.world.plot.render.PlotRender;
import java.util.Arrays;
import java.util.Collections;
public class PlotDebris extends Plot{
    public PlotDebris(String name, int crushResistance, int friction, int weight, int tension, PlotRender model) {
        super(name, crushResistance, friction, weight, tension, model);
        roadConnections = Collections.unmodifiableList(Arrays.asList(Direction.straight));
    }
}
