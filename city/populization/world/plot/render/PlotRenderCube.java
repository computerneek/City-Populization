package city.populization.world.plot.render;
import city.populization.render.ResourceLocation;
import city.populization.world.Chunk;
import city.populization.world.plot.PlotPos;
import org.lwjgl.opengl.GL11;

public class PlotRenderCube extends PlotRender{
    private ResourceLocation top, sides;
    public PlotRenderCube(String path) {
        if(path.endsWith(".png")){
            top = sides = ResourceLocation.get(ResourceLocation.Type.PLOT, path);
        }else{
            top = ResourceLocation.get(ResourceLocation.Type.PLOT, path+"\\top.png");
            sides = ResourceLocation.get(ResourceLocation.Type.PLOT, path+"\\sides.png");
        }
    }
    @Override
    public void render(Chunk world, PlotPos pos) {
        //Rendering is so small a task we don't need to worry about efficiency.  If it starts lagging down at some point, maybe I'll change that.
        top.bind();
        GL11.glBegin(GL11.GL_QUADS);
        top.vertex(0, 0);
        GL11.glVertex3d(0, 1, 0);
        top.vertex(1, 0);
        GL11.glVertex3d(1, 1, 0);
        top.vertex(1, 1);
        GL11.glVertex3d(1, 0, 0);
        top.vertex(0, 1);
        GL11.glVertex3d(0, 0, 0);
        sides.bind();//This call won't do anything if we call it.  It uses the same texture as 'top', so it's already bound.
        //SOUTH side
        sides.vertex(0, 0);
        GL11.glVertex3f(0, 0, 0);
        sides.vertex(1, 0);
        GL11.glVertex3f(1, 0, 0);
        sides.vertex(1, 1);
        GL11.glVertex3f(1, 0, -1);
        sides.vertex(0, 1);
        GL11.glVertex3f(0, 0, -1);
        //WEST side
        sides.vertex(0, 0);
        GL11.glVertex3f(1, 0, 0);
        sides.vertex(1, 0);
        GL11.glVertex3f(1, 1, 0);
        sides.vertex(1, 1);
        GL11.glVertex3f(1, 1, -1);
        sides.vertex(0, 1);
        GL11.glVertex3f(1, 0, -1);
        //NORTH side
        sides.vertex(0, 0);
        GL11.glVertex3f(1, 1, 0);
        sides.vertex(1, 0);
        GL11.glVertex3f(0, 1, 0);
        sides.vertex(1, 1);
        GL11.glVertex3f(0, 1, -1);
        sides.vertex(0, 1);
        GL11.glVertex3f(1, 1, -1);
        //EAST side
        sides.vertex(0, 0);
        GL11.glVertex3f(0, 1, 0);
        sides.vertex(1, 0);
        GL11.glVertex3f(0, 0, 0);
        sides.vertex(1, 1);
        GL11.glVertex3f(0, 0, -1);
        sides.vertex(0, 1);
        GL11.glVertex3f(0, 1, -1);
        GL11.glEnd();
    }
}
