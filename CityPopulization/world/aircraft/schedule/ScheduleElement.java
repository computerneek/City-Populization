package CityPopulization.world.aircraft.schedule;
import CityPopulization.world.aircraft.Aircraft;
import CityPopulization.world.aircraft.Template;
import CityPopulization.world.aircraft.cargo.AircraftCargo;
import CityPopulization.world.aircraft.passenger.AircraftPassenger;
import CityPopulization.world.player.Player;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import simplelibrary.config2.Config;
public class ScheduleElement {
    private final Template template;
    public int civilians;
    private final int workers;
    private final ResourceList resourceList;
    private final int timeBetweenArrivals;
    public int tick;
    private int cost;
    public int index;
    private static int index2;
    public ScheduleElement(Template template, int civilians, int workers, ResourceList resourceList, int timeBetweenArrivals, int cost){
        this.template=template;
        this.civilians=civilians;
        this.workers=workers;
        this.resourceList=resourceList;
        this.timeBetweenArrivals=timeBetweenArrivals;
        this.cost = cost;
        index = index2;
        index2++;
    }
    public String getAircraftName(){
        return template.name;
    }
    public int getAircraftCost(){
        if(workers+civilians>template.passengers){
            civilians=template.passengers-workers;
        }
        return cost+5*template.passengers-10*civilians+5*workers+resourceList.count()*2;
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
        Aircraft aircraft = template.createAircraft(player).loadPassengers(AircraftPassenger.workers(Math.max(0, workers))).loadPassengers(AircraftPassenger.civilians(Math.max(0, civilians))).setDepartureTime(template.departureTime);
        player.cash-=getAircraftCost();
        for(Resource resource : resourceList.listResources()){
            aircraft.loadCargo(AircraftCargo.resource(resource, resourceList.get(resource)));
        }
        aircraft.schedule = this;
        return aircraft;
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("template", template.name());
        config.set("civilians", civilians);
        config.set("workers", workers);
        config.set("resources", resourceList.save());
        config.set("time", timeBetweenArrivals);
        config.set("tick", tick);
        config.set("cost", cost);
        config.set("index", index);
        config.set("index2", index2);
        return config;
    }
    public static ScheduleElement load(Config config){
        Template template = Template.valueOf((String)config.get("template"));
        int civilians = config.get("civilians");
        int workers = config.get("workers");
        ResourceList list = ResourceList.load((Config)config.get("resources"));
        int time = config.get("time");
        int tick = config.get("tick");
        int cost = config.get("cost");
        int index = config.get("index");
        ScheduleElement element = new ScheduleElement(template, civilians, workers, list, time, cost);
        element.tick = tick;
        element.index = index;
        index2 = config.get("index2");
        return element;
    }
    public int getIndex(){
        return index;
    }
}
