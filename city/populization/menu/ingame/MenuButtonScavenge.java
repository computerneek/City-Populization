package city.populization.menu.ingame;
import city.populization.core.ClientSide;
import city.populization.world.plot.PlotPos;
public class MenuButtonScavenge extends MenuComponentButtonIngame {
    private final ClientSide c;
    private final PlotPos pos;
    public MenuButtonScavenge(ClientSide c, PlotPos pos) {
        super(harvestDebris, "Scavenge", "Debris");
        this.c = c;
        this.pos = pos;
    }
    @Override
    public void action() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO Ask server to scavenge debris
    }
}
