package CityPopulization.world.plot;
import CityPopulization.world.World;
import simplelibrary.config2.Config;
public class SkyScraper {
    public Plot basePlot;
    public int width;
    public int height;
    public SkyScraper(Plot basePlot, int width, int height){
        this.basePlot = basePlot;
        this.width = width;
        this.height = height;
        for(int i = 0; i<width; i++){
            for(int j = 0; j<height; j++){
                for(int k = 0; k<levels(); k++){
                    basePlot.world.getPlot(basePlot.x+i, basePlot.y+j, basePlot.z+k).skyscraper = this;
                }
            }
        }
    }
    public int maxLevels(){
        return basePlot.type.getSkyscraperHeight()*Math.min(height, width);
    }
    public void collapse(){
        for(int i = 0; i<width; i++){
            for(int j = 0; j<height; j++){
                Plot plot = basePlot.world.generateAndGetPlot(basePlot.x+i, basePlot.y+j, basePlot.z);
                for(Plot aplot : plot.getSkyscraperPlots()){
                    aplot.skyscraper=null;
                }
                plot.skyscraper=null;
                plot.setType(PlotType.Debris);
            }
        }
    }
    public int levels(){
        return basePlot.getSkyscraperPlots().length;
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("x", basePlot.x);
        config.set("y", basePlot.y);
        config.set("z", basePlot.z);
        config.set("width", width);
        config.set("height", height);
        return config;
    }
    public Plot[] getAllPlots(){
        Plot[] plots = new Plot[width*height*levels()];
        int boost = 0;
        int levels = levels();
        for(int i = 0; i<width; i++){
            for(int j = 0; j<height; j++){
                for(int k = 0; k<levels; k++){
                    plots[boost] = basePlot.world.generateAndGetPlot(basePlot.x+i, basePlot.y+j, basePlot.z+k);
                    boost++;
                }
            }
        }
        return plots;
    }
    public boolean canExpandRight(){
        for(int i = 0; i<height; i++){
            for(int j = 0; j<levels(); j++){
                if(basePlot.world.generateAndGetPlot(basePlot.x+width, basePlot.y+i, basePlot.z+j).type!=PlotType.Air){
                    return false;
                }
            }
        }
        return true;
    }
    public boolean canExpandLeft(){
        for(int i = 0; i<height; i++){
            for(int j = 0; j<levels(); j++){
                if(basePlot.world.generateAndGetPlot(basePlot.x-1, basePlot.y+i, basePlot.z+j).type!=PlotType.Air){
                    return false;
                }
            }
        }
        return true;
    }
    public boolean canExpandDown(){
        for(int i = 0; i<width; i++){
            for(int j = 0; j<levels(); j++){
                if(basePlot.world.generateAndGetPlot(basePlot.x+i, basePlot.y+height, basePlot.z+j).type!=PlotType.Air){
                    return false;
                }
            }
        }
        return true;
    }
    public boolean canExpandUp(){
        for(int i = 0; i<width; i++){
            for(int j = 0; j<levels(); j++){
                if(basePlot.world.generateAndGetPlot(basePlot.x+i, basePlot.y-1, basePlot.z+j).type!=PlotType.Air){
                    return false;
                }
            }
        }
        return true;
    }
    public void refresh(){
        new SkyScraper(basePlot, width, height);
    }
    public static void load(Config config, World world){
        new SkyScraper(world.getPlot((int)config.get("x"), (int)config.get("y"), (int)config.get("z")), (int)config.get("width"), (int)config.get("height"));
    }
}
