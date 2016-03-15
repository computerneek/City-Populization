package city.populization.world.plot;
import city.populization.menu.ingame.MenuComponentButtonIngame;
import city.populization.world.plot.type.PlotResource;
import city.populization.world.plot.type.PlotTownHall;
import city.populization.world.plot.type.PlotMarket;
import city.populization.world.plot.type.PlotWarehouse;
import city.populization.world.plot.type.PlotWorkshop;
import city.populization.world.plot.type.PlotHouse;
import city.populization.world.plot.type.PlotFarm;
import city.populization.world.plot.type.PlotRoad;
import city.populization.render.ResourceLocation;
import city.populization.world.Direction;
import city.populization.world.Player;
import city.populization.world.World;
import city.populization.world.plot.render.PlotRender;
import city.populization.world.plot.render.PlotRenderCube;
import city.populization.world.plot.render.PlotRenderRoad;
import city.populization.world.plot.render.PlotRenderStructure;
import city.populization.world.plot.type.PlotDebris;
import city.populization.world.plot.type.PlotElevator;
import city.populization.world.plot.type.PlotElevatorTop;
import city.populization.world.plot.type.PlotHall;
import city.populization.world.plot.type.PlotLobby;
import city.populization.world.plot.type.PlotRoom;
import city.populization.world.plot.type.PlotSolid;
import city.populization.world.plot.type.PlotStaircase;
import city.populization.world.plot.type.PlotStaircaseTop;
import city.populization.world.resource.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import simplelibrary.Queue;
public class Plot {
    private static List<Plot> allTypes;
    private static Queue<Plot> types = new Queue<>();
    private static ArrayList<String> names = new ArrayList<>();
    public static final Plot Air = new Plot("air", 0, 0, 0, 0, null);
    //<editor-fold defaultstate="collapsed" desc="Resources">
    public static final PlotResource Stone = new PlotResource("stone", 100000, 500, -100, 300, new PlotRenderCube("resources/stone.png"), Resource.Stone);
    public static final PlotResource Dirt = new PlotResource("dirt", 500, 50, -100, 0, new PlotRenderCube("resources/dirt.png"), Resource.Dirt);
    public static final PlotResource Sand = new PlotResource("sand", 100, 0, 150, 0, new PlotRenderCube("resources/sand.png"), Resource.Sand);
    public static final PlotResource Iron = new PlotResource("iron", 150000, 1000, -100, 1000, new PlotRenderCube("resources/iron.png"), Resource.Iron);
    public static final PlotResource Gold = new PlotResource("gold", 50000, 400, 5000, 0, new PlotRenderCube("resources/gold.png"), Resource.Gold);
    public static final PlotResource Diamond = new PlotResource("diamond", 500000, 10000, -50, 5000, new PlotRenderCube("resources/diamond.png"), Resource.Diamond);
    public static final PlotResource Coal = new PlotResource("coal", 25000, 750, -100, 150, new PlotRenderCube("resources/coal.png"), Resource.Coal);
    public static final PlotResource Clay = new PlotResource("clay", 250, 25, -100, 0, new PlotRenderCube("resources/clay.png"), Resource.Clay);
    public static final PlotResource Grass = new PlotResource("grass", 500, 50, -100, 0, new PlotRenderCube("resources/grass.png"), Resource.Dirt);
    public static final PlotResource Oil = new PlotResource("oil", 1000000, 0, 500, 0, new PlotRenderCube("resources/oil.png"), Resource.Oil);
    //</editor-fold>
    public static final PlotDebris Debris = new PlotDebris("debris", 0, 0, 500, 0, new PlotRenderStructure("buildings/debris"));
    public static final PlotRoad Road = new PlotRoad("road", 0, 0, 10, 0, new PlotRenderRoad("buildings/road", 1));
    public static final PlotWarehouse Warehouse = new PlotWarehouse("warehouse", 0, 0, 500, 0, new PlotRenderStructure("buildings/warehouse", 1));
    public static final PlotWorkshop Workshop = new PlotWorkshop("workshop", 0, 0, 250, 0, new PlotRenderStructure("buildings/workshop", 1));
    public static final PlotMarket Market = new PlotMarket("market", 0, 0, 250, 0, new PlotRenderStructure("buildings/market", 1));
    public static final PlotTownHall TownHall = new PlotTownHall("town_hall", 0, 0, 1000, 0, new PlotRenderStructure("buildings/townhall", 1));
    public static final PlotFarm Farm = new PlotFarm("farm", 0, 0, 500, 0, new PlotRenderStructure("buildings/farm", 1));
    //<editor-fold defaultstate="collapsed" desc="Houses">
    public static final PlotHouse Hut = new PlotHouse("hut", 0, 0, 50, 0, new PlotRenderStructure("buildings/hut"), 2, 25000);//level, bedrooms, cost
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Megastructures">
    //A room, as part of a larger structure.  Note:  They don't connect to normal roads, use hallways for that
    //Note:  These will not be used if they do not have power.  They will NOT conduct power.
    public static final PlotRoom Room;
    public static final PlotRoom Apartment;
    //A 'road' for only people, with walls & ceiling- for in a skyscraper, but usable anywhere.
    //People can only walk; travel is slower than on roads, but sheltered (considered sanitary, other players' units cannot pass)
    //Note:  Does not connect to normal roads.  Use a lobby for that.
    //Also:  These will not be used if they do not have power.  They will conduct power to connected hallways, rooms, elevators, or lobbies.
    public static final PlotHall Hall;
    //A lobby, to connect hallways and roads.  Front faces road, back faces hallway.
    //Note:  These will not be used if they do not have power.  They will conduct power to connected hallways, rooms, elevators, or lobbies.
    public static final PlotLobby Lobby;
    //A staircase for only people, with walls & ceiling.  The bottom is the 'front'.  NOTE:  For the staircase to be usable, the plot above must be either Staircase or StaircaseTop with the SAME ORIENTATION.
    //Note:  These will not be used if they do not have power.  They will conduct power to connected hallways, rooms, elevators, or lobbies.
    public static final PlotStaircase Staircase;
    //A top for the Staircase
    public static final PlotStaircaseTop StaircaseTop;
    //An elevator for only people, to go up & down in skyscrapers.  Also usable in, say, mines.....  Very heavily reinforced, can be substituted for a pillar- heavier, though, and more expensive.
    //Note:  Elevator is only usable if the top of the shaft ends in a powered ElevatorTop.
    //Note:  These will not be used if they do not have power.  They will conduct power to connected hallways, rooms, elevators, or lobbies.
    public static final PlotElevator Elevator;
    //The top of an elevator, for only people.
    //Note:  These do NOT conduct power, but they do use it.
    public static final PlotElevatorTop ElevatorTop;
    //Also:  Support lattice...   for, say, cave ceilings, towns (to prevent cave-ins), airborne towns, bridges, etc.  Solid, so non-skyscraper stuff can go on it.  There's gonna be some interesting skyscrapers...  and parking garages.
    //Note:  These do NOT conduct power.
    public static final PlotSolid Lattice;
    //Also:  Skyscraper support, cave roof support, bridge support....  Cannot be pathed through.
    //Note:  These do NOT conduct power.
    public static final PlotSolid Pillar;
    static{
        int crush = 5000, friction = 2500, weight = 500, tension = 1000;
        Room = new PlotRoom("room", crush, friction, weight, tension, new PlotRenderStructure("buildings/mega/room", 1), 1, 10000);
        Apartment = new PlotRoom("apartment", crush, friction, weight, tension, new PlotRenderStructure("buildings/mega/apartment", 1), 3, 50000);
        Hall = new PlotHall("hall", crush, friction, weight, tension, new PlotRenderStructure("buildings/mega/hall", 1));
        Lobby = new PlotLobby("lobby", crush, friction, weight, tension, new PlotRenderStructure("buildings/mega/lobby", 1));
        Staircase = new PlotStaircase("staircase", crush, friction, weight, tension, new PlotRenderStructure("buildings/mega/staircase/base", 1));
        StaircaseTop = new PlotStaircaseTop("staircase top", crush, friction, weight, tension, new PlotRenderStructure("buildings/mega/staircase/top", 1));
        Lattice = new PlotSolid("lattice", crush*3, friction*6, weight, tension, new PlotRenderStructure("buildings/mega/lattice", 1));
        crush = 60000; friction = 30000; weight = 5000; tension = 100000;
        Elevator = new PlotElevator("elevator", crush, friction, weight*2, tension, new PlotRenderStructure("buildings/mega/elevator/shaft", 1), new PlotRenderStructure("buildings/mega/elevator/car", 1), new PlotRenderStructure("buildings/mega/elevator/counterbalance", 1));
        //10 is the lvl1 max weight capacity at the top of the shaft.  The cables weigh 1 per plot; elevators won't go down far enough to zero their additional capacity with their current load
        ElevatorTop = new PlotElevatorTop("elevator top", crush, friction, weight*3, tension, new PlotRenderStructure("buildings/mega/elevator/top", 1), new PlotRenderStructure("buildings/mega/elevator/car", 1), new PlotRenderStructure("buildings/mega/elevator/counterbalance", 1), 10);
        Pillar = new PlotSolid("pillar", crush, friction, weight, tension, new PlotRenderStructure("buildings/mega/pillar", 1));
    }
    //</editor-fold>
    public final String name;
    public final int index;
    private final int crushResistance;
    private final int friction;
    private final int weight;
    private final int tension;
    public final PlotRender model;
    protected List<Direction> roadConnections = Collections.emptyList();
    protected List<Direction> hallConnections = Collections.emptyList();
    protected boolean isStaircase = false;
    protected boolean isPassible = false;
    protected boolean isElevator = false;
    /**
     * When falling and landing on something, plots weigh (speed+1)* normal, and are decelerated at least some by the crush, depending on the plot's crush resistance.  They cannot accelerate during a crush.
     * Also, when landing, non-resource plots are damaged (lvldown) or destroyed.
     * This keeps things from freefalling through the ground.
     */
    public Plot(String name, int crushResistance, int friction, int weight, int tension, PlotRender model){
        name = name.toLowerCase();
        for(int i = 0; i<name.length(); i++){
            if(name.charAt(i)=='_'){
                name = name.substring(0, i)+" "+name.substring(i+1);
            }
            if(i==0||name.charAt(i-1)==' '){
                name = name.substring(0, i)+name.substring(i, i+1).toUpperCase()+name.substring(i+1);
            }
        }
        name = this.name = name.substring(0, 1).toUpperCase()+name.substring(1);
        this.crushResistance = crushResistance;
        this.friction = friction;
        this.weight = weight;
        this.tension = tension;
        this.model = model;
        synchronized(Plot.class){
            if(types==null){
                throw new IllegalStateException("Plot creation has already been finalized!");
            }else if(name==null){
                throw new NullPointerException("Plot name cannot be null!");
            }else if(names.contains(name)){
                throw new IllegalArgumentException("Name '"+name+"' already in use!");
            }
            index = types.size();
            names.add(name);
            types.enqueue(this);
        }
    }
    public static synchronized List<Plot> getTypes(){
        return allTypes;
    }
    public static Plot byName(String name){
        for(Plot p : allTypes){
            if(p.name.equals(name)){
                return p;
            }
        }
        return null;
    }
    public static synchronized void finalizePlotConstruction(){
        synchronized(Plot.class){
            allTypes = Collections.unmodifiableList(types.toList());
            names = null;
            types = null;
            ResourceLocation.finalizeType(ResourceLocation.Type.PLOT);
            MenuComponentButtonIngame.back.getClass();//Just to cause the class to initialize & load its textures
        }
    }
    public void requestUpdate(World w, PlotPos pos){
        w.requestedUpdates.enqueue(pos);
    }
    public void onUpdate(World w, PlotPos pos){}
    public List<Direction> getRoadConnections() {
        return roadConnections;
    }
    public List<Direction> getHallConnections() {
        return hallConnections;
    }
    public boolean isStaircase(){
        return isStaircase;
    }
    public int getLevelCount(){
        return 1;
    }
    public boolean isBuildable(Player localPlayer, PlotPos pos, World world) {
        return false;
    }
    public int getCrushResistance() {
        return crushResistance;
    }
    public int getWeight() {
        return weight;
    }
}
