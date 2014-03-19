package CityPopulization.world.aircraft;
import CityPopulization.world.player.Player;
import java.lang.reflect.InvocationTargetException;
public enum Template {
    HELICOPTER_TINY("Tiny Helicopter", "helicopter/tiny", Helicopter.class, 1, 10, 100, 10, 10, 200, 200, 12, 250, 5);
    public final String name;
    public final String texture;
    public final Class<? extends Aircraft> clazz;
    public final int runwayLength;
    public final int passengers;
    public final int cargo;
    public final int fuel;
    public final int cost;
    public final int shortestTime;
    public final int timeInterval;
    public final int totalTimes;
    public final int departureTime;
    public final int costPerTimeInterval;
    private Template(String name, String texture, Class<? extends Aircraft> clazz, int runwayLength, int passengers, int cargo, int fuel, int cost, int shortestTime, int timeInterval, int totalTimes, int departureTime, int costPerTimeInterval){
        this.name = name;
        this.texture = texture;
        this.clazz=clazz;
        this.runwayLength=runwayLength;
        this.passengers=passengers;
        this.cargo=cargo;
        this.fuel=fuel;
        this.cost=cost;
        this.shortestTime=shortestTime;
        this.timeInterval=timeInterval;
        this.totalTimes=totalTimes;
        this.departureTime=departureTime;
        this.costPerTimeInterval=costPerTimeInterval;
    }
    public Aircraft createAircraft(Player player){
        try{
            Aircraft aircraft = clazz.getDeclaredConstructor(Player.class, String.class).newInstance(player, texture);
            aircraft.passengerCapacity = passengers;
            aircraft.minimumRunwayLength = runwayLength;
            aircraft.cargoCapacity = cargo;
            aircraft.maxFuelLevel = fuel;
            return aircraft;
        }catch(NoSuchMethodException|SecurityException|InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException ex){
            throw new RuntimeException(ex);
        }
    }
    @Override
    public String toString(){
        return name+" ("+passengers+" passengers; "+cargo+" cargo; "+fuel+" fuel; $"+cost+")";
    }
}