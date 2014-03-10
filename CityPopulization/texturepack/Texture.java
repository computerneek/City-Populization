package CityPopulization.texturepack;
public class Texture {
    private int levels;
    private boolean animation;
    private String path;
    public Texture(String path){
        String[] spl = path.split(":");
        if(spl.length>1){
            levels = Integer.parseInt(spl[0]);
            path = spl[1];
        }
        if(path.contains("<FRAME>")){
            animation = true;
        }
        this.path = path;
    }
    @Override
    public String toString(){
        if(levels>0){
            if(animation){
                return "Animated plot; "+levels+" levels ("+path+")";
            }else{
                return "Static plot; "+levels+" levels ("+path+")";
            }
        }else if(animation){
            return "Animation ("+path+")";
        }else{
            return "Static image ("+path+")";
        }
    }
}
