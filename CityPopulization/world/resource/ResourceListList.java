package CityPopulization.world.resource;
import java.util.ArrayList;
import java.util.Arrays;
public class ResourceListList{
    private ArrayList<ResourceList> list = new ArrayList<>();
    public ResourceListList(){}
    public ResourceListList(ResourceList... resourceLists){
        list.addAll(Arrays.asList(resourceLists));
    }
    public ResourceListList add(ResourceList list){
        this.list.add(list);
        return this;
    }
    public ResourceListList addAll(ResourceListList other){
        list.addAll(other.list);
        return this;
    }
    public ResourceListList multiply(double modifier){
        for(ResourceList resource : list){
            resource.multiply(modifier);
        }
        return this;
    }
    public ResourceList get(int which){
        if(which<0||which>=list.size()){
            return null;
        }
        return list.get(which);
    }
}
