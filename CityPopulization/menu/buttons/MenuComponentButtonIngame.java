package CityPopulization.menu.buttons;
import java.awt.event.ActionListener;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuComponentButtonIngame extends MenuComponentButton{
    public String[] label;
    public String image;
    public ActionListener listener;
    private int hotkey;
    public MenuComponentButtonIngame(double x, double y, double width, double height, String[] label, boolean enabled, String image, ActionListener listener, int hotkey){
        super(x, y, width, height, "", enabled);
        this.label = label;
        this.image = image;
        this.listener = listener;
        this.hotkey = hotkey;
    }
    @Override
    public void render(){
        int texture = -1;
        if(enabled){
            if(isPressed){
                texture = ImageStash.instance.getTexture("/gui/buttons/background/pressed.png");
            }else{
                if(isMouseOver){
                    texture = ImageStash.instance.getTexture("/gui/buttons/background/mouseover.png");
                }else{
                    texture = ImageStash.instance.getTexture("/gui/buttons/background/plain.png");
                }
            }
        }else{
            if(isMouseOver){
                texture = ImageStash.instance.getTexture("/gui/buttons/background/disabled.png");
            }else{
                texture = ImageStash.instance.getTexture("/gui/buttons/background/disabled.png");
            }
        }
        drawRect(x, y, x+width, y+height, texture);
        double textHeight = 0.04;
        drawRect(x+0.02+textHeight*label.length/2d, y+0.02, x+width-0.02-textHeight*label.length/2d, y+height-0.02-textHeight*label.length, ImageStash.instance.getTexture(image));
        GL11.glColor3f(0, 0, 0);
        for(int i = 0; i<label.length; i++){
            String text = label[label.length-i-1];
            drawCenteredText(x+0.01, y+height-0.02-textHeight*(i+1), x+width-0.01, y+height-0.02-textHeight*i, text);
        } 
        GL11.glColor3f(1, 1, 1);
    }
    public int getHotkey(){
        return hotkey;
    }
}
