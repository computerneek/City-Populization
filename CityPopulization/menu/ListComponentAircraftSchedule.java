package CityPopulization.menu;
import CityPopulization.world.aircraft.schedule.ScheduleElement;
import org.lwjgl.opengl.GL11;
import simplelibrary.game.GameHelper;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.components.ListComponent;
public class ListComponentAircraftSchedule extends ListComponent {
    private ScheduleElement element;
    public ListComponentAircraftSchedule(ScheduleElement element){
        this.element = element;
    }
    @Override
    public double getWidth(){
        return 1;
    }
    @Override
    public double getHeight(){
        return 0.2;
    }
    @Override
    public void render(double x, double y, double minX, double minY, double maxX, double maxY){
        int texture = ImageStash.instance.getTexture("/gui/aircraft/schedule.png");
        drawRectWithBounds(x, y, x+getWidth(), y+getHeight(), minX, minY, maxX, maxY, texture);
        GL11.glColor4f(0, 0, 0, 1);
        drawCenteredTextWithBounds(x+0.02, y+0.02, x+getWidth()-0.02, y+0.06, minX, minY, maxX, maxY, element.getAircraftName()+"; $"+element.getAircraftCost()+"; "+element.getFuelCost()+" fuel");
        drawCenteredTextWithBounds(x+0.02, y+0.06, x+getWidth()-0.02, y+0.1, minX, minY, maxX, maxY, "Passengers:  Up to "+element.getMaxPassengerCount()+" (Next:  "+element.getPassengerCount()+")");
        drawCenteredTextWithBounds(x+0.02, y+0.1, x+getWidth()-0.02, y+0.14, minX, minY, maxX, maxY, "Cargo:  "+element.getCargo().toString());
        drawCenteredTextWithBounds(x+0.02, y+0.14, x+getWidth()-0.02, y+0.18, minX, minY, maxX, maxY, "Next arrival in "+element.getTimeUntilNextArrival());
        GL11.glColor3f(1, 1, 1);
    }
    @Override
    public void onClicked(double x, double y, int button){}

}
