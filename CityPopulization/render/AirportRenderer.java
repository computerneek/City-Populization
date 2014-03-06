package CityPopulization.render;
import CityPopulization.world.aircraft.Terminal;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
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
        int y = plot.y;
        int z = plot.z;
        int levelCap = plot.getType().getMaximumLevel();
        int level = plot.getLevel()%levelCap;
        int frameCap = plot.getType().getFrameCap(level+1);
        int frame = plot.getFrameNumber()%frameCap;
        PlotType leftPlot = plot.getLeftPlot().getType();
        PlotType rightPlot = plot.getRightPlot().getType();
        boolean canLeft = leftPlot==PlotType.AirportTerminal;
        boolean canRight = rightPlot==PlotType.AirportTerminal;
        String specification = canLeft?(canRight?"dual":"left"):(canRight?"right":"lone");
        String path = "/textures/plots/"+textureFolder+"/level "+(level+1)+"/frame "+(frame+1)+"/"+specification+".png";
        render(x, y, z, path, plot.front);
    }
    private void renderTerminal(Plot plot, String textureFolder){
        int x = plot.x;
        int y = plot.y;
        int z = plot.z;
        int levelCap = plot.getType().getMaximumLevel();
        int level = plot.getLevel()%levelCap;
        int frameCap = plot.getType().getFrameCap(level+1);
        int frame = plot.getFrameNumber()%frameCap;
        PlotType leftPlot = plot.getLeftPlot().getType();
        PlotType rightPlot = plot.getRightPlot().getType();
        boolean canLeft = leftPlot==PlotType.AirportTerminal||leftPlot==PlotType.AirportEntrance;
        boolean canRight = rightPlot==PlotType.AirportTerminal||rightPlot==PlotType.AirportEntrance;
        String sideConnection = canLeft?(canRight?"dual":"left"):(canRight?"right":"lone");
        Terminal term = plot.terminal;
        String state = term.state==Terminal.UNLOADING?"unloading":(term.state==Terminal.LOADING?"loading":(term.state==Terminal.IDLE?"idle":(term.occupied>0?"pending":"empty")));
        String path = "/textures/plots/"+textureFolder+"/level "+(level+1)+"/frame "+(frame+1)+"/"+sideConnection+" "+state+".png";
        render(x, y, z, path, plot.front);
    }
    private void renderJetway(Plot plot, String textureFolder){
        int x = plot.x;
        int y = plot.y;
        int z = plot.z;
        int levelCap = plot.getType().getMaximumLevel();
        int level = plot.getLevel()%levelCap;
        int frameCap = plot.getType().getFrameCap(level+1);
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
        int y = plot.y;
        int z = plot.z;
        int levelCap = plot.getType().getMaximumLevel();
        int level = plot.getLevel()%levelCap;
        int frameCap = plot.getType().getFrameCap(level+1);
        int frame = plot.getFrameNumber()%frameCap;
        String path = "/textures/plots/"+textureFolder+"/level "+(level+1)+"/frame "+(frame+1)+".png";
        render(x, y, z, path, plot.front);
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
