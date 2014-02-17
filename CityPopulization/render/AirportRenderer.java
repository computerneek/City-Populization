package CityPopulization.render;
import CityPopulization.world.plot.Plot;
public class AirportRenderer implements PlotRenderer{
    public static final int ENTRANCE = 1;
    public static final int TERMINAL = 2;
    public static final int JETWAY = 3;
    public static final int RUNWAY = 4;
    private int type;
    public AirportRenderer(int type){
        this.type = type;
    }
    @Override
    public void render(Plot plot, String textureFolder){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
