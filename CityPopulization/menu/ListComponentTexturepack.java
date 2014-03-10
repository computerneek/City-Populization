package CityPopulization.menu;
import org.lwjgl.opengl.GL11;
import simplelibrary.game.GameHelper;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.components.ListComponentButton;
public class ListComponentTexturepack extends ListComponentButton{
    public final String label;
    private final double width;
    private final double height;
    public double textInset;
    public ListComponentTexturepack(String label, boolean enabled, double width, double height){
        super(label, enabled, width, height);
        this.label = label;
        this.width = width;
        this.height = height;
    }
    private void action(){
        parent.parent.listButtonClicked(this);
    }
    public String getLabel(){
        return label;
    }
    @Override
    public double getWidth(){
        return width;
    }
    @Override
    public double getHeight(){
        return height;
    }
    @Override
    public void render(double x, double y, double minX, double minY, double maxX, double maxY){
        int texture = ImageStash.instance.getTexture("/gui/texturepack.png");
        if(textInset<0){
            switch(parent.gui.type){
                case GameHelper.MODE_2D:
                case GameHelper.MODE_2D_CENTERED:
                    textInset = 5;
                    break;
                case GameHelper.MODE_3D:
                    textInset = Math.min(width/20, height/20);
                    break;
            }
        }
        drawRectWithBounds(x, y, x+width, y+height, minX, minY, maxX, maxY, texture);
        GL11.glColor4f(0, 0, 0, 1);
        drawCenteredTextWithBounds(x+textInset, y+textInset, x+width-textInset, y+height-textInset, minX, minY, maxX, maxY, label);
        GL11.glColor3f(1, 1, 1);
    }
    @Override
    public void onClicked(double x, double y, int button){
        if(button==0){
            action();
        }
    }
}
