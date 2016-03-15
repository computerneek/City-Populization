package city.populization.world.plot.type;
import city.populization.world.Direction;
import city.populization.world.Player;
import city.populization.world.World;
import city.populization.world.plot.Plot;
import city.populization.world.plot.PlotPos;
import city.populization.world.plot.entity.PlotEntity;
import city.populization.world.plot.entity.PlotEntityFarm;
import city.populization.world.plot.render.PlotRender;
import java.util.Arrays;
import java.util.Collections;
public class PlotFarm extends PlotEntitied{
    public PlotFarm(String name, int crushResistance, int friction, int weight, int tension, PlotRender model){
        super(name, crushResistance, friction, weight, tension, model);
        roadConnections = Collections.unmodifiableList(Arrays.asList(new Direction[]{Direction.SOUTH}));
    }
    @Override
    public PlotEntity createNewEntity() {
        return new PlotEntityFarm();
    }
    @Override
    public boolean isBuildable(Player localPlayer, PlotPos pos, World world) {
        return world.getPlot(pos.shift(Direction.DOWN)).getCrushResistance()>=getWeight();
    }
}
