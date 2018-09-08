package city.populization.world.plot.type;
import city.populization.world.Direction;
import city.populization.world.Player;
import city.populization.world.World;
import city.populization.world.plot.Plot;
import city.populization.world.plot.PlotPos;
import city.populization.world.plot.render.PlotRender;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
public class PlotHall extends Plot{
    public PlotHall(String name, int crushResistance, int friction, int weight, int tension, PlotRender model){
        super(name, crushResistance, friction, weight, tension, model);
        hallConnections = Collections.unmodifiableList(Arrays.asList(new Direction[]{Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST}));
        isPassible = true;
    }
    public static ArrayList<Direction> getConnections(World world, PlotPos pos){
        return ((PlotHall)Plot.Hall).getHallConnections(world, pos);
    }
    public ArrayList<Direction> getHallConnections(World world, PlotPos pos) {
        ArrayList<Direction> q = new ArrayList<>(4);
        for(Direction d : hallConnections){
            PlotPos other = pos.shift(d);
            Plot o = world.getPlot(other);
            if(o.getHallConnections().contains(Direction.SOUTH)){
                q.add(d);
            }
        }
        while(q.size()<4&&q.size()>1&&q.get(q.size()-1)==q.get(0).rotateRight90()) q.add(q.remove(0));
        return q;
    }
    @Override
    public boolean isBuildable(Player localPlayer, PlotPos pos, World world) {
        return world.getPlot(pos.shift(Direction.DOWN)).getCrushResistance()>=getWeight();
    }
}
