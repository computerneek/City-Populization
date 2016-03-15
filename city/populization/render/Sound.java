package city.populization.render;
import city.populization.core.Core;
import city.populization.core.Main;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import simplelibrary.Queue;
import simplelibrary.Sys;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.openal.SoundStash;
public class Sound {
    //A map to the resources of different types.  The outer queue holds separate resource locations; the inner one holds all duplicates.
    private static HashMap<String, Sound> byPath = new HashMap<>();
    private static Queue<Sound> awaitingCompletion = new Queue<>();
    private static Sound currentlyProcessing;
    private static boolean completed = false;
    private int soundIndex = 0;
    private final String path;
    private static final File myLog = Main.ran()?null:new File(System.getenv("USERPROFILE")+"\\Dropbox (Dolan Programmers)\\Programs\\City Populization Latest Build\\soundErrors.txt");
    static{
        if(myLog!=null){
            try{
                myLog.delete();
            }catch(Throwable t){};//Can't let it crash here because it can't delete an error log file that may or may not exist
        }
    }
    public static synchronized Sound get(String path){
        if(path==null){
            throw new IllegalArgumentException("Cannot create a sound with no path!");
        }
        if(byPath.containsKey(path)){
            return byPath.get(path);
        }
        for(Sound s : awaitingCompletion.toList()){
            if(s.path.equals(path)){
                return s;
            }
        }
        Sound s = new Sound(path);
        awaitingCompletion.enqueue(s);
        if(completed){
            complete();
        }
        return s;
    }
    public static synchronized boolean has(String path){
        if(byPath.containsKey(path)) return true;
        for(Sound s : awaitingCompletion.toList()){
            if(s.path.equals(path)){
                return true;
            }
        }
        return currentlyProcessing!=null&&currentlyProcessing.path.equals(path);
    }
    private Sound(String path){
        this.path = path;
    }
    public void play(){
        Core.playSound(path);
    }
    public static synchronized void complete() {
        boolean errored = false;
        int loaded = 0;
        for(Sound s : awaitingCompletion){
            currentlyProcessing = s;
            s.soundIndex = SoundStash.getBuffer(s.path);
            if(s.soundIndex==0){
                Sys.suppress(ErrorLevel.warning, SoundStash.lastError, SoundStash.lastException, ErrorCategory.audio);
                errored = true;
            }
            byPath.put(s.path, s);
            loaded++;
        }
        currentlyProcessing = null;
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
                    }));
                } catch (Throwable ex) {}
            }
            System.err.println("--------SOUND ERRORS--------");
            System.err.println();
            Sys.error(ErrorLevel.warning, "Errors occured when processing sounds!", null, ErrorCategory.other);
            System.err.println();
            System.err.println("--------SOUND ERRORS--------");
            if(newOut!=null){
                System.setErr(out);
                newOut.close();
            }
        }else{
            System.out.println("Successfully loaded "+loaded+" sound(s).");
        }
    }
}
