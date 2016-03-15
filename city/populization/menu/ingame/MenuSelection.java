package city.populization.menu.ingame;
import city.populization.core.ClientSide;
import city.populization.menu.MenuWorld;
import city.populization.world.Direction;
import city.populization.world.Player;
import city.populization.world.World;
import city.populization.world.plot.Plot;
import city.populization.world.plot.PlotOwner;
import city.populization.world.plot.PlotPos;
import city.populization.world.plot.type.PlotResource;
import org.lwjgl.opengl.Display;
import simplelibrary.Queue;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
public class MenuSelection extends Menu{
    private MenuComponentButtonIngame[] buttons;
    private MenuComponentButtonIngame next;
    private MenuComponentButtonIngame previous;
    private int scrollPos;
    private int lastWidth;
    private int lastHeight;
    public static MenuSelection onSelected(ClientSide client, Menu parent, World world, PlotPos selection, Player localPlayer) {
        if(parent==null) throw new NullPointerException("Cannot make a MenuSelection without a parent!");
        if(!(parent instanceof MenuWorld||parent instanceof MenuSelection)) throw new IllegalArgumentException("MenuSelection parent must be either MenuSelection or MenuWorld!");
        Plot p = world.getPlot(selection);
        int level = world.getLevel(selection);
        Direction heading = world.getHeading(selection);
        PlotOwner o = world.getOwner(selection);
        Queue<MenuComponentButtonIngame> buttons = new Queue<>();
        if(o==localPlayer){
            selectOwned(client, world, selection, localPlayer, p, level, heading, buttons);
        }else if(o==null){
            selectUnowned(client, world, selection, localPlayer, p, level, heading, buttons);
        }else if(o.isPlayer()){
            selectOpponent(client, world, selection, localPlayer, p, level, heading, buttons);
        }else{
            selectCivilian(client, world, selection, localPlayer, p, level, heading, buttons);
        }
        return new MenuSelection(client, parent, buttons.toList().toArray(new MenuComponentButtonIngame[buttons.size()]));
    }
    private static void selectOwned(ClientSide c, World world, PlotPos pos, Player player, Plot p, int level, Direction heading, Queue<MenuComponentButtonIngame> buttons) {
        if(p==Plot.Debris){
            buttons.enqueue(new MenuButtonScavenge(c, pos));
        }else if(level<p.getLevelCount()-1){
            buttons.enqueue(new MenuButtonUpgrade(c, pos, p, level));
        }
        if(p==Plot.Farm){
            //TODO plant farm button
        }else if(p==Plot.Market){
            //TODO market prices button
        }else if(p==Plot.Warehouse){
            //TODO storage & disposal buttons
        }else if(p==Plot.TownHall){
            //TODO wages & applications buttons
        }
        if(p!=Plot.TownHall||player.countTownHalls()>1){
            buttons.enqueue(new MenuButtonDestroy(c, pos, false));
        }
    }
    private static void selectUnowned(ClientSide c, World world, PlotPos pos, Player player, Plot p, int level, Direction heading, Queue<MenuComponentButtonIngame> buttons) {
        if(p==Plot.Debris){
            buttons.enqueue(new MenuButtonClaim(c, pos));
            buttons.enqueue(new MenuButtonScavenge(c, pos));
            buttons.enqueue(new MenuButtonDestroy(c, pos, true));
        }else if(p!=Plot.Air&&!(p instanceof PlotResource)){
            buttons.enqueue(new MenuButtonClaim(c, pos));
            buttons.enqueue(new MenuButtonDestroy(c, pos, false));
        }else if(p==Plot.Air){//Air doesn't have a level, or heading
            construction(world, pos, player, buttons);
        }else{//It's a PlotResource
            //If either it's not oil or we can pump oil...  then we can harvest.  Everything else, the basic tools are provided in the workshop.
            if(p!=Plot.Oil||player.canPumpOil()) buttons.enqueue(new MenuButtonHarvest(c, pos, ((PlotResource)p).resourceProduced));
            //TODO forests, oil, & coal burn
        }
    }
    private static void selectOpponent(ClientSide c, World world, PlotPos pos, Player player, Plot p, int level, Direction heading, Queue<MenuComponentButtonIngame> buttons) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO selected plot owned by another player
    }
    private static void selectCivilian(ClientSide c, World world, PlotPos pos, Player player, Plot p, int level, Direction heading, Queue<MenuComponentButtonIngame> buttons) {
        throw new UnsupportedOperationException("Not supported yet."); //TODO selected plot owned by a civilian
    }
    private static void construction(World world, PlotPos pos, Player localPlayer, Queue<MenuComponentButtonIngame> buttons) {
        for(Plot p : Plot.getTypes()){
            if(p.isBuildable(localPlayer, pos, world)){
                buttons.enqueue(new MenuButtonConstruct(world, pos, p, localPlayer));
            }
        }
    }
    @Override
    public void tick() {
        super.tick();
        parent.tick();
    }
    private MenuSelection(ClientSide client, Menu parent, MenuComponentButtonIngame... descriptors) {
        super(client.gui, parent);
        this.buttons = new MenuComponentButtonIngame[descriptors.length];
        System.arraycopy(descriptors, 0, buttons, 0, descriptors.length);
        for(MenuComponentButtonIngame b : buttons){
            add(b);
            b.x = b.y = 100;
            b.width = b.height = 0;//They should never be onscreen at that position- and even so, 0 size makes 'em hard to click
        }
        lastWidth = Display.getWidth();
        lastHeight = Display.getHeight();
        updateButtons();
    }
    @Override
    public void renderBackground() {
        parent.render(0);
        if(lastWidth!=Display.getWidth()||lastHeight!=Display.getHeight()){
            updateButtons();
        }
        lastWidth = Display.getWidth();
        lastHeight = Display.getHeight();
    }
    @Override
    public void render(int millisSinceLastTick) {
        renderBackground();
        String buttonInfo = "";
        for(MenuComponent component : components){
            component.draw();
            if(component instanceof MenuComponentButtonIngame&&component.isMouseOver){
                buttonInfo = ((MenuComponentButtonIngame)component).getInfo();
            }
        }
        float screenBottom = gui.helper.guiScale;
        double lastScreenWidth = (double)Display.getWidth()/Display.getHeight()*gui.helper.guiScale;
        drawCenteredText(-lastScreenWidth, screenBottom-0.31, lastScreenWidth, screenBottom-0.25, buttonInfo);
    }
    @Override
    public void mouseEvent(int button, boolean pressed, float x, float y, float xChange, float yChange, int wheelChange) {
        super.mouseEvent(button, pressed, x, y, xChange, yChange, wheelChange);
        double perButton = 0.4;
        double altitude = gui.helper.guiScale-perButton;
        if(y<altitude||button==2)  parent.mouseEvent(button, pressed, x, y, xChange, yChange, wheelChange);
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat) {
        super.keyboardEvent(character, key, pressed, repeat);
        parent.keyboardEvent(character, key, pressed, repeat);
    }
    private void updateButtons(){
        int buttonsToFit = (int) ((double)Display.getWidth()/Display.getHeight()*gui.helper.guiScale*2/0.4);
        if(buttonsToFit>=buttons.length){
            scrollPos = 0;
            centerButtons(0, buttons.length-1, false, false);
        }else if(scrollPos==0){
            hideButtons();
            centerButtons(0, buttonsToFit-2, false, true);
        }else if(scrollPos+buttonsToFit>buttons.length){
            hideButtons();
            centerButtons(scrollPos, buttons.length-1, true, false);
        }else{
            hideButtons();
            centerButtons(scrollPos, scrollPos+buttonsToFit-3, true, true);
        }
    }
    private void centerButtons(final int startIndex, final int endIndex, boolean backButton, boolean forwardButton){
        if(next!=null)remove(next);
        if(previous!=null)remove(previous);
        components.remove(next);
        components.remove(previous);
        if(!backButton) previous = null;
        if(!forwardButton) next = null;
        int buttonCount = endIndex-startIndex+1+(backButton?1:0)+(forwardButton?1:0);
        double perButton = 0.4;
        double altitude = gui.helper.guiScale-perButton;
        double start = -buttonCount*perButton/2;
        if(backButton){
            previous = new MenuComponentButtonIngame(MenuComponentButtonIngame.lastPage, "Back") {
                @Override
                public void action() {
                    scrollPos = Math.max(0, scrollPos-(int)((double)Display.getWidth()/Display.getHeight()*gui.helper.guiScale*2/0.4)+1);
                    if(scrollPos>0) scrollPos++;
                    updateButtons();
                }
            };
            add(previous);
            previous.x = start;
            previous.y = altitude;
            previous.width = previous.height = perButton;
            start += perButton;
        }
        for(int i = startIndex; i<=endIndex; i++){
            if(i<0||i>=buttons.length) continue;
            MenuComponentButtonIngame b = buttons[i];
            b.x = start;
            b.y = altitude;
            b.width = b.height = perButton;
            start += perButton;
        }
        if(forwardButton){
            next = new MenuComponentButtonIngame(MenuComponentButtonIngame.nextPage, "Next") {
                @Override
                public void action() {
                    scrollPos += endIndex-startIndex+1;//We simply....   go to the next page
                    updateButtons();
                }
            };
            add(next);
            next.x = start;
            next.y = altitude;
            next.width = next.height = perButton;
            start += perButton;
        }
    }
    private void hideButtons(){
        for (MenuComponentButtonIngame b : buttons) {
            b.x = b.y = 100;
            b.width = b.height = 0;
        }
    }
}
