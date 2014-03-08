package CityPopulization.render;
import CityPopulization.world.plot.Plot;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
public class RoadRenderer implements PlotRenderer{
    public RoadRenderer(){
    }
    @Override
    public void render(Plot plot, String textureFolder){
        int x = plot.x;
        int y = plot.y;
        int z = plot.z;
        int levelCap = plot.getType().getMaximumLevel();
        int level = plot.getLevel()%levelCap;
        int frameCap = plot.getType().getFrameCap(level+1);
        int frame = plot.getFrameNumber()%frameCap;
        String path = "/textures/plots/"+textureFolder+"/level "+(level+1)+"/frame "+(frame+1)+".png";
        render(x, y, z, path, plot.front);
        for(Side side : new Side[]{plot.front, plot.front.right(), plot.front.left(), plot.front.reverse()}){
            Plot other = side.getPlot(plot.world, plot.x, plot.y, plot.z);
            boolean can = plot.getPathableSides().contains(side)&&other!=null&&other.getPathableSides().contains(side.reverse())&&plot.owner==other.owner;
            if(can){
                renderPath(x, y, z, path, side);
            }
        }
    }
    private void render(int x, int y, int z, String path, Side facing){
        if(facing==null||facing==Side.UP||facing==Side.DOWN){
            facing = Side.FRONT;
        }
        y=-y;
        int texture = ImageStash.instance.getTexture(path);
        ImageStash.instance.bindTexture(texture);
        GL11.glBegin(GL11.GL_QUADS);
        switch(facing){
            case FRONT:
                {
                    GL11.glTexCoord2d(0, 0);
                    GL11.glVertex3d(x, y, z-0.99);
                    GL11.glTexCoord2d(0.5, 0);
                    GL11.glVertex3d(x+1, y, z-0.99);
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x+1, y-1, z-0.99);
                    GL11.glTexCoord2d(0, 0.5);
                    GL11.glVertex3d(x, y-1, z-0.99);
                }
                break;
            case BACK:
                {
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x, y, z-0.99);
                    GL11.glTexCoord2d(0, 0.5);
                    GL11.glVertex3d(x+1, y, z-0.99);
                    GL11.glTexCoord2d(0, 0);
                    GL11.glVertex3d(x+1, y-1, z-0.99);
                    GL11.glTexCoord2d(0.5, 0);
                    GL11.glVertex3d(x, y-1, z-0.99);
                }
                break;
            case RIGHT:
                {
                    GL11.glTexCoord2d(0, 0.5);
                    GL11.glVertex3d(x, y, z-0.99);
                    GL11.glTexCoord2d(0, 0);
                    GL11.glVertex3d(x+1, y, z-0.99);
                    GL11.glTexCoord2d(0.5, 0);
                    GL11.glVertex3d(x+1, y-1, z-0.99);
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x, y-1, z-0.99);
                    GL11.glTexCoord2d(0, 0.5);
                }
                break;
            case LEFT:
                {
                    GL11.glTexCoord2d(0.5, 0);
                    GL11.glVertex3d(x, y, z-0.99);
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x+1, y, z-0.99);
                    GL11.glTexCoord2d(0, 0.5);
                    GL11.glVertex3d(x+1, y-1, z-0.99);
                    GL11.glTexCoord2d(0, 0);
                    GL11.glVertex3d(x, y-1, z-0.99);
                }
                break;
            default:
                throw new AssertionError(facing.name());
        }
        GL11.glEnd();
    }
    private void renderPath(int x, int y, int z, String path, Side facing){
        if(facing==null||facing==Side.UP||facing==Side.DOWN){
            facing = Side.FRONT;
        }
        int texture = ImageStash.instance.getTexture(path);
        ImageStash.instance.bindTexture(texture);
        GL11.glBegin(GL11.GL_QUADS);
        switch(facing){
            case FRONT:
                {
                    GL11.glTexCoord2d(0.5, 0);
                    GL11.glVertex3d(x, y, z-0.99);
                    GL11.glTexCoord2d(1, 0);
                    GL11.glVertex3d(x+1, y, z-0.99);
                    GL11.glTexCoord2d(1, 0.5);
                    GL11.glVertex3d(x+1, y-1, z-0.99);
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x, y-1, z-0.99);
                }
                break;
            case BACK:
                {
                    GL11.glTexCoord2d(1, 0.5);
                    GL11.glVertex3d(x, y, z-0.99);
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x+1, y, z-0.99);
                    GL11.glTexCoord2d(0.5, 0);
                    GL11.glVertex3d(x+1, y-1, z-0.99);
                    GL11.glTexCoord2d(1, 0);
                    GL11.glVertex3d(x, y-1, z-0.99);
                }
                break;
            case LEFT:
                {
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x, y, z-0.99);
                    GL11.glTexCoord2d(0.5, 0);
                    GL11.glVertex3d(x+1, y, z-0.99);
                    GL11.glTexCoord2d(1, 0);
                    GL11.glVertex3d(x+1, y-1, z-0.99);
                    GL11.glTexCoord2d(1, 0.5);
                    GL11.glVertex3d(x, y-1, z-0.99);
                }
                break;
            case RIGHT:
                {
                    GL11.glTexCoord2d(1, 0);
                    GL11.glVertex3d(x, y, z-0.99);
                    GL11.glTexCoord2d(1, 0.5);
                    GL11.glVertex3d(x+1, y, z-0.99);
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x+1, y-1, z-0.99);
                    GL11.glTexCoord2d(0.5, 0);
                    GL11.glVertex3d(x, y-1, z-0.99);
                }
                break;
            default:
                throw new AssertionError(facing.name());
        }
        GL11.glEnd();
    }
}
