package city.populization.core;
import city.populization.connection.Connection;
import city.populization.world.World;
import city.populization.world.WorldData;
import city.populization.world.WorldLoader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import simplelibrary.Sys;
import simplelibrary.encryption.Encryption;
import simplelibrary.net.ConnectionManager;
import simplelibrary.net.authentication.Authenticator;
import simplelibrary.net.packet.PacketConfig;
import simplelibrary.net.packet.PacketString;
public class ServerSide {
    private final String password = Sys.generateRandomString(50);
    public ConnectionManager server;
    private ArrayList<Client> clients = new ArrayList<>();
    public World world;
    {
        Authenticator auth = new ServerAuthenticator(this);
        do{
            try {
                server = ConnectionManager.createServerSide(Settings.port, 10, 1000, ConnectionManager.TYPE_PACKET, auth, Encryption.UNENCRYPTED);
            } catch (IOException | IllegalArgumentException ex) {
                Settings.port++;
                if(Settings.port>65535){
                    Settings.port = Settings.DEFAULT_PORT;
                }
            }
            Settings.save();
        }while(server==null);
    }
    public World getWorld(){
        return world;
    }
    public String getPassword() {
        return password;
    }
    public void tick() {
        if(world!=null&&world.isRunning()){
            world.tick();
        }
        for (Iterator<Client> it = clients.iterator(); it.hasNext();) {
            Client c = it.next();
            c.tick();
            if(!c.isConnected()){
                it.remove();
            }
        }
        if(server!=null&&!server.connections.isEmpty()){
            clients.add(new Client(new Connection(server.connections.remove(0)), this));
        }
    }
    public void closeDown() {
        if(server!=null){
            try {
                server.close();
            } catch (IOException ex) {}
        }
        for(Client c : clients){
            c.disconnect();
        }
        if(world!=null){
            world.save();
        }
        world = null;
    }
    public void setWorld(World world){
        this.world = world;
        world.setTime(-100);
        for(Client c : clients){
            c.notifyWorldChange();
            if(world.data.isMultiplayer||c.isAdmin()){
                world.addPlayer(c);
            }else{
                world.addClient(c);
            }
        }
    }
    public Connection getLocalClientConnection() {
        return new Connection(server.createLoopback());
    }
    public void transmitSingleplayerWorldList(Client client){
        if(!client.isAdmin()){
            client.connection.send(new PacketString("ACCESS_DENIED"), "menu", "singleplayer");
        }
        WorldData[] datum = listSingleplayerWorlds();
        for(WorldData d : datum){
            client.connection.send(new PacketConfig(d.save()), "menu.singleplayer");
        }
    }
    public ArrayList<Client> getClients() {
        return new ArrayList<>(clients);
    }
    private WorldData[] listSingleplayerWorlds() {
        return WorldLoader.getSingleplayerList();
    }
    public void createSingleplayerWorld(WorldData d) {
        World w = WorldLoader.createSingleplayerWorld(d);
        setWorld(w);
    }
    public void deleteSingleplayerWorld(WorldData d) {
        WorldLoader.deleteSingleplayerWorld(d);
    }
    public void playSingleplayerWorld(WorldData d) {
        World w = WorldLoader.loadSingleplayerWorld(d);
        setWorld(w);
    }
}
