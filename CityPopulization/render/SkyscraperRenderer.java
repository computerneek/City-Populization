package CityPopulization.render;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer3D;
public class SkyscraperRenderer implements PlotRenderer {
    public SkyscraperRenderer(){
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
        if(plot.shouldRenderTopFace||plot.z>=plot.world.getLocalPlayer().getCameraZ()) render(x, y, z, path, Side.UP, plot);
        if(plot.shouldRenderLeftFace&&plot.world.getLocalPlayer().getCameraX()>=-x) render(x, y, z, path, Side.LEFT, plot);
        if(plot.shouldRenderRightFace&&plot.world.getLocalPlayer().getCameraX()<=-x-1) render(x, y, z, path, Side.RIGHT, plot);
        if(plot.shouldRenderFrontFace&&plot.world.getLocalPlayer().getCameraY()>=-y+1) render(x, y, z, path, Side.FRONT, plot);
        if(plot.shouldRenderBackFace&&plot.world.getLocalPlayer().getCameraY()<=-y) render(x, y, z, path, Side.BACK, plot);
    }
    private void render(int x, int y, int z, String path, Side facing, Plot plot){
        if(facing==null||facing==Side.DOWN){
            facing = Side.FRONT;
        }
        int texture = ImageStash.instance.getTexture(path);
        ImageStash.instance.bindTexture(texture);
        GL11.glBegin(GL11.GL_QUADS);
        switch(facing){
            case UP:
                {
                    GL11.glTexCoord2d(0, 0);
                    GL11.glVertex3d(x, y, z);
                    GL11.glTexCoord2d(1/3d, 0);
                    GL11.glVertex3d(x+1, y, z);
                    GL11.glTexCoord2d(1/3d, 0.5);
                    GL11.glVertex3d(x+1, y-1, z);
                    GL11.glTexCoord2d(0, 0.5);
                    GL11.glVertex3d(x, y-1, z);
                }
                break;
            case FRONT:
                {
                    GL11.glTexCoord2d(2/3d, 0);
                    GL11.glVertex3d(x, y-1, z);
                    GL11.glTexCoord2d(1, 0);
                    GL11.glVertex3d(x+1, y-1, z);
                    GL11.glTexCoord2d(1, 0.5);
                    GL11.glVertex3d(x+1, y-1, z-1);
                    GL11.glTexCoord2d(2/3d, 0.5);
                    GL11.glVertex3d(x, y-1, z-1);
                }
                break;
            case BACK:
                {
                    GL11.glTexCoord2d(2/3d, 0.5);
                    GL11.glVertex3d(x+1, y, z);
                    GL11.glTexCoord2d(1, 0.5);
                    GL11.glVertex3d(x, y, z);
                    GL11.glTexCoord2d(1, 1);
                    GL11.glVertex3d(x, y, z-1);
                    GL11.glTexCoord2d(2/3d, 1);
                    GL11.glVertex3d(x+1, y, z-1);
                }
                break;
            case RIGHT:
                {
                    GL11.glTexCoord2d(1/3d, 0.5);
                    GL11.glVertex3d(x+1, y-1, z);
                    GL11.glTexCoord2d(2/3d, 0.5);
                    GL11.glVertex3d(x+1, y, z);
                    GL11.glTexCoord2d(2/3d, 1);
                    GL11.glVertex3d(x+1, y, z-1);
                    GL11.glTexCoord2d(1/3d, 1);
                    GL11.glVertex3d(x+1, y-1, z-1);
                }
                break;
            case LEFT:
                {
                    GL11.glTexCoord2d(1/3d, 0);
                    GL11.glVertex3d(x, y, z);
                    GL11.glTexCoord2d(2/3d, 0);
                    GL11.glVertex3d(x, y-1, z);
                    GL11.glTexCoord2d(2/3d, 0.5);
                    GL11.glVertex3d(x, y-1, z-1);
                    GL11.glTexCoord2d(1/3d, 0.5);
                    GL11.glVertex3d(x, y, z-1);
                }
                break;
            default:
                throw new AssertionError(facing.name());
        }
        GL11.glEnd();
        if(plot.owner==plot.world.localPlayer){
            GL11.glTranslated(0, 0, z);
            if(plot.type.skyscraperFloorType!=null){
                int totalFill = 0;
                int totalCapacity = 0;
                for(Plot aplot : plot.skyscraper.getAllPlots()){
                    totalCapacity+=aplot.getMaximumCivilianCapacity();
                    totalFill+=aplot.civilians.size()+aplot.workers.size();
                }
                Renderer3D.drawText(x, -y+0.2, x+1, -y+0.4, totalFill+"/"+totalCapacity);
            }
            Renderer3D.drawText(x, -y+0.4, x+1, -y+0.6, plot.civiliansPresent.size()+"/"+plot.civilians.size());
            Renderer3D.drawText(x, -y+0.6, x+1, -y+0.8, plot.workersPresent.size()+"/"+plot.workers.size());
            Renderer3D.drawText(x, -y+0.8, x+1, -y+1, plot.civilians.size()+plot.workers.size()+"/"+plot.getMaximumCivilianCapacity());
            GL11.glTranslated(0, 0, -z);
        }
    }
    @Override
    public String[] getPaths(PlotType plot, int levels, String textureFolder){
        return new String[]{levels+":/textures/plots/"+textureFolder+"/level <LEVEL>/frame <FRAME>.png"};
    }
}
