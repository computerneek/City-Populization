package CityPopulization.world.player;
import CityPopulization.world.World;
import CityPopulization.world.aircraft.StartingHellicopter;
import CityPopulization.world.aircraft.cargo.AircraftCargo;
import CityPopulization.world.aircraft.passenger.AircraftPassenger;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.resource.Resource;
public class PlayerHuman extends Player {
    public PlayerHuman(){
        this(null);
    }
    public PlayerHuman(World world){
        super(world);
        setRace(Race.HUMAN);
    }
    @Override
    public Player createNew(World world){
        return new PlayerHuman(world);
    }
    @Override
    public void summonInitialWorkers(){
        world.generatePlot(0, 0, -1).setType(PlotType.Grass);
        world.generatePlot(-1, 0, -1).setType(PlotType.Grass);
        world.generatePlot(-1, -1, -1).setType(PlotType.Grass);
        world.generatePlot(0, -1, 0).setType(PlotType.Grass);
        world.generateAndGetPlot(0, 0, 0).setType(PlotType.AirportEntrance).setOwner(world.getLocalPlayer());
        world.generateAndGetPlot(-1, 0, 0).setType(PlotType.AirportTerminal).setOwner(world.getLocalPlayer());
        world.generateAndGetPlot(-1, -1, 0).setType(PlotType.AirportJetway).setOwner(world.getLocalPlayer());
        world.generateAndGetPlot(0, -1, 0).setType(PlotType.AirportRunway).setOwner(world.getLocalPlayer());
        world.getPlot(0, 0, 0)
                .addInboundAircraft(new StartingHellicopter(world.getLocalPlayer()))
                .loadPassengers(AircraftPassenger.workers(4))
                .loadCargo(AircraftCargo.resource(Resource.Dirt, 500))
                .loadCargo(AircraftCargo.resource(Resource.Wood, 500))
                .loadCargo(AircraftCargo.resource(Resource.Coal, 100))
                .loadCargo(AircraftCargo.resource(Resource.Iron, 100))
                .loadCargo(AircraftCargo.resource(Resource.Oil, 800))
                .loadCargo(AircraftCargo.tools(Tools.Construction, 1))
                .setDepartureTime(6000);
    }
}
