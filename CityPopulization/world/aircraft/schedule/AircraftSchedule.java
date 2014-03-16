package CityPopulization.world.aircraft.schedule;
import CityPopulization.world.plot.Plot;
import java.util.ArrayList;
public class AircraftSchedule {
    public ArrayList<ScheduleElement> elements = new ArrayList<>();
    private AircraftSchedule parent;
    public AircraftSchedule(){}
    private AircraftSchedule(AircraftSchedule parent){
        this();
        this.parent = parent;
        this.elements.addAll(parent.elements);
    }
    public AircraftSchedule copy(){
        return new AircraftSchedule(this);
    }
    public void update(Plot plot){
        for(ScheduleElement element : elements){
            element.update();
            if(element.isTimeForAircraftArrival()){
                plot.addInboundAircraft(element.getAircraft(plot.owner));
            }
        }
    }
}
