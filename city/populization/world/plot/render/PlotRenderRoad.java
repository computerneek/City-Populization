package city.populization.world.plot.render;
import city.populization.render.ResourceLocation;
import city.populization.world.Chunk;
import city.populization.world.Direction;
import city.populization.world.plot.PlotPos;
import city.populization.world.World;
import city.populization.world.plot.type.PlotRoad;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import simplelibrary.Queue;
public class PlotRenderRoad extends PlotRender{
    private ResourceLocation[] island, end, straight, curve, t, cross;
    public final int levels;
    public PlotRenderRoad(String path, int levels) {
        this.levels = levels;
        island = new ResourceLocation[levels];
        end = new ResourceLocation[levels];
        straight = new ResourceLocation[levels];
        curve = new ResourceLocation[levels];
        t = new ResourceLocation[levels];
        cross = new ResourceLocation[levels];
        for(int level = 0; level<levels; level++){
            String levelPath = path+"/level"+(level+1);
            island[level] = ResourceLocation.get(ResourceLocation.Type.PLOT, levelPath+"/island.png");
            end[level] = ResourceLocation.get(ResourceLocation.Type.PLOT, levelPath+"/end.png");
            straight[level] = ResourceLocation.get(ResourceLocation.Type.PLOT, levelPath+"/straight.png");
            curve[level] = ResourceLocation.get(ResourceLocation.Type.PLOT, levelPath+"/curve.png");
            t[level] = ResourceLocation.get(ResourceLocation.Type.PLOT, levelPath+"/T.png");
            cross[level] = ResourceLocation.get(ResourceLocation.Type.PLOT, levelPath+"/cross.png");
        }
    }
    @Override
    public Direction getFront(Chunk world, PlotPos pos, Direction heading) {
        ArrayList<Direction> dirs = PlotRoad.getConnections(world.zone.world, pos);
        return dirs.isEmpty()?heading:dirs.get(0);
    }
    @Override
    public void render(Chunk world, PlotPos pos) {
        ArrayList<Direction> dirs = PlotRoad.getConnections(world.zone.world, pos);
        int level = world.getLevel(pos);
        ResourceLocation loc = null;
        if(dirs.isEmpty()){
            loc = island[level];
        }else if(dirs.size()==1){
            loc = end[level];
        }else if(dirs.size()==2){
            if(dirs.get(0).reverse()==dirs.get(1)){
                loc = straight[level];
            }else{
                loc = curve[level];
            }
        }else if(dirs.size()==3){
            loc = t[level];
        }else{
            loc = cross[level];
        }
        loc.bind();
        GL11.glBegin(GL11.GL_QUADS);
        loc.vertex(0, 0);
        GL11.glVertex3f(0, 1, 0);
        loc.vertex(1, 0);
        GL11.glVertex3f(1, 1, 0);
        loc.vertex(1, 1);
        GL11.glVertex3f(1, 0, 0);
        loc.vertex(0, 1);
        GL11.glVertex3f(0, 0, 0);
        GL11.glEnd();
    }
}
