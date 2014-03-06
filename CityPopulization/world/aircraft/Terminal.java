package CityPopulization.world.aircraft;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import java.util.ArrayList;
import java.util.Iterator;
public class Terminal {
    public static final int IN = 1;
    public static final int OUT = 2;
    public int occupied;
    private Plot plot;
    public int occupiers;
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
    void onArrival(Aircraft aThis){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
