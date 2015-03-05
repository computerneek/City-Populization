package CityPopulization.render;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
public class RoadRenderer implements PlotRenderer{
    public RoadRenderer(){
    }
    @Override
    public void render(Plot plot, String textureFolder){
        int x = plot.x;
        int y = -plot.y;
        int z = plot.z;
        int levelCap = plot.getType().getMaximumLevel();
        int level = plot.getLevel()%levelCap;
        int frameCap = plot.getType().getFrameCap(level+1, plot.getType().getTextureIndex("1:/textures/plots/"+textureFolder+"/level <LEVEL>/frame <FRAME>.png"));
        int frame = plot.getFrameNumber()%frameCap;
        String path = "/textures/plots/"+textureFolder+"/level "+(level+1)+"/frame "+(frame+1)+".png";
        render(x, y, z, path, plot.front);
        for(Side side : new Side[]{plot.front, plot.front.right(), plot.front.left(), plot.front.reverse()}){
            Plot other = side.getPlot(plot.world, plot.x, plot.y, plot.z);
            boolean can = plot.getPathableSides(false).contains(side)&&other!=null&&other.getPathableSides(false).contains(side.reverse())&&plot.owner==other.owner;
            if(can){
                renderPath(x, y, z, path, side);
            }
        }
    }
    private void render(int x, int y, int z, String path, Side facing){
        if(facing==null||facing==Side.UP||facing==Side.DOWN){
            facing = Side.FRONT;
        }
        int texture = ImageStash.instance.getTexture(path);
        ImageStash.instance.bindTexture(texture);
        GL11.glBegin(GL11.GL_QUADS);
        switch(facing){
            case FRONT:
                {
                    GL11.glTexCoord2d(0, 0);
                    GL11.glVertex3d(x, y, z);
                    GL11.glTexCoord2d(0.5, 0);
                    GL11.glVertex3d(x+1, y, z);
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x+1, y-1, z);
                    GL11.glTexCoord2d(0, 0.5);
                    GL11.glVertex3d(x, y-1, z);
                }
                break;
            case BACK:
                {
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x, y, z);
                    GL11.glTexCoord2d(0, 0.5);
                    GL11.glVertex3d(x+1, y, z);
                    GL11.glTexCoord2d(0, 0);
                    GL11.glVertex3d(x+1, y-1, z);
                    GL11.glTexCoord2d(0.5, 0);
                    GL11.glVertex3d(x, y-1, z);
                }
                break;
            case RIGHT:
                {
                    GL11.glTexCoord2d(0.5, 0);
                    GL11.glVertex3d(x, y, z);
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x+1, y, z);
                    GL11.glTexCoord2d(0, 0.5);
                    GL11.glVertex3d(x+1, y-1, z);
                    GL11.glTexCoord2d(0, 0);
                    GL11.glVertex3d(x, y-1, z);
                }
                break;
            case LEFT:
                {
                    GL11.glTexCoord2d(0, 0.5);
                    GL11.glVertex3d(x, y, z);
                    GL11.glTexCoord2d(0, 0);
                    GL11.glVertex3d(x+1, y, z);
                    GL11.glTexCoord2d(0.5, 0);
                    GL11.glVertex3d(x+1, y-1, z);
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x, y-1, z);
                    GL11.glTexCoord2d(0, 0.5);
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
        GL11.glBegin(GL11.GL_TRIANGLES);
        switch(facing){
            case FRONT:
                {
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x, y-1, z);
                    GL11.glTexCoord2d(1, 0.5);
                    GL11.glVertex3d(x+1, y-1, z);
                    GL11.glTexCoord2d(0.75, 0.25);
                    GL11.glVertex3d(x+0.5, y-0.5, z);
                }
                break;
            case BACK:
                {
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x+1, y, z);
                    GL11.glTexCoord2d(1, 0.5);
                    GL11.glVertex3d(x, y, z);
                    GL11.glTexCoord2d(0.75, 0.25);
                    GL11.glVertex3d(x+0.5, y-0.5, z);
                }
                break;
            case RIGHT:
                {
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x+1, y-1, z);
                    GL11.glTexCoord2d(1, 0.5);
                    GL11.glVertex3d(x+1, y, z);
                    GL11.glTexCoord2d(0.75, 0.25);
                    GL11.glVertex3d(x+0.5, y-0.5, z);
                }
                break;
            case LEFT:
                {
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x, y, z);
                    GL11.glTexCoord2d(1, 0.5);
                    GL11.glVertex3d(x, y-1, z);
                    GL11.glTexCoord2d(0.75, 0.25);
                    GL11.glVertex3d(x+0.5, y-0.5, z);
                }
                break;
            default:
                throw new AssertionError(facing.name());
        }
        GL11.glEnd();
    }
    @Override
    public String[] getPaths(PlotType plot, int levels, String textureFolder){
        return new String[]{levels+":/textures/plots/"+textureFolder+"/level <LEVEL>/frame <FRAME>.png"};
    }
}
