package CityPopulization.render;
import CityPopulization.world.plot.Plot;
public interface PlotRenderer{
    public void render(Plot plot, String textureFolder);
    public String[] getPaths(int levels, String textureFolder);
}
