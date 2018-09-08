package CityPopulization.menu;
import CityPopulization.world.WorldInfo;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.components.ListComponent;
public class ListComponentWorld extends ListComponent{
    public final WorldInfo info;
    public ListComponentWorld(WorldInfo info){
        this.info = info;
    }
    @Override
    public double getWidthD(){
        return 0.8;
    }
    @Override
    public double getHeightD(){
        return 0.36;
    }
    @Override
    public void render(double x, double y, double minX, double minY, double maxX, double maxY){
        int texture = -1;
        if(isSelected){
            texture = ImageStash.instance.getTexture("/gui/worldSelected.png");
        }else{
            texture = ImageStash.instance.getTexture("/gui/world.png");
        }
        drawRectWithBounds(x, y, x+getWidthD(), y+getHeightD(), minX, minY, maxX, maxY, texture);
        GL11.glColor4f(0, 0, 0, 1);
        drawCenteredTextWithBounds(x+0.02, y+0.02, x+getWidthD()-0.02, y+0.1, minX, minY, maxX, maxY, info.name);
        drawCenteredTextWithBounds(x+0.02, y+0.1, x+getWidthD()-0.02, y+0.18, minX, minY, maxX, maxY, info.template);
        drawCenteredTextWithBounds(x+0.02, y+0.18, x+getWidthD()-0.02, y+0.26, minX, minY, maxX, maxY, info.size);
        drawCenteredTextWithBounds(x+0.02, y+0.26, x+getWidthD()-0.02, y+0.34, minX, minY, maxX, maxY, info.created);
        GL11.glColor3f(1, 1, 1);
    }
    @Override
    public void onClicked(double x, double y, int button){}
    
}
