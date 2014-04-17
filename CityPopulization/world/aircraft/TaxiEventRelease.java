package CityPopulization.world.aircraft;
import CityPopulization.world.plot.Plot;
import simplelibrary.config2.Config;
public class TaxiEventRelease implements TaxiEvent {
    private Plot plot;
    public TaxiEventRelease(Plot plot){
        this.plot = plot;
    }
    @Override
    public boolean update(Aircraft aThis){
        plot.terminal.occupiers--;
        if(plot.terminal.occupiers<1){
            plot.terminal.occupied = 0;
        }
        return true;
    }
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "release");
        config.set("x", plot.x);
        config.set("y", plot.y);
        config.set("z", plot.z);
        return config;
    }
}
