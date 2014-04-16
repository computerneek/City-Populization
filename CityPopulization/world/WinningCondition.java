package CityPopulization.world;
import java.util.ArrayList;
import simplelibrary.config2.Config;
public abstract class WinningCondition{
    private static ArrayList<WinningCondition> types = new ArrayList<>();
    private static ArrayList<String> names = new ArrayList<>();
    private String name;
    public WinningCondition(){}
    public WinningCondition(String name){
        if(names.contains(name)){
            System.err.println("Multiple winning conditions under the same name- "+name);
        }
        names.add(name);
        types.add(this);
        this.name = name;
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
    public Config save(){
        Config config = Config.newConfig();
        config.set("name", name);
        return config;
    }
    private static class NoWinningCondition extends WinningCondition{
        public NoWinningCondition(String name){
            super(name);
        }
    }
}
