package CityPopulization.menu;
import CityPopulization.texturepack.TexturepackCreator;
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
}
