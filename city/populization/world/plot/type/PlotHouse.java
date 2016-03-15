package city.populization.world.plot.type;
import city.populization.world.Direction;
import city.populization.world.Player;
import city.populization.world.World;
import city.populization.world.plot.Plot;
import city.populization.world.plot.PlotPos;
import city.populization.world.plot.entity.PlotEntity;
import city.populization.world.plot.entity.PlotEntityHouse;
import city.populization.world.plot.render.PlotRender;
import java.util.Arrays;
import java.util.Collections;
public class PlotHouse extends PlotEntitied{
    private final int bedrooms;
    private final int cost;
    public PlotHouse(String name, int crushResistance, int friction, int weight, int tension, PlotRender model, int bedrooms, int cost){
        super(name, crushResistance, friction, weight, tension, model);
        this.bedrooms = bedrooms;
        this.cost = cost;
        roadConnections = Collections.unmodifiableList(Arrays.asList(new Direction[]{Direction.SOUTH}));
    }
    @Override
    public PlotEntity createNewEntity() {
        return new PlotEntityHouse();
    }
    @Override
    public boolean isBuildable(Player localPlayer, PlotPos pos, World world) {
        return world.getPlot(pos.shift(Direction.DOWN)).getCrushResistance()>=getWeight();
    }
}
