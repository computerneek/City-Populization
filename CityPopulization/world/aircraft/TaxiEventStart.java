package CityPopulization.world.aircraft;
public class TaxiEventStart implements TaxiEvent {
    private int tick;
    @Override
    public boolean update(Aircraft aircraft){
        aircraft.setTargetSpeed(1);
        tick++;
        return tick>=35;
    }
}
