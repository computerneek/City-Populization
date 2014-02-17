package CityPopulization.world.resource;
import CityPopulization.world.resource.Resource;
import java.util.HashMap;
public class ResourceList{
    private HashMap<Resource, Integer> list = new HashMap<>();
    public ResourceList(){}
    public ResourceList(Object... resources){
        if(resources.length%2!=0){
            throw new IllegalArgumentException("Param must be repetitions of <resource> <count>");
        }
        for(int i = 0; i<resources.length; i+=2){
            if(!(resources[i] instanceof Resource)||!(resources[i+1] instanceof Integer)){
                throw new IllegalArgumentException("Param must be repetitions of <resource> <count>");
            }else{
                list.put((Resource)resources[i], (Integer)resources[i+1]);
            }
        }
    }
    public void add(Resource resource, int i){
        if(list.containsKey(resource)){
            list.put(resource, list.get(resource)+i);
        }else{
            list.put(resource, i);
        }
    }
    
}
