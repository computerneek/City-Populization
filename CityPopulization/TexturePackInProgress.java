package CityPopulization;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import multilib.gui.TexturePack;
/**
 *
 * @author Bryan Dolan
 */
public class TexturePackInProgress extends TexturePack {
    public String name(){
        return "Texture Pack In Progress";
    }
    public InputStream getResourceAsStream(String name){
        File file = new File(name);
        if(!file.exists()){
            return null;
        }else{
            try{
                return new FileInputStream(file);
            }catch(FileNotFoundException ex){
                return null;
            }
        }
    }
}
