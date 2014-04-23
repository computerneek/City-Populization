package CityPopulization.menu;
import CityPopulization.world.story.StoryMission;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.gui.components.ListComponent;
public class ListComponentStory extends ListComponent {
    public StoryMission mission;
    public ListComponentStory(StoryMission mission){
        this.mission = mission;
    }
    @Override
    public double getWidth(){
        return 0.8;
    }
    @Override
    public double getHeight(){
        return 0.36;
    }
    @Override
    public void render(double x, double y, double minX, double minY, double maxX, double maxY){
        int texture = -1;
        if(isSelected){
            texture = ImageStash.instance.getTexture("/gui/storySelected.png");
        }else{
            texture = ImageStash.instance.getTexture("/gui/story.png");
        }
        drawRectWithBounds(x, y, x+getWidth(), y+getHeight(), minX, minY, maxX, maxY, texture);
        GL11.glColor4f(0, 0, 0, 1);
        drawCenteredTextWithBounds(x+0.02, y+0.02, x+getWidth()-0.02, y+0.1, minX, minY, maxX, maxY, mission.name());
        drawCenteredTextWithBounds(x+0.02, y+0.1, x+getWidth()-0.02, y+0.18, minX, minY, maxX, maxY, mission.difficulty());
        drawCenteredTextWithBounds(x+0.02, y+0.18, x+getWidth()-0.02, y+0.26, minX, minY, maxX, maxY, mission.lastScore());
        drawCenteredTextWithBounds(x+0.02, y+0.26, x+getWidth()-0.02, y+0.34, minX, minY, maxX, maxY, mission.highScore());
        GL11.glColor3f(1, 1, 1);
    }
    @Override
    public void onClicked(double x, double y, int button){}

}
