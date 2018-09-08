package CityPopulization.world.story;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import simplelibrary.game.GameHelper;
import simplelibrary.opengl.gui.components.MenuComponent;
public class Title extends MenuComponent {
    public String text;
    public boolean editable;
    public double textInset = -1;
    public Title(String title){
        super(-0.8, -0.8, 1.6, 0.2);
        this.text = title;
    }
    @Override
    public void mouseEvent(double x, double y, int button, boolean isDown){}
    @Override
    public void render(){
        GL11.glColor3f(0, 0, 0);
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
        if(editable){
            drawText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, text+(((gui.tick&20)<10&&isSelected)?"_":""));
        }else{
            drawCenteredText(x+textInset, y+textInset, x+width-textInset, y+height-textInset, text);
        }
        GL11.glColor3f(1, 1, 1);
    }
}
