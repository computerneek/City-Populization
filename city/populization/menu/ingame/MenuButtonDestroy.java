package city.populization.menu.ingame;
import city.populization.core.ClientSide;
import city.populization.world.plot.PlotPos;
public class MenuButtonDestroy extends MenuComponentButtonIngame {
    private final ClientSide c;
    private final PlotPos pos;
    public MenuButtonDestroy(ClientSide c, PlotPos pos, boolean isDebris) {
        super(isDebris?destroyDebris:destroyStructure, "Destroy", c.world.getPlot(pos).name);
        this.c = c;
        this.pos = pos;
    }
    @Override
    public void action() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO Ask server to destroy plot
    }
}
