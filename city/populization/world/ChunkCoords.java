package city.populization.world;
import city.populization.world.plot.PlotPos;
import java.util.Iterator;
public class ChunkCoords implements Comparable<ChunkCoords>, Iterable<PlotPos>{
    public static final int EXPONENT = 3;
    public final int x;
    public final int y;
    public final int z;
    public ChunkCoords(int x, int y, int z, boolean isChunkCoords){
        if(isChunkCoords){
            this.x = x;
            this.y = y;
            this.z = z;
        }else{
            this.x = x>>EXPONENT;
            this.y = y>>EXPONENT;
            this.z = z>>EXPONENT;
        }
    }
    public int[] getMinCoords(){
        return new int[]{x<<EXPONENT, y<<EXPONENT, z<<EXPONENT};
    }
    public int[] getMaxCoords(){
        return new int[]{(x<<EXPONENT)+(1<<EXPONENT)-1, (y<<EXPONENT)+(1<<EXPONENT)-1, (z<<EXPONENT)+(1<<EXPONENT)-1};
    }
    @Override
    public int compareTo(ChunkCoords o){
        if(o==null){
            return 1;
        }
        if(o.x!=x){
            return x-o.x;
        }else if(o.y!=y){
            return y-o.y;
        }else if(o.z!=z){
            return z-o.z;
        }else{
            return 0;
        }
    }
    public boolean equals(Object o){
        if(o==null||!(o instanceof ChunkCoords)){
            return false;
        }
        ChunkCoords c = (ChunkCoords)o;
        return c.x==x&&c.y==y&&c.z==z;
    }
    @Override
    public int hashCode(){
        return x*(-1000000)+y+z*1000000;
    }
    @Override
    public Iterator<PlotPos> iterator() {
        return new Iterator<PlotPos>() {
            private int next = 0;
            @Override
            public boolean hasNext() {
                return next<(1<<(EXPONENT*3));
            }
            @Override
            public PlotPos next() {
                PlotPos c = getPos(next);
                next++;
                return c;
            }
            private PlotPos getPos(int index){
                int[] minCoords = getMinCoords();
                return new PlotPos(index%(1<<EXPONENT)+minCoords[0], (index/(1<<EXPONENT))%(1<<EXPONENT)+minCoords[1], (index/(1<<(EXPONENT*2)))+minCoords[2]);
            }
        };
    }
}
