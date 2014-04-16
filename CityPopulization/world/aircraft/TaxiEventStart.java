package CityPopulization.world.aircraft;
import simplelibrary.config2.Config;
public class TaxiEventStart implements TaxiEvent {
    private int tick;
    @Override
    public boolean update(Aircraft aircraft){
        aircraft.setTargetSpeed(1);
        tick++;
        return tick>=35;
    }
    @Override
    public Config save(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
