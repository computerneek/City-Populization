package CityPopulization.world.civilian;
import CityPopulization.world.player.Player;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import java.io.IOException;
import java.io.InputStream;
import org.lwjgl.opengl.GL11;
import simplelibrary.opengl.ImageStash;
public class Civilian{
    public Plot homePlot;
    public float x;
    public float y;
    public float z;
    public int[] dest;
    public double speed = 0.025;
    public Path path;
    public double dist;
    private Player player;
    private int tick;
    private int frameCap;
    {
        this.frameCap = findFrameCap();
    }
    private int findFrameCap(){
        int frameCap = 0;
        String textureFolder = (this instanceof Worker)?"worker":"civilian";
        for(int i = 1; frameCap==0; i++){
            String path = "/textures/"+textureFolder+"/frame "+i+".png";
            try(InputStream in = PlotType.class.getResourceAsStream(path)){
                if(in==null){
                    frameCap = i-1;
                }
            }catch(IOException ex){}
        }
        return frameCap;
    }
    public void update(){
        tick++;
        if(dest!=null||path!=null){
            pathingUpdate();
        }
        if(Math.round(x)==homePlot.x&&Math.round(y)==homePlot.y&&Math.round(z)==homePlot.z){
            updateOnHomePlot();
        }
    }
    public void pathingUpdate(){
        Plot plot = homePlot.world.generatePlot(Math.round(x), Math.round(y), Math.round(z));
        double traveledThisTick = 1;
        if(plot.getType()==PlotType.Road||plot.getType()==PlotType.Road){
            traveledThisTick*=plot.getLevel()+1;
        }
        if(plot.task!=null){
            traveledThisTick/=2;
        }
        dist+=traveledThisTick;
        while(dist>=1F){
            if(dest==null&&path!=null){
                dest = path.next();
                if(path.isComplete()){
                    path = null;
                }
            }
            move();
            dist--;
        }
    }
    public void updateOnHomePlot(){
        if(path==null&&dest==null){
            arriveHome();
        }
    }
    private void move(){
        float xDist = dest[0]-x;
        float yDist = dest[1]-y;
        float zDist = dest[2]-z;
        double dist = Math.sqrt(xDist*xDist+yDist*yDist+zDist*zDist);
        if(dist<=speed){
            x = dest[0];
            y = dest[1];
            z = dest[2];
            dest = null;
            return;
        }
        double ratio = speed/dist;
        x+=xDist*ratio;
        y+=yDist*ratio;
        z+=zDist*ratio;
    }
    private void arriveHome(){
        if(this instanceof Worker){
            homePlot.workersPresent.add((Worker)this);
        }else{
            homePlot.civiliansPresent.add(this);
        }
        homePlot.world.civilians.remove(this);
    }
    public void render(Player localPlayer){
        player = homePlot.owner;
        boolean canPlayerSeePlane = player==localPlayer;
        Plot currentPlot = player.world.getPlot(Math.round(x), Math.round(y), Math.round(z));
        if(currentPlot!=null&&localPlayer==currentPlot.getOwner()){
            canPlayerSeePlane = true;
        }
        if(!canPlayerSeePlane){
            return;
        }
        String textureFolder = (this instanceof Worker)?"worker":"civilian";
        GL11.glTranslatef(x+0.5f, -y-0.5f, z-0.98f);
        ImageStash.instance.bindTexture(ImageStash.instance.getTexture("/textures/"+textureFolder+"/frame "+(tick%frameCap+1)+".png"));
        GL11.glColor3f(1, 1, 1);
        render();
        GL11.glTranslatef(-x-0.5f, y+0.5f, -z+0.98f);
    }
    private void render(){
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex3d(-0.3, -0.3, 0);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex3d(0.3, -0.3, 0);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex3d(0.3, 0.3, 0);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex3d(-0.3, 0.3, 0);
        GL11.glEnd();
    }
}
