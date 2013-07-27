package CityPopulization;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import multilib.net.ConnectionManager;
import multilib.net.Packet;
import multilib.net.Packet1Integer;
public class WorkerPath{
    private static Random rand = new Random();
    public static boolean isPathTo(int[] currentCoords, int[] coords){
        return findPathTo(currentCoords, coords, false)!=null;
    }
    public static WorkerPath findPathTo(int[] currentCoords, int[] coords){
        return findPathTo(currentCoords, coords, true);
    }
    public static WorkerPath findPathTo(int[] currentCoords, int[] coords, boolean findFastest){
        WorkerPath value = new WorkerPath();
        int startX = currentCoords[0];
        int startY = currentCoords[1];
        int endX = coords[0];
        int endY = coords[1];
        if(startX==endX&&startY==endY){
            value.path(startX, startY);
            return value;
        }
        ArrayList<WorkerPath> list = new ArrayList<>();
        WorkerPath currentPath = new WorkerPath();
        ArrayList<Plot> noPath = new ArrayList<>();
        int[] loc = currentCoords;
        int[] dest = coords;
        int direction = 1;
        currentPath.path(loc[0], loc[1]);
        findPathsTo(list, currentPath, loc, dest, direction, findFastest);
        if(list.isEmpty()){
            return null;
        }
        WorkerPath path = findFastestPath(list);
        return path;
    }
    private static WorkerPath findPathsTo(ArrayList<WorkerPath> list, WorkerPath currentPath, int[] loc, int[] dest, int direction, boolean multiple){
        if(!multiple&&!list.isEmpty()){
            return currentPath;
        }
        if(loc[0]==dest[0]&&loc[1]==dest[1]){
            WorkerPath path = currentPath.copy();
            list.add(currentPath);
            return path;
        }
        int nextDirection = direction;
        WorkerPath oldPath = currentPath;
        for(int i = 0; i<4; i++){
            if(canGo(nextDirection, loc, dest, currentPath)){
                currentPath.path(Worker.adjustToWorkerCoords(alterLocation(loc, nextDirection)));
                currentPath = findPathsTo(list, currentPath, alterLocation(loc, nextDirection), dest, nextDirection, multiple);
                currentPath.unpath();
            }
            nextDirection = nextDirection == 1?4:nextDirection - 1;
        }
        return currentPath;
    }
    private static WorkerPath findFastestPath(ArrayList<WorkerPath> list){
        int[] times = getTimesForPaths(list);
        int minimumTime = getSmallestValue(times);
        ArrayList<WorkerPath> paths = getAllPathsWithTime(list, minimumTime);
        return paths.get(rand.nextInt(paths.size()));
    }
    private static int[] getTimesForPaths(ArrayList<WorkerPath> list){
        int[] times = new int[list.size()];
        for(int i = 0; i<times.length; i++){
            times[i] = list.get(i).getTimeForPath();
        }
        return times;
    }
    private static int getSmallestValue(int[] times){
        int min = times[0];
        for(int i = 1; i<times.length; i++){
            min = Math.min(min, times[i]);
        }
        return min;
    }
    private static ArrayList<WorkerPath> getAllPathsWithTime(ArrayList<WorkerPath> list, int minimumTime){
        ArrayList<WorkerPath> value = new ArrayList<>();
        for(WorkerPath path : list){
            if(path.getTimeForPath()==minimumTime){
                value.add(path);



            }
        }
        return value;
    }
    private ArrayList<int[]> points = new ArrayList<>();
    public int[] getNextPoint(){
        return points.remove(0);
    }
    private void path(int x, int y){
        path(new int[]{x*50+25, y*50+25});
    }
    private void path(int[] point){
        points.add(point);
    }
    public boolean isComplete(){
        return points.isEmpty();
    }
    public static boolean canGo(int direction, int[] loc, int[] dest, WorkerPath currentPath){
        boolean value;
        Plot Loc = main.world[loc[0]][loc[1]];
        Plot Dest = Loc.getPlot(direction);
        if(Dest==null||currentPath.containsPoint(Worker.adjustToWorkerCoords(Dest.getCoords()))){
            return false;
        }
        return Dest.canEnterFromPlot(Loc)&&(Dest.isSamePlot(dest)||Dest.isTravelable());
    }
    public static int[] alterLocation(int[] location, int direction){
        return main.world[location[0]][location[1]].getPlot(direction).getCoords();
    }
    private void unpath(){
        points.remove(points.size()-1);
    }
    private WorkerPath copy(){
        WorkerPath path = new WorkerPath();
        for(int[] point : points){
            path.path(new int[]{point[0], point[1]});
        }
        return path;
    }
    private int getTimeForPath(){
        double distance = 0;
        for(int i = 0; i<points.size(); i++){
            distance+=(50/main.getPlot(main.getPlotCoordinates(points.get(i))).getWorkerSpeed(null));
        }
        return (int)(distance*1_000D);
    }
    private boolean containsPoint(int[] point){
        for(int[] contained : points){
            if(contained[0]==point[0]&&contained[1]==point[1]){
                return true;
            }
        }
        return false;
    }
    public void save(DataOutputStream out) throws IOException{
        out.writeInt(points.size());
        for(int[] point : points){
            out.writeInt(point[0]);
            out.writeInt(point[1]);
        }
    }
    public static WorkerPath load(DataInputStream in) throws IOException{
        WorkerPath path = new WorkerPath();
        int points = in.readInt();
        for(int i = 0; i<points; i++){
            path.path(new int[]{in.readInt(), in.readInt()});
        }
        return path;
    }
}