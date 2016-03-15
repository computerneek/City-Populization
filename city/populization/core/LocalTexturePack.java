package city.populization.core;
import city.populization.render.ResourceLocation;
import java.io.InputStream;
import simplelibrary.texture.TexturePack;
public class LocalTexturePack extends TexturePack {
    @Override
    public InputStream getResourceAsStream(String name) {
        ResourceLocation.onUsed(name);
        return super.getResourceAsStream(name);
    }
}
