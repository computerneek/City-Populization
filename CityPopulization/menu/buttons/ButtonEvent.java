package CityPopulization.menu.buttons;
import CityPopulization.world.civilian.WorkerTask;
import CityPopulization.world.plot.Plot;
public class ButtonEvent {
    private String type;
    private WorkerTask task;
    private Plot plot;
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
                task.targetPlot.task = task;
                task.owner.getWorkerTaskManager().addTask(task);
                break;
            default:
                throw new AssertionError(type);
        }
    }
}
