package CityPopulization.world.player;
import CityPopulization.menu.MenuIngame;
import CityPopulization.menu.buttons.Button;
import CityPopulization.menu.buttons.ButtonEvent;
import CityPopulization.menu.buttons.ButtonSet;
import CityPopulization.render.Side;
import CityPopulization.world.World;
import CityPopulization.world.aircraft.StartingHelicopter;
import CityPopulization.world.aircraft.Template;
import CityPopulization.world.aircraft.cargo.AircraftCargo;
import CityPopulization.world.aircraft.passenger.AircraftPassenger;
import CityPopulization.world.aircraft.schedule.ScheduleElement;
import CityPopulization.world.civilian.WorkerTask;
import CityPopulization.world.civilian.WorkerTaskSegment;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
public class PlayerHuman extends Player {
    public PlayerHuman(){
        super(null);
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
                .addInboundAircraft(new StartingHelicopter(this))
                .loadPassengers(AircraftPassenger.workers(4))
                .loadCargo(AircraftCargo.resource(Resource.Tools, 1))
                .loadCargo(AircraftCargo.resource(Resource.Dirt, 500))
                .loadCargo(AircraftCargo.resource(Resource.Wood, 500))
                .loadCargo(AircraftCargo.resource(Resource.Coal, 100))
                .loadCargo(AircraftCargo.resource(Resource.Iron, 1100))
                .loadCargo(AircraftCargo.resource(Resource.Oil, 750))
                .loadCargo(AircraftCargo.resource(Resource.Fuel, 49))
                .setDepartureTime(20*60*2);
        world.generateAndGetPlot(-1, 0, 0).setType(PlotType.AirportTerminal).setOwner(this).setFront(Side.BACK);
        world.generateAndGetPlot(-1, -1, 0).setType(PlotType.AirportJetway).setOwner(this);
        world.generateAndGetPlot(0, -1, 0).setType(PlotType.AirportRunway).setOwner(this).setFront(Side.LEFT);
        resourceStructures.add(world.getPlot(0, 0, 0));
        cash = 1000;
        world.getPlot(0, 0, 0).terminal.schedule.elements.add(new ScheduleElement(Template.HELICOPTER_TINY, 1, 0, new ResourceList(), 12000, 300));
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
        switch(plot.getType()){
            case AirportRunway:
            case AirportJetway:
            case Road:
            case AirportTerminal:
            case Workshop:
            case Store:
            case Elevator:
                onPlainOwnedPlotClicked(plot, set);
                break;
            case AirportEntrance:
                onAirportClicked(plot, set);
                break;
            case Warehouse:
                onWarehouseClicked(plot, set);
                break;
            case Bank:
                onBankClicked(plot, set);
                break;
            case House:
                onHouseClicked(plot, set);
                break;
            default:
                System.err.println("Unrecognized plot type "+plot.getType().name()+"!  (PlayerHuman)");
        }
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
                                .setCost(new ResourceList())
                                .setCash(100)
                                .setRevenue(new ResourceList().addAll(plot.getType().resourceHarvested))
                                .addSegment(new WorkerTaskSegment()
                                        .setType("Plot Type")
                                        .setData(PlotType.Air, 0, Side.FRONT))));
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
                                .setCost(type.getConstructionCost(race))
                                .setCash(100)
                                .setRevenue(new ResourceList())
                                .addSegment(new WorkerTaskSegment()
                                        .setType("Plot Type")
                                        .setData(type, 0, Side.FRONT))));
    }
    @Override
    public void update(){
        super.update(); //To change body of generated methods, choose Tools | Templates.
    }
    private void onPlainOwnedPlotClicked(Plot plot, ButtonSet set){
        if(plot.task==null){
            if(plot.canUpgrade(race)){
                set.add(createUpgradeButton(plot));
            }
            set.add(createDowngradeButton(plot));
            set.add(createDestroyButton(plot));
        }else if(!plot.task.started){
            set.add(createCancelTaskButton(plot));
        }
    }
    private Button createUpgradeButton(Plot plot){
        return new Button()
                .setImage("/gui/buttons/"+race.getName()+"/upgrade"+plot.getType().textureFolder+".png")
                .setText("Upgrade")
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(new WorkerTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setCost(plot.getType().getCost(plot.getLevel()+1, race))
                                .setCash(100*(plot.getLevel()+1)*(plot.getLevel()+1))
                                .setRevenue(new ResourceList())
                                .addSegment(new WorkerTaskSegment()
                                        .setType("Plot Type")
                                        .setData(plot.getType(), plot.getLevel()+1, plot.getFront()))));
    }
    private Button createDowngradeButton(Plot plot){
        return new Button()
                .setImage("/gui/buttions/"+race.getName()+"/downgrade"+plot.getType().textureFolder+".png")
                .setText("Downgrade")
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(new WorkerTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setCost(new ResourceList())
                                .setCash(100*(plot.getLevel()+1)*(plot.getLevel()+1))
                                .setRevenue(plot.getType().getCost(plot.getLevel(), race))
                                .addSegment(new WorkerTaskSegment()
                                        .setType("Plot Type")
                                        .setData(plot.getLevel()>0?plot.getType():PlotType.Air, plot.getLevel()>0?plot.getLevel()-1:0, plot.getLevel()>0?plot.getFront():Side.FRONT))));
    }
    private Button createDestroyButton(Plot plot){
        ResourceList revenue = new ResourceList();
        for(int i = 0; i<plot.getLevel()+1; i++){
            revenue.addAll(plot.getType().getCost(i, race));
        }
        revenue.multiply(0.1);
        return new Button()
                .setImage("/gui/buttons/"+race.getName()+"/destroy"+plot.getType().textureFolder+".png")
                .setText("Destroy")
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(new WorkerTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setCost(new ResourceList())
                                .setCash(500)
                                .setRevenue(revenue)
                                .addSegment(new WorkerTaskSegment()
                                        .setType("Plot Type")
                                        .setData(PlotType.Air, 0, Side.FRONT))));
    }
    private Button createAirportScheduleButton(Plot plot){
        return new Button()
                .setImage("/gui/buttons/"+race.getName()+"/airportSchedule.png")
                .setText("Airline", "Schedule")
                .setEvent(new ButtonEvent()
                        .setType("Airport")
                        .setPlot(plot));
    }
    private void onAirportClicked(Plot plot, ButtonSet set){
        onPlainOwnedPlotClicked(plot, set);
        set.add(createAirportScheduleButton(plot));
    }
    private void onWarehouseClicked(Plot plot, ButtonSet set){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void onBankClicked(Plot plot, ButtonSet set){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void onHouseClicked(Plot plot, ButtonSet set){
        onPlainOwnedPlotClicked(plot, set);
    }
}
