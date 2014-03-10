package CityPopulization.texturepack;
import CityPopulization.Core;
import java.util.ArrayList;
public class TexturepackCreator {
    private static ArrayList<Texture> textures = new ArrayList<>();
    public static void outputAllFiles(){
        for(Texture texture : textures){
            System.out.println(texture.toString());
        }
    }
    public static void addTexture(Texture texture){
        textures.add(texture);
    }
    static{
        Core.loadAllSoundsAndTextures();
    }
}
