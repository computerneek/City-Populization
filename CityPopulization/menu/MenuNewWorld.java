package CityPopulization.menu;
import CityPopulization.Core;
import CityPopulization.world.GameDifficulty;
import CityPopulization.world.WinningCondition;
import CityPopulization.world.WorldData;
import CityPopulization.world.player.Race;
import CityPopulization.world.plot.Template;
import org.lwjgl.input.Keyboard;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentOptionButton;
import simplelibrary.opengl.gui.components.MenuComponentTextBox;
public class MenuNewWorld extends Menu{
    public WinningCondition theGoal=WinningCondition.get("None");
    private final MenuComponentTextBox name;
    private final MenuComponentOptionButton race;
    private final MenuComponentOptionButton template;
    private final MenuComponentButton goal;
    private final MenuComponentOptionButton gameSpeed;
    private final MenuComponentOptionButton gameMode;
    private final MenuComponentOptionButton gameDifficulty;
    private final MenuComponentButton create;
    private final MenuComponentButton cancel;
    private final Race[] races;
    private final String[] racesS;
    private final Template[] templates;
    private final String[] templatesS;
    private final GameDifficulty[] difficulties;
    private final String[] difficultiesS;
    public MenuNewWorld(GUI gui, Menu parent){
        super(gui, parent);
        races = Race.values();
        racesS = new String[races.length];
        for(int i = 0; i<races.length; i++){
            racesS[i]=races[i].getName();
        }
        templates = Template.values();
        templatesS = new String[templates.length];
        for(int i = 0; i<templates.length; i++){
            templatesS[i] = templates[i].getName();
        }
        difficulties = GameDifficulty.values();
        difficultiesS = new String[difficulties.length];
        for(int i = 0; i<difficulties.length; i++){
            difficultiesS[i] = difficulties[i].getName();
        }
        name = add(new MenuComponentTextBox(-0.8, -0.78, 1.6, 0.16, "New World", true));
        race = add(new MenuComponentOptionButton(-0.8, -0.58, 1.6, 0.16, "Race", true, 0, racesS));
        template = add(new MenuComponentOptionButton(-0.8, -0.38, 1.6, 0.16, "Template", true, 0, templatesS));
        goal = add(new MenuComponentButton(-0.8, -0.18, 1.6, 0.16, "Goal", true));
        gameSpeed = add(new MenuComponentOptionButton(-0.8, 0.02, 1.6, 0.16, "Game speed", true, 0, "1x", "2x", "4x", "8x", "16x", "32x", "64x"));
        gameMode = add(new MenuComponentOptionButton(-0.8, 0.22, 1.6, 0.16, "Mode", true, 0, "Standard", "Sandbox"));
        gameDifficulty = add(new MenuComponentOptionButton(-0.8, 0.42, 1.6, 0.16, "Difficulty", true, 5, difficultiesS));
        create = add(new MenuComponentButton(-1.58, 0.62, 1.56, 0.16, "Create", false));
        cancel = add(new MenuComponentButton(0.02, 0.62, 1.56, 0.16, "Cancel", true));
    }
    @Override
    public void renderBackground(){
        drawText(-0.8, -0.88, 0.8, -0.8, "World Name:");
        create.enabled = !name.text.isEmpty();
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==goal){
            goal();
        }else if(button==create){
            create();
        }else if(button==cancel){
            cancel();
        }
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(key==Keyboard.KEY_F11&&pressed&&!repeat){
            Core.helper.setFullscreen(!Core.helper.isFullscreen());
        }
        super.keyboardEvent(character, key, pressed, repeat);
    }
    private void goal(){
        gui.open(new MenuNewWorldGoal(gui, this));
    }
    private void create(){
        WorldData data = new WorldData();
        data.name = name.text;
        data.race = races[race.getIndex()];
        data.template = templates[template.getIndex()];
        data.goal = theGoal;
        data.sandbox = gameMode.getIndex()==1;
        data.gameSpeed = (int)Math.pow(2, gameSpeed.getIndex());
        data.difficulty = difficulties[gameDifficulty.getIndex()];
        Core.createEmpireWorld(data);
    }
    private void cancel(){
        gui.open(parent);
    }
}
