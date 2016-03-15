package city.populization.world.plot;
import city.populization.world.Direction;
public class PlotPos {
    public final int x;
    public final int y;
    public final int z;
    public PlotPos(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public PlotPos shift(Direction direction, int distance){
        return new PlotPos(x+direction.x*distance, y+direction.y*distance, z+direction.z*distance);
    }
    public PlotPos shift(Direction direction){
        return new PlotPos(x+direction.x, y+direction.y, z+direction.z);
    }
    @Override
    public boolean equals(Object obj) {
        return obj!=null&&obj instanceof PlotPos&&areEqual((PlotPos)obj);
    }
    private boolean areEqual(PlotPos pos) {
        return x==pos.x&&y==pos.y&&z==pos.z;
    }
}
