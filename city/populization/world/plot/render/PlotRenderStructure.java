package city.populization.world.plot.render;
import city.populization.render.ResourceLocation;
import city.populization.world.Chunk;
import city.populization.world.plot.PlotPos;
import city.populization.world.World;
import org.lwjgl.opengl.GL11;
public class PlotRenderStructure extends PlotRender{
    private final ResourceLocation texture;
    private final ResourceLocation[] textures;
    public final int levels;
    public PlotRenderStructure(String path){
        texture = ResourceLocation.get(ResourceLocation.Type.PLOT, path+"/texture.png");
        textures = null;
        levels = 0;
    }
    public PlotRenderStructure(String path, int levels) {
        texture = null;
        textures = new ResourceLocation[levels];
        this.levels = levels;
        for(int level = 0; level<levels; level++){
            String levelPath = path+"/level"+(level+1);
            textures[level] = ResourceLocation.get(ResourceLocation.Type.PLOT, levelPath+"/texture.png");
        }
    }
    @Override
    public void render(Chunk world, PlotPos pos) {
        ResourceLocation texture = this.texture==null?textures[world.getLevel(pos)]:this.texture;
        texture.bind();
        GL11.glBegin(GL11.GL_QUADS);
        texture.vertex(0, 0);
        GL11.glVertex3f(0, 1, 0);
        texture.vertex(1, 0);
        GL11.glVertex3f(1, 1, 0);
        texture.vertex(1, 1);
        GL11.glVertex3f(1, 0, 0);
        texture.vertex(0, 1);
        GL11.glVertex3f(0, 0, 0);
        GL11.glEnd();
    }
}
