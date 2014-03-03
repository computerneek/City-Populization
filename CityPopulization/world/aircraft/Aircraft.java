package CityPopulization.world.aircraft;
import CityPopulization.render.Side;
import CityPopulization.world.aircraft.cargo.AircraftCargo;
import CityPopulization.world.aircraft.landingSequence.LandingSequenceEvent;
import CityPopulization.world.aircraft.passenger.AircraftPassenger;
import CityPopulization.world.player.Player;
import CityPopulization.world.plot.Plot;
import java.util.ArrayList;
import java.util.Iterator;
public abstract class Aircraft{
    public final Player player;
    private ArrayList<AircraftPassenger> passengers = new ArrayList<>();
    private ArrayList<AircraftCargo> cargo = new ArrayList<>();
    protected int passengerCapacity;
    protected int cargoCapacity;
    private int departureTime;
    private int cargoOccupied;
    public Runway runway;
    private Terminal terminal;
    private ArrayList<LandingSequenceEvent> landingSequence = new ArrayList<>();
    private String state;
    private int x;
    private int y;
    private int z;
    private Side heading;
    private int speed;
    private int targetSpeed;
    public Aircraft(Player player){
        this.player = player;
    }
    public Aircraft loadPassengers(ArrayList<AircraftPassenger> passengers){
        for(Iterator<AircraftPassenger> it=passengers.iterator(); it.hasNext();){
            AircraftPassenger passenger=it.next();
            if(this.passengerCapacity>this.passengers.size()){
                this.passengers.add(passenger);
                it.remove();
            }
        }
        return this;
    }
    public Aircraft setDepartureTime(int departureTime){
        this.departureTime = departureTime;
        return this;
    }
    public Aircraft loadCargo(ArrayList<AircraftCargo> resources){
        for(Iterator<AircraftCargo> it=resources.iterator(); it.hasNext();){
            AircraftCargo cargo=it.next();
            if(cargoCapacity-cargoOccupied>=cargo.getSpaceOccupied()){
                this.cargo.add(cargo);
                cargoOccupied+=cargo.getSpaceOccupied();
                it.remove();
            }
        }
        return this;
    }
    public abstract int getRequiredRunwayLength();
    public void setRunway(Runway runway){
        this.runway = runway;
    }
    public void setTerminal(Terminal terminal){
        this.terminal = terminal;
    }
    public void land(){
        landingSequence = getLandingSequence();
        player.world.aircraft.add(this);
        state = "Landing";
    }
    public abstract ArrayList<LandingSequenceEvent> getLandingSequence();
    public void update(){
        if(heading==null){
            x+=heading.xModification*0.1F*speed;
            y+=heading.yModification*0.1F*speed;
            z+=heading.zModification*0.1F*speed;
            NEED_BETTER_MOVEMENT_CODE_USING_PYTHAGOREAN_STUFF
            if(speed<targetSpeed){
                speed+=Math.min(targetSpeed-speed, 0.1F);
            }else if(speed>targetSpeed){
                speed-=Math.min(speed-targetSpeed, 0.05F);
            }
        }
        switch(state){
            case "Landing":
                landingUpdate();
                break;
            default:
                throw new AssertionError(state);
        }
        checkForCrashes();
    }
    private void landingUpdate(){
        if(landingSequence.get(0).update(this)){
            landingSequence.remove(0);
        }
    }
    private void checkForCrashes(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public void setLocation(Plot plot){
        this.x = plot.x;
        this.y = plot.y;
        this.z = plot.z;
    }
    public void setHeading(Side front){
        this.heading = front;
    }
    public void setSpeed(int speed){
        this.speed = speed;
        this.targetSpeed = speed;
    }
    public void setTargetSpeed(int speed){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public void setTargetHeight(int height){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
