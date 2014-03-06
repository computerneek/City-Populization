package CityPopulization.world.aircraft;
import CityPopulization.world.aircraft.cargo.AircraftCargo;
import CityPopulization.world.aircraft.passenger.AircraftPassenger;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import java.util.ArrayList;
import java.util.Iterator;
public class Terminal {
    public static final int IN = 1;
    public static final int OUT = 2;
    public static final int UNLOADING = 1;
    public static final int IDLE = 2;
    public static final int LOADING = 3;
    public int occupied;
    private Plot plot;
    public int occupiers;
    private Aircraft aircraft;
    private int timeLanded;
    public int state;
    public ArrayList<AircraftCargo> cargo = new ArrayList<>();
    public ArrayList<AircraftPassenger> passengers = new ArrayList<>();
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
    private void findRunways(ArrayList<Runway> runways){
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
        timeLanded++;
        if(state==UNLOADING){
            if(!aircraft.cargo.isEmpty()){
                entrance.cargo.add(aircraft.cargo.remove(0));
            }
            if(timeLanded%20==0&&!aircraft.passengers.isEmpty()){
                entrance.plot.addPassenger(aircraft.passengers.remove(0));
            }
        }
    }
}
