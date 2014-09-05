package CityPopulization.world.aircraft;
import CityPopulization.Core;
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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.lwjgl.opengl.GL11;
import simplelibrary.config2.Config;
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
    float x;
    float y;
    float z;
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
    private int crashed;
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
        landingSequence = getLandingSequence(runway.size());
        player.world.aircraft.add(this);
        state = "Landing";
        terminal.occupied = Terminal.IN;
        runway.getStartPlot().terminal.occupied = Terminal.IN;
    }
    public abstract ArrayList<LandingSequenceEvent> getLandingSequence(int runwayLength);
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
        if(crashed>0){
            targetSpeed-=0.01;
            targetPitch--;
            if(targetPitch<-90){
                targetPitch = -90;
            }
            if(targetSpeed<0){
                targetSpeed = 0;
            }
            if(targetPitch<=-90||targetSpeed<=0){
                crashed++;
            }
            Plot plot = player.world.getPlot(Math.round(x), Math.round(y), Math.round(z)+1);
            if(plot.getType().getConstructionCost(player.race)!=null||!plot.getType().isOpaque()){
                plot.setType(PlotType.Debris).setOwner(player);
            }else if(plot.getType()!=PlotType.Debris){
                player.world.aircraft.remove(this);
            }
            if(crashed>100){
                player.world.aircraft.remove(this);
            }
        }else{
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
        }
    }
    private void landingUpdate(){
        if(landingSequence.isEmpty()){
            if(state.equals("Landing")){
                state="Landed";
            }else{
                runway.getStartPlot().terminal.occupied = 0;
                runway.getStartPlot().terminal.occupiers = 0;
                player.world.aircraft.remove(this);
                if(schedule!=null){
                    for(AircraftPassenger pass : passengers){
                        if(pass instanceof AircraftPassengerCivilian){
                            schedule.civilians--;
                            return;
                        }
                    }
                    schedule.civilians++;
                }
                player.cash+=cargoOccupied;
            }
            return;
        }
        if(landingSequence.get(0).update(this)){
            landingSequence.remove(0);
        }
        checkForCrashes();
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
            crash(x, y, z);
        }
    }
    private void crash(int x, int y, int z){
        if(terminal!=null&&state.equals("Landing")){
            terminal.occupiers--;
            terminal.occupied = 0;
            terminal.state = Terminal.IDLE;
        }
        runway.getStartPlot().terminal.occupiers--;
        runway.getStartPlot().terminal.occupied = 0;
        player.world.getPlot(x, y, z).setType(PlotType.Debris).setOwner(player);
        player.world.getPlot(Math.round(x), Math.round(y), Math.round(z)+1).setType(PlotType.Debris).setOwner(player);
        crashed = 1;
        if(schedule!=null){
            for(AircraftPassenger pass : passengers){
                schedule.civilians = -Math.abs(schedule.civilians)-5;
                if(pass instanceof AircraftPassengerCivilian){
                    if(schedule.civilians>0){
                        schedule.civilians--;
                    }
                }
            }
        }
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
        GL11.glTranslatef(x+0.5f, -y-0.5f, z+1);
        GL11.glRotatef(-heading+90, 0, 0, 1);
        GL11.glRotatef(tilt, 0, 1, 0);
        ImageStash.instance.bindTexture(ImageStash.instance.getTexture("/textures/aircraft/"+textureFolder+"/frame "+(tick%frameCap+1)+".png"));
        GL11.glColor3f(1, 1, 1);
        render();
        GL11.glRotatef(-tilt, 0, 1, 0);
        GL11.glRotatef(heading-90, 0, 0, 1);
        GL11.glTranslatef(-x-0.5f, y+0.5f, -z-1);
    }
    private void render(){
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex3d(-0.3, 0.3, 0);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex3d(0.3, 0.3, 0);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex3d(0.3, -0.3, 0);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex3d(-0.3, -0.3, 0);
        GL11.glEnd();
    }
    private void landedUpdate(){
        AircraftPath path = AircraftPath.findPath(runway, terminal);
        if(path!=null){
            this.path = path;
            taxiSequence = path.generateDirections();
            runway.getStartPlot().terminal.occupiers = 1;
            state = "TaxiIn";
        }
    }
    private void departureUpdate(){
        ArrayList<Runway> runways = new ArrayList<>();
        terminal.findRunways(runways);
        speed = 0;
        for(Runway runway : runways){
            if(runway.size()<getRequiredRunwayLength()||runway.isOccupied()){
                continue;
            }
            AircraftPath path = AircraftPath.findPath(terminal, runway);
            if(path!=null){
                this.path = path;
                taxiSequence = path.generateDirections();
                runway.getStartPlot().terminal.occupied = Terminal.OUT;
                setLocation(terminal.plot);
                z-=1;
                setHeading(terminal.plot.front);
                targetSpeed = 0;
                state = "TaxiOut";
                this.runway = runway;
                break;
            }
        }
    }
    private void taxiUpdate(){
        if(taxiSequence.isEmpty()){
            if(state.equals("TaxiIn")){
                player.world.aircraft.remove(this);
                terminal.onArrival(this);
            }else{
                landingSequence = getTakeoffSequence(runway.size());
                state = "Takeoff";
            }
            return;
        }
        if(taxiSequence.get(0).update(this)){
            taxiSequence.remove(0);
        }
    }
    public abstract ArrayList<LandingSequenceEvent> getTakeoffSequence(int runwayLength);
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
    public Config save(){
        Config config = Config.newConfig();
        config.set("class", getClass().getCanonicalName());
        config.set("texture", textureFolder);
        config.set("player", player.world.otherPlayers.indexOf(player));
        Config two = Config.newConfig();
        two.set("count", passengers.size());
        for(int i = 0; i<passengers.size(); i++){
            two.set(i+"", passengers.get(i).save());
        }
        config.set("passengers", two);
        two = Config.newConfig();
        two.set("count", cargo.size());
        for(int i = 0; i<cargo.size(); i++){
            two.set(i+"", cargo.get(i).save());
        }
        config.set("cargo", two);
        config.set("passCapacity", passengerCapacity);
        config.set("cargoCapacity", cargoCapacity);
        config.set("departureTime", departureTime);
        config.set("cargo", cargoOccupied);
        if(runway!=null){
            config.set("runwayx", runway.getStartPlot().x);
            config.set("runwayy", runway.getStartPlot().y);
            config.set("runwayz", runway.getStartPlot().z);
        }
        if(terminal!=null){
            config.set("terminalx", terminal.plot.x);
            config.set("terminaly", terminal.plot.y);
            config.set("terminalz", terminal.plot.z);
        }
        if(landingSequence!=null){
            two = Config.newConfig();
            two.set("count", landingSequence.size());
            for(int i = 0; i<landingSequence.size(); i++){
                two.set(i+"", landingSequence.get(i).save());
            }
            config.set("sequence", two);
        }
        if(state!=null){
            config.set("state", state);
        }
        config.set("x", x);
        config.set("y", y);
        config.set("z", z);
        config.set("heading", heading);
        config.set("targetHeading", targetHeading);
        config.set("pitch", pitch);
        config.set("tilt", tilt);
        config.set("speed", speed);
        config.set("targetSpeed", targetSpeed);
        config.set("targetPitch", targetPitch);
        config.set("tick", tick);
        if(path!=null){
            config.set("path", path.save());
        }
        if(taxiSequence!=null){
            two = Config.newConfig();
            two.set("count", taxiSequence.size());
            for(int i = 0; i<taxiSequence.size(); i++){
                two.set(i+"", taxiSequence.get(i).save());
            }
            config.set("taxiSequence", two);
        }
        config.set("fuelLevel", fuelLevel);
        config.set("maxFuelLevel", maxFuelLevel);
        if(schedule!=null){
            config.set("schedule", schedule.getIndex());
        }
        config.set("crashed", crashed);
        return config;
    }
    public static Aircraft load(Config config){
        try{
            int which = config.get("player");
            Player player = which==-1?Core.loadingWorld.localPlayer:Core.loadingWorld.otherPlayers.get(which);
            String texture = config.get("texture");
            Aircraft air = (Aircraft)Class.forName((String)config.get("class")).getConstructor(Player.class, String.class).newInstance(player, texture);
            Config two = config.get("passengers");
            for(int i = 0; i<(int)two.get("count"); i++){
                air.passengers.add(AircraftPassenger.load((Config)two.get(i+"")));
            }
            air.passengerCapacity = config.get("passCapacity");
            air.cargoCapacity = config.get("cargoCapacity");
            air.cargoOccupied = config.get("cargo");
            if(config.hasProperty("runwayx")){
                air.runway = Runway.findRunway(Core.loadingWorld.generatePlot((int)config.get("runwayx"), (int)config.get("runwayy"), (int)config.get("runwayz")));
            }
            if(config.hasProperty("terminalx")){
                air.terminal = Core.loadingWorld.getPlot((int)config.get("terminalx"), (int)config.get("terminaly"), (int)config.get("terminalz")).terminal;
            }
            if(config.hasProperty("sequence")){
                two = config.get("sequence");
                air.landingSequence = new ArrayList<>();
                for(int i = 0; i<(int)two.get("count"); i++){
                    air.landingSequence.add(LandingSequenceEvent.load((Config)two.get(i+"")));
                }
            }
            if(config.hasProperty("state")){
                air.state = config.get("state");
            }
            air.x = config.get("x");
            air.y = config.get("y");
            air.z = config.get("z");
            air.heading = config.get("heading");
            air.targetHeading = config.get("targetHeading");
            air.pitch = config.get("pitch");
            air.tilt = config.get("tilt");
            air.speed = config.get("speed");
            air.targetSpeed = config.get("targetSpeed");
            air.targetPitch = config.get("targetPitch");
            air.tick = config.get("tick");
            if(config.hasProperty("path")){
                air.path = AircraftPath.load((Config)config.get("path"));
            }
            if(config.hasProperty("taxiSequence")){
                two = config.get("taxiSequence");
                air.taxiSequence = new ArrayList<>();
                for(int i = 0; i<(int)two.get("count"); i++){
                    air.taxiSequence.add(TaxiEvent.load((Config)two.get(i+"")));
                }
            }
            air.fuelLevel = config.get("fuelLevel");
            air.maxFuelLevel = config.get("maxFuelLevel");
            if(config.hasProperty("schedule")){
                int index = config.get("schedule");
                FOR:for(HashMap<Integer, HashMap<Integer, Plot>> plts : Core.loadingWorld.plots.values()){
                    for(HashMap<Integer, Plot> plots : plts.values()){
                        for(Plot plot : plots.values()){
                            for(ScheduleElement element : plot.terminal.schedule.elements){
                                if(element.index==index){
                                    air.schedule = element;
                                    break FOR;
                                }
                            }
                        }
                    }
                }
            }
            air.crashed = (int)(config.hasProperty("crashed")?config.get("crashed"):0);
            return air;
        }catch(ClassNotFoundException|NoSuchMethodException|SecurityException|InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException ex){
            throw new RuntimeException(ex);
        }
    }
}
