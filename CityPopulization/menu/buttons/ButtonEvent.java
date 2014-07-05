package CityPopulization.menu.buttons;
import CityPopulization.Core;
import CityPopulization.menu.MenuAirportSchedule;
import CityPopulization.world.civilian.WorkerTask;
import CityPopulization.world.plot.Plot;
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
                throw new AssertionError(type);
        }
    }
}
