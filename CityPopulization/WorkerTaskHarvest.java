package CityPopulization;
public class WorkerTaskHarvest extends WorkerTask{
    public WorkerTaskHarvest(Plot plot){
        super("Harvest", plot, 1, 50*plot.getLevel(), 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
    }
    public void work(){
        while(workers.size()>1){
            workers.remove(1);
        }
        progress++;
        if(progress>=(100*plot.getLevel())){
            ((Worker)workers.get(0)).resources+=plot.getLevel()*plot.getLevel();
            progress = 0;
        }
    }
    public void forceCancel(){
        super.forceCancel();
    }
    public void tick(){
    }
    public void assignWorker(Civillian worker){
        workers.add(worker);
        worker.workingTime = 0;
        worker.status = Worker.Status.working;
    }
}
