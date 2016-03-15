package city.populization.menu.ingame;
import city.populization.core.ClientSide;
import city.populization.world.plot.PlotPos;
public class MenuButtonClaim extends MenuComponentButtonIngame {
    public MenuButtonClaim(ClientSide c, PlotPos pos) {
        super(claim, "Claim");
    }
    @Override
    public void action() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO Ask server to claim plot
    }
}
