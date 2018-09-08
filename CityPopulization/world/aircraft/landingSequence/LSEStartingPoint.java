package CityPopulization.world.aircraft.landingSequence;
import CityPopulization.render.Side;
import CityPopulization.world.aircraft.Aircraft;
import CityPopulization.world.aircraft.Runway;
import CityPopulization.world.plot.Plot;
import simplelibrary.config2.Config;
public class LSEStartingPoint extends LandingSequenceEvent {
    private int distanceBack;
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
        if(distanceBack<0){
            distanceBack*=-1;
            side = side.reverse();
        }
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
    @Override
    public Config save(){
        Config config = Config.newConfig();
        config.set("type", "startingPoint");
        config.set("dist", distanceBack);
        config.set("height", height);
        config.set("speed", speed);
        return config;
    }
}
