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
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.civilian.CivilianTask;
import CityPopulization.world.civilian.Path;
import CityPopulization.world.civilian.WorkerTask;
import CityPopulization.world.civilian.WorkerTaskSegment;
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
    public void summonInitialWorkers(int workers){
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
        for(int j = 0; j<workers; j++){
            Civilian worker = new Civilian().upgradeToWorker();
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
            set.add(createHarvestAllButton(name, resource, plot));
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
    private Button createHarvestAllButton(String image, Resource revenue, Plot plot){
        return new Button()
                .setHotkey(Keyboard.KEY_H)
                .setImage("/gui/buttons/harvestAll"+image+".png")
                .setText("Harvest","All")
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(new WorkerTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setCost(new ResourceList())
                                .setCash(100*plot.getLevel()+100)
                                .setRevenue(new ResourceList().addAll(plot.getType().resourceHarvested).multiply(world.difficulty.incomeModifier).multiply(plot.getLevel()+1))
                                .addSegment(new WorkerTaskSegment.PlotType(
                                        plot,
                                        PlotType.Air,
                                        0,
                                        Side.FRONT,
                                        null)).configure()));
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
                                .addSegment(new WorkerTaskSegment.PlotType(
                                        plot,
                                        plot.getLevel()>0?plot.getType():PlotType.Air,
                                        plot.getLevel()>0?plot.getLevel()-1:0,
                                        plot.getLevel()>0?plot.getFront():Side.FRONT,
                                        null)).configure()));
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
                                .addSegment(new WorkerTaskSegment.PlotType(plot, PlotType.Air, 0, Side.FRONT, null)).configure()));
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
                                .addSegment(new WorkerTaskSegment.PlotType(plot, type, 0, Side.FRONT, this)).configure()));
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
                                .addSegment(new WorkerTaskSegment.PlotType(plot, plot.getType(), plot.getLevel()+1, plot.getFront(), this)).configure()));
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
                                .addSegment(new WorkerTaskSegment.PlotType(
                                        plot,
                                        plot.getLevel()>0?plot.getType():PlotType.Air,
                                        plot.getLevel()>0?plot.getLevel()-1:0,
                                        plot.getLevel()>0?plot.getFront():Side.FRONT, plot.getLevel()>0?this:null)).configure()));
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
                                .addSegment(new WorkerTaskSegment.PlotType(plot, PlotType.Air, 0, Side.FRONT, null)).configure()));
    }
    private Button createAirportScheduleButton(Plot plot){
        return new Button()
                .setImage("/gui/buttons/"+race.getName()+"/airportSchedule.png")
                .setText("Airline", "Schedule")
                .setEvent(new ButtonEvent()
                        .setType("Airport")
                        .setPlot(plot));
    }
    private Button createSellResourceButton(Plot plot, Resource resource){
        return new Button()
                .setImage("/gui/buttons/"+race.getName()+"/resources/sell/"+resource.name()+".png")
                .setText("Sell", resource.name())
                .setEvent(new ButtonEvent()
                        .setType("Sell_"+resource.name())
                        .setPlot(plot));
    }
    private void onAirportClicked(Plot plot, ButtonSet set){
        onPlainOwnedPlotClicked(plot, set);
        set.add(createAirportScheduleButton(plot));
        if(plot.owner!=null){
            ResourceList resources = new ResourceList();
            for(Plot aplot : plot.owner.resourceStructures){
                resources.addAll(aplot.resources);
            }
            for(Resource resource : resources.listResources()){
                set.add(createSellResourceButton(plot, resource));
            }
        }
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
            set.add(createNewWorkersButton(plot, workshop));
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
                                .addSegment(new WorkerTaskSegment.ResourceCollection(new ResourceList(Resource.Tools, 2), workshop))
                                .addSegment(new WorkerTaskSegment.TrainWorker(workshop, 1))
                                .addSegment(new WorkerTaskSegment.ResourceReturns(new ResourceList(Resource.Tools, 1), workshop)).prepare()));
    }
    private Button createNewWorkersButton(Plot plot, Plot workshop){
        int workers = 0;
        if(plot.skyscraper!=null){
            for(Plot p : plot.skyscraper.getAllPlots()){
                workers+=p.civilians.size();
            }
        }else{
            workers = plot.civilians.size();
        }
        WorkerTask tsk;
        Button bttn = new Button()
                .setHotkey(Keyboard.KEY_T)
                .setImage("/gui/buttons/"+race.getName()+"/trainWorker"+(plot.getLevel()+1)+".png")
                .setText("Train All","Workers")
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(tsk = new CivilianTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setAltPlot(workshop)
                                .setCost(new ResourceList(Resource.Tools, workers))
                                .setCash(100)
                                .setRevenue(new ResourceList())
                                .addSegment(new WorkerTaskSegment.ResourceCollection(new ResourceList(Resource.Tools, 1+workers), workshop))
                                .addSegment(new WorkerTaskSegment.TrainWorker(workshop, workers))
                                .addSegment(new WorkerTaskSegment.ResourceReturns(new ResourceList(Resource.Tools, 1), workshop)).prepare()));
        return bttn;
    }
    private void onSkyscraperClicked(Plot plot, ButtonSet set){
        if(plot.task==null){
            Plot[] plots = plot.skyscraper.getAllPlots();
            if(plot.canUpgrade(race)){
                set.add(createUpgradeSkyscraperButton(plot));
            }
            set.add(createDestroySkyscraperButton(plot));
            Plot[] plots2 = plot.getSkyscraperPlots();
            if(plot.canAddSkyscraperFloor()){
                set.add(createAddSkyscraperFloorButton(plot));
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
            set.add(createNewWorkersButton(plot, workshop));
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
                                .addSegment(new WorkerTaskSegment.PlotType(plot, plot.getType(), plot.getLevel(), plot.front.right(), this)).configure()));
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
                                .addSegment(new WorkerTaskSegment.PlotType(plot, plot.getType(), plot.getLevel(), plot.front.left(), this)).configure()));
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
                                .addSegment(new WorkerTaskSegment.PlotType(plot, plot.getType(), plot.getLevel(), plot.front.reverse(), this)).configure()));
    }
    private Button createUpgradeSkyscraperButton(Plot plot){
        WorkerTask task;
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
                                .setCost(plot.getType().getCost(plot.getLevel()+1, race).multiply(plots.length).add(Resource.Tools, plots.length-1))
                                .setCash(100*(plot.getLevel()+1)*(plot.getLevel()+1)*plots.length)
                                .setRevenue(new ResourceList(Resource.Tools, plots.length-1))));
        WorkerTaskSegment.Concurrent procedure = new WorkerTaskSegment.Concurrent();
        for(Plot aplot : plots){
            WorkerTaskSegment.Sequential s = new WorkerTaskSegment.Sequential();
            s.addSegment(new WorkerTaskSegment.ResourceCollection(plot.getType().getCost(plot.getLevel()+1, race).add(Resource.Tools, 1), aplot));
            s.addSegment(new WorkerTaskSegment.PlotType(aplot, aplot.getType(), aplot.getLevel()+1, aplot.getFront(), this));
            s.addSegment(new WorkerTaskSegment.ResourceReturns(new ResourceList(Resource.Tools, 1), aplot));
            procedure.addSegment(s);
        }
        task.addSegment(procedure);
        task.prepare();
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
        Button button = new Button()
                .setHotkey(Keyboard.KEY_X)
                .setImage("/gui/buttons/"+race.getName()+"/destroy"+plot.getType().textureFolder+(plot.getLevel()+1)+".png")
                .setText("Destroy")
                .setEvent(new ButtonEvent()
                        .setType("Task")
                        .setTask(task = new WorkerTask()
                                .setOwner(this)
                                .setPlot(plot)
                                .setCost(new ResourceList(Resource.Tools, plots.length-1))
                                .setCash(500)
                                .setRevenue(revenue.add(Resource.Tools, plots.length-1))));
        WorkerTaskSegment.Concurrent procedure = new WorkerTaskSegment.Concurrent();
        task.addSegment(procedure);
        for(Plot p : plots){
            procedure.addSegment(new WorkerTaskSegment.ResourceCollection(new ResourceList(Resource.Tools, 1), p));
        }
        for(int i = plot.skyscraper.levels()-1; i>=0; i--){
            procedure = new WorkerTaskSegment.Concurrent();
            task.addSegment(procedure);
            for(int x = 0; x<plot.skyscraper.width; x++){
                for(int y = 0; y<plot.skyscraper.height; y++){
                    WorkerTaskSegment.Sequential s = new WorkerTaskSegment.Sequential();
                    procedure.addSegment(s);
                    Plot plot2 = plot.world.getPlot(x+plot.x, y+plot.y, i+plot.z);
                    for(int level = plot2.getLevel(); level>=0; level--){
                        s.addSegment(new WorkerTaskSegment.PlotType(plot2, level==0?PlotType.Air:plot2.getType(), level==0?0:level-1, level==0?Side.FRONT:plot2.getFront(), level==0?null:this));
                        ResourceList resources = new ResourceList().addAll(plot.getType().getCost(level, race));
                        if(level==0){
                            resources.add(Resource.Tools, 1);
                        }
                        s.addSegment(new WorkerTaskSegment.ResourceReturns(resources, plot2));
                    }
                }
            }
        }
        task.prepare();
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
        WorkerTaskSegment.Concurrent set = new WorkerTaskSegment.Concurrent();
        for(int j=0; j<plot.skyscraper.height; j++){
            for(int k=0; k<plot.skyscraper.width; k++){
                WorkerTaskSegment.Sequential s = new WorkerTaskSegment.Sequential();
                Plot plot2 = aplot.world.generateAndGetPlot(plot.x+k, plot.y+j, aplot.z);
                for(int i = 0; i<=plot.level; i++){
                    ResourceList lst = new ResourceList().addAll(plot.getType().getCost(i, race));
                    if(i==0){
                        lst.add(Resource.Tools, 1);
                    }
                    s.addSegment(new WorkerTaskSegment.ResourceCollection(lst, plot2));
                    s.addSegment(new WorkerTaskSegment.PlotType(plot2, plot.type.skyscraperFloorType, i, plot.front, this));
                    if(i==plot.level){
                        s.addSegment(new WorkerTaskSegment.ResourceReturns(new ResourceList(Resource.Tools, 1), aplot));
                    }
                }
                set.addSegment(s);
            }
        }
        task.addSegment(set);
        task.prepare();
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
        WorkerTaskSegment.Concurrent c = new WorkerTaskSegment.Concurrent();
        task.addSegment(c);
        for(int i = 0; i<plot.skyscraper.height; i++){
            WorkerTaskSegment.Sequential s = new WorkerTaskSegment.Sequential();
            WorkerTaskSegment.Concurrent c2 = null;
            c.addSegment(s);
            for(int j = 0; j<plot.skyscraper.levels(); j++){
                Plot p = plot.world.getPlot(plot.x+plot.skyscraper.width, plot.y+i, plot.z+j);
                //Build the floor
                s.addSegment(new WorkerTaskSegment.ResourceCollection(plot.getType().getCost(0, race), p));
                s.addSegment(new WorkerTaskSegment.PlotType(p, plot.getType(), 0, p.getFront(), this));
                if(plot.level>0&&j<plot.skyscraper.levels()){
                    c2 = new WorkerTaskSegment.Concurrent();
                    s.addSegment(c2);
                    s = new WorkerTaskSegment.Sequential();
                    c2.addSegment(s);
                }
                for(int k = 1; k<=plot.level; k++){
                    //Upgrade the floor to level
                    s.addSegment(new WorkerTaskSegment.ResourceCollection(plot.getType().getCost(k, race), p));
                    s.addSegment(new WorkerTaskSegment.PlotType(p, plot.getType(), k, p.getFront(), this));
                }
                if(plot.level>0&&j<plot.skyscraper.levels()){
                    //Prep the sequence for the next level
                    s = new WorkerTaskSegment.Sequential();
                    c2.addSegment(s);
                }
            }
        }
        task.prepare();
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
                                setType("SkyscraperL "+(plot.skyscraper.width+1)+" "+plot.skyscraper.height).
                                setPlot(plot).
                                setTask(new WorkerTask().setPlot(plot.world.getPlot(plot.x-1, plot.y, plot.z)))));
        WorkerTaskSegment.Concurrent c = new WorkerTaskSegment.Concurrent();
        task.addSegment(c);
        for(int i = 0; i<plot.skyscraper.height; i++){
            WorkerTaskSegment.Sequential s = new WorkerTaskSegment.Sequential();
            WorkerTaskSegment.Concurrent c2 = null;
            c.addSegment(s);
            for(int j = 0; j<plot.skyscraper.levels(); j++){
                Plot p = plot.world.getPlot(plot.x-1, plot.y+i, plot.z+j);
                //Build the floor
                s.addSegment(new WorkerTaskSegment.ResourceCollection(plot.getType().getCost(0, race), p));
                s.addSegment(new WorkerTaskSegment.PlotType(p, plot.getType(), 0, p.getFront(), this));
                if(plot.level>0&&j<plot.skyscraper.levels()){
                    c2 = new WorkerTaskSegment.Concurrent();
                    s.addSegment(c2);
                    s = new WorkerTaskSegment.Sequential();
                    c2.addSegment(s);
                }
                for(int k = 1; k<=plot.level; k++){
                    //Upgrade the floor to level
                    s.addSegment(new WorkerTaskSegment.ResourceCollection(plot.getType().getCost(k, race), p));
                    s.addSegment(new WorkerTaskSegment.PlotType(p, plot.getType(), k, p.getFront(), this));
                }
                if(plot.level>0&&j<plot.skyscraper.levels()){
                    //Prep the sequence for the next level
                    s = new WorkerTaskSegment.Sequential();
                    c2.addSegment(s);
                }
            }
        }
        task.prepare();
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
        WorkerTaskSegment.Concurrent c = new WorkerTaskSegment.Concurrent();
        task.addSegment(c);
        for(int i = 0; i<=plot.skyscraper.levels(); i++){
            if(i>0&&plot.level>0){
                for(int j = 0; j<plot.skyscraper.width; j++){
                    WorkerTaskSegment.Sequential s = new WorkerTaskSegment.Sequential();
                    c.addSegment(s);
                    Plot p = plot.world.getPlot(plot.x+j, plot.y+plot.skyscraper.height, plot.z+i-1);
                    for(int k = 1; k<=plot.level; k++){
                        s.addSegment(new WorkerTaskSegment.ResourceCollection(plot.getType().getCost(k, race), p));
                        s.addSegment(new WorkerTaskSegment.PlotType(p, p.getType(), k, p.getFront(), this));
                    }
                    s.addSegment(new WorkerTaskSegment.ResourceReturns(new ResourceList(Resource.Tools, 1), p));
                }
            }
            if(i<plot.skyscraper.levels()){
                WorkerTaskSegment.Sequential s = new WorkerTaskSegment.Sequential();
                c.addSegment(s);
                WorkerTaskSegment.Concurrent c2 = new WorkerTaskSegment.Concurrent();
                s.addSegment(c2);
                for(int j = 0; j<plot.skyscraper.width; j++){
                    WorkerTaskSegment.Sequential s2 = new WorkerTaskSegment.Sequential();
                    c2.addSegment(s2);
                    Plot p = plot.world.getPlot(plot.x+j, plot.y+plot.skyscraper.height, plot.z+i);
                    s2.addSegment(new WorkerTaskSegment.ResourceCollection(new ResourceList().addAll(plot.getType().getCost(0, race)).add(Resource.Tools, 1), p));
                    s2.addSegment(new WorkerTaskSegment.PlotType(p, i==0?plot.type:plot.type.skyscraperFloorType, 0, plot.getFront(), this));
                }
                c = new WorkerTaskSegment.Concurrent();
                s.addSegment(c);
            }
        }
        task.prepare();
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
        WorkerTaskSegment.Concurrent c = new WorkerTaskSegment.Concurrent();
        task.addSegment(c);
        for(int i = 0; i<=plot.skyscraper.levels(); i++){
            if(i>0&&plot.level>0){
                for(int j = 0; j<plot.skyscraper.width; j++){
                    WorkerTaskSegment.Sequential s = new WorkerTaskSegment.Sequential();
                    c.addSegment(s);
                    Plot p = plot.world.getPlot(plot.x+j, plot.y-1, plot.z+i-1);
                    for(int k = 1; k<=plot.level; k++){
                        s.addSegment(new WorkerTaskSegment.ResourceCollection(plot.getType().getCost(k, race), p));
                        s.addSegment(new WorkerTaskSegment.PlotType(p, p.getType(), k, p.getFront(), this));
                    }
                    s.addSegment(new WorkerTaskSegment.ResourceReturns(new ResourceList(Resource.Tools, 1), p));
                }
            }
            if(i<plot.skyscraper.levels()){
                WorkerTaskSegment.Sequential s = new WorkerTaskSegment.Sequential();
                c.addSegment(s);
                WorkerTaskSegment.Concurrent c2 = new WorkerTaskSegment.Concurrent();
                s.addSegment(c2);
                for(int j = 0; j<plot.skyscraper.width; j++){
                    WorkerTaskSegment.Sequential s2 = new WorkerTaskSegment.Sequential();
                    c2.addSegment(s2);
                    Plot p = plot.world.getPlot(plot.x+j, plot.y-1, plot.z+i);
                    s2.addSegment(new WorkerTaskSegment.ResourceCollection(new ResourceList().addAll(plot.getType().getCost(0, race)).add(Resource.Tools, 1), p));
                    s2.addSegment(new WorkerTaskSegment.PlotType(p, i==0?plot.type:plot.type.skyscraperFloorType, 0, plot.getFront(), this));
                }
                c = new WorkerTaskSegment.Concurrent();
                s.addSegment(c);
            }
        }
        task.prepare();
        return button;
    }
}
