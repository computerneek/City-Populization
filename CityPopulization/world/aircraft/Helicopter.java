package CityPopulization.world.aircraft;
import CityPopulization.world.aircraft.landingSequence.LSEAircraftRotation;
import CityPopulization.world.aircraft.landingSequence.LSEMovement;
import CityPopulization.world.aircraft.landingSequence.LSEPitch;
import CityPopulization.world.aircraft.landingSequence.LSEStartingPoint;
import CityPopulization.world.aircraft.landingSequence.LandingSequenceEvent;
import CityPopulization.world.player.Player;
import java.util.ArrayList;
public class Helicopter extends Aircraft{
    public static final int topSpeed = 5;
    public Helicopter(Player player, String textureFolder){
        super(player, textureFolder);
    }
    @Override
    public int getRequiredRunwayLength(){
        return 1;
    }
    @Override
    public ArrayList<LandingSequenceEvent> getLandingSequence(){
        ArrayList<LandingSequenceEvent> lst = new ArrayList<>();
        lst.add(new LSEStartingPoint(7, 4, topSpeed));
        lst.add(new LSEMovement(131, 1));
        lst.add(new LSEPitch(-90));
        lst.add(new LSEMovement(191, 1));
        lst.add(new LSEPitch(0));
        lst.add(new LSEMovement(30, 1));
        lst.add(new LSEMovement(20, 0));
        lst.add(new LSEStartingPoint(0, 0, 0));
        return lst;
    }
    @Override
    public ArrayList<LandingSequenceEvent> getTakeoffSequence(){
        ArrayList<LandingSequenceEvent> lst = new ArrayList<>();
        lst.add(new LSEStartingPoint(0, 0, -1));
        lst.add(new LSEAircraftRotation(180));
        lst.add(new LSEMovement(20, 1));
        lst.add(new LSEPitch(90));
        lst.add(new LSEMovement(191, 1));
        lst.add(new LSEPitch(0));
        lst.add(new LSEMovement(30, 1));
        lst.add(new LSEMovement(60, topSpeed));
        return lst;
    }
}
