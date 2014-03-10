package CityPopulization.world.civilian;
import CityPopulization.world.plot.Plot;
import CityPopulization.world.plot.PlotType;
public class Civilian{
    public Plot homePlot;
    public float x;
    public float y;
    public float z;
    public int[] dest;
    public double speed = 0.5;
    public Path path;
    public double dist;
    public void update(){
        if(dest!=null||path!=null){
            pathingUpdate();
        }
        if(Math.round(x)==homePlot.x&&Math.round(y)==homePlot.y&&Math.round(z)==homePlot.z){
            updateOnHomePlot();
        }
    }
    public void pathingUpdate(){
        Plot plot = homePlot.world.generatePlot(Math.round(x), Math.round(y), Math.round(z));
        double traveledThisTick = speed;
        if(plot.getType()==PlotType.Road||plot.getType()==PlotType.Road){
            traveledThisTick*=plot.getLevel();
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void arriveHome(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
