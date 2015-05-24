package CityPopulization.world.civilian;
import CityPopulization.Core;
import CityPopulization.world.civilian.event.Event;
import CityPopulization.world.civilian.event.EventSequence;
import CityPopulization.world.player.Player;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.resource.ResourceList;
import CityPopulization.world.story.StoryMission;
import java.io.IOException;
import java.io.InputStream;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import simplelibrary.config2.Config;
import simplelibrary.opengl.ImageStash;
public class Civilian{
    public Plot homePlot;
    public float x;
    public float y;
    public float z;
    public int[] lastDest;
    public int[] dest;
    public double speed = 0.025;
    public Path path;
    public double dist;
    public Player player;
    private int tick;
    private int frameCap;
    public WorkerTask task;
    public WorkerTask.Subtask subtask;
    public int timer = 12000;
    public ResourceList resources = new ResourceList();
    public boolean worker;
    {
        this.frameCap = findFrameCap();
    }
    public void assign(WorkerTask task){
        this.task = task;
        if(!(homePlot.world instanceof StoryMission)||((StoryMission)homePlot.world).workersExpire()){
            timer-=240;
        }
    }
    private int findFrameCap(){
        int frameCap = 0;
        String textureFolder = (worker)?"worker":"civilian";
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
        if(task!=null){
            task.update(this);
        }
        tick++;
        if(dest!=null||path!=null){
            pathingUpdate();
        }
        if(Math.round(x)==homePlot.x&&Math.round(y)==homePlot.y&&Math.round(z)==homePlot.z){
            updateOnHomePlot();
        }else if(task==null&&subtask==null&&dest==null&&path==null){
            if(homePlot.workers.contains(this)||homePlot.civilians.contains(this)){
                path = Path.findPath(homePlot.world.getPlot(Math.round(x), Math.round(y), Math.round(z)), homePlot, worker);
            }else{
                homePlot = Path.findAirportEntrance(homePlot.world.getPlot(Math.round(x), Math.round(y), Math.round(z)), true);
            }
        }
    }
    public void pathingUpdate(){
        if(dest==null&&path!=null){
            if(path.isComplete()){
                path=null;
            }else{
                lastDest = dest;
                dest=path.next();
            }
        }
        Plot plot = homePlot.world.generatePlot(Math.round(x), Math.round(y), Math.round(z));
        double traveledThisTick = 1;
        if(plot.getType()==PlotType.Road||plot.getType()==PlotType.Elevator||plot.getSkyscraperPlots().length>0){
            traveledThisTick*=plot.getLevel()+1;
        }
        if(plot.task!=null){
            traveledThisTick/=(worker)?2:10;
        }
        dist+=traveledThisTick;
        while(dist>=1F&&dest!=null){
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
        if(task!=null&&subtask==null){
            task.assignOrDismiss(this);
            return;
        }
        if(path==null&&dest==null){
            arriveHome();
        }
    }
    private void move(){
        Plot to = homePlot.world.getPlot(dest[0], dest[1], dest[2]);
        Plot from = null;
        if(lastDest!=null){
            from = homePlot.world.getPlot(lastDest[0], lastDest[1], lastDest[2]);
        }
        if(!path.isComplete()&&!(to.getType()==PlotType.Road||to.getType()==PlotType.Elevator||to.skyscraper!=null)&&from!=to&&from!=null){
            path = null;
            if(from.getType()==PlotType.Road||from.getType()==PlotType.Elevator||from.skyscraper!=null){
                dest = lastDest;
                lastDest = null;
            }
        }
        float xDist = dest[0]-x;
        float yDist = dest[1]-y;
        float zDist = dest[2]-z;
        double dist = Math.sqrt(xDist*xDist+yDist*yDist+zDist*zDist);
        double mult = 1;
        if(player!=null){
            mult = player.world.difficulty.moveSpeedModifier;
        }
        if(dist<=speed*mult){
            x = dest[0];
            y = dest[1];
            z = dest[2];
            dest = null;
            return;
        }
        double ratio = (speed*mult)/dist;
        x+=xDist*ratio;
        y+=yDist*ratio;
        z+=zDist*ratio;
    }
    private void arriveHome(){
        if(worker){
            homePlot.workersPresent.add(this);
        }else{
            homePlot.civiliansPresent.add(this);
        }
        homePlot.world.civilians.remove(this);
        for(Player player : homePlot.world.otherPlayers){
            player.civilianRemoved(this);
        }
        homePlot.world.schedulePlotUpdate(homePlot);
    }
    public void render(Player localPlayer){
        player = homePlot.owner;
        boolean canPlayerSee = player==localPlayer;
        Plot currentPlot = Core.world.getPlot(Math.round(x), Math.round(y), Math.round(z));
        if(currentPlot!=null&&localPlayer==currentPlot.getOwner()){
            canPlayerSee = true;
        }
        if(!canPlayerSee){
            return;
        }
        String textureFolder = (worker)?"worker":"civilian";
        GL11.glTranslatef(x+0.5f, -y-0.5f, z+0.01f);
        ImageStash.instance.bindTexture(ImageStash.instance.getTexture("/textures/"+textureFolder+"/frame "+(tick%frameCap+1)+".png"));
        GL11.glColor4f(1, 1, 1, z!=localPlayer.getCameraZ()?0.2f:1);
        render();
        GL11.glTranslatef(-x-0.5f, y+0.5f, -z+0.01f);
    }
    private void render(){
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex3d(-0.3, 0.3, 0);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex3d(0.3, 0.3, 0);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex3d(0.3, -0.3, 0);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex3d(-0.3, -0.3, 0);
        GL11.glEnd();
        if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)&&Keyboard.isKeyDown(Keyboard.KEY_I)&&(path!=null||dest!=null)){
            GL11.glBegin(GL11.GL_LINES);
            if(dest!=null){
                GL11.glVertex3d(x, y, z);
                GL11.glVertex3d(dest[0], dest[1], dest[2]);
                path.draw(dest[0], dest[1], dest[2]);
            }else{
                path.draw(x, y, z);
            }
            GL11.glEnd();
        }
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("worker", worker);
        config.set("x", x);
        config.set("y", y);
        config.set("z", z);
        config.set("homex", homePlot.x);
        config.set("homey", homePlot.y);
        config.set("homez", homePlot.z);
        if(dest!=null){
            config.set("destx", dest[0]);
            config.set("desty", dest[1]);
            config.set("destz", dest[2]);
        }
        if(path!=null){
            config.set("path", path.save());
        }
        if(player!=null){
            config.set("player", player.world.otherPlayers.indexOf(player));
        }
        config.set("tick", tick);
        if(task!=null){
            config.set("taskX", task.targetPlot.x);
            config.set("taskY", task.targetPlot.y);
            config.set("taskZ", task.targetPlot.z);
            if(subtask!=null){
                config.set("subtask", subtask.save());
            }
        }
        config.set("timer", timer);
        config.set("resources", resources.save());
        return config;
    }
    public static Civilian load(Config config){
        Civilian civilian = ((boolean)config.get("worker"))?new Civilian().upgradeToWorker():new Civilian();
        civilian.x = config.get("x");
        civilian.y = config.get("y");
        civilian.z = config.get("z");
        civilian.homePlot = Core.loadingWorld.getPlot((int)config.get("homex"), (int)config.get("homey"), (int)config.get("homez"));
        if(config.hasProperty("destx")){
            civilian.dest = new int[]{config.get("destx"), config.get("desty"), config.get("destz")};
        }
        if(config.hasProperty("path")){
            civilian.path = Path.load((Config)config.get("path"));
        }
        if(config.hasProperty("player")){
            int which = config.get("player");
            civilian.player = which==-1?Core.loadingWorld.localPlayer:Core.loadingWorld.otherPlayers.get(which);
        }
        civilian.tick = config.get("tick");
        if(config.hasProperty("taskX")){
            civilian.task = civilian.homePlot.world.getPlot((int)config.get("taskX"), (int)config.get("taskY"), (int)config.get("taskZ")).task;
            if(config.hasProperty("subtask")){
                civilian.subtask = civilian.task.readSubtask((Config)config.get("subtask"));
            }
        }
        civilian.timer = config.get("timer");
        civilian.resources = ResourceList.load((Config)config.get("resources"));
        return civilian;
    }
    public Plot getCurrentPlot(){
        return homePlot.world.getPlot(Math.round(x), Math.round(y), Math.round(z));
    }
    public Civilian upgradeToWorker(){
        worker = true;
        speed *= 2;
        return this;
    }
}
