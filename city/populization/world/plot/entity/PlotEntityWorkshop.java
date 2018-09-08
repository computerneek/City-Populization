package city.populization.world.plot.entity;
import city.populization.world.plot.PlotPos;
import simplelibrary.config2.Config;
public class PlotEntityWorkshop extends PlotEntity{
    @Override
    public Config save() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO save the workshop
    }
    @Override
    public void load(Config data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void tick() {
        //TODO Anyone idling in the workshop works on craft projects of their own, which belong to the owner of the workshop
    }
    @Override
    public void construct() {
    }
}
