package CityPopulization;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import multilib.gui.ImageStash;
import multilib.net.ConnectionManager;
import multilib.net.Packet;
import multilib.net.Packet1Integer;
import multilib.net.Packet3Boolean;
public class GameHandler {
    //<editor-fold defaultstate="collapsed" desc="Variables">
    public static final int TYPE_STANDARD = 0;
    public static final int TYPE_TUTORIAL = 1;
    public static final int TYPE_STORY = 2;
    public int workersToWarpIn = 25;
    public boolean shouldSetupBase = true;
    public int seed = 1;
    public boolean isSizeChallenger = false;
    public int speed = 50;
    public int civilliansPerLevel = 1;
    public int type = TYPE_STANDARD;
    public int level = 0;
    public int progress = 0;
    private int tick;
    //</editor-fold>
    public GameHandler(){}
    public GameHandler setWorkersToWarpIn(int workersToWarpIn){
        this.workersToWarpIn = workersToWarpIn;
        return this;
    }
    public GameHandler setShouldSetupBase(boolean shouldSetupBase){
        this.shouldSetupBase = shouldSetupBase;
        return this;
    }
    public GameHandler setSeed(int seed){
        this.seed = seed;
        return this;
    }
    public GameHandler setIsSizeChallenger(boolean isSizeChallenger){
        this.isSizeChallenger = isSizeChallenger;
        return this;
    }
    public GameHandler setSpeed(int speed){
        this.speed = speed;
        return this;
    }
    public GameHandler setCivilliansPerLevel(int civilliansPerLevel){
        this.civilliansPerLevel = civilliansPerLevel;
        return this;
    }
    public GameHandler setType(int type){
        this.type = type;
        return this;
    }
    public GameHandler setLevel(int level){
        this.level = level;
        return this;
    }
    public GameHandler setProgress(int progress){
        this.progress = progress;
        return this;
    }
    public void setupBase(){
        throw new UnsupportedOperationException("Could not set up the base!");
    }
    public long getCash(){
        if(isSizeChallenger){
            return (((long)main.world.length)*((long)main.world[0].length))*60L*10L+100_000L;
        }else{
            return 100_000L;
        }
    }
    public void preTick(){
        tick++;
    }
    public boolean tick(){
        if(type==TYPE_TUTORIAL){
            if(tick==94){
                progress = 1;
                main.isGamePaused = true;
                return true;
            }else if(main.helper.mouse.leftButtonPressed){
                if(progress==1){
                    progress = 2;
                    main.selected = main.world[0][0];
                    return true;
                }else if(progress==2){
                    progress = 3;
                    main.selected = main.world[1][0];
                    return true;
                }else if(progress==3){
                    progress = 4;
                    main.selected = main.world[0][1];
                    return true;
                }else if(progress==4){
                    progress = 5;
                    main.selected = main.world[2][0];
                    return true;
                }else if(progress==5){
                    progress = 6;
                    main.isGamePaused = false;
                    main.selected = null;
                    return true;
                }
            }
        }
        return false;
    }
    public void render(){
        switch(type){
            case TYPE_STANDARD:
                return;
            case TYPE_TUTORIAL://<editor-fold defaultstate="collapsed">
                switch(progress){
                    case 0://<editor-fold defaultstate="collapsed">
                        return;//</editor-fold>
                    case 1://<editor-fold defaultstate="collapsed">
                        Renderer.drawScaledRect(100, 150, 1500, 800, ImageStash.instance.getTexture("/textbox_1400_650.png"));
                        Renderer.drawScaledText(120, 170, 1480, 250, "Welcome to the tutorial!");
                        Renderer.drawScaledText(120, 250, 1480, 290, "Click anywhere to continue");
                        break;//</editor-fold>
                    case 2://<editor-fold defaultstate="collapsed">
                        Renderer.drawScaledRect(50, 50, 100, 100, ImageStash.instance.getTexture("/plotPointout/right.png"));
                        Renderer.drawScaledRect(50, 100, 100, 150, ImageStash.instance.getTexture("/plotPointout/bottomRight.png"));
                        Renderer.drawScaledRect(0, 100, 50, 150, ImageStash.instance.getTexture("/plotPointout/bottom.png"));
                        Renderer.drawScaledRect(100, 150, 1500, 800, ImageStash.instance.getTexture("/textbox_1400_650.png"));
                        Renderer.drawScaledText(120, 170, 1480, 250, "City Entrance");
                        Renderer.drawScaledText(120, 250, 1480, 290, "This is your city entrance.");
                        Renderer.drawScaledText(120, 290, 1480, 330, "Civillians enter your city through it.  It is very important");
                        Renderer.drawScaledText(120, 330, 1480, 370, "that every civillian has a path to this particular plot.");
                        Renderer.drawScaledText(120, 370, 1480, 410, "This may be changed in version 3.");
                        Renderer.drawScaledText(120, 410, 1480, 450, "Sorry about the poor plot pointout textures; they will be");
                        Renderer.drawScaledText(120, 450, 1480, 490, "changed in version 3.  Look closely at the upper-left corner");
                        Renderer.drawScaledText(120, 490, 1480, 530, "of the world to find the black wire frame arrows.");
                        Renderer.drawScaledText(120, 530, 1480, 570, "Click anywhere to continue");
                        break;//</editor-fold>
                    case 3://<editor-fold defaultstate="collapsed">
                        Renderer.drawScaledRect(0, 50, 50, 100, ImageStash.instance.getTexture("/plotPointout/left.png"));
                        Renderer.drawScaledRect(0, 100, 50, 150, ImageStash.instance.getTexture("/plotPointout/bottomLeft.png"));
                        Renderer.drawScaledRect(50, 100, 100, 150, ImageStash.instance.getTexture("/plotPointout/bottom.png"));
                        Renderer.drawScaledRect(100, 100, 150, 150, ImageStash.instance.getTexture("/plotPointout/bottomRight.png"));
                        Renderer.drawScaledRect(100, 50, 150, 100, ImageStash.instance.getTexture("/plotPointout/right.png"));
                        Renderer.drawScaledRect(100, 150, 1500, 800, ImageStash.instance.getTexture("/textbox_1400_650.png"));
                        Renderer.drawScaledText(120, 170, 1480, 250, "House");
                        Renderer.drawScaledText(120, 250, 1480, 290, "This is a house.  Civillians and workers live in houses.");
                        Renderer.drawScaledText(120, 290, 1480, 330, "Each civillian spawns at your city entrance and travels to");
                        Renderer.drawScaledText(120, 330, 1480, 370, "the house that they are assigned.");
                        Renderer.drawScaledText(120, 370, 1480, 410, "Sorry about the tiny zoom of the world.  That will also be");
                        Renderer.drawScaledText(120, 410, 1480, 450, "changed in version 3.  For now, adjust the window size to");
                        Renderer.drawScaledText(120, 450, 1480, 490, "change the world zoom.  Optimal inner window size:");
                        Renderer.drawScaledText(120, 490, 1480, 530, "1,600px wide (default:  800) by 1000px high (default:  500)");
                        Renderer.drawScaledText(120, 530, 1480, 570, "Click anywhere to continue");
                        break;//</editor-fold>
                    case 4://<editor-fold defaultstate="collapsed">
                        Renderer.drawScaledRect(0, 50, 50, 100, ImageStash.instance.getTexture("/plotPointout/top.png"));
                        Renderer.drawScaledRect(50, 50, 100, 100, ImageStash.instance.getTexture("/plotPointout/top.png"));
                        Renderer.drawScaledRect(100, 50, 150, 100, ImageStash.instance.getTexture("/plotPointout/topRight.png"));
                        Renderer.drawScaledRect(100, 100, 150, 150, ImageStash.instance.getTexture("/plotPointout/right.png"));
                        Renderer.drawScaledRect(100, 150, 150, 200, ImageStash.instance.getTexture("/plotPointout/bottomRight.png"));
                        Renderer.drawScaledRect(50, 150, 100, 200, ImageStash.instance.getTexture("/plotPointout/bottom.png"));
                        Renderer.drawScaledRect(0, 150, 50, 200, ImageStash.instance.getTexture("/plotPointout/bottom.png"));
                        Renderer.drawScaledRect(100, 150, 1500, 800, ImageStash.instance.getTexture("/textbox_1400_650.png"));
                        Renderer.drawScaledText(120, 170, 1480, 250, "Roads");
                        Renderer.drawScaledText(120, 250, 1480, 290, "These are roads.  Civillians and workers can travel freely");
                        Renderer.drawScaledText(120, 290, 1480, 330, "on them.");
                        Renderer.drawScaledText(120, 330, 1480, 370, "By the way- you may or may not have noticed that, once the");
                        Renderer.drawScaledText(120, 370, 1480, 410, "game window opens, closing the window doesn't do anything.");
                        Renderer.drawScaledText(120, 410, 1480, 450, "It simply doesn't close.  That's because this version, and");
                        Renderer.drawScaledText(120, 450, 1480, 490, "the one before it, don't read that input.  To bypass this");
                        Renderer.drawScaledText(120, 490, 1480, 530, "known bug, press the 'escape' key and click 'exit'.");
                        Renderer.drawScaledText(120, 530, 1480, 570, "Click anywhere to continue");
                        break;//</editor-fold>
                    case 5://<editor-fold defaultstate="collapsed">
                        Renderer.drawScaledRect(100, 150, 1500, 800, ImageStash.instance.getTexture("/textbox_1400_650.png"));
                        Renderer.drawScaledText(120, 170, 1480, 250, "Extra Info");
                        Renderer.drawScaledText(120, 250, 1480, 290, "The escape menu is very useful.  Not only can you exit the");
                        Renderer.drawScaledText(120, 290, 1480, 330, "game from it, but you can also save or load your game.");
                        Renderer.drawScaledText(120, 330, 1480, 370, "Note that the save 'Last Played', as created by City Populization");
                        Renderer.drawScaledText(120, 370, 1480, 410, "2.8, is a corrupted file as far as any version is concerned.");
                        Renderer.drawScaledText(120, 410, 1480, 450, "This version of City Populization cannot read those created by");
                        Renderer.drawScaledText(120, 450, 1480, 490, "City Populization 2.9, and vice versa.  Version 3 will not be");
                        Renderer.drawScaledText(120, 490, 1480, 530, "able to load worlds saved by City Populization 2.9.9.");
                        Renderer.drawScaledText(120, 530, 1480, 570, "However, versions beyond version 3 are planned to have back");
                        Renderer.drawScaledText(120, 570, 1480, 610, "compatibility for older versions back to version 3.");
                        Renderer.drawScaledText(120, 610, 1480, 650, "At any point in the game, you can press the space bar to view the");
                        Renderer.drawScaledText(120, 650, 1480, 690, "numbers of civillians and workers waiting to enter your city.");
                        Renderer.drawScaledText(120, 690, 1480, 730, "Click anywhere to continue");
                        break;//</editor-fold>
                }
                break;
            case TYPE_STORY:
                return;
            default:
                throw new IllegalArgumentException("Unknown type!");
        }
    }
    public void save(DataOutputStream out) throws IOException{
        out.writeInt(workersToWarpIn);
        out.writeBoolean(shouldSetupBase);
        out.writeInt(seed);
        out.writeBoolean(isSizeChallenger);
        out.writeInt(speed);
        out.writeInt(civilliansPerLevel);
        out.writeInt(type);
        out.writeInt(level);
        out.writeInt(progress);
        out.writeInt(tick);
    }
    public void load(DataInputStream in) throws IOException{
        workersToWarpIn = in.readInt();
        shouldSetupBase = in.readBoolean();
        seed = in.readInt();
        isSizeChallenger = in.readBoolean();
        speed = in.readInt();
        civilliansPerLevel = in.readInt();
        type = in.readInt();
        level = in.readInt();
        progress = in.readInt();
        tick = in.readInt();
    }
}
