package CityPopulization.world.story;
import java.util.ArrayList;
import java.util.Arrays;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import simplelibrary.font.FontManager;
import simplelibrary.opengl.ImageStash;
public class Conversation extends Display {
    private ArrayList<Object> objs;
    private ArrayList<Character> chars = new ArrayList<>();
    private int whichOne = -1;
    private String message;
    private double screenWidth;
    private float screenHeight;
    private String last;
    private String[] lastLines;
    public Conversation(Object[] objs){
        this.objs = new ArrayList<>(Arrays.asList(objs));
        update();
    }
    @Override
    public void render(int millisSinceLastTick){
        parent.render(0);
        parent.render(millisSinceLastTick);
        screenWidth = (double)org.lwjgl.opengl.Display.getWidth()/org.lwjgl.opengl.Display.getHeight()*gui.helper.guiScale;
        screenHeight = gui.helper.guiScale;
        GL11.glColor4d(0, 0, 0, 0.2);
        drawRect(-screenWidth, -screenHeight, screenWidth, screenHeight, 0);
        GL11.glColor4d(1, 1, 1, 1);
        for(int i = 0; screenHeight-0.4*i>=-screenHeight+0.4; i++){
            if(chars.size()>2*i){
                drawRect(-screenWidth, screenHeight-0.4*i-0.4, -screenWidth+0.4, screenHeight-0.4*i, ImageStash.instance.getTexture("/story/characters/"+chars.get(0).texture));
                drawCenteredText(-screenWidth+0.4, screenHeight-0.4*i-0.4, 0, screenHeight-0.4*i-0.4+0.06, chars.get(2*i).name);
                drawRect(-screenWidth, screenHeight-0.4*i-0.4, 0, screenHeight-0.4*i, ImageStash.instance.getTexture("/story/conversation/background.png"));
                if(whichOne==2*i&&message!=null){
                    drawMessage(-screenWidth+0.4, screenHeight-0.4*i-0.4, 0, screenHeight-0.4*i);
                }
            }
            if(chars.size()>1+2*i){
                drawRect(screenWidth-0.4, screenHeight-0.4*i-0.4, screenWidth, screenHeight-0.4*i, ImageStash.instance.getTexture("/story/characters/"+chars.get(0).texture));
                drawCenteredText(0, screenHeight-0.4*i-0.4, screenWidth-0.4, screenHeight-0.4*i-0.4+0.06, chars.get(2*i+1).name);
                drawRect(0, screenHeight-0.4*i-0.4, screenWidth-0.4, screenHeight-0.4*i, ImageStash.instance.getTexture("/story/conversation/background.png"));
                if(whichOne==1+2*i&&message!=null){
                    drawMessage(0, screenHeight-0.4*i-0.4, screenWidth-0.4, screenHeight-0.4*i);
                }
            }
        }
    }
    private void update(){
        if(objs.isEmpty()){
            gui.open(parent);
            return;
        }
        Object obj = objs.remove(0);
        if(obj==null){
            if(chars.isEmpty()){
                objs.add(0, Character.VOICE);
                update();
            }
            whichOne++;
            whichOne%=chars.size();
            update();
        }else if(obj instanceof Character){
            if(chars.contains((Character)obj)){
                chars.remove((Character)obj);
            }else{
                chars.add((Character)obj);
            }
            update();
        }else if(obj instanceof String){
            if(chars.isEmpty()){
                objs.add(0, Character.VOICE);
                update();
            }
            whichOne++;
            whichOne%=chars.size();
            message = (String)obj;
        }
    }
    @Override
    public void mouseEvent(int button, boolean pressed, float x, float y, float xChange, float yChange, int wheelChange){
        if(button==0&&pressed){
            update();
        }
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(character==' '||key==Keyboard.KEY_SPACE||key==Keyboard.KEY_RETURN&&pressed&&!repeat){
            update();
        }else if(key==Keyboard.KEY_ESCAPE&&pressed){
            gui.open(parent);
        }
    }
    private void drawMessage(double left, double top, double right, double bottom){
        if(message!=last||lastLines == null){
            ArrayList<String> str = new ArrayList<>();
            String remaining = message;
            while(remaining!=null&&!remaining.isEmpty()){
                if(FontManager.getLengthForStringWithHeight(remaining, 0.04)<=right-left){
                    str.add(remaining);
                    remaining = "";
                    continue;
                }
                int asize = remaining.length()-1;
                String test = remaining.substring(0, remaining.length()-1);
                while(FontManager.getLengthForStringWithHeight(test, 0.04)>=right-left){
                    asize--;
                    test = test.substring(0, asize);
                }
                while(test.lastIndexOf(' ')==-1&&test.length()<remaining.length()){
                    asize++;
                    test = remaining.substring(0, asize);
                }
                if(test.lastIndexOf(' ')!=-1){
                    test = test.substring(0, test.lastIndexOf(' ')+1);
                }
                str.add(test);
                remaining = remaining.substring(test.length());
            }
            String[] lines = str.toArray(new String[str.size()]);
            last = message;
            lastLines = lines;
        }
        for(int i = 0; i*0.04+0.06<=bottom-top-0.04&&i<lastLines.length; i++){
            drawCenteredText(left, top+i*0.04+0.06, right, top+i*0.04+0.1, lastLines[i]);
        }
    }
}
