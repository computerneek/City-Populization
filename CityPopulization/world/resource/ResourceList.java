package CityPopulization.world.resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
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
    public ResourceList add(Resource resource, int i){
        if(list.containsKey(resource)){
            list.put(resource, list.get(resource)+i);
        }else{
            list.put(resource, i);
        }
        return this;
    }
    public ResourceList addAll(ResourceList other){
        for(Resource resource : other.list.keySet()){
            add(resource, other.list.get(resource));
        }
        return this;
    }
    public ResourceList multiply(double modifier){
        for(Resource resource : new ArrayList<>(list.keySet())){
            int value = (int)Math.floor(modifier*list.get(resource));
            if(value<=0){
                list.remove(resource);
            }else{
                list.put(resource, value);
            }
        }
        return this;
    }
    public Set<Resource> listResources(){
        return list.keySet();
    }
    public int get(Resource resource){
        return list.get(resource);
    }
}
