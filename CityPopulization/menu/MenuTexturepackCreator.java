package CityPopulization.menu;
import CityPopulization.Core;
import CityPopulization.texturepack.TexturepackCreator;
import org.lwjgl.input.Keyboard;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class MenuTexturepackCreator  extends Menu{
    public MenuTexturepackCreator(GUI gui, Menu parent){
        super(gui, parent);
    }
    @Override
    public void render(int millisSinceLastTick){
        gui.open(parent);
        TexturepackCreator.outputAllFiles();
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(key==Keyboard.KEY_F11&&pressed&&!repeat){
            Core.helper.setFullscreen(!Core.helper.isFullscreen());
        }
        super.keyboardEvent(character, key, pressed, repeat);
    }
}
