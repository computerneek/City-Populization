package city.populization.render;
import city.populization.core.Core;
import city.populization.core.Main;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;
import simplelibrary.Queue;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.opengl.ImageStash;
public class ResourceLocation {
    //A map to the resources of different types.  The outer queue holds separate resource locations; the inner one holds all duplicates.
    private static HashMap<Type, Queue<Queue<ResourceLocation>>> byType = new HashMap<>();
    private static final ArrayList<ResourceLocation> locations = new ArrayList<>();
    private static final ArrayList<Type> finalized = new ArrayList<>();
    private static final Set<String> paths = new HashSet<>();
    private int textureIndex = 0;
    private float minX = -1;
    private float minY = -1;
    private float maxX = -1;
    private float maxY = -1;
    private final String path;
    private static final File myLog = Main.ran()?null:new File(System.getenv("USERPROFILE")+"\\Dropbox (Dolan Programmers)\\Programs\\City Populization Latest Build\\textureErrors.txt");
    static{
        if(myLog!=null){
            try{
                myLog.delete();
            }catch(Throwable t){};//Can't let it crash here because it can't delete an error log file that may or may not exist
        }
    }
    public static synchronized ResourceLocation get(Type type, String path){
        if(path==null){
            throw new IllegalArgumentException("Cannot create a resource location with no path!");
        }else{
            path = "/textures/"+type.path+"/"+path;
        }
        for(ResourceLocation l : locations){
            if(l.path.equals(path)){
                return l;
            }
        }
        if(type==null||finalized.contains(type)){
            ResourceLocation r = new ResourceLocation(path);
            locations.add(r);
            r.minX = r.minY = 0;
            r.maxX = r.maxY = 1;
            r.textureIndex = ImageStash.instance.getTexture(path);
            return r;
        }
        return new ResourceLocation(type, path);
    }
    public static synchronized boolean has(String path){
        return paths.contains(path);
    }
    public static synchronized void onUsed(String path){
        if(has(path)||Sound.has(path)) return;
        paths.add(path);
        final PrintStream out = System.err;
        PrintStream newOut = null;
        if(myLog!=null){
            try {
                myLog.getParentFile().mkdirs();
                final OutputStream o = new FileOutputStream(myLog, true);
                System.setErr(newOut = new PrintStream(new OutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        o.write(b);
                        out.write(b);
                    }
                    @Override
                    public void close() throws IOException {
                        o.close();
                    }
                }));
            } catch (Throwable ex) {}
        }
        System.err.println("----  Unregistered resource used:  "+path);
        if(newOut!=null){
            System.setErr(out);
            newOut.close();
        }
    }
    private ResourceLocation(String path){
        this.path = path;
        paths.add(path);
    }
    private ResourceLocation(Type type, String path) {
        this(path);
        for(Queue<Queue<ResourceLocation>> v : byType.values()){
            Queue<ResourceLocation> v2 = v.peek();
            if(v2!=null&&v2.peek()!=null&&v2.peek().path.equals(path)){
                v2.enqueue(this);
                return;
            }
        }
        if(!byType.containsKey(type)){
            byType.put(type, new Queue<Queue<ResourceLocation>>());
        }
        Queue<Queue<ResourceLocation>> outer = byType.get(type);
        Queue<ResourceLocation> inner = new Queue<>();
        inner.enqueue(this);
        outer.enqueue(inner);
    }
    public void bind(){
        bind(this);
    }
    public void vertex(float x, float y){
        GL11.glTexCoord2d(x(x), y(y));
    }
    public float x(float x){
        return minX+(maxX-minX)*x;
    }
    public float y(float y){
        return minY+(maxY-minY)*y;
    }
    public static synchronized void bind(ResourceLocation loc){
        int index = loc.textureIndex;
        if(index==0){
            throw new IllegalArgumentException("Resource locations must be finalized before they can be used!");
        }
        if(ImageStash.instance.getBoundTexture()!=index){
            ImageStash.instance.bindTexture(index);
        }
    }
    public static class Type{
        public static final Type PLOT = new Type("plot");
        public static final Type BUTTON_INGAME = new Type("buttons/ingame");
        private final String path;
        public Type(String path){
            this.path = path;
        }
    }
    public static synchronized void finalizeType(Type type) {
        finalized.add(type);
        Queue<Queue<ResourceLocation>> toFinalize = byType.remove(type);
        if(toFinalize==null||toFinalize.isEmpty()){
            return;
        }
        int width = 1;
        int height = 1;
        while(width*height<toFinalize.size()){
            if(height<width){
                height*=2;
            }else{
                width*=2;
            }
        }
        int[] totalRGBData = null;
        boolean errored = false;
        int read = 0;
        int x = 0, y = 0;
        int X = -1, Y = -1;
        for(Queue<ResourceLocation> q : toFinalize.toList()){
            ResourceLocation loc = q.peek();
            while(loc==null&&!q.isEmpty()){
                q.dequeue();
                loc = q.peek();
            }
            if(loc==null) continue;
            for(ResourceLocation l : q.toList()){
                if(l!=null){
                    l.minX = (1f/width)*x;
                    l.minY = (1f/height)*y;
                    l.maxX = (1f/width)*(x+1);
                    l.maxY = (1f/height)*(y+1);
                }
            }
            String path = loc.path;
            BufferedImage img;
            try {
                if(path.endsWith("/texture.png")){
                    Sys.suppress(ErrorLevel.warning, "No model exists for location "+path.substring(0, path.length()-12)+"!", null, ErrorCategory.bug);
                    errored = true;
                }
                img = ImageIO.read(ResourceLocation.class.getResourceAsStream(path));
            } catch (IOException | IllegalArgumentException ex) {
                Sys.suppress(ErrorLevel.warning, "Could not read image "+path+", allocating default", ex, ErrorCategory.fileIO);
                errored = true;
                x++;
                if(x>=width){
                    x-=width;
                    y++;
                }
                continue;
            }
            if(totalRGBData==null){
                X = img.getWidth();
                Y = img.getHeight();
                totalRGBData = new int[width*X * height*Y];
                for(int i = 0; i<totalRGBData.length; i++){
                    int localX = i%X;
                    int localY = (i/(width*X))%Y;
                    int red = (0xFF*localX/X)<<16;
                    int green = (0xFF*localY/Y)<<8;
                    int blue = 0xFF*(X-localX)/X;
                    totalRGBData[i] = 0xFF000000+red+green+blue;
                }
            }else if(X!=img.getWidth()||Y!=img.getHeight()){
                Sys.suppress(ErrorLevel.warning, null, new IllegalArgumentException("ERROR:  Dimensions "+img.getWidth()+" x "+img.getHeight()+" of image at "+path+" did not match expected "+X+" x "+y+"!  Image may not look right."), ErrorCategory.other);
                errored = true;
            }
            int myX = Math.min(X, img.getWidth());
            int myY = Math.min(Y, img.getHeight());
            img.getRGB(0, 0, myX, myY, totalRGBData, x*X+(y*(X*width*Y)), X*width);
            x++;
            if(x>=width){
                x-=width;
                y++;
            }
            read++;
        }
        if(totalRGBData==null){
            X = 32;
            Y = 32;
            totalRGBData = new int[width*X * height*Y];
            for(int i = 0; i<totalRGBData.length; i++){
                int localX = i%X;
                int localY = (i/(width*X))%Y;
                int red = (0xFF*localX/X)<<16;
                int green = (0xFF*localY/Y)<<8;
                int blue = 0xFF*(X-localX)/X;
                totalRGBData[i] = 0xFF000000+red+green+blue;
            }
        }
        BufferedImage img = new BufferedImage(X*width, Y*height, BufferedImage.TYPE_INT_ARGB);
        img.setRGB(0, 0, X*width, Y*height, totalRGBData, 0, X*width);
        File file = new File(Core.getAppdataRoot()+"\\debugTexture_"+type.path+".png");
        file.getParentFile().mkdirs();
        try {
            ImageIO.write(img, "png", file);
        } catch (IOException ex) {
            Sys.suppress(ErrorLevel.warning, "Could not debug-dump texture "+file.getName()+"!", ex, ErrorCategory.other);
            errored = true;
        }
        int imgIndex = ImageStash.instance.allocateAndSetupTexture(img);
        int totalTextures = toFinalize.size();
        for(Queue<ResourceLocation> q : toFinalize){
            for(ResourceLocation r : q){
                if(r!=null){
                    r.textureIndex = imgIndex;
                }
                locations.add(r);
            }
        }
        if(errored){
            final PrintStream out = System.err;
            PrintStream newOut = null;
            if(myLog!=null){
                try {
                    myLog.getParentFile().mkdirs();
                    final OutputStream o = new FileOutputStream(myLog, true);
                    System.setErr(newOut = new PrintStream(new OutputStream() {
                        @Override
                        public void write(int b) throws IOException {
                            o.write(b);
                            out.write(b);
                        }
                        @Override
                        public void close() throws IOException {
                            o.close();
                        }
                    }));
                } catch (Throwable ex) {}
            }
            System.err.println("--------RESOURCE ERRORS--------");
            System.err.println();
            Sys.error(ErrorLevel.warning, "Errors occured when processing resources in location '"+type.path+"'!", null, ErrorCategory.other);
            System.err.println();
            System.err.println("--------RESOURCE ERRORS--------");
            if(newOut!=null){
                System.setErr(out);
                newOut.close();
            }
        }else{
            System.out.println("Successfully stitched resource location '"+type.path+"' with "+totalTextures+" "+X+"x"+Y+" texture(s).");
        }
    }
}
