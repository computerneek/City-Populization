package CityPopulization.world;
import java.util.ArrayList;
public enum GameDifficulty{
    SLEEPING("Sleeping", 5),
    SLEEPY("Sleepy", 4),
    LAZY("Lazy", 3),
    VERY_EASY("Very Easy", 2),
    EASY("Easy", 1.5),
    NORMAL("Normal", 1),
    HARD("Hard", 0.8),
    VERY_HARD("Very Hard", 0.6),
    EXPERT("Expert", 0.4),
    INSANE("Insane", 0.2),
    NUTS_OF_STEEL("Nuts of Steel", 0.1);
    private String name;
    public final double incomeModifier;//Modifier for income
    public final double moveSpeedModifier;//Modifier for things move speed
    public final double homeOccupantModifier;//Modifier for the number of people in one house
    public final double stabilityModifier;//Modifier for how often people like staying in their house
    public final int gameSpeedModifier;//Modifier for the game speed (compensates for move speed on harder levels)
    private GameDifficulty(String name, double modifier){
        this.name = name;
        this.incomeModifier = modifier;
        this.moveSpeedModifier = modifier;
        this.homeOccupantModifier = modifier;
        this.stabilityModifier = modifier;
        this.gameSpeedModifier = (int)Math.round(Math.max(1/modifier, 1));
    }
    public String getName(){
        return name;
    }
}
