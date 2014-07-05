package CityPopulization.world.aircraft;
import CityPopulization.world.aircraft.landingSequence.LSEAircraftRotation;
import CityPopulization.world.aircraft.landingSequence.LSEMovement;
import CityPopulization.world.aircraft.landingSequence.LSEPitch;
import CityPopulization.world.aircraft.landingSequence.LSEStartingPoint;
import CityPopulization.world.aircraft.landingSequence.LandingSequenceEvent;
import CityPopulization.world.player.Player;
import java.util.ArrayList;
public class SmallPlane extends Aircraft{
    public static final int topSpeed = 5;
    public SmallPlane(Player player, String textureFolder){
        super(player, textureFolder);
    }
    @Override
    public int getRequiredRunwayLength(){
        return 3;
    }
    @Override
    public ArrayList<LandingSequenceEvent> getLandingSequence(int runwayLength){
        ArrayList<LandingSequenceEvent> lst = new ArrayList<>();
        lst.add(new LSEStartingPoint(27, 4, topSpeed));
        lst.add(new LSEMovement(47, 4));
        lst.add(new LSEPitch(-10));
        lst.add(new LSEMovement(375, 3));
        lst.add(new LSEPitch(0));
        lst.add(new LSEMovement(31+25*(runwayLength-getRequiredRunwayLength()), 2));
        lst.add(new LSEMovement(40, 0));
        lst.add(new LSEStartingPoint(-2-(runwayLength-getRequiredRunwayLength()), 0, 0));
        return lst;
    }
    @Override
    public ArrayList<LandingSequenceEvent> getTakeoffSequence(int runwayLength){
        ArrayList<LandingSequenceEvent> lst = new ArrayList<>();
        lst.add(new LSEStartingPoint(-2-(runwayLength-getRequiredRunwayLength()), 0, 0));
        lst.add(new LSEAircraftRotation(180));
        lst.add(new LSEMovement(40+25*(runwayLength-getRequiredRunwayLength()), 2));
        lst.add(new LSEPitch(10));
        lst.add(new LSEMovement(100, 3));
        lst.add(new LSEMovement(250, 4));
        lst.add(new LSEPitch(0));
        lst.add(new LSEMovement(40, topSpeed));
        return lst;
    }
}
