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
    private double incomeModifier;
    private double moveSpeedModifier;
    private double homeOccupantModifier;
    private double stabilityModifier;
    private GameDifficulty(String name, double modifier){
        this.name = name;
        this.incomeModifier = modifier;
        this.moveSpeedModifier = modifier;
        this.homeOccupantModifier = modifier;
        this.stabilityModifier = modifier;
    }
    public String getName(){
        return name;
    }
}
