package city.populization.world.plot.entity;
import city.populization.world.plot.PlotPos;
import simplelibrary.config2.Config;
public class PlotEntityWarehouse extends PlotEntity{
    @Override
    public Config save() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO save the warehouse
    }
    @Override
    public void load(Config data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void tick() {
        //TODO Goods stored in the warehouse need to tick
    }
    @Override
    public void construct() {
    }
}
