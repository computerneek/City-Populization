package city.populization.world;
import city.populization.world.civilian.Civilian;
import city.populization.world.plot.PlotOwner;
import city.populization.world.plot.PlotPos;
import city.populization.world.plot.Plot;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import static city.populization.world.ChunkCoords.EXPONENT;
public class Zone implements Iterable<ChunkCoords>{
    private final Chunk[] chunks = new Chunk[512];
    public final World world;
    public final ZoneCoords coords;
    private boolean changed = true;
    public Zone(World world, ZoneCoords coords){
        this.world = world;
        this.coords = coords;
        world.zones.put(coords, this);
        for(int i = 0; i<chunks.length; i++){
            new Chunk(this, getPos(i));
        }
    }
    private int getIndex(int x, int y, int z){
        return x+y*8+z*64;
    }
    private ChunkCoords getPos(int index){
        int[] minCoords = coords.getMinChunkCoords();
        return new ChunkCoords(index%(1<<EXPONENT)+minCoords[0], (index/(1<<EXPONENT))%(1<<EXPONENT)+minCoords[1], (index/(1<<(EXPONENT*2)))+minCoords[2], true);
    }
    private int getIndex(ChunkCoords c){
        return getIndexFromChunk(c.x, c.y, c.z);
    }
    private int getIndexFromChunk(int x, int y, int z){
        int[] coords = this.coords.getMinChunkCoords();
        return getIndex(x-coords[0], y-coords[1], z-coords[2]);
    }
    private int getIndex(PlotPos c){
        return getIndexFromPlot(c.x, c.y, c.z);
    }
    private int getIndexFromPlot(int x, int y, int z){
        return getIndexFromChunk(x>>EXPONENT, y>>EXPONENT, z>>EXPONENT);
    }
    private Chunk getChunk(int index){
        if(chunks[index]==null){
            new Chunk(this, getPos(index));
        }
        return chunks[index];
    }
    public Chunk getChunk(ChunkCoords c){
        return getChunk(getIndex(c));
    }
    public Chunk getChunk(PlotPos p){
        return getChunk(getIndex(p));
    }
    public synchronized void setChunk(ChunkCoords coords, Chunk chunk){
        if(coords.equals(chunk.coords)){
            chunks[getIndex(coords)] = chunk;
            chunk.flagChanged();
        }
    }
    public Plot getPlot(PlotPos p){
        return getChunk(p).getPlot(p);
    }
    public PlotOwner getOwner(PlotPos p){
        return getChunk(p).getOwner(p);
    }
    public int getLevel(PlotPos p){
        return getChunk(p).getLevel(p);
    }
    public Direction getHeading(PlotPos p){
        return getChunk(p).getHeading(p);
    }
    public Player[] getVisibilities(PlotPos p){
        return getChunk(p).getVisibilities(p);
    }
    public void setOwner(PlotPos home, PlotOwner owner) {
        getChunk(home).setOwner(home, owner);
    }
    public void setVisibilities(PlotPos p, Player[] vis){
        getChunk(p).setVisibilities(p, vis);
    }
    public void setPlot(PlotPos p, Plot plot, PlotOwner owner, Direction heading, int level){
        getChunk(p).setPlot(p, plot, owner, heading, level);
    }
    public void flagChanged() {
        changed = true;
    }
    public long getZoneSeed() {
        return world.getSeed()+new Random(coords.x*10000+coords.y*100+coords.z).nextLong();
    }
    @Override
    public Iterator<ChunkCoords> iterator() {
        return new Iterator<ChunkCoords>() {
            private int next = 0;
            @Override
            public boolean hasNext() {
                return next<chunks.length;
            }
            @Override
            public ChunkCoords next() {
                ChunkCoords c = getPos(next);
                next++;
                return c;
            }
        };
    }
    public void render(int displayZ) {
        for(Chunk c : chunks){
            if(c!=null){
                c.render(displayZ);
            }
        }
    }
}
