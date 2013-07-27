package CityPopulization;
public enum Material{
    Dirt("dirtDigger"),
    Oil("oilDriller"),
    Coal("coalMiner"),
    Stone("stoneMiner"),
    Iron("ironMiner"),
    Wood("woodCutter"),
    Sand("sandDigger"),
    Clay("clayDigger"),
    Gold("goldMiner");
    private String typestring;
    private Material(String typestring){
        this.typestring = typestring;
    }
    public String getTypeStringForWorkers(){
        return typestring;
    }
}
