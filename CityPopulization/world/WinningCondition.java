package CityPopulization.world;
import java.util.ArrayList;
public abstract class WinningCondition{
    private static ArrayList<WinningCondition> types = new ArrayList<>();
    private static ArrayList<String> names = new ArrayList<>();
    public WinningCondition(){}
    public WinningCondition(String name){
        if(names.contains(name)){
            System.err.println("Multiple winning conditions under the same name- "+name);
        }
        names.add(name);
        types.add(this);
    }
    public static String[] listTypes(){
        return names.toArray(new String[names.size()]);
    }
    public int getGoalType(){
        return types.indexOf(this);
    }
    public static WinningCondition getGoal(int type){
        return types.get(type);
    }
    public static WinningCondition get(String name){
        return types.get(names.indexOf(name));
    }
    static{
        new NoWinningCondition("None");
    }
    private static class NoWinningCondition extends WinningCondition{
        public NoWinningCondition(String name){
            super(name);
        }
    }
}
