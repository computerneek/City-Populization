package CityPopulization.world.player;
import CityPopulization.menu.MenuIngame;
import CityPopulization.menu.buttons.Button;
import CityPopulization.menu.buttons.ButtonEvent;
import CityPopulization.menu.buttons.ButtonSet;
import CityPopulization.render.Side;
import CityPopulization.world.World;
import CityPopulization.world.aircraft.StartingHellicopter;
import CityPopulization.world.aircraft.cargo.AircraftCargo;
import CityPopulization.world.aircraft.passenger.AircraftPassenger;
import CityPopulization.world.civilian.WorkerTask;
import CityPopulization.world.civilian.WorkerTaskSegment;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import java.util.ArrayList;
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
        world.generateAndGetPlot(0, 0, 0).setType(PlotType.AirportEntrance).setOwner(this)
                .addInboundAircraft(new StartingHellicopter(this))
                .loadPassengers(AircraftPassenger.workers(4))
                .loadCargo(AircraftCargo.resource(Resource.Tools, 1))
                .loadCargo(AircraftCargo.resource(Resource.Dirt, 500))
                .loadCargo(AircraftCargo.resource(Resource.Wood, 500))
                .loadCargo(AircraftCargo.resource(Resource.Coal, 100))
                .loadCargo(AircraftCargo.resource(Resource.Iron, 100))
                .loadCargo(AircraftCargo.resource(Resource.Oil, 750))
                .loadCargo(AircraftCargo.resource(Resource.Fuel, 49))
                .setDepartureTime(20*60*15);
        world.generateAndGetPlot(-1, 0, 0).setType(PlotType.AirportTerminal).setOwner(this).setFront(Side.BACK);
        world.generateAndGetPlot(-1, -1, 0).setType(PlotType.AirportJetway).setOwner(this);
        world.generateAndGetPlot(0, -1, 0).setType(PlotType.AirportRunway).setOwner(this).setFront(Side.LEFT);
        resourceStructures.add(world.getPlot(0, 0, 0));
        world.getPlot(0, 0, 0).resources.add(Resource.Cash, 1000);
    }
    @Override
    public void onPlotClicked(int plotX, int plotY, MenuIngame menu, int button){
        Plot plot = world.getPlot(plotX, plotY, cameraZ);
        ButtonSet set = new ButtonSet();
        if(plot!=null&&button==0){
            if(plot.owner!=null&&plot.owner!=this||(plot.task!=null&&plot.task.owner!=this)){
                onEnemyPlotClicked(plot, set);
            }else if(plot.owner==null){
                onOwnerlessPlotClicked(plot, set);
            }else{
                onOwnedPlotClicked(plot, set);
            }
        }
        menu.setButtonSet(set);
    }
    private void onEnemyPlotClicked(Plot plot, ButtonSet set){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void onOwnerlessPlotClicked(Plot plot, ButtonSet set){
        switch(plot.getType()){
            case ClayDeposit:
                onResourceMineClicked(plot, set, "Clay", Resource.Clay);
                break;
            case CoalDeposit:
                onResourceMineClicked(plot, set, "Coal", Resource.Coal);
                break;
            case Dirt:
            case Grass:
                onResourceMineClicked(plot, set, "Dirt", Resource.Dirt);
                break;
            case GoldDeposit:
                onResourceMineClicked(plot, set, "Gold", Resource.Gold);
                break;
            case IronDeposit:
                onResourceMineClicked(plot, set, "Iron Deposit", Resource.Iron);
                break;
            case OilDeposit:
                onResourceMineClicked(plot, set, "Oil Well", Resource.Oil);
                break;
            case Sand:
                onResourceMineClicked(plot, set, "Sand", Resource.Sand);
                break;
            case Stone:
                onResourceMineClicked(plot, set, "Stone", Resource.Stone);
                break;
            case Woods:
                onResourceMineClicked(plot, set, "Woods", Resource.Wood);
                break;
            case Air:
                onAirClicked(plot, set);
                break;
        }
    }
    private void onOwnedPlotClicked(Plot plot, ButtonSet set){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void onResourceMineClicked(Plot plot, ButtonSet set, String name, Resource resource){
        if(plot.task==null){
            set.add(createHarvestResourceButton(name, resource, plot));
        }else if(!plot.task.started){
            set.add(createCancelTaskButton(plot));
        }
    }
    private Button createCancelTaskButton(Plot plot){
        return new Button()
                .setImage("/gui/buttons/cancel.png")
                .setText("Cancel")
                .setEvent(new ButtonEvent()
                        .setType("Cancel Task")
                        .setPlot(plot));
    }
    private Button createHarvestResourceButton(String image, Resource revenue, Plot plot){
        return new Button()
                .setImage("/gui/buttons/harvest"+image+".png")
                .setText("Harvest")
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(new WorkerTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setCost(new ResourceList(Resource.Cash, 100, Resource.Tools, 1))
                                .setRevenue(new ResourceList().addAll(plot.getType().resourceHarvested).multiply(world.difficulty.incomeModifier).add(Resource.Tools, 1))
                                .addSegment(new WorkerTaskSegment()
                                        .setType("Plot Type")
                                        .setData(PlotType.Air, Side.FRONT))));
    }
    private void onAirClicked(Plot plot, ButtonSet set){
        if(plot.task==null){
            for(PlotType type : PlotType.values()){
                if(type.getConstructionCost(race)!=null){
                    set.add(createConstructionButton(plot, type));
                }
            }
        }else if(!plot.task.started){
            set.add(createCancelTaskButton(plot));
        }
    }
    private Button createConstructionButton(Plot plot, PlotType type){
        return new Button()
                .setImage("/gui/buttons/"+race.getName()+"/build"+type.textureFolder+".png")
                .setText("Construct", type.name)
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(new WorkerTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setCost(type.getConstructionCost(race).add(Resource.Tools, 1))
                                .setRevenue(new ResourceList().add(Resource.Tools, 1))
                                .addSegment(new WorkerTaskSegment()
                                        .setType("Plot Type")
                                        .setData(type, Side.FRONT))));
    }
}
