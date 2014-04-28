package CityPopulization.menu.buttons;
import CityPopulization.menu.MenuIngame;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.story.MenuIngameRestricted;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import simplelibrary.opengl.gui.components.MenuComponent;
public class ButtonSet {
    public ArrayList<Button> buttons = new ArrayList<>();
    public int buttonIndex;
    public void add(Button button){
        buttons.add(button);
    }
    public void display(final PlotType type, final int startButton, final MenuIngame menu){
        if(menu instanceof MenuIngameRestricted){
            for(Iterator<Button> it=buttons.iterator(); it.hasNext();){
                Button button=it.next();
                if(!((MenuIngameRestricted)menu).approve(type, button)){
                    it.remove();
                }
            }
        }
        buttonIndex = startButton;
        if(buttonIndex>=buttons.size()){
            buttonIndex = buttons.size()-1;
        }
        if(buttonIndex<0){
            buttonIndex = 0;
        }
        double buttonSize = 0.25;
        double buttonOffset = 0.75;
        double screenWidth = (double)Display.getWidth()/Display.getHeight()*menu.gui.helper.guiScale*2;
        int buttonCount = (int)Math.floor((screenWidth-buttonOffset)/buttonSize);
        buttonOffset-=screenWidth/2;
        int drawableButtons = buttonCount;
        boolean addPreviousButton = false;
        if(buttonIndex>0){
            drawableButtons--;
            addPreviousButton = true;
        }
        int buttonsToDraw = buttons.size()-buttonIndex;
        boolean addNextButton = false;
        if(drawableButtons<buttonsToDraw){
            drawableButtons--;
            addNextButton = true;
        }
        for(Iterator<MenuComponent> it=menu.components.iterator(); it.hasNext();){
            MenuComponent component=it.next();
            if(component instanceof MenuComponentButtonIngame){
                it.remove();
            }
        }
        menu.components.clear();
        if(addPreviousButton){
            menu.add(new MenuComponentButtonIngame(buttonOffset, menu.screenBottom-buttonSize, buttonSize, buttonSize, new String[]{"Back"}, true, "/gui/buttons/back.png", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    display(type, buttonIndex-1, menu);
                }
            }, Keyboard.KEY_LEFT));
            buttonOffset+=buttonSize;
        }
        for(int i = 0; i<drawableButtons; i++){
            if(i+buttonIndex>=buttons.size()){
                break;
            }
            final Button button = buttons.get(i+buttonIndex);
            menu.add(new MenuComponentButtonIngame(buttonOffset+i*buttonSize, menu.screenBottom-buttonSize, buttonSize, buttonSize, button.text, true, button.image, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    button.onClicked();
                }
            }, button.getHotkey()));
        }
        if(addNextButton){
            menu.add(new MenuComponentButtonIngame(buttonOffset+drawableButtons*buttonSize, menu.screenBottom-buttonSize, buttonSize, buttonSize, new String[]{"Next"}, true, "/gui/buttons/next.png", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    display(type, buttonIndex+1, menu);
                }
            }, Keyboard.KEY_RIGHT));
        }
    }
}
