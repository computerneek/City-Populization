package city.populization.menu;
import city.populization.core.ClientSide;
import city.populization.core.Core;
import city.populization.world.Template;
import city.populization.world.WorldData;
import java.util.Random;
import org.lwjgl.input.Keyboard;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentOptionButton;
import simplelibrary.opengl.gui.components.MenuComponentSlider;
import simplelibrary.opengl.gui.components.MenuComponentTextBox;
public class MenuNewSingleplayerWorld extends Menu {
    private final ClientSide client;
//    public WinningCondition theGoal=WinningCondition.get("None");
//    private final GameMode[] gamemodes;
    private final String[] gamemodesS;
    private final MenuComponentTextBox name;
    private final MenuComponentTextBox seed;
    private final MenuComponentOptionButton race;
    private final MenuComponentOptionButton template;
    private final MenuComponentButton goal;
    private final MenuComponentOptionButton gameMode;
    private final MenuComponentOptionButton gameDifficulty;
    private final MenuComponentButton create;
    private final MenuComponentButton cancel;
//    private final Race[] races;
    private final String[] racesS;
    private final Template[] templates;
    private final String[] templatesS;
//    private final GameDifficulty[] difficulties;
    private final String[] difficultiesS;
    private final MenuComponentSlider startingWorkers;
    public MenuNewSingleplayerWorld(ClientSide client, Menu parent) {
        super(client.gui, parent);
        this.client = client;
//        races = Race.values();
//        racesS = new String[races.length];
//        for(int i = 0; i<races.length; i++){
//            racesS[i]=races[i].getName();
//        }
        racesS = new String[]{"(NYI)"};
        templates = Template.values();
        templatesS = new String[templates.length];
        for(int i = 0; i<templates.length; i++){
            templatesS[i] = templates[i].getName();
        }
//        difficulties = GameDifficulty.values();
//        difficultiesS = new String[difficulties.length];
//        for(int i = 0; i<difficulties.length; i++){
//            difficultiesS[i] = difficulties[i].getName();
//        }
        difficultiesS = new String[]{"(NYI)"};
//        gamemodes = GameMode.values();
//        gamemodesS = new String[GameMode.values().length];
//        for(int i = 0; i<gamemodes.length; i++){
//            gamemodesS[i] = gamemodes[i].name;
//        }
        gamemodesS = new String[]{"(NYI)"};
        name = add(new MenuComponentTextBox(-0.8, -0.88, 1.6, 0.16, "New World", true));
        seed = add(new MenuComponentTextBox(-0.8, -0.58, 1.6, 0.16, "", true));
        race = add(new MenuComponentOptionButton(-0.8, -0.38, 1.6, 0.16, "Race", false, 0, racesS));
        template = add(new MenuComponentOptionButton(-0.8, -0.18, 1.6, 0.16, "Template", false, 0, templatesS));
        goal = add(new MenuComponentButton(-0.8, 0.02, 1.6, 0.16, "Goal (NYI)", false));
        gameMode = add(new MenuComponentOptionButton(-0.8, 0.22, 1.6, 0.16, "Mode", false, 0, gamemodesS));
        gameDifficulty = add(new MenuComponentOptionButton(-0.8, 0.42, 1.6, 0.16, "Difficulty", false, /*5*/0, difficultiesS));
        startingWorkers = add(new MenuComponentSlider(-0.8, 0.62, 1.6, 0.16, 1, 10, 1, true));
        create = add(new MenuComponentButton(-1.58, 0.82, 1.56, 0.16, "Create", false));
        cancel = add(new MenuComponentButton(0.02, 0.82, 1.56, 0.16, "Cancel", true));
    }
    @Override
    public void renderBackground(){
        drawText(-0.8, -0.98, 0.8, -0.9, "World Name:");
        drawText(-0.8, -0.68, 0.8, -0.6, "World Seed (Blank for random):");
        create.enabled = !name.text.isEmpty()&&cancel.enabled;
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
        create.enabled = false;
        cancel.enabled = false;
        WorldData data = new WorldData();
        data.name = name.text;
        if(seed.text.isEmpty()){
            data.seed = new Random().nextLong();
        }else{
            try{
                data.seed = Long.parseLong(seed.text);
            }catch(NumberFormatException ex){
                data.seed = seed.text.hashCode();
            }
        }
//        data.race = races[race.getIndex()];
        data.template = templates[template.getIndex()];
//        data.goal = theGoal;
//        data.gamemode = gamemodes[gameMode.getIndex()];
//        data.difficulty = difficulties[gameDifficulty.getIndex()];
        data.workers = (int)Math.round(startingWorkers.getValue());
        client.onCreateSingleplayer(data);
    }
    private void cancel(){
        gui.open(parent);
    }
}
