package city.populization.world.plot.type;
import city.populization.world.plot.Plot;
import city.populization.world.plot.entity.PlotEntity;
import city.populization.world.plot.render.PlotRender;
public abstract class PlotEntitied extends Plot{
    public PlotEntitied(String name, int crushResistance, int friction, int weight, int tension, PlotRender model) {
        super(name, crushResistance, friction, weight, tension, model);
    }
    public abstract PlotEntity createNewEntity();
}
