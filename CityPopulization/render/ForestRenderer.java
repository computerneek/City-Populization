package CityPopulization.render;
import CityPopulization.world.plot.Plot;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
public class ForestRenderer implements PlotRenderer{
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
        int texture = ImageStash.instance.getTexture(path);
        ImageStash.instance.bindTexture(texture);
        GL11.glBegin(GL11.GL_QUADS);
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
//        for(int i = 0; i<3; i++){
//            for(int j = 0; j<3; j++){
//                Render a tree at i/4, j/4 within the plot
//            }
//        }
        GL11.glEnd();
    }
    @Override
    public String[] getPaths(int levels, String textureFolder){
        return new String[]{levels+":/textures/plots/"+textureFolder+"/level <LEVEL>/frame <FRAME>.png"};
    }
    
}
