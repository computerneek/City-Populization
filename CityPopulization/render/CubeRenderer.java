package CityPopulization.render;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
public class CubeRenderer implements PlotRenderer{
    @Override
    public void render(Plot plot, String textureFolder){
        int x = plot.x;
        int y = -plot.y;
        int z = plot.z;
        int levelCap = plot.getType().getMaximumLevel();
        int level = plot.getLevel()%levelCap;
        int frameCap = plot.getType().getFrameCap(level+1, plot.getType().getTextureIndex("1:/textures/plots/"+textureFolder+"/"+(plot.type.resourceHarvested.count()==0?"level <LEVEL>/":"")+"frame <FRAME>.png"));
        int frame = plot.getFrameNumber()%frameCap;
        String path = "/textures/plots/"+textureFolder+"/"+(plot.type.resourceHarvested.count()==0?"level "+(level+1)+"/":"")+"frame "+(frame+1)+".png";
        int texture = ImageStash.instance.getTexture(path);
        ImageStash.instance.bindTexture(texture);
        GL11.glBegin(GL11.GL_QUADS);
        if(plot.shouldRenderTopFace||plot.z>=plot.world.getLocalPlayer().getCameraZ()){
            GL11.glTexCoord2d(0, 0);
            GL11.glVertex3d(x, y, z);
            GL11.glTexCoord2d(1/3d, 0);
            GL11.glVertex3d(x+1, y, z);
            GL11.glTexCoord2d(1/3d, 0.5);
            GL11.glVertex3d(x+1, y-1, z);
            GL11.glTexCoord2d(0, 0.5);
            GL11.glVertex3d(x, y-1, z);
        }
        if(plot.shouldRenderLeftFace&&plot.world.getLocalPlayer().getCameraX()>=-x){
            GL11.glTexCoord2d(1/3d, 0);
            GL11.glVertex3d(x, y, z);
            GL11.glTexCoord2d(2/3d, 0);
            GL11.glVertex3d(x, y-1, z);
            GL11.glTexCoord2d(2/3d, 0.5);
            GL11.glVertex3d(x, y-1, z-1);
            GL11.glTexCoord2d(1/3d, 0.5);
            GL11.glVertex3d(x, y, z-1);
        }
        if(plot.shouldRenderRightFace&&plot.world.getLocalPlayer().getCameraX()<=-x-1){
            GL11.glTexCoord2d(1/3d, 0.5);
            GL11.glVertex3d(x+1, y-1, z);
            GL11.glTexCoord2d(2/3d, 0.5);
            GL11.glVertex3d(x+1, y, z);
            GL11.glTexCoord2d(2/3d, 1);
            GL11.glVertex3d(x+1, y, z-1);
            GL11.glTexCoord2d(1/3d, 1);
            GL11.glVertex3d(x+1, y-1, z-1);
        }
        if(plot.shouldRenderFrontFace&&plot.world.getLocalPlayer().getCameraY()>=-y+1){
            GL11.glTexCoord2d(2/3d, 0);
            GL11.glVertex3d(x, y-1, z);
            GL11.glTexCoord2d(1, 0);
            GL11.glVertex3d(x+1, y-1, z);
            GL11.glTexCoord2d(1, 0.5);
            GL11.glVertex3d(x+1, y-1, z-1);
            GL11.glTexCoord2d(2/3d, 0.5);
            GL11.glVertex3d(x, y-1, z-1);
        }
        if(plot.shouldRenderBackFace&&plot.world.getLocalPlayer().getCameraY()<=-y){
            GL11.glTexCoord2d(2/3d, 0.5);
            GL11.glVertex3d(x+1, y, z);
            GL11.glTexCoord2d(1, 0.5);
            GL11.glVertex3d(x, y, z);
            GL11.glTexCoord2d(1, 1);
            GL11.glVertex3d(x, y, z-1);
            GL11.glTexCoord2d(2/3d, 1);
            GL11.glVertex3d(x+1, y, z-1);
        }
        GL11.glEnd();
    }
    @Override
    public String[] getPaths(PlotType type, int levels, String textureFolder){
        return new String[]{levels+":/textures/plots/"+textureFolder+"/"+(type.resourceHarvested.count()==0?"level <LEVEL>/":"")+"frame <FRAME>.png"};
    }
}
