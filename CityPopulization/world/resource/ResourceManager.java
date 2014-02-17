package CityPopulization.world.resource;
import CityPopulization.world.resource.Resource;
import java.util.HashMap;
public class ResourceManager{
    private boolean sandboxMode;
    private final HashMap<Resource, Long> resources = new HashMap<>();
    public void setQuantity(Resource resource, long quantity){
        resources.put(resource, quantity);
    }
    public void setSandbox(boolean sandboxMode){
        this.sandboxMode = sandboxMode;
    }
}
