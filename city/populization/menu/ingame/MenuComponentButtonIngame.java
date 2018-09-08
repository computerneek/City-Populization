package city.populization.menu.ingame;
import city.populization.render.ResourceLocation;
import city.populization.render.ResourceLocation.Type;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public abstract class MenuComponentButtonIngame extends MenuComponentButton {
    public static final ResourceLocation background_pressed = ResourceLocation.get(Type.BUTTON_INGAME, "background/pressed.png");
    public static final ResourceLocation background_mouseover = ResourceLocation.get(Type.BUTTON_INGAME, "background/mouseover.png");
    public static final ResourceLocation background_plain = ResourceLocation.get(Type.BUTTON_INGAME, "background/plain.png");
    public static final ResourceLocation background_disabled = ResourceLocation.get(Type.BUTTON_INGAME, "background/disabled.png");
    public static final ResourceLocation back = ResourceLocation.get(Type.BUTTON_INGAME, "menuControl/back.png");
    public static final ResourceLocation lastPage = ResourceLocation.get(Type.BUTTON_INGAME, "menuControl/lastPage.png");
    public static final ResourceLocation nextPage = ResourceLocation.get(Type.BUTTON_INGAME, "menuControl/nextPage.png");
    public static final ResourceLocation claim = ResourceLocation.get(Type.BUTTON_INGAME, "structures/generic/claim.png");
    public static final ResourceLocation destroyStructure = ResourceLocation.get(Type.BUTTON_INGAME, "structures/generic/destroy.png");
    public static final ResourceLocation destroyDebris = ResourceLocation.get(Type.BUTTON_INGAME, "structures/debris/clear.png");
    public static final ResourceLocation harvestDebris = ResourceLocation.get(Type.BUTTON_INGAME, "structures/debris/scavenge.png");
    public static final ResourceLocation build = ResourceLocation.get(Type.BUTTON_INGAME, "structures/generic/build.png");
    public static final ResourceLocation upgrade = ResourceLocation.get(Type.BUTTON_INGAME, "structures/generic/upgrade.png");
    public static final ResourceLocation digDirt = ResourceLocation.get(Type.BUTTON_INGAME, "resources/digDirt.png");
    public static final ResourceLocation digSand = ResourceLocation.get(Type.BUTTON_INGAME, "resources/digSand.png");
    public static final ResourceLocation mineStone = ResourceLocation.get(Type.BUTTON_INGAME, "resources/mineStone.png");
    public static final ResourceLocation mineCoal = ResourceLocation.get(Type.BUTTON_INGAME, "resources/mineCoal.png");
    public static final ResourceLocation digClay = ResourceLocation.get(Type.BUTTON_INGAME, "resources/digClay.png");
    public static final ResourceLocation pumpOil = ResourceLocation.get(Type.BUTTON_INGAME, "resources/pumpOil.png");
    public static final ResourceLocation mineIron = ResourceLocation.get(Type.BUTTON_INGAME, "resources/mineIron.png");
    public static final ResourceLocation mineGold = ResourceLocation.get(Type.BUTTON_INGAME, "resources/mineGold.png");
    public static final ResourceLocation mineDiamond = ResourceLocation.get(Type.BUTTON_INGAME, "resources/mineDiamond.png");
    public static final ResourceLocation harvestResource = ResourceLocation.get(Type.BUTTON_INGAME, "resources/harvestGeneric.png");
    public static final ResourceLocation build_road = ResourceLocation.get(Type.BUTTON_INGAME, "structures/road/build.png");
    public static final ResourceLocation build_warehouse = ResourceLocation.get(Type.BUTTON_INGAME, "structures/warehouse/build.png");
    public static final ResourceLocation build_workshop = ResourceLocation.get(Type.BUTTON_INGAME, "structures/workshop/build.png");
    public static final ResourceLocation build_market = ResourceLocation.get(Type.BUTTON_INGAME, "structures/market/build.png");
    public static final ResourceLocation build_townHall = ResourceLocation.get(Type.BUTTON_INGAME, "structures/townHall/build.png");
    public static final ResourceLocation build_farm = ResourceLocation.get(Type.BUTTON_INGAME, "structures/farm/build.png");
    public static final ResourceLocation build_hut = ResourceLocation.get(Type.BUTTON_INGAME, "structures/hut/build.png");
    public static final ResourceLocation build_room = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/room/build.png");
    public static final ResourceLocation build_apartment = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/apartment/build.png");
    public static final ResourceLocation build_hall = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/hall/build.png");
    public static final ResourceLocation build_lobby = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/lobby/build.png");
    public static final ResourceLocation build_staircase = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/staircase/base/build.png");
    public static final ResourceLocation build_staircaseTop = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/staircase/top/build.png");
    public static final ResourceLocation build_elevator = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/elevator/base/build.png");
    public static final ResourceLocation build_elevatorTop = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/elevator/top/build.png");
    public static final ResourceLocation build_lattice = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/lattice/build.png");
    public static final ResourceLocation build_pillar = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/pillar/build.png");
    public static final ResourceLocation upgradeRoad = ResourceLocation.get(Type.BUTTON_INGAME, "structures/road/upgrade.png");
    public static final ResourceLocation upgradeWarehouse = ResourceLocation.get(Type.BUTTON_INGAME, "structures/warehouse/upgrade.png");
    public static final ResourceLocation upgradeWorkshop = ResourceLocation.get(Type.BUTTON_INGAME, "structures/workshop/upgrade.png");
    public static final ResourceLocation upgradeMarket = ResourceLocation.get(Type.BUTTON_INGAME, "structures/market/upgrade.png");
    public static final ResourceLocation upgradeTownHall = ResourceLocation.get(Type.BUTTON_INGAME, "structures/townHall/upgrade.png");
    public static final ResourceLocation upgradeFarm = ResourceLocation.get(Type.BUTTON_INGAME, "structures/farm/upgrade.png");
    public static final ResourceLocation upgradeRoom = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/room/upgrade.png");
    public static final ResourceLocation upgradeApartment = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/apartment/upgrade.png");
    public static final ResourceLocation upgradeHall = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/hall/upgrade.png");
    public static final ResourceLocation upgradeLobby = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/lobby/upgrade.png");
    public static final ResourceLocation upgradeStaircase = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/staircase/base/upgrade.png");
    public static final ResourceLocation upgradeStaircaseTop = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/staircase/top/upgrade.png");
    public static final ResourceLocation upgradeElevator = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/elevator/base/upgrade.png");
    public static final ResourceLocation upgradeElevatorTop = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/elevator/top/upgrade.png");
    public static final ResourceLocation upgradeLattice = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/lattice/upgrade.png");
    public static final ResourceLocation upgradePillar = ResourceLocation.get(Type.BUTTON_INGAME, "structures/mega/pillar/upgrade.png");
    public static final ResourceLocation upgradeStructure = ResourceLocation.get(Type.BUTTON_INGAME, "structures/generic/upgrade.png");
    //TODO add the rest of the textures needed for the vanilla buttons
    static{
        //TODO modding support?
        ResourceLocation.finalizeType(Type.BUTTON_INGAME);
    }
    private final ResourceLocation foreground;
    private final String[] label;
    public MenuComponentButtonIngame(ResourceLocation foreground, String... label){
        super(0, 0, 0, 0, "", true);
        this.foreground = foreground;
        this.label = label;
    }
    @Override
    public void render(){
        ResourceLocation tex;
        if(enabled){
            if(isPressed){
                tex = background_pressed;
            }else{
                if(isMouseOver){
                    tex = background_mouseover;
                }else{
                    tex = background_plain;
                }
            }
        }else{
            if(isMouseOver){
                tex = background_disabled;
            }else{
                tex = background_disabled;
            }
        }
        tex.bind();
        drawRect(x, y, x+width, y+height, -1, tex.x(0), tex.y(0), tex.x(1), tex.y(1));//any texture index <0 means no change, use what's already bound
        double textHeight = 0.04;
        foreground.bind();
        tex = foreground;
        drawRect(x+0.02+textHeight*label.length/2d, y+0.02,
                x+width-0.02-textHeight*label.length/2d, y+height-0.02-textHeight*label.length, -1, tex.x(0), tex.y(0), tex.x(1), tex.y(1));
        GL11.glColor3f(0, 0, 0);
        for(int i = 0; i<label.length; i++){
            String text = label[label.length-i-1];
            drawCenteredText(x+0.01, y+height-0.02-textHeight*(i+1), x+width-0.01, y+height-0.02-textHeight*i, text);
        } 
        GL11.glColor3f(1, 1, 1);
    }
    @Override
    public abstract void action();
    public String getInfo() {
        return "";
    }
}
