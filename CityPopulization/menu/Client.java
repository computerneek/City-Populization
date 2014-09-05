package CityPopulization.menu;
import CityPopulization.VersionManager;
import CityPopulization.packets.PacketPlot;
import CityPopulization.packets.PacketPlotRequest;
import CityPopulization.world.player.Player;
import CityPopulization.world.player.Race;
import java.io.IOException;
import simplelibrary.Sys;
import simplelibrary.config2.Config;
import simplelibrary.error.ErrorCategory;
import simplelibrary.error.ErrorLevel;
import simplelibrary.net.ConnectionManager;
import simplelibrary.net.packet.Packet;
import simplelibrary.net.packet.PacketConfig;
import simplelibrary.net.packet.PacketInteger;
import simplelibrary.net.packet.PacketString;
public class Client {
    public boolean ready;
    public final ConnectionManager client;
    public Race race;
    private boolean shouldClose;
    private boolean inited;
    private Player player;
    public Client(ConnectionManager client){
        this.client = client;
        startThread();
    }
    public void setPlayer(Player player){
        this.player = player;
    }
    public void prepare(){
        client.send(new PacketString("Prepare"));
    }
    public boolean isConnected(){
        return !client.isClosed();
    }
    public void close(){
        shouldClose = true;
    }
    public void send(MenuOpenServer menu){
        while(!inited){
            try{
                Thread.sleep(1);
            }catch(InterruptedException ex){}
        }
        doSend(menu);
    }
    public synchronized void doSend(MenuOpenServer menu){
        send("Template", menu.template.getIndex());
        send("Speed", menu.gameSpeed.getIndex());
        send("Mode", menu.gameMode.getIndex());
        send("Difficulty", menu.gameDifficulty.getIndex());
        send("Style", menu.settingsStyle.getIndex());
        send("Distance", (int)Math.round(menu.playerDistance.getValue()));
        send("Goal", menu.theGoal.save());
    }
    public void send(String key, int value){
        client.send(new PacketString(key));
        client.send(new PacketInteger(value));
    }
    public void send(String key, String value){
        client.send(new PacketString(key));
        client.send(new PacketString(value));
    }
    public void send(String key, Config value){
        client.send(new PacketString(key));
        client.send(new PacketConfig(value));
    }
    private void startThread(){
        new Thread(){
            public void run(){
                try{
                    doThread();
                }catch(Throwable twbl){
                    Sys.error(ErrorLevel.severe, null, twbl, ErrorCategory.InternetIO);
                }finally{
                    try{
                        client.close();
                    }catch(IOException ex){}
                }
            }
        }.start();
    }
    private void doThread() throws IOException{
        doConnectionInit();
        doConnectionWatching();
    }
    private synchronized void doConnectionWatching(){
        while(!client.isClosed()){
            while(!client.inboundPackets.isEmpty()){
                processPacket();
            }
            try{
                wait(50);
            }catch(InterruptedException ex){}
        }
    }
    private synchronized void doConnectionInit() throws IOException{
        inited = true;
        client.send(new PacketString("City Populization"));
        client.send(new PacketString(VersionManager.currentVersion));
        if(!"City Populization".equals(getString(client.receive()))){
            client.close();
            return;
        }
        int ID = VersionManager.getVersionID(getString(client.receive()));
        if(ID>=0&&ID<VersionManager.getVersionID(VersionManager.currentVersion)){
            client.send(new PacketString("client outdated"));
            client.close();
        }
    }
    private String getString(Packet packet){
        if(packet!=null&&packet instanceof PacketString){
            return ((PacketString)packet).value;
        }
        return null;
    }
    private void processPacket(){
        processPacket(client.receive());
    }
    private void processPacket(Packet packet){
        if(packet instanceof PacketString){
            String val = ((PacketString)packet).value;
            switch(val){
                case "Race":
                    race = Race.getByName(((PacketString)client.receive()).value);
                    break;
                case "Ready":
                    ready = true;
                    break;
                default:
                    throw new AssertionError(val);
            }
        }else if(packet instanceof PacketPlotRequest){
            PacketPlotRequest pkt = (PacketPlotRequest)packet;
            player.generatePlot(pkt.x, pkt.y, pkt.z);
        }else{
            throw new UnsupportedOperationException(packet.getClass().getName());
        }
    }
}
