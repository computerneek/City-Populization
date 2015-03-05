package CityPopulization.render;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
public interface PlotRenderer{
    public void render(Plot plot, String textureFolder);
    public String[] getPaths(PlotType type, int levels, String textureFolder);
}
