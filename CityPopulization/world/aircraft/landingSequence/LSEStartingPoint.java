package CityPopulization.world.aircraft.landingSequence;
import CityPopulization.render.Side;
import CityPopulization.world.aircraft.Aircraft;
import CityPopulization.world.aircraft.Runway;
import CityPopulization.world.plot.Plot;
public class LSEStartingPoint implements LandingSequenceEvent {
    private final int distanceBack;
    private final int height;
    private final int speed;
    public LSEStartingPoint(int distanceBack, int height, int speed){
        this.distanceBack=distanceBack;
        this.height=height;
        this.speed=speed;
    }
    @Override
    public boolean update(Aircraft aircraft){
        Runway runway = aircraft.runway;
        Plot touchdown = runway.getTouchdownPlot();
        Side side = touchdown.front.reverse();
        Plot plot = touchdown;
        for(int i = 0; i<distanceBack; i++){
            plot = side.getPlot(aircraft.player.world, plot.x, plot.y, plot.z);
        }
        for(int i = 0; i<height; i++){
            plot = Side.UP.getPlot(aircraft.player.world, plot.x, plot.y, plot.z);
        }
        plot = Side.DOWN.getPlot(aircraft.player.world, plot.x, plot.y, plot.z);
        aircraft.setLocation(plot);
        aircraft.setHeading(touchdown.front);
        aircraft.setSpeed(speed);
        return true;
    }
}
