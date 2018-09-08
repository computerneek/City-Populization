package city.populization.world.plot.type;
import city.populization.world.Direction;
import city.populization.world.Player;
import city.populization.world.World;
import city.populization.world.plot.Plot;
import city.populization.world.plot.PlotPos;
import city.populization.world.plot.render.PlotRender;
import java.util.Arrays;
import java.util.Collections;
public class PlotSolid extends Plot{
    public PlotSolid(String name, int crushResistance, int friction, int weight, int tension, PlotRender model) {
        super(name, crushResistance, friction, weight, tension, model);
        roadConnections = Collections.unmodifiableList(Arrays.asList(Direction.straight));
        hallConnections = roadConnections;
    }
    @Override
    public boolean isBuildable(Player localPlayer, PlotPos pos, World world) {
        return true;
    }
}
