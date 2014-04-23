package CityPopulization.world.story;
public class Character {
    public static final Character TRANSIT = new Character("Transit Computer", "transit");
    public static final Character MAYOR = new Character("Mayor", "mayor");
    public static final Character TECH = new Character("Technician", "tech");
    public static final Character WORKER = new Character("Worker", "worker");
    public static final Character COMPUTER = new Character("Computer", "computer");
    public static final Character VOICE = new Character("Ominous Voice", "voice");
    public String name;
    public String texture;
    private Character(String name, String texture){
        this.name = name;
        this.texture = texture;
    }
}
