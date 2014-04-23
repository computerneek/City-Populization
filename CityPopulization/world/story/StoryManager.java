package CityPopulization.world.story;
import java.util.ArrayList;
public class StoryManager{
    private static ArrayList<StoryMission> missions = new ArrayList<>();
    public static StoryMission getMission(int missionNumber){
        if(missionNumber>=missions.size()){
            return null;
        }
        if(missionNumber>0&&!missions.get(missionNumber-1).isComplete()){
            return null;
        }
        return missions.get(missionNumber);
    }
    static{
        missions.add(new Mission1Tutorial());
    }
}
