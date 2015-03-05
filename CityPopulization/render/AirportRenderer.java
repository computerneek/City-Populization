package CityPopulization.render;
import CityPopulization.world.aircraft.Terminal;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
import simplelibrary.opengl.Renderer3D;
public class AirportRenderer implements PlotRenderer{
    public static final int ENTRANCE = 1;
    public static final int TERMINAL = 2;
    public static final int JETWAY = 3;
    public static final int RUNWAY = 4;
    private int type;
    public AirportRenderer(int type){
        this.type = type;
    }
    @Override
    public void render(Plot plot, String textureFolder){
        switch(type){
            case ENTRANCE:
                renderEntrance(plot, textureFolder);
                return;
            case TERMINAL:
                renderTerminal(plot, textureFolder);
                return;
            case JETWAY:
                renderJetway(plot, textureFolder);
                return;
            case RUNWAY:
                renderRunway(plot, textureFolder);
                return;
            default:
                throw new AssertionError(type);
        }
    }
    private void renderEntrance(Plot plot, String textureFolder){
        int x = plot.x;
        int y = -plot.y;
        int z = plot.z;
        int levelCap = plot.getType().getMaximumLevel();
        int level = plot.getLevel()%levelCap;
        PlotType leftPlot = plot.getLeftPlot().getType();
        PlotType rightPlot = plot.getRightPlot().getType();
        boolean canLeft = leftPlot==PlotType.AirportTerminal;
        boolean canRight = rightPlot==PlotType.AirportTerminal;
        String specification = canLeft?(canRight?"dual":"left"):(canRight?"right":"lone");
        int frameCap = plot.getType().getFrameCap(level+1, plot.getType().getTextureIndex("1:/textures/plots/"+textureFolder+"/level <LEVEL>/frame <FRAME>/"+specification+".png"));
        int frame = plot.getFrameNumber()%frameCap;
        String path = "/textures/plots/"+textureFolder+"/level "+(level+1)+"/frame "+(frame+1)+"/"+specification+".png";
        render(x, y, z, path, plot.front);
        GL11.glTranslated(0, 0, z);
        Renderer3D.drawText(x, -y+0.8, x+1, -y+1, plot.civilians.size()+"-"+plot.workers.size());
        GL11.glTranslated(0, 0, -z);
    }
    private void renderTerminal(Plot plot, String textureFolder){
        int x = plot.x;
        int y = -plot.y;
        int z = plot.z;
        int levelCap = plot.getType().getMaximumLevel();
        int level = plot.getLevel()%levelCap;
        PlotType leftPlot = plot.getLeftPlot().getType();
        PlotType rightPlot = plot.getRightPlot().getType();
        boolean canLeft = leftPlot==PlotType.AirportTerminal||leftPlot==PlotType.AirportEntrance;
        boolean canRight = rightPlot==PlotType.AirportTerminal||rightPlot==PlotType.AirportEntrance;
        String sideConnection = canLeft?(canRight?"dual":"left"):(canRight?"right":"lone");
        Terminal term = plot.terminal;
        String state = term.state==Terminal.UNLOADING?"unloading":(term.state==Terminal.LOADING?"loading":(term.state==Terminal.IDLE?"idle":(term.occupied>0?"pending":"empty")));
        int frameCap = plot.getType().getFrameCap(level+1, plot.getType().getTextureIndex("1:/textures/plots/"+textureFolder+"/level <LEVEL>/frame <FRAME>/"+sideConnection+" "+state+".png"));
        int frame = plot.getFrameNumber()%frameCap;
        String path = "/textures/plots/"+textureFolder+"/level "+(level+1)+"/frame "+(frame+1)+"/"+sideConnection+" "+state+".png";
        render(x, y, z, path, plot.front);
    }
    private void renderJetway(Plot plot, String textureFolder){
        int x = plot.x;
        int y = -plot.y;
        int z = plot.z;
        int levelCap = plot.getType().getMaximumLevel();
        int level = plot.getLevel()%levelCap;
        int frameCap = plot.getType().getFrameCap(level+1, plot.getType().getTextureIndex("1:/textures/plots/"+textureFolder+"/level <LEVEL>/frame <FRAME>.png"));
        int frame = plot.getFrameNumber()%frameCap;
        String path = "/textures/plots/"+textureFolder+"/level "+(level+1)+"/frame "+(frame+1)+".png";
        render(x, y, z, path, plot.front);
        PlotType leftPlot = plot.getLeftPlot().getType();
        PlotType rightPlot = plot.getRightPlot().getType();
        PlotType frontPlot = plot.getFrontPlot().getType();
        PlotType backPlot = plot.getBackPlot().getType();
        boolean canLeft = leftPlot==PlotType.AirportTerminal||leftPlot==PlotType.AirportJetway||leftPlot==PlotType.AirportRunway;
        boolean canRight = rightPlot==PlotType.AirportTerminal||rightPlot==PlotType.AirportJetway||rightPlot==PlotType.AirportRunway;
        boolean canForward = frontPlot==PlotType.AirportTerminal||frontPlot==PlotType.AirportJetway||frontPlot==PlotType.AirportRunway;
        boolean canBackward = backPlot==PlotType.AirportTerminal||backPlot==PlotType.AirportJetway||backPlot==PlotType.AirportRunway;
        if(canForward){
            renderPath(x, y, z, path, plot.front);
        }
        if(canRight){
            renderPath(x, y, z, path, plot.front.right());
        }
        if(canLeft){
            renderPath(x, y, z, path, plot.front.left());
        }
        if(canBackward){
            renderPath(x, y, z, path, plot.front.reverse());
        }
    }
    private void renderRunway(Plot plot, String textureFolder){
        int x = plot.x;
        int y = -plot.y;
        int z = plot.z;
        int levelCap = plot.getType().getMaximumLevel();
        int level = plot.getLevel()%levelCap;
        int frameCap = plot.getType().getFrameCap(level+1, plot.getType().getTextureIndex("1:/textures/plots/"+textureFolder+"/level <LEVEL>/frame <FRAME>.png"));
        int frame = plot.getFrameNumber()%frameCap;
        String path = "/textures/plots/"+textureFolder+"/level "+(level+1)+"/frame "+(frame+1)+".png";
        render(x, y, z, path, plot.front);
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
        GL11.glBegin(GL11.GL_QUADS);
        switch(facing){
            case FRONT:
                {
                    GL11.glTexCoord2d(0.5, 0);
                    GL11.glVertex3d(x, y, z);
                    GL11.glTexCoord2d(1, 0);
                    GL11.glVertex3d(x+1, y, z);
                    GL11.glTexCoord2d(1, 0.5);
                    GL11.glVertex3d(x+1, y-1, z);
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x, y-1, z);
                }
                break;
            case BACK:
                {
                    GL11.glTexCoord2d(1, 0.5);
                    GL11.glVertex3d(x, y, z);
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x+1, y, z);
                    GL11.glTexCoord2d(0.5, 0);
                    GL11.glVertex3d(x+1, y-1, z);
                    GL11.glTexCoord2d(1, 0);
                    GL11.glVertex3d(x, y-1, z);
                }
                break;
            case LEFT:
                {
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x, y, z);
                    GL11.glTexCoord2d(0.5, 0);
                    GL11.glVertex3d(x+1, y, z);
                    GL11.glTexCoord2d(1, 0);
                    GL11.glVertex3d(x+1, y-1, z);
                    GL11.glTexCoord2d(1, 0.5);
                    GL11.glVertex3d(x, y-1, z);
                }
                break;
            case RIGHT:
                {
                    GL11.glTexCoord2d(1, 0);
                    GL11.glVertex3d(x, y, z);
                    GL11.glTexCoord2d(1, 0.5);
                    GL11.glVertex3d(x+1, y, z);
                    GL11.glTexCoord2d(0.5, 0.5);
                    GL11.glVertex3d(x+1, y-1, z);
                    GL11.glTexCoord2d(0.5, 0);
                    GL11.glVertex3d(x, y-1, z);
                }
                break;
            default:
                throw new AssertionError(facing.name());
        }
        GL11.glEnd();
    }
    @Override
    public String[] getPaths(PlotType plot, int levels, String textureFolder){
        ArrayList<String> lst = new ArrayList<>();
        String tex = levels+":/textures/plots/"+textureFolder+"/level <LEVEL>/frame <FRAME>/<SPEC>.png";
        String[] sides = {"dual", "left", "right", "lone"};
        String[] states = {"unloading", "loading", "idle", "pending", "empty"};
        switch(type){
            case ENTRANCE:
                for(String side : sides){
                    lst.add(spec(tex, side));
                }
                break;
            case TERMINAL:
                for(String side : sides){
                    for(String state : states){
                        lst.add(spec(tex, side+" "+state));
                    }
                }
                break;
            case JETWAY:
            case RUNWAY:
                lst.add(levels+":/textures/plots/"+textureFolder+"/level <LEVEL>/frame <FRAME>.png");
                break;
            default:
                throw new AssertionError(type);
        }
        return lst.toArray(new String[lst.size()]);
    }
    private String spec(String tex, String spec){
        return tex.replaceAll("<SPEC>", spec);
    }
}
