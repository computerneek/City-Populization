package city.populization.world.plot.entity;
import city.populization.world.civilian.Civilian;
import city.populization.world.plot.PlotPos;
import java.util.ArrayList;
import simplelibrary.config2.Config;
public class PlotEntityHouse extends PlotEntity{
    private final ArrayList<Civilian> occupants = new ArrayList<>();
    @Override
    public Config save() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO save the house!
    }
    @Override
    public void load(Config data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void tick() {
        //TODO house tick- idle civilians with knowhow may create stuff to sell at the market
        //TODO If the house has power and someone in it, the owner knows automatically what is needed to restock (ring ring telephone)
    }
    @Override
    public void construct() {
    }
    public ArrayList<Civilian> getOccupants() {
        return occupants;
    }
}
