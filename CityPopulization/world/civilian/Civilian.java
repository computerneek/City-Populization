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
import org.lwjgl.opengl.GL11;
import simplelibrary.config2.Config;
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
    public Player player;
    private int tick;
    private int frameCap;
    public EventSequence eventSequence;
    public Event currentEvent;
    public int timer = 12000;
    public ResourceList resources = new ResourceList();
    {
        this.frameCap = findFrameCap();
    }
    public void assign(EventSequence sequence){
        this.eventSequence = sequence;
        if(!(homePlot.world instanceof StoryMission)||((StoryMission)homePlot.world).workersExpire()){
            timer-=240;
        }
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
        if(currentEvent!=null){
            currentEvent.work(this);
        }
        if(currentEvent==null||currentEvent.isComplete()&&path==null&&dest==null){
            currentEvent = eventSequence==null?null:eventSequence.nextEvent();
            if(eventSequence==null||eventSequence.isComplete()){
                eventSequence = null;
            }
            if(currentEvent!=null){
                currentEvent.start(this);
            }
        }
        tick++;
        if(dest!=null||path!=null){
            pathingUpdate();
        }
        if(Math.round(x)==homePlot.x&&Math.round(y)==homePlot.y&&Math.round(z)==homePlot.z){
            updateOnHomePlot();
        }
    }
    public void pathingUpdate(){
        if(dest==null&&path!=null){
            if(path.isComplete()){
                path=null;
            }else{
                dest=path.next();
            }
        }
        Plot plot = homePlot.world.generatePlot(Math.round(x), Math.round(y), Math.round(z));
        double traveledThisTick = 1;
        if(plot.getType()==PlotType.Road||plot.getType()==PlotType.Elevator||plot.getSkyscraperPlots().length>0){
            traveledThisTick*=plot.getLevel()+1;
        }
        if(plot.task!=null){
            traveledThisTick/=2;
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
        if(eventSequence!=null||currentEvent!=null){
            return;
        }
        if(path==null&&dest==null){
            arriveHome();
        }
    }
    private void move(){
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
        if(this instanceof Worker){
            homePlot.workersPresent.add((Worker)this);
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
        String textureFolder = (this instanceof Worker)?"worker":"civilian";
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
    }
    public Config save(){
        Config config = Config.newConfig();
        config.set("worker", this instanceof Worker);
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
        if(eventSequence!=null){
            config.set("events", eventSequence.save());
        }
        if(currentEvent!=null){
            config.set("event", currentEvent.save());
        }
        config.set("timer", timer);
        config.set("resources", resources.save());
        return config;
    }
    public static Civilian load(Config config){
        Civilian civilian = ((boolean)config.get("worker"))?new Worker():new Civilian();
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
        if(config.hasProperty("events")){
            civilian.eventSequence = EventSequence.load((Config)config.get("events"));
        }
        if(config.hasProperty("event")){
            civilian.currentEvent = Event.load((Config)config.get("event"));
        }
        civilian.timer = config.get("timer");
        civilian.resources = ResourceList.load((Config)config.get("resources"));
        return civilian;
    }
}
