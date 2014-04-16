package CityPopulization.menu.buttons;
import CityPopulization.menu.MenuIngame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import org.lwjgl.opengl.Display;
public class ButtonSet {
    public ArrayList<Button> buttons = new ArrayList<>();
    public int buttonIndex;
    public void add(Button button){
        buttons.add(button);
    }
    public void display(final int startButton, final MenuIngame menu){
        buttonIndex = startButton;
        if(buttonIndex>=buttons.size()){
            buttonIndex = buttons.size()-1;
        }
        if(buttonIndex<0){
            buttonIndex = 0;
        }
        double buttonSize = 0.25;
        double buttonOffset = 0.5;
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
        menu.components.clear();
        if(addPreviousButton){
            menu.add(new MenuComponentButtonIngame(buttonOffset, menu.screenBottom-buttonSize, buttonSize, buttonSize, new String[]{"Back"}, true, "/gui/buttons/back.png", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    display(buttonIndex-1, menu);
                }
            }));
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
            }));
        }
        if(addNextButton){
            menu.add(new MenuComponentButtonIngame(buttonOffset+drawableButtons*buttonSize, menu.screenBottom-buttonSize, buttonSize, buttonSize, new String[]{"Next"}, true, "/gui/buttons/next.png", new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    display(buttonIndex+1, menu);
                }
            }));
        }
    }
}
