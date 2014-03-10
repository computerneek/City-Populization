package CityPopulization.render;
import CityPopulization.world.plot.Plot;
public class NonRenderer implements PlotRenderer{
    @Override
    public void render(Plot plot, String textureFolder){}
    @Override
    public String[] getPaths(int levels, String textureFolder){
        return new String[0];
    }
}
