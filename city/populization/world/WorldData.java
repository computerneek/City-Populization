package city.populization.world;
import simplelibrary.config2.Config;
public class WorldData {
    public boolean isMultiplayer;
    public String address;
    public int port;
    public int workers;
    public String name;
    public String filepath;
    public long seed;
    public Template template;
    public Config save() {
        Config c = Config.newConfig();
        c.set("timestamp", System.currentTimeMillis());
        c.set("isMultiplayer", isMultiplayer);
        if(name!=null){
            c.set("name", name);
        }
        if(filepath!=null){
            c.set("filepath", filepath);
        }
        if(address!=null){
            c.set("address", address);
            c.set("port", port);
        }
        c.set("workers", workers);
        c.set("seed", seed);
        if(template!=null){
            c.set("template", template.name());
        }
        return c;
    }
    public WorldData load(Config c){
        if(c.hasProperty("isMultiplayer")){
            isMultiplayer = c.get("isMultiplayer");
        }
        name = c.get("name");
        filepath = c.get("filepath");
        address = c.get("address");
        if(c.hasProperty("port")){
            port = c.get("port");
        }
        if(c.hasProperty("workers")){
            workers = c.get("workers");
        }
        if(c.hasProperty("seed")){
            seed = c.get("seed");
        }
        if(c.hasProperty("template")){
            try{
                template = Template.valueOf((String)c.get("template"));
            }catch(IllegalArgumentException ex){}//Ignore it, so older clients won't crash when they see a newer version of file with a newer template
        }
        return this;
    }
}
