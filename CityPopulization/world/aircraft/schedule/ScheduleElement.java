package CityPopulization.world.aircraft.schedule;
import CityPopulization.world.aircraft.Aircraft;
import CityPopulization.world.aircraft.Template;
import CityPopulization.world.aircraft.cargo.AircraftCargo;
import CityPopulization.world.aircraft.passenger.AircraftPassenger;
import CityPopulization.world.player.Player;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
public class ScheduleElement {
    private final Template template;
    private final int civilians;
    private final int workers;
    private final ResourceList resourceList;
    private final int timeBetweenArrivals;
    private final int departureTime;
    private int tick;
    public ScheduleElement(Template template, int civilians, int workers, ResourceList resourceList, int timeBetweenArrivals, int departureTime){
        this.template=template;
        this.civilians=civilians;
        this.workers=workers;
        this.resourceList=resourceList;
        this.timeBetweenArrivals=timeBetweenArrivals;
        this.departureTime=departureTime;
    }
    public String getAircraftName(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public int getAircraftCost(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public int getFuelCost(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public int getMaxPassengerCount(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public int getPassengerCount(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public ResourceList getCargo(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public String getTimeUntilNextArrival(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public void update(){
        tick++;
    }
    public boolean isTimeForAircraftArrival(){
        return tick>=timeBetweenArrivals;
    }
    public Aircraft getAircraft(Player player){
        tick = 0;
        Aircraft aircraft = template.createAircraft(player).loadPassengers(AircraftPassenger.civilians(civilians)).loadPassengers(AircraftPassenger.workers(workers)).setDepartureTime(departureTime);
        for(Resource resource : resourceList.listResources()){
            aircraft.loadCargo(AircraftCargo.resource(resource, resourceList.get(resource)));
        }
        return aircraft;
    }

}
