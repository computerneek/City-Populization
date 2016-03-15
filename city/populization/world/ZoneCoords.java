package city.populization.world;
import city.populization.world.plot.PlotPos;
import java.util.Iterator;

import static city.populization.world.ChunkCoords.EXPONENT;
public class ZoneCoords implements Comparable<ZoneCoords>, Iterable<ChunkCoords> {
    public final int x;
    public final int y;
    public final int z;
    public static ZoneCoords fromZone(int x, int y, int z){
        return new ZoneCoords(x, y, z, true);
    }
    public static ZoneCoords fromChunk(int x, int y, int z){
        return new ZoneCoords(x, y, z, false);
    }
    public static ZoneCoords fromChunk(ChunkCoords chunk){
        return fromChunk(chunk.x, chunk.y, chunk.z);
    }
    public static ZoneCoords fromPlot(int x, int y, int z){
        return fromChunk(new ChunkCoords(x, y, z, false));
    }
    public static ZoneCoords fromPlot(PlotPos plot){
        return fromPlot(plot.x, plot.y, plot.z);
    }
    private ZoneCoords(int x, int y, int z, boolean isZoneCoords){
        if(isZoneCoords){
            this.x = x;
            this.y = y;
            this.z = z;
        }else{
            this.x = x>>EXPONENT;
            this.y = y>>EXPONENT;
            this.z = (z+(1<<(EXPONENT-1)))>>EXPONENT;
        }
    }
    public int[] getMinChunkCoords(){
        return new int[]{x<<EXPONENT, y<<EXPONENT, (z<<EXPONENT)-(1<<(EXPONENT-1))};
    }
    public int[] getMaxChunkCoords(){
        return new int[]{
            (x<<EXPONENT)+(1<<EXPONENT)-1,
            (y<<EXPONENT)+(1<<EXPONENT)-1,
            (z<<EXPONENT)+(1<<(EXPONENT-1))-1};
    }
    public int[] getMinCoords(){
        return new int[]{x<<(EXPONENT*2), y<<(EXPONENT*2), (((z<<EXPONENT)-(1<<(EXPONENT-1)))<<EXPONENT)-(1<<(EXPONENT-1))};
    }
    public int[] getMaxCoords(){
        return new int[]{
            (((x<<EXPONENT)+(1<<EXPONENT)-1)<<EXPONENT)+(1<<EXPONENT)-1,
            (((y<<EXPONENT)+(1<<EXPONENT)-1)<<EXPONENT)+(1<<EXPONENT)-1,
            (((z<<EXPONENT)+(1<<(EXPONENT-1))-1)<<EXPONENT)+(1<<(EXPONENT-1))-1
        };
    }
    @Override
    public int compareTo(ZoneCoords o){
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
        if(o==null||!(o instanceof ZoneCoords)){
            return false;
        }
        ZoneCoords c = (ZoneCoords)o;
        return c.x==x&&c.y==y&&c.z==z;
    }
    @Override
    public int hashCode(){
        return x*(-1000000)+y+z*1000000;
    }
    @Override
    public Iterator<ChunkCoords> iterator() {
        return new Iterator<ChunkCoords>() {
            private int next = 0;
            @Override
            public boolean hasNext() {
                return next<(1<<(EXPONENT*3));
            }
            @Override
            public ChunkCoords next() {
                ChunkCoords c = getPos(next);
                next++;
                return c;
            }
            private ChunkCoords getPos(int index){
                int[] minCoords = getMinCoords();
                return new ChunkCoords(index%(1<<EXPONENT)+minCoords[0], (index/(1<<EXPONENT))%(1<<EXPONENT)+minCoords[1], (index/(1<<(EXPONENT*2)))+minCoords[2], true);
            }
        };
    }
}
