package CityPopulization.world.resource;
public enum Resource{
    Tools(1),
    Dirt(1),
    Wood(1),
    Iron(2),
    Fuel(5),
    Clay(1),
    Coal(1),
    Gold(5),
    Oil(2),
    Sand(1),
    Stone(1);
    private int cost;
    private Resource(int cost){
        this.cost = cost;
    }
    public int getCost(int count){
        return cost*count;
    }
}
