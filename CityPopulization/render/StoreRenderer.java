package CityPopulization.render;
import CityPopulization.render.PlotRenderer;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
public class StoreRenderer implements PlotRenderer{
    public StoreRenderer(){
    }
    @Override
    public void render(Plot plot, String textureFolder){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public String[] getPaths(PlotType plot, int levels, String textureFolder){
        return new String[]{levels+":/textures/plots/"+textureFolder+"/level <LEVEL>/frame <FRAME>.png"};
    }
    
}
