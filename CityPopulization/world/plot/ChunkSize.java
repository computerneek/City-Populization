package CityPopulization.world.plot;
/**
 * A chunk size class.  Coordinates are powers of two.
 * ex. new ChunkSize(2, 3, 4) means the sizes are:
 * X = 2^2 = 4
 * Y = 2^3 = 8
 * Z = 2^4 = 16
 * @author Bryan
 */
public class ChunkSize {
    public final int x;
    public final int y;
    public final int z;
    public ChunkSize(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
