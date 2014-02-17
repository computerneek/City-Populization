package CityPopulization.world;
import CityPopulization.world.WinningCondition;
import CityPopulization.world.plot.Template;
import CityPopulization.world.player.Race;
import CityPopulization.world.GameDifficulty;
public class WorldData{
    public Race race;
    public Template template;
    public WinningCondition goal;
    public int gameSpeed;
    public GameDifficulty difficulty;
    public String name;
    public boolean sandbox;
}
