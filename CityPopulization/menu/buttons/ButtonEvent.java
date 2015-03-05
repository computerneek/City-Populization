package CityPopulization.menu.buttons;
import CityPopulization.Core;
import CityPopulization.menu.MenuAirportSchedule;
import CityPopulization.world.civilian.WorkerTask;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.SkyScraper;
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
        }
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
                    task.prepare();
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
                    return;
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
