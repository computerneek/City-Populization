package CityPopulization.world.aircraft;
import CityPopulization.render.Side;
import CityPopulization.world.World;
import CityPopulization.world.aircraft.cargo.AircraftCargo;
import CityPopulization.world.aircraft.landingSequence.LandingSequenceEvent;
import CityPopulization.world.aircraft.passenger.AircraftPassenger;
import CityPopulization.world.aircraft.passenger.AircraftPassengerCivilian;
import CityPopulization.world.aircraft.schedule.ScheduleElement;
import CityPopulization.world.player.Player;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
public abstract class Aircraft{
    public final Player player;
    public ArrayList<AircraftPassenger> passengers = new ArrayList<>();
    public ArrayList<AircraftCargo> cargo = new ArrayList<>();
    public int passengerCapacity;
    public int cargoCapacity;
    public int departureTime;
    int cargoOccupied;
    public Runway runway;
    private Terminal terminal;
    private ArrayList<LandingSequenceEvent> landingSequence = new ArrayList<>();
    private String state;
    private float x;
    private float y;
    private float z;
    private float heading;
    private float targetHeading;
    private float pitch;
    private boolean tiltToMatchPitch;
    private float tilt;
    private float speed;
    private float targetSpeed;
    public float targetPitch;
    private final String textureFolder;
    private int frameCap;
    private int tick;
    private AircraftPath path;
    private ArrayList<TaxiEvent> taxiSequence;
    public int fuelLevel = 0;
    public int maxFuelLevel = 50;
    public int minimumRunwayLength = 1;
    public ScheduleElement schedule;
    public Aircraft(Player player, String textureFolder){
        this.player = player;
        this.textureFolder=textureFolder;
        this.frameCap = findFrameCap();
    }
    private int findFrameCap(){
        int frameCap = 0;
        for(int i = 1; frameCap==0; i++){
            String path = "/textures/aircraft/"+textureFolder+"/frame "+i+".png";
            try(InputStream in = PlotType.class.getResourceAsStream(path)){
                if(in==null){
                    frameCap = i-1;
                }
            }catch(IOException ex){}
        }
        return frameCap;
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
    public boolean loadOneCargo(ArrayList<AircraftCargo> resources){
        for(Iterator<AircraftCargo> it=resources.iterator(); it.hasNext();){
            AircraftCargo cargo=it.next();
            if(cargoCapacity-cargoOccupied>=cargo.getSpaceOccupied()){
                this.cargo.add(cargo);
                cargoOccupied+=cargo.getSpaceOccupied();
                it.remove();
                return true;
            }
        }
        return false;
    }
    public int getRequiredRunwayLength(){
        return minimumRunwayLength;
    }
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
        terminal.occupied = Terminal.IN;
        runway.getStartPlot().terminal.occupied = Terminal.IN;
    }
    public abstract ArrayList<LandingSequenceEvent> getLandingSequence();
    public void update(){
        tick++;
        float distance = 0.02F*speed;
        float xyDist = (float)Math.cos(Math.toRadians(pitch))*distance;
        float zDist = (float)Math.sin(Math.toRadians(pitch))*distance;
        float xDist = (float)Math.cos(Math.toRadians(heading))*xyDist;
        float yDist = (float)Math.sin(Math.toRadians(heading))*xyDist;
        x+=xDist;
        y+=yDist;
        z+=zDist;
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
        if(heading<targetHeading){
            heading+=Math.min(targetHeading-heading, 3);
        }else if(heading>targetHeading){
            heading-=Math.min(heading-targetHeading, 3);
        }
        if(tiltToMatchPitch){
            tilt = pitch;
        }
        switch(state){
            case "Landing":
            case "Takeoff":
                landingUpdate();
                break;
            case "Landed":
                landedUpdate();
                break;
            case "TaxiIn":
            case "TaxiOut":
                taxiUpdate();
                break;
            case "Departure":
                departureUpdate();
                break;
            default:
                throw new AssertionError(state);
        }
        checkForCrashes();
    }
    private void landingUpdate(){
        if(landingSequence.isEmpty()){
            if(state.equals("Landing")){
                state="Landed";
            }else{
                runway.getStartPlot().terminal.occupied = 0;
                player.world.aircraft.remove(this);
                if(schedule!=null){
                    for(AircraftPassenger pass : passengers){
                        if(pass instanceof AircraftPassengerCivilian){
                            if(schedule.civilians>1){
                                schedule.civilians--;
                            }
                            return;
                        }
                    }
                    schedule.civilians++;
                }
            }
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
                heading = 90;
                break;
            case BACK:
                heading = 270;
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
        targetHeading = heading;
    }
    public void setSpeed(int speed){
        this.speed = speed;
        this.targetSpeed = speed;
    }
    public void setTargetSpeed(int speed){
        targetSpeed = speed;
    }
    private void checkForCrash(int x, int y, int z){
        z++;
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
    public void render(Player localPlayer){
        boolean canPlayerSeePlane = player==localPlayer;
        Plot currentPlot = player.world.getPlot(Math.round(x), Math.round(y), Math.round(z));
        if(currentPlot!=null&&localPlayer==currentPlot.getOwner()){
            canPlayerSeePlane = true;
        }
        if(!canPlayerSeePlane){
            return;
        }
        GL11.glTranslatef(x+0.5f, -y-0.5f, z);
        GL11.glRotatef(-heading+90, 0, 0, 1);
        GL11.glRotatef(tilt, 0, 1, 0);
        ImageStash.instance.bindTexture(ImageStash.instance.getTexture("/textures/aircraft/"+textureFolder+"/frame "+(tick%frameCap+1)+".png"));
        GL11.glColor3f(1, 1, 1);
        render();
        GL11.glRotatef(-tilt, 0, 1, 0);
        GL11.glRotatef(heading-90, 0, 0, 1);
        GL11.glTranslatef(-x-0.5f, y+0.5f, -z);
    }
    private void render(){
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex3d(-0.3, -0.3, 0);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex3d(0.3, -0.3, 0);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex3d(0.3, 0.3, 0);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex3d(-0.3, 0.3, 0);
        GL11.glEnd();
    }
    private void landedUpdate(){
        AircraftPath path = AircraftPath.findPath(runway, terminal);
        if(path!=null){
            this.path = path;
            taxiSequence = path.generateDirections();
            state = "TaxiIn";
        }
    }
    private void departureUpdate(){
        ArrayList<Runway> runways = new ArrayList<>();
        terminal.findRunways(runways);
        for(Runway runway : runways){
            if(runway.size()<getRequiredRunwayLength()){
                continue;
            }
            AircraftPath path = AircraftPath.findPath(terminal, runway);
            if(path!=null){
                this.path = path;
                taxiSequence = path.generateDirections();
                runway.getStartPlot().terminal.occupied = Terminal.OUT;
                setLocation(terminal.plot);
                setHeading(terminal.plot.front);
                speed = 0;
                targetSpeed = 0;
                state = "TaxiOut";
            }
        }
    }
    private void taxiUpdate(){
        if(taxiSequence.isEmpty()){
            if(state.equals("TaxiIn")){
                player.world.aircraft.remove(this);
                terminal.onArrival(this);
            }else{
                landingSequence = getTakeoffSequence();
                state = "Takeoff";
            }
            return;
        }
        if(taxiSequence.get(0).update(this)){
            taxiSequence.remove(0);
        }
    }
    public abstract ArrayList<LandingSequenceEvent> getTakeoffSequence();
    public float getHeading(){
        return heading;
    }
    public void setHeading(float heading){
        this.heading = heading;
    }
    public void setTargetHeading(float targetHeading){
        this.targetHeading = targetHeading;
    }
    public void depart(){
        player.world.aircraft.add(this);
        state = "Departure";
    }
}
