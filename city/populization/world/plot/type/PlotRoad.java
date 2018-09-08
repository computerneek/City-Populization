package city.populization.world.plot.type;
import city.populization.world.Direction;
import city.populization.world.World;
import city.populization.world.plot.Plot;
import city.populization.world.plot.PlotPos;
import city.populization.world.plot.render.PlotRenderRoad;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class PlotRoad extends Plot{
    public final int levels;
    public PlotRoad(String name, int crushResistance, int friction, int weight, int tension, PlotRenderRoad model){
        super(name, crushResistance, friction, weight, tension, model);
        levels = model.levels;
        roadConnections = Collections.unmodifiableList(Arrays.asList(new Direction[]{Direction.SOUTH, Direction.EAST, Direction.NORTH, Direction.WEST}));
        isPassible = true;
    }
    public static ArrayList<Direction> getConnections(World world, PlotPos pos){
        return ((PlotRoad)Plot.Road).getRoadConnections(world, pos);
    }
    public ArrayList<Direction> getRoadConnections(World world, PlotPos pos) {
        ArrayList<Direction> q = new ArrayList<>(4);
        for(Direction d : roadConnections){
            PlotPos other = pos.shift(d);
            Plot o = world.getPlot(other);
            if(containsAfterRotation(world.getHeading(other), o.getRoadConnections(), d.reverse())){
                q.add(d);
            }
        }
        while(q.size()<4&&q.size()>1&&q.get(q.size()-1)==q.get(0).rotateRight90()) q.add(q.remove(0));
        while(q.size()==3&&q.get(q.size()-1)==q.get(0).rotateLeft90()) q.add(q.remove(0));
        if(q.size()==3) q.add(q.remove(1));
        if(q.size()==3) q.add(q.remove(0));
        return q;
    }
    @Override
    public int getLevelCount() {
        return levels;
    }
    private boolean containsAfterRotation(Direction heading, List<Direction> roadConnections, Direction reverse) {
        switch(heading){
            case SOUTH: break;
            case NORTH: reverse = reverse.reverse(); break;
            case EAST: reverse = reverse.rotateLeft90(); break;
            case WEST: reverse = reverse.rotateRight90(); break;
        }
        return roadConnections.contains(reverse);
    }
}
