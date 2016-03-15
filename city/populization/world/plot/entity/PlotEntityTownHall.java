package city.populization.world.plot.entity;
import city.populization.world.plot.PlotPos;
import simplelibrary.config2.Config;
public class PlotEntityTownHall extends PlotEntity{
    @Override
    public Config save() {
        throw new UnsupportedOperationException("Not supported yet."); //TODO save the town hall!
    }
    @Override
    public void load(Config data) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    @Override
    public void tick() {
        //TODO Town Hall tick- any job interviews in progress continue
    }
    @Override
    public void construct() {
    }
}
