package CityPopulization.world.resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import simplelibrary.config2.Config;
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
    public ResourceList add(Resource resource, int count){
        if(list.containsKey(resource)){
            list.put(resource, list.get(resource)+count);
        }else{
            list.put(resource, count);
        }
        return this;
    }
    public ResourceList addAll(ResourceList other){
        if(other==null){
            return this;
        }
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
    public ArrayList<Resource> listResources(){
        ArrayList<Resource> val = new ArrayList<>(list.keySet());
        Collections.sort(val);
        return val;
    }
    public int get(Resource resource){
        return list.containsKey(resource)?list.get(resource):0;
    }
    public ResourceList removeAll(ResourceList other){
        for(Resource resource : other.list.keySet()){
            remove(resource, other.list.get(resource));
        }
        return this;
    }
    public ResourceList remove(Resource resource, int count){
        if(get(resource)>count){
            list.put(resource, list.get(resource)-count);
        }else{
            list.remove(resource);
        }
        return this;
    }
    @Override
    public String toString(){
        if(list.isEmpty()){
            return "Nothing";
        }
        String value = "";
        ArrayList<Resource> resources = listResources();
        Resource.Wood.compareTo(Resource.Oil);
        Collections.sort(resources);
        for(Resource resource : resources){
            if(!value.isEmpty()){
                value+=", ";
            }
            value += get(resource)+" "+resource.name();
        }
        return value;
    }
    public int count(){
        int count = 0;
        for(Resource resource : list.keySet()){
            count+=list.get(resource);
        }
        return count;
    }
    public void set(Resource resource, int value){
        list.put(resource, value);
    }
    public ResourceList split(int resources){
        ResourceList lst = new ResourceList();
        if(resources>=count()){
            lst.addAll(this);
            list.clear();
            return lst;
        }
        for(int i=0; i<resources&&count()>0; i++){
            Resource resource = listResources().get(0);
            remove(resource, 1);
            lst.add(resource, 1);
        }
        return lst;
    }
    public Config save(){
        Config config = Config.newConfig();
        for(Resource res : list.keySet()){
            config.set(res.name(), get(res));
        }
        return config;
    }
    public static ResourceList load(Config config){
        ResourceList lst = new ResourceList();
        for(Resource res : Resource.values()){
            if(config.hasProperty(res.name())&&(int)config.get(res.name())>0){
                lst.list.put(res, (int)config.get(res.name()));
            }
        }
        return lst;
    }
}
