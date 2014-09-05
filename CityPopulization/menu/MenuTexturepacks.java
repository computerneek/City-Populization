package CityPopulization.menu;
import CityPopulization.Core;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import simplelibrary.openal.SoundStash;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.ListComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentMulticolumnList;
import simplelibrary.texture.TexturePackManager;
public class MenuTexturepacks extends Menu {
    private final MenuComponentMulticolumnList list;
    private final MenuComponentButton newButton;
    private final MenuComponentButton doneButton;
    private int selectedIndex;
    private int tick;
    public MenuTexturepacks(GUI gui, Menu parent){
        super(gui, parent);
        list = add(new MenuComponentMulticolumnList(-1.6, -1.0, 3.2, 1.8, 1.5, 0.1));
        newButton = add(new MenuComponentButton(-1.58, 0.82, 1.56, 0.16, "New Texturepack (NYI)", false));
        doneButton = add(new MenuComponentButton(0.02, 0.82, 1.56, 0.16, "Done", true));
        refreshTexturepacks();
    }
    @Override
    public void renderBackground(){}
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==newButton){
            newTexturepack();
        }else if(button==doneButton){
            gui.open(parent);
        }
    }
    @Override
    public void listButtonClicked(ListComponentButton button){
        TexturePackManager.instance.setTexturePack(button.getLabel());
        ImageStash.instance.clearTextures();
        SoundStash.clearSources();
        SoundStash.clearBuffers();
        refreshTexturepacks();
    }
    @Override
    public void tick(){
        tick++;
        if(tick%20==0){
            refreshTexturepacks();
        }
    }
    private void newTexturepack(){
        gui.open(new MenuTexturepackCreator(gui, this));
    }
    private void refreshTexturepacks(){
        TexturePackManager.instance.findTexturePacks();
        ArrayList<String> texturepacks = TexturePackManager.instance.listTexturePacks();
        list.components.clear();
        for(String texturepack : texturepacks){
            list.add(new ListComponentButton(texturepack, true, 1.5, 0.1));
        }
        list.selectedIndex = TexturePackManager.instance.texturePacks.indexOf(TexturePackManager.instance.currentTexturePack);
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(key==Keyboard.KEY_F11&&pressed&&!repeat){
            Core.helper.setFullscreen(!Core.helper.isFullscreen());
        }
        super.keyboardEvent(character, key, pressed, repeat);
    }
}
