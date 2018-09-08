package city.populization.world.plot.type;
import city.populization.world.Direction;
import city.populization.world.Player;
import city.populization.world.World;
import city.populization.world.plot.Plot;
import city.populization.world.plot.PlotPos;
import city.populization.world.plot.entity.PlotEntity;
import city.populization.world.plot.render.PlotRender;
import java.util.Arrays;
import java.util.Collections;
public class PlotElevatorTop extends PlotEntitied{
    private final PlotRender carModel;
    private final PlotRender counterbalanceModel;
    private final int maxCapacityAtTop;
    public PlotElevatorTop(String name, int crushResistance, int friction, int weight, int tension, PlotRender model, PlotRender carModel, PlotRender counterbalanceModel, int maxCapacityAtTop){
        super(name, crushResistance, friction, weight, tension, model);
        this.carModel = carModel;
        this.counterbalanceModel = counterbalanceModel;
        hallConnections = Collections.unmodifiableList(Arrays.asList(new Direction[]{Direction.SOUTH}));
        this.maxCapacityAtTop = maxCapacityAtTop;
    }
    @Override
    public PlotEntity createNewEntity() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO create a new Elevator entity for the top of the shaft
    }
    @Override
    public boolean isBuildable(Player localPlayer, PlotPos pos, World world) {
        return world.getPlot(pos.shift(Direction.DOWN))==Plot.Elevator;
    }
}
