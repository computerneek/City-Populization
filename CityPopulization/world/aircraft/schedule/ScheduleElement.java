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
    public int civilians;
    private final int workers;
    private final ResourceList resourceList;
    private final int timeBetweenArrivals;
    private int tick;
    private int cost;
    public ScheduleElement(Template template, int civilians, int workers, ResourceList resourceList, int timeBetweenArrivals, int cost){
        this.template=template;
        this.civilians=civilians;
        this.workers=workers;
        this.resourceList=resourceList;
        this.timeBetweenArrivals=timeBetweenArrivals;
        this.cost = Math.max(cost, template.cost);
    }
    public String getAircraftName(){
        return template.name;
    }
    public int getAircraftCost(){
        if(workers+civilians>template.passengers){
            civilians=template.passengers-workers;
        }
        return cost+5*template.passengers-10*civilians+5*workers-resourceList.count();
    }
    public int getFuelCost(){
        return template.fuel;
    }
    public int getMaxPassengerCount(){
        return template.passengers;
    }
    public int getPassengerCount(){
        return civilians;
    }
    public ResourceList getCargo(){
        return new ResourceList().addAll(resourceList);
    }
    public String getTimeUntilNextArrival(){
        int ticks = timeBetweenArrivals-tick;
        int seconds = ticks/20;
        ticks%=20;
        int minutes = seconds/60;
        seconds%=60;
        return minutes+":"+(seconds<10?"0":"")+seconds;
    }
    public void update(){
        tick++;
    }
    public boolean isTimeForAircraftArrival(){
        return tick>=timeBetweenArrivals;
    }
    public Aircraft getAircraft(Player player){
        tick = 0;
        Aircraft aircraft = template.createAircraft(player).loadPassengers(AircraftPassenger.workers(workers)).loadPassengers(AircraftPassenger.civilians(civilians)).setDepartureTime(template.departureTime);
        player.cash-=getAircraftCost();
        for(Resource resource : resourceList.listResources()){
            aircraft.loadCargo(AircraftCargo.resource(resource, resourceList.get(resource)));
        }
        aircraft.schedule = this;
        return aircraft;
    }
}
