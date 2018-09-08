package city.populization.world.plot.type;
import city.populization.world.Direction;
import city.populization.world.Player;
import city.populization.world.World;
import city.populization.world.plot.Plot;
import city.populization.world.plot.PlotPos;
import city.populization.world.plot.entity.PlotEntity;
import city.populization.world.plot.entity.PlotEntityTownHall;
import city.populization.world.plot.render.PlotRender;
import java.util.Arrays;
import java.util.Collections;
public class PlotTownHall extends PlotEntitied{
    public PlotTownHall(String name, int crushResistance, int friction, int weight, int tension, PlotRender model){
        super(name, crushResistance, friction, weight, tension, model);
        roadConnections = Collections.unmodifiableList(Arrays.asList(new Direction[]{Direction.SOUTH}));
    }
    @Override
    public PlotEntity createNewEntity() {
        return new PlotEntityTownHall();
    }
    @Override
    public boolean isBuildable(Player localPlayer, PlotPos pos, World world) {
        return localPlayer!=null&&localPlayer.countTownHalls()<9&&
                world.getPlot(pos.shift(Direction.DOWN)).getCrushResistance()>=getWeight();
    }
}
