package CityPopulization.world.player;
import CityPopulization.Core;
import CityPopulization.menu.MenuIngame;
import CityPopulization.menu.buttons.Button;
import CityPopulization.menu.buttons.ButtonEvent;
import CityPopulization.menu.buttons.ButtonSet;
import CityPopulization.packets.PacketCash;
import CityPopulization.render.Side;
import CityPopulization.world.World;
import CityPopulization.world.aircraft.Template;
import CityPopulization.world.aircraft.schedule.ScheduleElement;
import CityPopulization.world.civilian.CivilianTask;
import CityPopulization.world.civilian.Path;
import CityPopulization.world.civilian.Worker;
import CityPopulization.world.civilian.WorkerTask;
import CityPopulization.world.civilian.WorkerTaskSegment;
import CityPopulization.world.civilian.WorkerTaskSegmentSet;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import org.lwjgl.input.Keyboard;
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
        world.generateAndGetPlot(-1+offsetX, 0+offsetY, 0).setType(PlotType.AirportTerminal).setOwner(this).setFront(Side.BACK);
        world.generateAndGetPlot(-1+offsetX, -1+offsetY, 0).setType(PlotType.AirportJetway).setOwner(this);
        world.generateAndGetPlot(0+offsetX, -1+offsetY, 0).setType(PlotType.AirportRunway).setOwner(this).setFront(Side.LEFT);
        world.generateAndGetPlot(0+offsetX, 0+offsetY, 0).setType(PlotType.AirportEntrance).setOwner(this).terminal.fuel=100;
        world.generateAndGetPlot(1+offsetX, -1+offsetY, 0).setType(PlotType.Air);
        if(!sandbox){
            world.generateAndGetPlot(0+offsetX, 1+offsetY, 0).setType(PlotType.Road).setOwner(this);
            world.generateAndGetPlot(1+offsetX, 1+offsetY, 0).setType(PlotType.Road).setOwner(this);
            world.generateAndGetPlot(1+offsetX, 0+offsetY, 0).setType(PlotType.Warehouse).setLevel(1000/getResourcesPerWarehouse()+(1000%getResourcesPerWarehouse()>0?0:-1)).setOwner(this).resources.addAll(new ResourceList(
                Resource.Tools, 200,
                Resource.Dirt, 250,
                Resource.Wood, 250,
                Resource.Iron, 250,
                Resource.Coal, 50
            ));
            world.getPlot(0+offsetX, 0+offsetY, 0).terminal.schedule.elements.add(new ScheduleElement(Template.HELICOPTER_PASSENGER, 1, 1, new ResourceList(Resource.Fuel, 10), 1200, 0));
        }
        for(int j = 0; j<1; j++){
            Worker worker = new Worker();
            worker.homePlot = world.getPlot(0+offsetX, 0+offsetY, 0);
            worker.player = this;
            worker.homePlot.workers.add(worker);
            worker.homePlot.workersPresent.add(worker);
        }
        cash = 5000;
        if(client!=null){
            sendPlot(0, 0, 0);
            client.client.send(new PacketCash(cash));
        }
    }
    @Override
    public void onPlotClicked(int plotX, int plotY, MenuIngame menu, int button){
        Plot plot = world.getPlot(plotX, plotY, cameraZ);
        if(plot==null||!plot.playerVisibilities.contains(this)){
            plot = null;
        }
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
        if(plot==null||!plot.playerVisibilities.contains(this)){
            return;
        }
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
            case Debris:
                plot.setOwner(this);
                onPlainOwnedPlotClicked(plot, set);
                break;
        }
    }
    private void onOwnedPlotClicked(Plot plot, ButtonSet set){
        if(plot.skyscraper!=null){
            onSkyscraperClicked(plot.skyscraper.basePlot, set);
            return;
        }
        switch(plot.getType()){
            case AirportJetway:
            case Road:
            case Workshop:
            case Store:
            case Elevator:
                onPlainOwnedPlotClicked(plot, set);
                break;
            case AirportRunway:
            case AirportTerminal:
                onRotatablePlotClicked(plot, set);
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
            case Air:
                onOwnerlessPlotClicked(plot, set);
                break;
            case Support:
                onPlainOwnedPlotClicked(plot, set);
                break;
            case Debris:
                onPlainOwnedPlotClicked(plot, set);
                break;
            default:
                System.err.println("Unrecognized plot type "+plot.getType().name()+"!  (PlayerHuman)");
        }
    }
    private void onResourceMineClicked(Plot plot, ButtonSet set, String name, Resource resource){
        if(plot.task==null){
            set.add(createHarvestResourceButton(name, resource, plot));
            set.add(createDestroyResourceButton(name, resource, plot));
        }else if(!plot.task.started){
            set.add(createCancelTaskButton(plot));
        }
    }
    private Button createCancelTaskButton(Plot plot){
        return new Button()
                .setHotkey(Keyboard.KEY_C)
                .setImage("/gui/buttons/cancel.png")
                .setText("Cancel")
                .setEvent(new ButtonEvent()
                        .setType("Cancel Task")
                        .setPlot(plot));
    }
    private Button createHarvestResourceButton(String image, Resource revenue, Plot plot){
        return new Button()
                .setHotkey(Keyboard.KEY_H)
                .setImage("/gui/buttons/harvest"+image+".png")
                .setText("Harvest",revenue.name())
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(new WorkerTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setCost(new ResourceList())
                                .setCash(100)
                                .setRevenue(new ResourceList().addAll(plot.getType().resourceHarvested).multiply(world.difficulty.incomeModifier))
                                .addSegment(new WorkerTaskSegment()
                                        .setType("Plot Type")
                                        .setData(plot.getLevel()>0?plot.getType():PlotType.Air, plot.getLevel()>0?plot.getLevel()-1:0, plot.getLevel()>0?plot.getFront():Side.FRONT))));
    }
    private Button createDestroyResourceButton(String image, Resource revenue, Plot plot){
        return new Button()
                .setHotkey(Keyboard.KEY_D)
                .setImage("/gui/buttons/destroy"+image+".png")
                .setText("Destroy")
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(new WorkerTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setCost(new ResourceList())
                                .setCash(100)
                                .setRevenue(new ResourceList().addAll(plot.getType().resourceHarvested).multiply(plot.getLevel()+1).multiply(0.1*world.difficulty.incomeModifier))
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
                .setHotkey(type.getHotkey())
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
    private void onRotatablePlotClicked(Plot plot, ButtonSet set){
        onPlainOwnedPlotClicked(plot, set);
        if(plot.task==null){
            set.add(createRotateRightButton(plot));
            set.add(createRotateLeftButton(plot));
            set.add(createRotateAroundButton(plot));
        }
    }
    private Button createUpgradeButton(Plot plot){
        return new Button()
                .setHotkey(Keyboard.KEY_U)
                .setImage("/gui/buttons/"+race.getName()+"/upgrade"+plot.getType().textureFolder+(plot.getLevel()+1)+".png")
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
                .setHotkey(Keyboard.KEY_D)
                .setImage("/gui/buttions/"+race.getName()+"/downgrade"+plot.getType().textureFolder+(plot.getLevel()+1)+".png")
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
                .setHotkey(Keyboard.KEY_X)
                .setImage("/gui/buttons/"+race.getName()+"/destroy"+plot.getType().textureFolder+(plot.getLevel()+1)+".png")
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
        onPlainOwnedPlotClicked(plot, set);
    }
    private void onBankClicked(Plot plot, ButtonSet set){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void onHouseClicked(Plot plot, ButtonSet set){
        onRotatablePlotClicked(plot, set);
        Plot workshop = Path.findWorkshop(plot, false);
        if(!plot.civilians.isEmpty()&&plot.task==null&&workshop!=null){
            set.add(createNewWorkerButton(plot, workshop));
        }
    }
    private Button createNewWorkerButton(Plot plot, Plot workshop){
        return new Button()
                .setHotkey(Keyboard.KEY_T)
                .setImage("/gui/buttons/"+race.getName()+"/trainWorker"+(plot.getLevel()+1)+".png")
                .setText("Train","Worker")
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(new CivilianTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setAltPlot(workshop)
                                .setCost(new ResourceList(Resource.Tools, 1))
                                .setCash(100)
                                .setRevenue(new ResourceList())
                                .addSegment(new WorkerTaskSegment()
                                        .setType("Train Worker"))));
    }
    private void onSkyscraperClicked(Plot plot, ButtonSet set){
        if(plot.task==null){
            Plot[] plots = plot.skyscraper.getAllPlots();
            if(plot.canUpgrade(race)){
                set.add(createUpgradeSkyscraperButton(plot));
            }
            set.add(createDowngradeSkyscraperButton(plot));
            set.add(createDestroySkyscraperButton(plot));
            Plot[] plots2 = plot.getSkyscraperPlots();
            if(plot.canAddSkyscraperFloor()){
                set.add(createAddSkyscraperFloorButton(plot));
            }
            if(plots2.length>1){
                set.add(createRemoveSkyscraperFloorButton(plot));
            }
            if(plot.skyscraper.canExpandRight()){
                set.add(createExpandSkyscraperRightButton(plot));
            }
            if(plot.skyscraper.canExpandLeft()){
                set.add(createExpandSkyscraperLeftButton(plot));
            }
            if(plot.skyscraper.canExpandUp()){
                set.add(createExpandSkyscraperUpButton(plot));
            }
            if(plot.skyscraper.canExpandDown()){
                set.add(createExpandSkyscraperDownButton(plot));
            }
            if(plot.skyscraper.width>1&&plot.getSkyscraperPlots().length<=plot.skyscraper.width*plot.type.getSkyscraperHeight()){
                set.add(createShrinkSkyscraperRightButton(plot));
                set.add(createShrinkSkyscraperLeftButton(plot));
            }
            if(plot.skyscraper.height>1&&plot.getSkyscraperPlots().length>=plot.skyscraper.height*plot.type.getSkyscraperHeight()){
                set.add(createShrinkSkyscraperUpButton(plot));
                set.add(createShrinkSkyscraperDownButton(plot));
            }
        }else if(!plot.task.started){
            set.add(createCancelTaskButton(plot));
        }
        Plot workshop = Path.findWorkshop(plot, false);
        int civilians = 0;
        for(Plot aplot : plot.skyscraper.getAllPlots()){
            civilians+=aplot.civilians.size();
        }
        if(civilians>0&&plot.task==null&&workshop!=null){
            set.add(createNewWorkerButton(plot, workshop));
        }
    }
    private Button createRotateRightButton(Plot plot){
        return new Button()
                .setHotkey(Keyboard.KEY_R)
                .setImage("/gui/buttons/"+race.getName()+"/rotateRight.png")
                .setText("Turn","Right")
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(new WorkerTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setCost(new ResourceList())
                                .setCash(100*(plot.getLevel()+1))
                                .setRevenue(new ResourceList())
                                .addSegment(new WorkerTaskSegment()
                                        .setType("Plot Type")
                                        .setData(plot.getType(), plot.getLevel(), plot.front.right()))));
    }
    private Button createRotateLeftButton(Plot plot){
        return new Button()
                .setHotkey(Keyboard.KEY_L)
                .setImage("/gui/buttons/"+race.getName()+"/rotateLeft.png")
                .setText("Turn","Left")
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(new WorkerTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setCost(new ResourceList())
                                .setCash(100*(plot.getLevel()+1))
                                .setRevenue(new ResourceList())
                                .addSegment(new WorkerTaskSegment()
                                        .setType("Plot Type")
                                        .setData(plot.getType(), plot.getLevel(), plot.front.left()))));
    }
    private Button createRotateAroundButton(Plot plot){
        return new Button()
                .setHotkey(Keyboard.KEY_A)
                .setImage("/gui/buttons/"+race.getName()+"/rotateBack.png")
                .setText("Turn","Around")
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(new WorkerTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setCost(new ResourceList())
                                .setCash(100*(plot.getLevel()+1))
                                .setRevenue(new ResourceList())
                                .addSegment(new WorkerTaskSegment()
                                        .setType("Plot Type")
                                        .setData(plot.getType(), plot.getLevel(), plot.front.reverse()))));
    }
    private Button createUpgradeSkyscraperButton(Plot plot){
        WorkerTask task;
        WorkerTaskSegmentSet aset;
        Plot[] plots = plot.skyscraper.getAllPlots();
        Button button = new Button()
                .setHotkey(Keyboard.KEY_U)
                .setImage("/gui/buttons/"+race.getName()+"/upgrade"+plot.getType().textureFolder+(plot.getLevel()+1)+".png")
                .setText("Upgrade")
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(task = new WorkerTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setCost(plot.getType().getCost(plot.getLevel()+1, race).multiply(plots.length))
                                .setCash(100*(plot.getLevel()+1)*(plot.getLevel()+1)*plots.length)
                                .setRevenue(new ResourceList())));
        task.addSegment(aset = new WorkerTaskSegmentSet());
        for(Plot aplot : plots){
            aset.add(new WorkerTaskSegment()
                    .setType("Plot Type")
                    .setPlot(aplot)
                    .setData(aplot.getType(), aplot.getLevel()+1, aplot.getFront()));
        }
        return button;
    }
    private Button createDowngradeSkyscraperButton(Plot plot){
        WorkerTask task;
        Plot[] plots = plot.skyscraper.getAllPlots();
        Button button = new Button()
                .setHotkey(Keyboard.KEY_D)
                .setImage("/gui/buttions/"+race.getName()+"/downgrade"+plot.getType().textureFolder+(plot.getLevel()+1)+".png")
                .setText("Downgrade")
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(task = new WorkerTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setCost(new ResourceList())
                                .setCash(100*(plot.getLevel()+1)*(plot.getLevel()+1)*plots.length)
                                .setRevenue(plot.getType().getCost(plot.getLevel(), race).multiply(plots.length))));
        for(int i = plots.length-1; i>=0; i--){
            Plot aplot = plots[i];
            task.addSegment(new WorkerTaskSegment()
                    .setType("Plot Type")
                    .setPlot(aplot)
                    .setData(aplot.getLevel()>0?aplot.getType():PlotType.Air, aplot.getLevel()>0?aplot.getLevel()-1:0, aplot.getLevel()>0?aplot.getFront():Side.FRONT));
        }
        return button;
    }
    private Button createDestroySkyscraperButton(Plot plot){
        Plot[] plots = plot.skyscraper.getAllPlots();
        WorkerTask task;
        ResourceList revenue = new ResourceList();
        for(int i = 0; i<plot.getLevel()+1; i++){
            revenue.addAll(plot.getType().getCost(i, race));
        }
        revenue.multiply(plots.length);
        Plot[] plots2 = plot.getSkyscraperPlots();
        WorkerTaskSegmentSet aset;
        Button button = new Button()
                .setHotkey(Keyboard.KEY_X)
                .setImage("/gui/buttons/"+race.getName()+"/destroy"+plot.getType().textureFolder+(plot.getLevel()+1)+".png")
                .setText("Destroy")
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(task = new WorkerTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setCost(new ResourceList())
                                .setCash(500)
                                .setRevenue(revenue)));
        for(int i = plots2.length-1; i>=0; i--){
            Plot aplot = plots2[i];
            for(int j = 0; j<aplot.level+1; j++){
                task.addSegment(aset = new WorkerTaskSegmentSet());
                for(int k=0; k<plot.skyscraper.height; k++){
                    for(int l=0; l<plot.skyscraper.width; l++){
                        Plot aplot2 = plot.world.getPlot(plot.x+l, plot.y+k, aplot.z);
                        aset.add(new WorkerTaskSegment()
                                .setType("Plot Type")
                                .setPlot(aplot2)
                                .setData(j<plot.level?aplot2.getType():PlotType.Air, j<plot.level?plot.level-j-1:0, j<plot.level?plot.getFront():Side.FRONT));
                    }
                }
            }
        }
        return button;
    }
    private Button createAddSkyscraperFloorButton(Plot plot){
        ResourceList cost = new ResourceList();
        int cash = 0;
        for(int i = 0; i<plot.level+1; i++){
            cost.addAll(plot.getType().getCost(i, race));
            cash+=100*(i+1)*(i+1);
        }
        WorkerTask task;
        cost.multiply(plot.skyscraper.height).multiply(plot.skyscraper.width);
        cash = cash*plot.skyscraper.height*plot.skyscraper.width;
        Plot[] plots = plot.getSkyscraperPlots();
        Button button = new Button().
                setHotkey(Keyboard.KEY_A).
                setImage("/gui/buttons/"+race.getName()+"/addFloor"+plot.getType().textureFolder+(plot.getLevel()+1)+".png").
                setText("Add Floor").setEvent(new ButtonEvent().
                        setType("Task").setTask(task=new WorkerTask().
                                setOwner(this).
                                setPlot(plot).
                                setCost(cost).setCash(cash).setRevenue(new ResourceList())));
        Plot aplot = Core.world.generateAndGetPlot(plot.x, plot.y, plots[plots.length-1].z+1);
        for(int i = 0; i<plot.level+1; i++){
            for(int j=0; j<plot.skyscraper.height; j++){
                for(int k=0; k<plot.skyscraper.width; k++){
                    Plot aplot2 = aplot.world.generateAndGetPlot(plot.x+k, plot.y+j, aplot.z);
                    task.addSegment(new WorkerTaskSegment().
                            setType("Plot Type").
                            setPlot(aplot2).
                            setData(plot.type.skyscraperFloorType, i, plot.front));
                }
            }
        }
        return button;
    }
    private Button createRemoveSkyscraperFloorButton(Plot plot){
        ResourceList cost = new ResourceList();
        int cash = 0;
        for(int i = 0; i<plot.level+1; i++){
            cost.addAll(plot.getType().getCost(i, race));
            cash+=100*(i+1)*(i+1);
        }
        cost.multiply(plot.skyscraper.width).multiply(plot.skyscraper.height);
        cash*=plot.skyscraper.width*plot.skyscraper.height;
        WorkerTask task;
        Plot[] plots = plot.getSkyscraperPlots();
        Button button = new Button().
                setHotkey(Keyboard.KEY_R).
                setImage("/gui/buttons/"+race.getName()+"/removeFloor"+plot.getType().textureFolder+(plot.getLevel()+1)+".png").
                setText("Remove Floor").setEvent(new ButtonEvent().
                        setType("Task").setTask(task=new WorkerTask().
                                setOwner(this).
                                setPlot(plot).
                                setCost(new ResourceList()).setCash(cash).setRevenue(cost)));
        for(int i = 0; i<plot.level+1; i++){
            for(int j=0; j<plot.skyscraper.height; j++){
                for(int k=0; k<plot.skyscraper.width; k++){
                    Plot aplot = plot.world.generateAndGetPlot(plot.x+k, plot.y+j, plots[plots.length-1].z);
                    task.addSegment(new WorkerTaskSegment().
                            setType("Plot Type").
                            setPlot(aplot).
                            setData(i==plot.level?plot.type.skyscraperFloorType:PlotType.Air, i<plot.level?plot.level-i-1:0, i<plot.level?plot.getFront():Side.FRONT));
                }
            }
        }
        return button;
    }
    private Button createExpandSkyscraperRightButton(Plot plot){
        ResourceList cost = new ResourceList();
        int cash = 0;
        for(int i = 0; i<plot.level+1; i++){
            cost.addAll(plot.getType().getCost(i, race));
            cash+=100*(i+1)*(i+1);
        }
        cost.multiply(plot.skyscraper.height).multiply(plot.skyscraper.levels());
        cash = cash*plot.skyscraper.height*plot.skyscraper.levels();
        WorkerTask task;
        Button button = new Button().
//                setHotkey(Keyboard.KEY_R).
                setImage("/gui/buttons/"+race.getName()+"/expandRight"+plot.getType().textureFolder+(plot.getLevel()+1)+".png").
                setText("Expand", "Right").
                setEvent(new ButtonEvent.Pair().
                        addEvent(new ButtonEvent().
                                setType("Task").
                                setTask(task = new WorkerTask().
                                        setOwner(this).
                                        setPlot(plot).
                                        setCost(cost).
                                        setCash(cash).
                                        setRevenue(new ResourceList()))).
                        addEvent(new ButtonEvent().
                                setType("Skyscraper "+(plot.skyscraper.width+1)+" "+plot.skyscraper.height).
                                setPlot(plot).
                                setTask(new WorkerTask().setPlot(plot))));
        for(int i = 0; i<plot.level+1; i++){
            WorkerTaskSegmentSet set;
            task.addSegment(set = new WorkerTaskSegmentSet());
            for(int k = 0; k<plot.skyscraper.levels(); k++){
                for(int j = 0; j<plot.skyscraper.height; j++){
                    Plot aplot = plot.world.generateAndGetPlot(plot.x+plot.skyscraper.width, plot.y+j, plot.z+k);
                    set.add(new WorkerTaskSegment().
                            setType("Plot Type").
                            setPlot(aplot).
                            setData(k==0?plot.type:plot.type.skyscraperFloorType, i, plot.front));
                }
                if(i==0){
                    task.addSegment(set = new WorkerTaskSegmentSet());
                }
            }
        }
        return button;
    }
    private Button createExpandSkyscraperLeftButton(Plot plot){
        ResourceList cost = new ResourceList();
        int cash = 0;
        for(int i = 0; i<plot.level+1; i++){
            cost.addAll(plot.getType().getCost(i, race));
            cash+=100*(i+1)*(i+1);
        }
        cost.multiply(plot.skyscraper.height).multiply(plot.skyscraper.levels());
        cash = cash*plot.skyscraper.height*plot.skyscraper.levels();
        WorkerTask task;
        Button button = new Button().
//                setHotkey(Keyboard.KEY_R).
                setImage("/gui/buttons/"+race.getName()+"/expandLeft"+plot.getType().textureFolder+(plot.getLevel()+1)+".png").
                setText("Expand", "Left").
                setEvent(new ButtonEvent.Pair().
                        addEvent(new ButtonEvent().
                                setType("Task").
                                setTask(task = new WorkerTask().
                                        setOwner(this).
                                        setPlot(plot).
                                        setCost(cost).
                                        setCash(cash).
                                        setRevenue(new ResourceList()))).
                        addEvent(new ButtonEvent().
                                setType("Skyscraper "+(plot.skyscraper.width+1)+" "+plot.skyscraper.height).
                                setPlot(plot).
                                setTask(new WorkerTask().setPlot(plot.world.getPlot(plot.x-1, plot.y, plot.z)))));
        for(int i = 0; i<plot.level+1; i++){
            WorkerTaskSegmentSet set;
            task.addSegment(set = new WorkerTaskSegmentSet());
            for(int k = 0; k<plot.skyscraper.levels(); k++){
                for(int j = 0; j<plot.skyscraper.height; j++){
                    Plot aplot = plot.world.generateAndGetPlot(plot.x-1, plot.y+j, plot.z+k);
                    set.add(new WorkerTaskSegment().
                            setType("Plot Type").
                            setPlot(aplot).
                            setData(k==0?plot.type:plot.type.skyscraperFloorType, i, plot.front));
                }
                if(i==0){
                    task.addSegment(set = new WorkerTaskSegmentSet());
                }
            }
        }
        return button;
    }
    private Button createExpandSkyscraperDownButton(Plot plot){
        ResourceList cost = new ResourceList();
        int cash = 0;
        for(int i = 0; i<plot.level+1; i++){
            cost.addAll(plot.getType().getCost(i, race));
            cash+=100*(i+1)*(i+1);
        }
        cost.multiply(plot.skyscraper.width).multiply(plot.skyscraper.levels());
        cash = cash*plot.skyscraper.width*plot.skyscraper.levels();
        WorkerTask task;
        Button button = new Button().
//                setHotkey(Keyboard.KEY_R).
                setImage("/gui/buttons/"+race.getName()+"/expandDown"+plot.getType().textureFolder+(plot.getLevel()+1)+".png").
                setText("Expand", "Down").
                setEvent(new ButtonEvent.Pair().
                        addEvent(new ButtonEvent().
                                setType("Task").
                                setTask(task = new WorkerTask().
                                        setOwner(this).
                                        setPlot(plot).
                                        setCost(cost).
                                        setCash(cash).
                                        setRevenue(new ResourceList()))).
                        addEvent(new ButtonEvent().
                                setType("Skyscraper "+plot.skyscraper.width+" "+(plot.skyscraper.height+1)).
                                setPlot(plot).
                                setTask(new WorkerTask().setPlot(plot))));
        for(int i = 0; i<plot.level+1; i++){
            WorkerTaskSegmentSet set;
            task.addSegment(set = new WorkerTaskSegmentSet());
            for(int k = 0; k<plot.skyscraper.levels(); k++){
                for(int j = 0; j<plot.skyscraper.width; j++){
                    Plot aplot = plot.world.generateAndGetPlot(plot.x+j, plot.y+plot.skyscraper.height, plot.z+k);
                    set.add(new WorkerTaskSegment().
                            setType("Plot Type").
                            setPlot(aplot).
                            setData(k==0?plot.type:plot.type.skyscraperFloorType, i, plot.front));
                }
                if(i==0){
                    task.addSegment(set = new WorkerTaskSegmentSet());
                }
            }
        }
        return button;
    }
    private Button createExpandSkyscraperUpButton(Plot plot){
        ResourceList cost = new ResourceList();
        int cash = 0;
        for(int i = 0; i<plot.level+1; i++){
            cost.addAll(plot.getType().getCost(i, race));
            cash+=100*(i+1)*(i+1);
        }
        cost.multiply(plot.skyscraper.width).multiply(plot.skyscraper.levels());
        cash = cash*plot.skyscraper.width*plot.skyscraper.levels();
        WorkerTask task;
        Button button = new Button().
//                setHotkey(Keyboard.KEY_R).
                setImage("/gui/buttons/"+race.getName()+"/expandUp"+plot.getType().textureFolder+(plot.getLevel()+1)+".png").
                setText("Expand", "Up").
                setEvent(new ButtonEvent.Pair().
                        addEvent(new ButtonEvent().
                                setType("Task").
                                setTask(task = new WorkerTask().
                                        setOwner(this).
                                        setPlot(plot).
                                        setCost(cost).
                                        setCash(cash).
                                        setRevenue(new ResourceList()))).
                        addEvent(new ButtonEvent().
                                setType("Skyscraper "+plot.skyscraper.width+" "+(plot.skyscraper.height+1)).
                                setPlot(plot).
                                setTask(new WorkerTask().setPlot(plot.world.generateAndGetPlot(plot.x, plot.y-1, plot.z)))));
        for(int i = 0; i<plot.level+1; i++){
            WorkerTaskSegmentSet set;
            task.addSegment(set = new WorkerTaskSegmentSet());
            for(int k = 0; k<plot.skyscraper.levels(); k++){
                for(int j = 0; j<plot.skyscraper.width; j++){
                    Plot aplot = plot.world.generateAndGetPlot(plot.x+j, plot.y-1, plot.z+k);
                    set.add(new WorkerTaskSegment().
                            setType("Plot Type").
                            setPlot(aplot).
                            setData(k==0?plot.type:plot.type.skyscraperFloorType, i, plot.front));
                }
                if(i==0){
                    task.addSegment(set = new WorkerTaskSegmentSet());
                }
            }
        }
        return button;
    }
    private Button createShrinkSkyscraperRightButton(Plot plot){
        ResourceList cost = new ResourceList();
        int cash = 0;
        for(int i = 0; i<plot.level+1; i++){
            cost.addAll(plot.getType().getCost(i, race));
            cash+=100*(i+1)*(i+1);
        }
        cost.multiply(plot.skyscraper.height).multiply(plot.skyscraper.levels());
        cash = cash*plot.skyscraper.height*plot.skyscraper.levels();
        WorkerTask task;
        Button button = new Button().
//                setHotkey(Keyboard.KEY_R).
                setImage("/gui/buttons/"+race.getName()+"/shrinkRight"+plot.getType().textureFolder+(plot.getLevel()+1)+".png").
                setText("Shrink", "Right").
                setEvent(new ButtonEvent.Pair().
                        addEvent(new ButtonEvent().
                                setType("Task").
                                setTask(task = new WorkerTask().
                                        setOwner(this).
                                        setPlot(plot).
                                        setCost(cost).
                                        setCash(cash).
                                        setRevenue(new ResourceList()))).
                        addEvent(new ButtonEvent().
                                setType("Skyscraper "+(plot.skyscraper.width-1)+" "+plot.skyscraper.height).
                                setTask(new WorkerTask().setPlot(plot))));
        for(int i = 0; i<plot.level+1; i++){
            WorkerTaskSegmentSet set;
            task.addSegment(set = new WorkerTaskSegmentSet());
            for(int j = 0; j<plot.skyscraper.height; j++){
                for(int k = 0; k<plot.skyscraper.levels(); k++){
                    Plot aplot = plot.world.generateAndGetPlot(plot.x+plot.skyscraper.width-1, plot.y+j, plot.z+plot.skyscraper.levels()-1-k);
                    set.add(new WorkerTaskSegment().
                            setType("Plot Type").
                            setPlot(aplot).
                            setData(i==plot.level?plot.type.skyscraperFloorType:PlotType.Air, i<plot.level?plot.level-i-1:0, i<plot.level?plot.getFront():Side.FRONT));
                }
            }
        }
        return button;
    }
    private Button createShrinkSkyscraperLeftButton(Plot plot){
        ResourceList cost = new ResourceList();
        int cash = 0;
        for(int i = 0; i<plot.level+1; i++){
            cost.addAll(plot.getType().getCost(i, race));
            cash+=100*(i+1)*(i+1);
        }
        cost.multiply(plot.skyscraper.height).multiply(plot.skyscraper.levels());
        cash = cash*plot.skyscraper.height*plot.skyscraper.levels();
        WorkerTask task;
        Button button = new Button().
//                setHotkey(Keyboard.KEY_R).
                setImage("/gui/buttons/"+race.getName()+"/shrinkLeft"+plot.getType().textureFolder+(plot.getLevel()+1)+".png").
                setText("Shrink", "Left").
                setEvent(new ButtonEvent.Pair().
                        addEvent(new ButtonEvent().
                                setType("Task").
                                setTask(task = new WorkerTask().
                                        setOwner(this).
                                        setPlot(plot).
                                        setCost(cost).
                                        setCash(cash).
                                        setRevenue(new ResourceList()))).
                        addEvent(new ButtonEvent().
                                setType("Skyscraper "+(plot.skyscraper.width-1)+" "+plot.skyscraper.height).
                                setTask(new WorkerTask().setPlot(plot.world.generateAndGetPlot(plot.x+1, plot.y, plot.z)))));
        int level = plot.level;
        int levels = plot.skyscraper.levels();
        for(int i = 0; i<level+1; i++){
            WorkerTaskSegmentSet set;
            task.addSegment(set = new WorkerTaskSegmentSet());
            for(int j = 0; j<plot.skyscraper.height; j++){
                for(int k = 0; k<levels; k++){
                    Plot aplot = plot.world.generateAndGetPlot(plot.x, plot.y+j, plot.z+levels-1-k);
                    set.add(new WorkerTaskSegment().
                            setType("Plot Type").
                            setPlot(aplot).
                            setData(i==level?plot.type.skyscraperFloorType:PlotType.Air, i<level?level-i-1:0, i<level?plot.getFront():Side.FRONT));
                }
            }
        }
        return button;
    }
    private Button createShrinkSkyscraperUpButton(Plot plot){
        ResourceList cost = new ResourceList();
        int cash = 0;
        for(int i = 0; i<plot.level+1; i++){
            cost.addAll(plot.getType().getCost(i, race));
            cash+=100*(i+1)*(i+1);
        }
        cost.multiply(plot.skyscraper.width).multiply(plot.skyscraper.levels());
        cash = cash*plot.skyscraper.width*plot.skyscraper.levels();
        WorkerTask task;
        Button button = new Button().
//                setHotkey(Keyboard.KEY_R).
                setImage("/gui/buttons/"+race.getName()+"/shrinkUp"+plot.getType().textureFolder+(plot.getLevel()+1)+".png").
                setText("Shrink", "Up").
                setEvent(new ButtonEvent.Pair().
                        addEvent(new ButtonEvent().
                                setType("Task").
                                setTask(task = new WorkerTask().
                                        setOwner(this).
                                        setPlot(plot).
                                        setCost(cost).
                                        setCash(cash).
                                        setRevenue(new ResourceList()))).
                        addEvent(new ButtonEvent().
                                setType("Skyscraper "+plot.skyscraper.width+" "+(plot.skyscraper.height-1)).
                                setTask(new WorkerTask().setPlot(plot.world.generateAndGetPlot(plot.x, plot.y+1, plot.z)))));
        int level = plot.level;
        int levels = plot.skyscraper.levels();
        for(int i = 0; i<level+1; i++){
            WorkerTaskSegmentSet set;
            task.addSegment(set = new WorkerTaskSegmentSet());
            for(int j = 0; j<plot.skyscraper.width; j++){
                for(int k = 0; k<levels; k++){
                    Plot aplot = plot.world.generateAndGetPlot(plot.x+j, plot.y, plot.z+levels-1-k);
                    set.add(new WorkerTaskSegment().
                            setType("Plot Type").
                            setPlot(aplot).
                            setData(i==level?plot.type.skyscraperFloorType:PlotType.Air, i<level?level-i-1:0, i<level?plot.getFront():Side.FRONT));
                }
            }
        }
        return button;
    }
    private Button createShrinkSkyscraperDownButton(Plot plot){
        ResourceList cost = new ResourceList();
        int cash = 0;
        for(int i = 0; i<plot.level+1; i++){
            cost.addAll(plot.getType().getCost(i, race));
            cash+=100*(i+1)*(i+1);
        }
        cost.multiply(plot.skyscraper.width).multiply(plot.skyscraper.levels());
        cash = cash*plot.skyscraper.width*plot.skyscraper.levels();
        WorkerTask task;
        Button button = new Button().
//                setHotkey(Keyboard.KEY_R).
                setImage("/gui/buttons/"+race.getName()+"/shrinkDown"+plot.getType().textureFolder+(plot.getLevel()+1)+".png").
                setText("Shrink", "Down").
                setEvent(new ButtonEvent.Pair().
                        addEvent(new ButtonEvent().
                                setType("Task").
                                setTask(task = new WorkerTask().
                                        setOwner(this).
                                        setPlot(plot).
                                        setCost(cost).
                                        setCash(cash).
                                        setRevenue(new ResourceList()))).
                        addEvent(new ButtonEvent().
                                setType("Skyscraper "+plot.skyscraper.width+" "+(plot.skyscraper.height-1)).
                                setTask(new WorkerTask().setPlot(plot))));
        int level = plot.level;
        int levels = plot.skyscraper.levels();
        for(int i = 0; i<level+1; i++){
            WorkerTaskSegmentSet set;
            task.addSegment(set = new WorkerTaskSegmentSet());
            for(int j = 0; j<plot.skyscraper.width; j++){
                for(int k = 0; k<levels; k++){
                    Plot aplot = plot.world.generateAndGetPlot(plot.x+j, plot.y+plot.skyscraper.height-1, plot.z+levels-1-k);
                    set.add(new WorkerTaskSegment().
                            setType("Plot Type").
                            setPlot(aplot).
                            setData(i==level?plot.type.skyscraperFloorType:PlotType.Air, i<level?level-i-1:0, i<level?plot.getFront():Side.FRONT));
                }
            }
        }
        return button;
    }
}
