package city.populization.core;
import city.populization.connection.ChanneledPacketSet;
import city.populization.connection.Connection;
import city.populization.world.WorldData;
import java.io.IOException;
import java.util.ArrayList;
import simplelibrary.config2.Config;
import simplelibrary.net.authentication.Authentication;
import simplelibrary.net.packet.Packet;
import simplelibrary.net.packet.PacketAuthenticationConfirmed;
import simplelibrary.net.packet.PacketConfig;
import simplelibrary.net.packet.PacketString;
public class Client {
    public final Connection connection;
    private final ServerSide server;
    private String username;
    public boolean isConnected(){
        return !connection.connection.isClosed();
    }
    public Client(Connection c, ServerSide s){
        this.connection = c;
        this.server = s;
        Authentication auth = c.connection.getAuthentication();
        if(auth!=null){
            Config data = auth.getAuthData();
            username = data.get("username");
        }
        if(server.getWorld()!=null){
            server.getWorld().addClient(this);
        }
    }
    public boolean isAdmin(){
        Authentication auth = connection.connection.getAuthentication();
        return auth!=null&&auth.getAuthData().get("isAdmin", false);
    }
    public String getUsername() {
        return username;
    }
    public void tick() {
        if(!isConnected()){
            return;
        }
        processPackets(connection.readAll());
    }
    public void notifyWorldChange() {
        connection.send(new PacketString("WORLD_CHANGE"), "world");
    }
    private void processPackets(ChanneledPacketSet packets) {
        ArrayList<String> channels = packets.getReceivedChannels().toList();
        for(String s : channels){
            processChannel(s, packets.subchannelSet(s));
        }
    }
    private void processChannel(String s, ChanneledPacketSet subchannelSet) {
        if(s==null){
            processChannelFree(subchannelSet);
        }else if(s.equals("menu")){
            for(String c : subchannelSet.getReceivedChannels().toList()){
                processMenuChannel(c, subchannelSet.subchannelSet(c));
            }
        }else if(s.equals("world")){
            for(String c : subchannelSet.getReceivedChannels().toList()){
                processWorldChannel(c, subchannelSet.subchannelSet(c));
            }
        }else{
            throw new AssertionError("Unknown channel "+s+"!");
        }
    }
    private void processChannelFree(ChanneledPacketSet subchannelSet){
        Packet p;
        while((p=subchannelSet.get(null))!=null){
            if(p instanceof PacketString){
                PacketString packet = (PacketString)p;
                if(packet.value.equals("disconnect")){
                    try {
                        connection.connection.close();
                    } catch (IOException ex) {}
                }else{
                    throw new AssertionError("Unknown channeless packet data:  "+packet.value);
                }
            }else if(p.getClass()==PacketAuthenticationConfirmed.class){
                //Simply ignore it, we don't care- just means the other side knows it's authenticated
            }else{
                throw new AssertionError("Unknown channeless packet type:  "+p.toString());
            }
        }
    }
    public void disconnect() {
        connection.send(new PacketString("disconnect"));
        try {
            connection.connection.close();
        } catch (IOException ex) {}
    }
    private void processMenuChannel(String s, ChanneledPacketSet subchannelSet) {
        if("singleplayer".equals(s)){
            for(String c : subchannelSet.getReceivedChannels().toList()){
                processMenuSingleplayerChannel(c, subchannelSet.subchannelSet(c));
            }
        }else{
            throw new AssertionError("Unknown channel menu"+(s==null?"":"."+s)+"!");
        }
    }
    private void processMenuSingleplayerChannel(String s, ChanneledPacketSet subchannelSet) {
        if(s==null){
            processMenuSingleplayer(subchannelSet);
        }else if(s.equals("create")){
            processMenuSingleplayerCreate(subchannelSet);
        }else if(s.equals("delete")){
            processMenuSingleplayerDelete(subchannelSet);
        }else if(s.equals("play")){
            processMenuSingleplayerPlay(subchannelSet);
        }else{
            throw new AssertionError("Unknown channel menu.singleplayer."+s+"!");
        }
    }
    private void processMenuSingleplayer(ChanneledPacketSet subchannelSet) {
        Packet p;
        while((p=subchannelSet.get(null))!=null){
            if(p.getClass()==PacketString.class){
                PacketString packet = (PacketString)p;
                if(packet.value.equals("REQUEST_WORLDLIST")){
                    if(isAdmin()){
                        server.transmitSingleplayerWorldList(this);
                    }else{
                        connection.send(new PacketString("ACCESS_DENIED"), "menu.singleplayer");
                    }
                }else{
                    throw new AssertionError("Unknown String packet data on channel menu.singleplayer:  "+packet.value);
                }
            }else{
                throw new AssertionError("Unknown packet type on channel menu.singleplayer:  "+p.toString());
            }
        }
    }
    private void processMenuSingleplayerCreate(ChanneledPacketSet subchannelSet){
        Packet p;
        while((p=subchannelSet.get(null))!=null){
            if(p.getClass()==PacketConfig.class){
                if(isAdmin()){
                    PacketConfig config = (PacketConfig)p;
                    WorldData d = new WorldData().load(config.value);
                    server.createSingleplayerWorld(d);
                }else{
                    connection.send(new PacketString("ACCESS_DENIED"), "menu.singleplayer.create");
                }
            }else{
                throw new AssertionError("Unknown packet type on channel menu.singleplayer.create:  "+p.toString());
            }
        }
    }
    private void processMenuSingleplayerDelete(ChanneledPacketSet subchannelSet){
        Packet p;
        while((p=subchannelSet.get(null))!=null){
            if(p.getClass()==PacketConfig.class){
                if(isAdmin()){
                    PacketConfig config = (PacketConfig)p;
                    WorldData d = new WorldData().load(config.value);
                    server.deleteSingleplayerWorld(d);
                }else{
                    connection.send(new PacketString("ACCESS_DENIED"), "menu.singleplayer.delete");
                }
            }else{
                throw new AssertionError("Unknown packet type on channel menu.singleplayer.delete:  "+p.toString());
            }
        }
    }
    private void processMenuSingleplayerPlay(ChanneledPacketSet subchannelSet){
        Packet p;
        while((p=subchannelSet.get(null))!=null){
            if(p.getClass()==PacketConfig.class){
                if(isAdmin()){
                    PacketConfig config = (PacketConfig)p;
                    WorldData d = new WorldData().load(config.value);
                    server.playSingleplayerWorld(d);
                }else{
                    connection.send(new PacketString("ACCESS_DENIED"), "menu.singleplayer.play");
                }
            }else{
                throw new AssertionError("Unknown packet type on channel menu.singleplayer.play:  "+p.toString());
            }
        }
    }
    private void processWorldChannel(String s, ChanneledPacketSet subchannelSet){
        if("time".equals(s)){
            for(String c : subchannelSet.getReceivedChannels().toList()){
                processWorldTimeChannel(c, subchannelSet.subchannelSet(c));
            }
        }else{
            throw new AssertionError("Unknown channel world"+(s==null?"":"."+s)+"!");
        }
    }
    private void processWorldTimeChannel(String s, ChanneledPacketSet subchannelSet){
        if(s==null){
            processWorldTime(subchannelSet);
        }else{
            throw new AssertionError("Unknown channel world.time"+(s==null?"":"."+s)+"!");
        }
    }
    private void processWorldTime(ChanneledPacketSet subchannelSet){
        Packet p;
        while((p=subchannelSet.get(null))!=null){
            if(p.getClass()==PacketString.class){
                PacketString packet = (PacketString) p;
                String val = packet.value;
                if("PAUSE_REQUEST".equals(val)){
                    if(isAdmin()){
                        server.world.pause();
                    }
                }else{
                    throw new AssertionError("Unknown String packet data on channel world.time:  "+val);
                }
            }else{
                throw new AssertionError("Unknown packet type on channel world.time:  "+p.toString());
            }
        }
    }
    public boolean isAuthorized() {
        Authentication auth = connection.connection.getAuthentication();
        return auth!=null&&(auth.getAuthData().get("isAdmin", false)||auth.getAuthData().get("isVerified", false));
    }
}
