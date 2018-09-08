package city.populization.world;
public enum Direction {
    NOWHERE(0, 0, 0),
    UP(0, 0, 1),
    DOWN(0, 0, -1),
    NORTH(0, 1, 0),
    SOUTH(0, -1, 0),
    EAST(-1, 0, 0),
    WEST(1, 0, 0),
    NORTHEAST(EAST.x, NORTH.y, 0),
    NORTHWEST(WEST.x, NORTH.y, 0),
    SOUTHEAST(EAST.x, SOUTH.y, 0),
    SOUTHWEST(WEST.x, SOUTH.y, 0),
    UPNORTH(0, NORTH.y, UP.z),
    UPSOUTH(0, SOUTH.y, UP.z),
    DOWNNORTH(0, NORTH.y, DOWN.z),
    DOWNSOUTH(0, SOUTH.y, DOWN.z),
    UPEAST(EAST.x, 0, UP.z),
    UPWEST(WEST.x, 0, UP.z),
    DOWNEAST(EAST.x, 0, DOWN.z),
    DOWNWEST(WEST.x, 0, DOWN.z),
    UPNORTHEAST(EAST.x, NORTH.y, UP.z),
    UPNORTHWEST(WEST.x, NORTH.y, UP.z),
    UPSOUTHEAST(EAST.x, SOUTH.y, UP.z),
    UPSOUTHWEST(WEST.x, SOUTH.y, UP.z),
    DOWNNORTHEAST(EAST.x, NORTH.y, DOWN.z),
    DOWNNORTHWEST(WEST.x, NORTH.y, DOWN.z),
    DOWNSOUTHEAST(EAST.x, SOUTH.y, DOWN.z),
    DOWNSOUTHWEST(WEST.x, SOUTH.y, DOWN.z);
    public static final Direction[] straight = {UP, DOWN, NORTH, SOUTH, EAST, WEST};
    public static final Direction[] diagonal = {UPNORTH, UPSOUTH, UPEAST, UPWEST, DOWNNORTH, DOWNSOUTH, DOWNEAST, DOWNWEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST};
    public static final Direction[] flat = {NORTH, SOUTH, EAST, WEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST};
    public static final Direction[] straightFlat = {NORTH, SOUTH, EAST, WEST};
    public static final Direction[] diagonalFlat = {NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST};
    public static final Direction[] corners = {UPNORTHEAST, UPNORTHWEST, UPSOUTHEAST, UPSOUTHWEST, DOWNNORTHEAST, DOWNNORTHWEST, DOWNSOUTHEAST, DOWNSOUTHWEST};
    public static final Direction[] all = {UP, DOWN, NORTH, SOUTH, EAST, WEST, UPNORTH, UPSOUTH, UPEAST, UPWEST, DOWNNORTH, DOWNSOUTH, DOWNEAST, DOWNWEST, NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST, UPNORTHEAST, UPNORTHWEST, UPSOUTHEAST, UPSOUTHWEST, DOWNNORTHEAST, DOWNNORTHWEST, DOWNSOUTHEAST, DOWNSOUTHWEST};
    public final int x;
    public final int y;
    public final int z;
    private Direction(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public static Direction byCoords(int x, int y, int z){
        if(x>1) x=1;
        if(x<-1) x=-1;
        if(y>1) y=1;
        if(y<-1) y=-1;
        if(z>1) z=1;
        if(z<-1) z=-1;
        for(Direction d : values()){
            if(d.x==x&&d.y==y&&d.z==z){
                return d;
            }
        }
        return NOWHERE;
    }
    public Direction rotateLeft45(){
        if(x!=0&&y!=0){
            //Semi-cardinal direction w/ possible up-down, need to flatten one cardinal
            if(x==EAST.x){
                if(y==NORTH.y) return byCoords(x, 0, z);
                else return byCoords(0, y, z);
            }else{
                if(y==NORTH.y) return byCoords(0, y, z);
                else return byCoords(x, 0, z);
            }
        }else if(x!=0||y!=0){
            //Cardinal direction w/ possible up-down, need to pop out on the other cardinal
            if(x==0){
                if(y==NORTH.y) return byCoords(EAST.x, 0, z);
                else return byCoords(WEST.x, 0, z);
            }else{
                if(x==EAST.x) return byCoords(0, SOUTH.y, z);
                else return byCoords(0, NORTH.y, z);
            }
        }else{
            //No X or Y projection- UP, DOWN, or NOWHERE.  They don't turn.
            return this;
        }
    }
    public Direction rotateLeft90(){
        return byCoords(-y, x, z);
    }
    public Direction rotateRight45(){
        if(x!=0&&y!=0){
            //Semi-cardinal direction w/ possible up-down, need to flatten one cardinal
            if(x==EAST.x){
                if(y==NORTH.y) return byCoords(0, y, z);
                else return byCoords(x, 0, z);
            }else{
                if(y==NORTH.y) return byCoords(x, 0, z);
                else return byCoords(0, y, z);
            }
        }else if(x!=0||y!=0){
            //Cardinal direction w/ possible up-down, need to pop out on the other cardinal
            if(x==0){
                if(y==NORTH.y) return byCoords(WEST.x, 0, z);
                else return byCoords(EAST.x, 0, z);
            }else{
                if(x==EAST.x) return byCoords(0, NORTH.y, z);
                else return byCoords(0, SOUTH.y, z);
            }
        }else{
            //No X or Y projection- UP, DOWN, or NOWHERE.  They don't turn.
            return this;
        }
    }
    public Direction rotateRight90(){
        return byCoords(y, -x, z);
    }
    public Direction reverse(){
        return byCoords(-x, -y, -z);//While UP & DOWN don't turn, they do back up- to each other.
    }
    public Direction rotateRight135(){
        return rotateRight90().rotateRight45();
    }
    public Direction rotateLeft135(){
        return rotateLeft90().rotateLeft45();
    }
    public Direction rotateUp45(){
        if(z>0){
            return UP;//Only thing up from UPNORTH, UPNORTHEAST, etc. is up.  Up doesn't go up.
        }else if(z==0){
            return byCoords(x, y, 1);
        }else if(x!=0||y!=0){
            return byCoords(x, y, 0);//DOWNNORTH, DOWNNORTHEAST, etc. go to NORTH, NORTHEAST, etc.
        }else{
            return this;//DOWN doesn't go UP, it only reverses
        }
    }
    public Direction rotateDown45(){
        if(z<0){
            return DOWN;//Only thing down from DOWNNORTH, etc. is down.  Down doesn't go down.
        }else if(z==0){
            return byCoords(x, y, -1);
        }else if(x!=0||y!=0){
            return byCoords(x, y, 0);
        }else{
            return this;
        }
    }
    public Direction rotateUp90(){
        if(z>=0){
            return UP;
        }else if(x!=0||y!=0){
            return byCoords(x, y, 1);
        }else{
            return this;
        }
    }
    public Direction rotateDown90(){
        if(z<=0){
            return DOWN;
        }else if(x!=0||y!=0){
            return byCoords(x, y, -1);
        }else{
            return this;
        }
    }
}
