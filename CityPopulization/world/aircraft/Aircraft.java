package CityPopulization.world.aircraft;
import CityPopulization.render.Side;
import CityPopulization.world.World;
import CityPopulization.world.aircraft.cargo.AircraftCargo;
import CityPopulization.world.aircraft.landingSequence.LandingSequenceEvent;
import CityPopulization.world.aircraft.passenger.AircraftPassenger;
import CityPopulization.world.player.Player;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
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
    private float heading;
    private float pitch;
    private boolean tiltToMatchPitch;
    private float tilt;
    private float speed;
    private float targetSpeed;
    public float targetPitch;
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
        terminal.occupied = true;
    }
    public abstract ArrayList<LandingSequenceEvent> getLandingSequence();
    public void update(){
        float distance = 0.02F*speed;
        float xyDist = (float)Math.cos(Math.toRadians(pitch))*distance;
        float zDist = (float)Math.sin(Math.toRadians(pitch))*distance;
        float xDist = (float)Math.cos(Math.toRadians(heading))*xyDist;
        float yDist = (float)Math.sin(Math.toRadians(heading))*xyDist;
        if(speed<targetSpeed){
            speed+=Math.min(targetSpeed-speed, 0.1F);
        }else if(speed>targetSpeed){
            speed-=Math.min(speed-targetSpeed, 0.05F);
        }
        if(pitch<targetPitch){
            pitch+=Math.min(targetPitch-pitch, 3);
        }else if(pitch>targetPitch){
            pitch-=Math.min(pitch-targetPitch, 3);
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
        if(landingSequence.isEmpty()){
            state="Landed";
            return;
        }
        if(landingSequence.get(0).update(this)){
            landingSequence.remove(0);
        }
    }
    private void checkForCrashes(){
        ArrayList<Plot> plots = new ArrayList<>();
        int xDown = Math.round(x-0.3f);
        int xUp = Math.round(x+0.3f);
        int yDown = Math.round(y-0.3f);
        int yUp = Math.round(y+0.3f);
        int zDown = Math.round(z-0.3f);
        int zUp = Math.round(z+0.3f);
        checkForCrash(xDown, yDown, zDown);
        if(xUp!=xDown){
            checkForCrash(xUp, yDown, zDown);
            if(yUp!=yDown){
                checkForCrash(xDown, yUp, zDown);
                checkForCrash(xUp, yUp, zDown);
                if(zUp!=zDown){
                    checkForCrash(xDown, yDown, zUp);
                    checkForCrash(xUp, yDown, zUp);
                    checkForCrash(xDown, yUp, zUp);
                    checkForCrash(xUp, yUp, zUp);
                }
            }else if(zUp!=zDown){
                checkForCrash(xDown, yDown, zUp);
                checkForCrash(xUp, yDown, zUp);
            }
        }else if(yUp!=yDown){
            checkForCrash(xDown, yUp, zDown);
            if(zUp!=zDown){
                checkForCrash(xDown, yDown, zUp);
                checkForCrash(xDown, yUp, zUp);
            }
        }else if(zUp!=zDown){
            checkForCrash(xDown, yDown, zUp);
        }
    }
    public void setLocation(Plot plot){
        this.x = plot.x;
        this.y = plot.y;
        this.z = plot.z;
    }
    public void setHeading(Side front){
        switch(front){
            case FRONT:
                heading = 270;
                break;
            case BACK:
                heading = 90;
                break;
            case LEFT:
                heading = 180;
                break;
            case RIGHT:
                heading = 0;
                break;
            default:
                throw new AssertionError(front.name());
        }
    }
    public void setSpeed(int speed){
        this.speed = speed;
        this.targetSpeed = speed;
    }
    public void setTargetSpeed(int speed){
        targetSpeed = speed;
    }
    private void checkForCrash(int x, int y, int z){
        World world = player.world;
        Plot plot = world.getPlot(x, y, z);
        if(plot==null){
            return;
        }
        if(plot.getType().causesAirlineCrash()){
            crash();
        }
    }
    private void crash(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
