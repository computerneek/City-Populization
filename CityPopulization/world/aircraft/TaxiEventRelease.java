package CityPopulization.world.aircraft;
import CityPopulization.world.plot.Plot;
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
}
