package CityPopulization.render;
import CityPopulization.world.World;
import CityPopulization.world.plot.Plot;
public enum Side{
    FRONT(0, -1, 0){
        @Override
        public Side right(){
            return LEFT;
        }
        @Override
        public Side left(){
            return RIGHT;
        }
        @Override
        public Side reverse(){
            return BACK;
        }
    },
    BACK(0, 1, 0){
        @Override
        public Side right(){
            return RIGHT;
        }
        @Override
        public Side left(){
            return LEFT;
        }
        @Override
        public Side reverse(){
            return FRONT;
        }
    },
    LEFT(-1, 0, 0){
        @Override
        public Side right(){
            return FRONT;
        }
        @Override
        public Side left(){
            return BACK;
        }
        @Override
        public Side reverse(){
            return RIGHT;
        }
    },
    RIGHT(1, 0, 0){
        @Override
        public Side right(){
            return FRONT;
        }
        @Override
        public Side left(){
            return BACK;
        }
        @Override
        public Side reverse(){
            return LEFT;
        }
    },
    UP(0, 0, 1){
        @Override
        public Side right(){
            return UP;
        }
        @Override
        public Side left(){
            return UP;
        }
        @Override
        public Side reverse(){
            return DOWN;
        }
    },
    DOWN(0, 0, -1){
        @Override
        public Side right(){
            return DOWN;
        }
        @Override
        public Side left(){
            return DOWN;
        }
        @Override
        public Side reverse(){
            return UP;
        }
    };
    public final int xModification;
    public final int yModification;
    public final int zModification;
    Side(int xModification, int yModification, int zModification){
        this.xModification=xModification;
        this.yModification=yModification;
        this.zModification=zModification;
    }
    public abstract Side right();
    public abstract Side left();
    public abstract Side reverse();
    public Plot getPlot(World world, int x, int y, int z){
        return world.generatePlot(x+xModification, y+yModification, z+zModification);
    }
}
