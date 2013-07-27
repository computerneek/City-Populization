package CityPopulization;
import java.util.ArrayList;
import java.util.HashMap;
public enum PlotType{
    empty("An Empty Plot", "Empty Plot", false, false, null, 1, 1, 20, 10),
    mainBase("A town hall", "Town Hall", false, false, null, 2, 10, 1, 1),
    highway("A Highway", "Highway", true, false, null, 1, 25, 1, 1),
    house("A House", "House", true, false, null, 1, 75, 1, 1),
    workshop("A Workshop", "Workshop", true, false, null, 1, 10, 1, 1),
    warehouse("A Warehouse", "Warehouse", true, false, null, 1, 1, 1, 1),//Presently operation-free//No GUI
    school("A School", "School", true, false, null, 1, 1, 1, 1),//Presently operation-free//No GUI
    militaryBase("A Military Base", "Military Base", true, false, null, 1, 1, 1, 1),//Presently operation-free//No GUI
    police("A Police Department", "Police Department", true, false, null, 1, 1, 1, 1),//Presently operation-free//No GUI
    fireDepartment("A Fire Department", "Fire Department", true, false, null, 1, 1, 1, 1),//Presently operation-free//No GUI
    hospital("A Hospital", "Hospital", true, false, null, 1, 1, 1, 1),//Presently operation-free//No GUI
    departmentStore("A Department Store", "Department Store", true, false, null, 1, 1, 1, 1),//Presently operation-free//No GUI
    shoppingMall("A Shopping Mall", "Shopping Mall", true, false, null, 1, 1, 1, 1),//No GUI
    restaurant("A Restaurant", "Restaurant", true, false, null, 1, 1, 1, 1),//Presently operation-free//No GUI
    amusementPark("An Amusement Park", "Amusement Park", true, false, null, 1, 1, 1, 1),//Presently operation-free//No GUI
    park("A Park", "Park", true, false, null, 1, 1, 1, 1),//Presently operation-free//No GUI
    dirtMine("A Dirt Mine", "Dirt Mine", true, true, null, 1, 12, 1, 1),
    coalMine("A Coal Mine", "Coal Mine", true, true, null, 1, 12, 1, 1),
    oilWell("An Oil Well", "Oil Well", true, true, null, 2, 12, 1, 1),
    forest("A Forest", "Forest", true, true, null, 1, 12, 1, 1),
    quarry("A Stone Mine", "Stone Mine", true, true, null, 1, 12, 1, 1),
    ironMine("An Iron Mine", "Iron Mine", true, true, null, 1, 12, 1, 1),
    sandPit("A Sand Pit", "Sand Pit", true, true, null, 1, 12, 1, 1),
    clayPit("A Clay Pit", "Clay Pit", true, true, null, 1, 12, 1, 1),
    goldMine("A Gold Mine", "Gold Mine", true, true, null, 1, 12, 1, 1),
    zombieland("A Zombie Land", "Zombieland", false, false, null, 0, 1, 1, 1),//Presently operation-free//No GUI
    repairStation("A Repair Station", "Repair Station", true, false, null, 1, 1, 1, 1);
    private final int[][] animationTicks;
    private final int[][] frame;
    public final int[][] tpf;
    public final int[][] frames;
    public final int levels;
    private boolean isFarm;
    private static void initialize(){
        PlotType[] types = values();
        for(PlotType type : types){
            ArrayList<HashMap<Material, Integer>> lst = new ArrayList<>();
            for(int i = 0; i<type.levels; i++){
                lst.add(new HashMap<Material, Integer>());
            }
            costs.put(type, lst);
        }
        initialized = true;
    }
    private static boolean initialized = false;
    public final String name;
    public final boolean canBuild;
    public final String constructionTag;
    private static final HashMap<PlotType, ArrayList<HashMap<Material, Integer>>> costs = new HashMap<>();
    public final boolean isTemporary;
    public final PlotType permanent;
    public final int zombieLevel;
    private static final int missingCostReplacementValue = 0;
    private PlotType(String name, String constructionTag, boolean canBuild, boolean isFarm, PlotType permVersion, int zombieDestinationLevel, int levels, int frames, int ticksPerFrame){
        this.name = name;
        this.constructionTag = constructionTag;
        this.isTemporary = permVersion!=null;
        this.canBuild = canBuild&&!isTemporary;
        this.permanent = permVersion;
        this.zombieLevel = zombieDestinationLevel;
        this.levels = levels;
        this.isFarm = isFarm;
        int animationCount=isFarm?3:1;
        this.tpf = new int[levels][animationCount];
        this.frame = new int[levels][animationCount];
        this.frames = new int[levels][animationCount];
        this.animationTicks = new int[levels][animationCount];
        for(int i = 0; i<tpf.length; i++){
            for(int j = 0; j<animationCount; j++){
                tpf[i][j] = ticksPerFrame;
                frame[i][j] = 1;
                this.frames[i][j] = frames;
                animationTicks[i][j] = 0;
            }
        }
    }
    public int getCost(int level, Material material){
        if(!initialized){
            initialize();
        }
        if(!hasCosts(level)&&material==Material.Dirt){
            System.err.println("Plot type "+name()+" level "+level+" needs costs!");
            return missingCostReplacementValue;
        }else if(!hasCosts(level)){
            return missingCostReplacementValue;
        }
        ArrayList<HashMap<Material, Integer>> lst = costs.get(this);
        HashMap<Material, Integer> map = lst.get(level-1);
        Integer cost = map.get(material);
        return cost==null?0:cost.intValue();
    }
    boolean hasCosts(int level){
        if(!initialized){
            initialize();
        }
        ArrayList<HashMap<Material, Integer>> lst = costs.get(this);
        HashMap<Material, Integer> map = lst.get(level-1);
        return !map.isEmpty();
    }
    public static PlotType getConst(String constTag){
        for(PlotType type : values()){
            if(type.constructionTag.equals(constTag)){
                return type;
            }
        }
        return null;
    }
    public static void setCost(PlotType type, int level, Material material, int cost){
        if(!initialized){
            initialize();
        }
        costs.get(type).get(level-1).put(material, Integer.valueOf(cost));
    }
    public static void setCosts(PlotType type, Material[] materials, int[][] costs){
        if(type==null||materials==null||costs==null){
            throw new IllegalArgumentException("No params can be null!");
        }
        for(int level = 0; level<costs.length; level++){
            if(costs[level]==null){
                continue;
            }
            for(int material = 0; material<materials.length&&material<costs[level].length-1; material++){
                if(materials[material]==null){
                    continue;
                }
                if(costs[level][material+1]==0){
                    continue;
                }
                setCost(type, costs[level][0], materials[material], costs[level][material+1]);
            }
        }
    }
    public void updateAnimations(){
        for(int i = 0; i<animationTicks.length; i++){
            for(int j = 0; j<animationTicks[i].length; j++){
                animationTicks[i][j]++;
                if(animationTicks[i][j]>=tpf[i][j]){
                    animationTicks[i][j] = 0;
                    tickAnimation(i, j);
                }
            }
        }
    }
    private void tickAnimation(int level, int animation){
        frame[level][animation]++;
        if(frame[level][animation]>frames[level][animation]){
            frame[level][animation] = 1;
        }
    }
    public int getFrame(int level, int type){
        return frame[level-1][type];
    }
    public boolean isFarm(){
        return isFarm;
    }
    public Material getResource(){
        if(this==dirtMine){
            return Material.Dirt;
        }else if(this==coalMine){
            return Material.Coal;
        }else if(this==oilWell){
            return Material.Oil;
        }else if(this==quarry){
            return Material.Stone;
        }else if(this==ironMine){
            return Material.Iron;
        }else if(this==forest){
            return Material.Wood;
        }else if(this==sandPit){
            return Material.Sand;
        }else if(this==clayPit){
            return Material.Clay;
        }else if(this==goldMine){
            return Material.Gold;
        }else{
            return null;
        }
    }
}
