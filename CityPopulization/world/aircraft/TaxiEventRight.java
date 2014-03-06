package CityPopulization.world.aircraft;
public class TaxiEventRight implements TaxiEvent {
    private int tick;
    public TaxiEventRight(){}
    @Override
    public boolean update(Aircraft aircraft){
        if(tick==0){
            aircraft.setTargetHeading(aircraft.getHeading()-90);
        }
        tick++;
        return tick>=40;
    }
}
