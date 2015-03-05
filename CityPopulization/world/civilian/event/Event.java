package CityPopulization.world.civilian.event;
import CityPopulization.Core;
import CityPopulization.render.Side;
import CityPopulization.world.civilian.Civilian;
import CityPopulization.world.civilian.Path;
import CityPopulization.world.civilian.WorkerTaskSegmentSet;
import CityPopulization.world.plot.PlotType;
import CityPopulization.world.resource.ResourceList;
import simplelibrary.config2.Config;
public abstract class Event {
    public abstract boolean isComplete();
    public abstract void start(Civilian worker);
    public abstract void work(Civilian aThis);
    public abstract Config save();
    public abstract boolean validate();
    public static Event load(Config config){
        Event event = null;
        int which;
        switch((String)config.get("type")){
            case "load":
                event = new EventLoad(ResourceList.load((Config)config.get("resources")));
                ((EventLoad)event).complete = config.get("complete");
                break;
            case "path":
                event = new EventPath(Path.load((Config)config.get("path")));
                break;
            case "plot set":
                which = config.hasProperty("owner")?(int)config.get("owner"):-2;
                event = new EventPlotSet(Core.loadingWorld.getPlot((int)config.get("x"), (int)config.get("y"), (int)config.get("z")), PlotType.valueOf((String)config.get("plottype")), (int)config.get("level"), Side.valueOf((String)config.get("front")), which==-2?null:(which==-1?Core.loadingWorld.localPlayer:Core.loadingWorld.otherPlayers.get(which)));
                ((EventPlotSet)event).started = config.get("started");
                break;
            case "satisfy":
                if(config.hasProperty("index")){
                    event = new EventSatisfy(Core.loadingWorld.getPlot((int)config.get("x"), (int)config.get("y"), (int)config.get("z")).task.segments.get((int)config.get("index")));
                }else{
                    event = new EventSatisfy(((WorkerTaskSegmentSet)Core.loadingWorld.getPlot((int)config.get("x"), (int)config.get("y"), (int)config.get("z")).task.segments.get((int)config.get("index1"))).segments.get((int)config.get("index2")));
                }
                ((EventSatisfy)event).started = config.get("started");
                break;
            case "train":
                event = new EventTrainWorker();
                ((EventTrainWorker)event).started = config.get("started");
                break;
            case "unload":
                event = new EventUnload();
                ((EventUnload)event).complete = config.get("complete");
                break;
            case "wait":
                event = new EventWait((int)config.get("ticks"));
                ((EventWait)event).time = config.get("time");
                break;
            default:
                throw new AssertionError((String)config.get("type"));
        }
        return event;
    }
}
