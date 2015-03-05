package CityPopulization.render;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
public class NonRenderer implements PlotRenderer{
    @Override
    public void render(Plot plot, String textureFolder){}
    @Override
    public String[] getPaths(PlotType plot, int levels, String textureFolder){
        return new String[0];
    }
}
