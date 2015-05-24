package CityPopulization.menu.buttons;
import CityPopulization.Core;
import CityPopulization.menu.MenuAirportSchedule;
import CityPopulization.world.civilian.WorkerTask;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.SkyScraper;
import CityPopulization.world.resource.Resource;
import CityPopulization.world.resource.ResourceList;
import java.util.ArrayList;
public class ButtonEvent {
    private String type;
    private WorkerTask task;
    private Plot plot;
    public String getInfo(){
        switch(type){
            case "Task":
                return "Costs $"+task.cash+" and "+task.cost.toString()+"; returns "+task.revenue.toString()+".";
            case "Airport":
                return "";
            case "Cancel Task":
                return "Refunds $"+plot.task.cash+" instantly";
            default:
                if(type.startsWith("Sell_")){
                    return "Sells "+Math.min(500, getMinResourceQuantity(Resource.valueOf(type.substring(5))))+" "+type.substring(5)+" for $"+Resource.valueOf(type.substring(5)).getCost(Math.min(500, getMinResourceQuantity(Resource.valueOf(type.substring(5)))));
                }
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public int getMinResourceQuantity(Resource r){
        if(plot==null||plot.owner==null){
            return 0;
        }
        ResourceList lst = new ResourceList();
        for(Plot plot : this.plot.owner.resourceStructures){
            lst.addAll(plot.resources);
        }
        return lst.get(r);
    }
    public ButtonEvent setType(String type){
        this.type = type;
        return this;
    }
    public ButtonEvent setTask(WorkerTask task){
        this.task = task;
        return this;
    }
    public ButtonEvent setPlot(Plot plot){
        this.plot = plot;
        return this;
    }
    public void onClicked(){
        switch(type){
            case "Task":
                if(task.owner.cash>=task.cash){
                    task.targetPlot.task = task;
                    task.start();
                }
                break;
            case "Airport":
                Core.gui.open(new MenuAirportSchedule(Core.gui, Core.gui.menu, plot.terminal.schedule));
                break;
            case "Cancel Task":
                task = plot.task;
                if(task!=null&&!task.started){
                    plot.task = null;
                    task.segments.clear();
                }
                task.owner.cash+=task.cash;
                break;
            default:
                if(type.startsWith("Skyscraper")){
                    String[] spl = type.split(" ");
                    new SkyScraper(plot, Integer.parseInt(spl[1]), Integer.parseInt(spl[2]));
                    break;
                }else if(type.startsWith("Sell_")){
                    String str = "Sells "+Math.min(500, getMinResourceQuantity(Resource.valueOf(type.substring(5))))+" "+type.substring(5)+" for $"+Resource.valueOf(type.substring(5)).getCost(Math.min(500, getMinResourceQuantity(Resource.valueOf(type.substring(5)))));
                    plot.task = new WorkerTask().setCost(new ResourceList(Resource.valueOf(type.substring(5)), Math.min(500, getMinResourceQuantity(Resource.valueOf(type.substring(5)))))).setRevenue(new ResourceList()).setCash(0).setPlot(plot).setOwner(plot.owner);
                    plot.task.cost.remove(Resource.Tools, 1);
                    plot.task.revenue.remove(Resource.Tools, 1);
                    plot.task.configure();
                    plot.task.segments.remove(1);
                    break;
                }
                throw new AssertionError(type);
        }
    }
    public static class Pair extends ButtonEvent{
        ArrayList<ButtonEvent> events = new ArrayList<>();
        public Pair(){}
        public Pair addEvent(ButtonEvent event){
            events.add(event);
            return this;
        }
        public void onClicked(){
            for(ButtonEvent event : events){
                event.onClicked();
            }
        }
        public String getInfo(){
            return events.get(0).getInfo();
        }
    }
}
