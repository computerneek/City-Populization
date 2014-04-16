package CityPopulization.world.aircraft;
import CityPopulization.world.aircraft.cargo.AircraftCargo;
import CityPopulization.world.aircraft.cargo.AircraftCargoResource;
import CityPopulization.world.aircraft.passenger.AircraftPassenger;
import CityPopulization.world.aircraft.schedule.AircraftSchedule;
import CityPopulization.world.civilian.Worker;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.resource.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import simplelibrary.config2.Config;
public class Terminal {
    public static final int IN = 1;
    public static final int OUT = 2;
    public static final int UNLOADING = 1;
    public static final int IDLE = 2;
    public static final int LOADING = 3;
    public int occupied;
    public Plot plot;
    public int occupiers;
    private Aircraft aircraft;
    private int timeLanded;
    private int timeWaiting;
    public int state;
    public int fuel;
    private int tick;
    public AircraftSchedule schedule = new AircraftSchedule();
    public Terminal(Plot plot){
        this.plot = plot;
    }
    public void attemptToLandAircraft(Aircraft aircraft){
        ArrayList<Runway> runways = new ArrayList<>();
        findRunways(runways);
        for(Iterator<Runway> it=runways.iterator(); it.hasNext();){
            Runway runway=it.next();
            if(runway.isOccupied()||runway.size()<aircraft.getRequiredRunwayLength()){
                it.remove();
                continue;
            }
        }
        if(runways.isEmpty()){
            return;
        }
        Runway runway = runways.get(0);
        aircraft.setRunway(runway);
        aircraft.setTerminal(this);
        aircraft.land();
    }
    public void findRunways(ArrayList<Runway> runways){
        ArrayList<Plot> coveredPlots = new ArrayList<>();
        ArrayList<Plot> currentPlots = new ArrayList<>();
        currentPlots.add(plot.getFrontPlot());
        while(!currentPlots.isEmpty()){
            Plot plot = currentPlots.remove(0);
            if(coveredPlots.contains(plot)||plot.getOwner()!=this.plot.getOwner()){
                continue;
            }
            coveredPlots.add(plot);
            if(plot.getType()==PlotType.AirportJetway){
                currentPlots.add(plot.getFrontPlot());
                currentPlots.add(plot.getLeftPlot());
                currentPlots.add(plot.getRightPlot());
                currentPlots.add(plot.getBackPlot());
            }else if(plot.getType()==PlotType.AirportRunway&&plot.getFrontPlot().getType()==PlotType.AirportJetway&&coveredPlots.contains(plot.getFrontPlot())){
                runways.add(new Runway(plot));
            }
        }
    }
    public Plot getPlot(){
        return plot;
    }
    public void onArrival(Aircraft aircraft){
        this.aircraft = aircraft;
        timeLanded = 0;
        state = UNLOADING;
    }
    public void update(Terminal entrance){
        tick++;
        if(entrance.plot.resources.get(Resource.Fuel)>0&&tick%20==0&&fuel<500){
            fuel++;
            entrance.plot.resources.remove(Resource.Fuel, 1);
        }
        if(aircraft==null){
            return;
        }
        timeLanded++;
        timeWaiting++;
        if(aircraft.fuelLevel<aircraft.maxFuelLevel&&entrance.fuel>0){
            entrance.fuel--;
            aircraft.fuelLevel++;
        }
        boolean canLoad = false;
        if(state==UNLOADING){
            if(!aircraft.cargo.isEmpty()){
                AircraftCargoResource cargo;
                entrance.plot.resources.add((cargo = (AircraftCargoResource)aircraft.cargo.remove(0)).getResource(), 1);
                aircraft.cargoOccupied-=cargo.getSpaceOccupied();
            }
            if(timeLanded%20==0&&!aircraft.passengers.isEmpty()){
                entrance.plot.addPassenger(aircraft.passengers.remove(0));
            }
            if(aircraft.cargo.isEmpty()&&aircraft.passengers.isEmpty()){
                state = IDLE;
            }
        }else if(state==IDLE){
            if(timeLanded>=aircraft.departureTime){
                state = LOADING;
                timeWaiting = 0;
            }
        }else if(state==LOADING){
            if(entrance.plot.inboundResources.count()>0&&aircraft.cargoOccupied<aircraft.cargoCapacity){
                canLoad = true;
                if(aircraft.loadOneCargo(AircraftCargo.resource(entrance.plot.inboundResources.listResources().get(0), 1))){
                    entrance.plot.inboundResources.remove(entrance.plot.inboundResources.listResources().get(0), 1);
                    timeWaiting = 0;
                }
            }
            if(aircraft.passengers.size()<aircraft.passengerCapacity&&(!entrance.plot.workers.isEmpty()||!entrance.plot.civilians.isEmpty())){
                canLoad = true;
                if(timeLanded%20==0){
                    if(!entrance.plot.civiliansPresent.isEmpty()){
                        aircraft.loadPassengers(AircraftPassenger.civilians(1));
                        entrance.plot.civilians.remove(entrance.plot.civiliansPresent.remove(0));
                        timeWaiting = 0;
                    }else if(!entrance.plot.workersPresent.isEmpty()){
                        for(Worker worker : entrance.plot.workersPresent){
                            if(worker.timer<=0){
                                aircraft.loadPassengers(AircraftPassenger.workers(1));
                                entrance.plot.workers.remove(entrance.plot.workersPresent.remove(0));
                                timeWaiting = 0;
                                break;
                            }
                        }
                    }
                }
            }
            if((timeWaiting>=200||!canLoad)&&aircraft.fuelLevel==aircraft.maxFuelLevel){
                aircraft.depart();
                aircraft = null;
                timeLanded = 0;
                timeWaiting = 0;
                state = 0;
            }
        }
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("occupied", occupied);
        config.set("occupiers", occupiers);
        config.set("tick", tick);
        if(plot.getType()==PlotType.AirportEntrance||plot.getType()==PlotType.AirportTerminal){
            if(aircraft!=null){
                config.set("aircraft", aircraft.save());
            }
            config.set("landed", timeLanded);
            config.set("waiting", timeWaiting);
            config.set("state", state);
            config.set("fuel", fuel);
            config.set("schedule", schedule.save());
        }
        return config;
    }
}
