package city.populization.world.resource;
public enum Resource{
//    Tools(1),
//    Wood(1),
//    Fuel(5),
    Dirt(1),
    Sand(1),
    Stone(1),
    Coal(2),
    Clay(2),
    Oil(4),
    Iron(5),
    Gold(20),
    Diamond(100),
    Wheat(1),
    SaladGreens(1),
    Apples(1),
    Sugar(1),
    Grapes(1);
    private int cost;
    private Resource(int cost){
        this.cost = cost;
    }
    public int getCost(int count){
        return cost*count;
    }
}
