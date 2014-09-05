package CityPopulization.menu;
import CityPopulization.Core;
import CityPopulization.world.plot.PlotType;
import java.io.IOException;
import java.io.InputStream;
import org.lwjgl.input.Keyboard;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponent;
import simplelibrary.opengl.gui.components.MenuComponentButton;
public class MenuIngameVictory extends Menu {
    public static int highestAnimFrame = 0;
    public static void init(){
        highestAnimFrame = findFrameCap();
    }
    private static int findFrameCap(){
        int frameCap = 0;
        for(int i = 1; frameCap==0; i++){
            String path = "/textures/victory/frame "+i+".png";
            try(InputStream in = PlotType.class.getResourceAsStream(path)){
                if(in==null){
                    frameCap = i-1;
                }else{
                    ImageStash.instance.getTexture(path);
                }
            }catch(IOException ex){}
        }
        return frameCap;
    }
    public int animFrame = 0;
    public MenuIngameVictory(){
        super(Core.gui, Core.gui.menu);
    }
    @Override
    public void tick(){
        animFrame++;
        if(animFrame==highestAnimFrame+1){
            makeButtons();
        }
    }
    private void makeButtons(){
        add(new MenuComponentButton(-0.8, -0.08, 1.6, 0.16, "Victory", true));
    }
    @Override
    public void render(int millisSinceLastTick){
        parent.render(millisSinceLastTick);
        drawRect(-3.2, -2, 3.2, 2, ImageStash.instance.getTexture("/textures/victory/frame "+Math.max(1, Math.min(animFrame, highestAnimFrame))+".png"));
        for(MenuComponent component : components){
            component.draw();
        }
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        Core.world.reset();
        Core.world = null;
        Menu menu = parent;
        while(menu instanceof MenuIngame){
            menu = menu.parent;
        }
        gui.open(menu);
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(key==Keyboard.KEY_F11&&pressed&&!repeat){
            Core.helper.setFullscreen(!Core.helper.isFullscreen());
        }
        super.keyboardEvent(character, key, pressed, repeat);
    }
}