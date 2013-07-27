package CityPopulization;
import java.util.ArrayList;
public class Profiler {
    private static ArrayList<String> currentSection = new ArrayList<>();
    private static ArrayList<Long> times = new ArrayList<>();
    public static void start(String section){
        currentSection.add(section);
        times.add(System.nanoTime());
    }
    public static void endStart(String section){
        end();
        start(section);
    }
    public static void end(){
        if(currentSection.isEmpty()){
            throw new IllegalArgumentException("No session to end!");
        }
        String name = currentSection.get(0);
        for(int i = 1; i<currentSection.size(); i++){
            name+="."+currentSection.get(i);
        }
        currentSection.remove(currentSection.size()-1);
        long time = System.nanoTime()-times.remove(times.size()-1);
        if(time>=50000000){
            System.out.println(name+"   took "+(((double)time)/1000000D)+" miliseconds.");
        }
    }
    public static void endAll(){
        while(!currentSection.isEmpty()){
            end();
        }
    }
}
