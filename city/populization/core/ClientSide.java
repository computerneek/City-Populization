package city.populization.core;
import city.populization.menu.MenuWorld;
import city.populization.menu.MenuUsername;
import city.populization.menu.MenuAuthenticating;
import city.populization.connection.ChanneledPacketSet;
import city.populization.connection.Connection;
import city.populization.connection.PacketPlot;
import city.populization.connection.PacketPlotPos;
import city.populization.menu.ListComponentWorld;
import city.populization.menu.MenuMain;
import city.populization.menu.MenuSingleplayer;
import city.populization.world.Player;
import city.populization.world.World;
import city.populization.world.WorldData;
import city.populization.world.plot.Plot;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.opengl.Display;
import simplelibrary.config2.Config;
import simplelibrary.net.packet.Packet;
import simplelibrary.net.packet.PacketAuthenticated;
import simplelibrary.net.packet.PacketAuthenticationFailed;
import simplelibrary.net.packet.PacketAuthenticationRequired;
import simplelibrary.net.packet.PacketConfig;
import simplelibrary.net.packet.PacketLong;
import simplelibrary.net.packet.PacketString;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
public class ClientSide {
    public static GUI gui;
    public Connection connection;
    public World world;
    private MenuSingleplayer singleplayerMenu;
    public final ServerSide server;
    public Player localPlayer;
    private boolean started;
    private String username = null;
    private String sessionID = null;
    private long SIDexpiration = Long.MAX_VALUE;
    private final Thread t = new Thread(){
        public void run(){
            while(gui.helper.running){
                while(gui==null){
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ex) {}
                    if(!gui.helper.running){
                        return;
                    }
                }
                doTick();
                Display.sync(20);
            }
        }
    };
    private boolean isLocal;
    public ClientSide(ServerSide server) {
        this.server = server;
    }
    public void tick() {
        if(!started){
            started = true;
            t.setName("ClientSide tick thread");
            t.start();
        }
    }
    public void doTick(){
        if((connection==null||connection.connection.isClosed())&&!server.server.isClosed()){
            connection = server.getLocalClientConnection();
            isLocal = true;
        }
        processPackets();
        gui.tick();
    }
    public World getWorld(){
        return world;
    }
    void onShutdown() {
        connection.send(null, new PacketString("disconnect"));
        try {
            connection.connection.close();
        } catch (IOException ex) {}
    }
    private void processPackets() {
        ChanneledPacketSet packets = connection.readAll();
        processPackets(packets);
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
        }else if(s.equals("world")){
            for(String c : subchannelSet.getReceivedChannels().toList()){
                processWorldChannel(c, subchannelSet.subchannelSet(c));
            }
        }else if(s.equals("menu")){
            for(String c : subchannelSet.getReceivedChannels().toList()){
                processMenuChannel(c, subchannelSet.subchannelSet(c));
            }
        }else{
            throw new AssertionError("Unknown channel "+s+"!");
        }
    }
    private void processChannelFree(ChanneledPacketSet subchannelSet){
        Packet p;
        while((p=subchannelSet.get(null))!=null){
            if(p.getClass()==PacketString.class){
                PacketString packet = (PacketString)p;
                if(packet.value.equals("disconnect")){
                    try {
                        connection.connection.close();
                    } catch (IOException ex) {}
                }else{
                    throw new AssertionError("Unknown channeless String packet data:  "+packet.value);
                }
            }else if(p.getClass()==PacketAuthenticationRequired.class){
                if(isLocal){
                    connection.connection.authenticate(getUsername(), server.getPassword());
                }else{
                    Config auth = Config.newConfig();
                    auth.set("sid", getSessionID());
                    auth.set("username", getUsername());
                    connection.connection.authenticate(auth);
                    gui.open(new MenuAuthenticating(this, new MenuMain(this, null)));
                }
            }else if(p.getClass()==PacketAuthenticated.class){
                if(gui.menu.getClass()==MenuAuthenticating.class){
                    gui.open(gui.menu.parent);//Server authentication passed.
                }
            }else if(p.getClass()==PacketAuthenticationFailed.class){
                if(gui.menu.getClass()==MenuAuthenticating.class){
                    try {
                        connection.connection.close();
                    } catch (IOException ex) {}
                    gui.open(gui.menu.parent);
                    //Server authentication failed!  So we disconnect... causing a reversion to the internal server.
                }
            }else{
                throw new AssertionError("Unknown channeless packet type:  "+p.toString());
            }
        }
    }
    private void processWorldChannel(String s, ChanneledPacketSet subchannelSet) {
        if(world==null){
            world = new World();
            localPlayer = new PlayerLocal(world);
        }
        if(s==null){
            processWorld(subchannelSet);
        }else if(s.equals("time")){
            processWorldTime(subchannelSet);
        }else if(s.equals("player")){
            for(String c : subchannelSet.getReceivedChannels().toList()){
                processWorldPlayerChannel(c, subchannelSet.subchannelSet(c));
            }
        }else if(s.equals("plot")){
            for(String c : subchannelSet.getReceivedChannels().toList()){
                processWorldPlotChannel(c, subchannelSet.subchannelSet(c));
            }
        }else{
            throw new AssertionError("Unknown channel world."+s+"!");
        }
    }
    private void processWorld(ChanneledPacketSet subchannelSet) {
        Packet p;
        while((p=subchannelSet.get(null))!=null){
            if(p.getClass()==PacketString.class){
                PacketString packet = (PacketString)p;
                if(packet.value.equals("WORLD_CHANGE")){
                    world = new World();
                    localPlayer = new PlayerLocal(world);
                    Menu menu = gui.menu;
                    while(menu.parent!=null){
                        menu = menu.parent;
                    }
                    gui.open(new MenuWorld(this, menu));
                }else{
                    throw new AssertionError("Unknown channeless String packet data:  "+packet.value);
                }
            }
        }
    }
    private void processWorldTime(ChanneledPacketSet subchannelSet){
        Packet p;
        while((p=subchannelSet.get(null))!=null){
            if(p instanceof PacketLong){
                world.setTime(((PacketLong)p).value);
            }else{
                throw new AssertionError("Unknown packet type on channel 'world.time':  "+p.toString());
            }
        }
    }
    private void processWorldPlayerChannel(String s, ChanneledPacketSet subchannelSet) {
        if(world==null){
            world = new World();
        }
        if("local".equals(s)){
            for(String c : subchannelSet.getReceivedChannels().toList()){
                processWorldPlayerLocalChannel(c, subchannelSet.subchannelSet(c));
            }
//        if(s==null){
//            processWorld(subchannelSet);
//        }else if(s.equals("time")){
//            processWorldTime(subchannelSet);
//        }else if(s.equals("player")){
//            for(String c : subchannelSet.getReceivedChannels().toList()){
//                processWorldPlayerChannel(c, subchannelSet.subchannelSet(c));
//            }
        }else{
            throw new AssertionError("Unknown channel world.player"+(s==null?"":"."+s)+"!");
        }
    }
    private void processWorldPlayerLocalChannel(String s, ChanneledPacketSet subchannelSet) {
        if(world==null){
            world = new World();
        }
        if("cash".equals(s)){
            processWorldPlayerLocalCash(subchannelSet);
//        if(s==null){
//            processWorld(subchannelSet);
//        }else if(s.equals("time")){
//            processWorldTime(subchannelSet);
//        }else if(s.equals("player")){
//            for(String c : subchannelSet.getReceivedChannels().toList()){
//                processWorldPlayerChannel(c, subchannelSet.subchannelSet(c));
//            }
        }else{
            throw new AssertionError("Unknown channel world.player.local"+(s==null?"":"."+s)+"!");
        }
    }
    private void processWorldPlayerLocalCash(ChanneledPacketSet subchannelSet){
        Packet p;
        while((p=subchannelSet.get(null))!=null){
            if(p.getClass()==PacketLong.class){
                localPlayer.setCash(((PacketLong)p).value);
            }else{
                throw new AssertionError("Unknown packet type on channel 'world.player.local.cash':  "+p.toString());
            }
        }
    }
    private void processWorldPlotChannel(String s, ChanneledPacketSet subchannelSet) {
        if(world==null){
            world = new World();
        }
        if("change".equals(s)){
            processWorldPlotChange(subchannelSet);
//            for(String c : subchannelSet.getReceivedChannels().toList()){
//                processWorldPlayerLocalChannel(c, subchannelSet.subchannelSet(c));
//            }
//        if(s==null){
//            processWorld(subchannelSet);
//        }else if(s.equals("time")){
//            processWorldTime(subchannelSet);
//        }else if(s.equals("player")){
//            for(String c : subchannelSet.getReceivedChannels().toList()){
//                processWorldPlayerChannel(c, subchannelSet.subchannelSet(c));
//            }
        }else{
            throw new AssertionError("Unknown channel world.plot"+(s==null?"":"."+s)+"!");
        }
    }
    private void processWorldPlot(ChanneledPacketSet subchannelSet){
        Packet p;
        while((p=subchannelSet.get(null))!=null){
            if(p.getClass()==PacketPlot.class){
                PacketPlot packet = (PacketPlot) p;
                world.setPlot(packet.getPos(), packet.getType(), packet.getOwner(localPlayer), packet.getHeading(), packet.getLevel());
            }else{
                throw new AssertionError("Unknown packet type on channel 'world.plot':  "+p.toString());
            }
        }
    }
    private void processWorldPlotChange(ChanneledPacketSet subchannelSet){
        Packet p;
        while((p=subchannelSet.get(null))!=null){
            if(p.getClass()==PacketPlot.class){
                PacketPlot packet = (PacketPlot) p;
                localPlayer.onPlotChange(packet.getPos(), packet.getType(), packet.getOwner(localPlayer));
                world.setPlot(packet.getPos(), packet.getType(), packet.getOwner(localPlayer), packet.getHeading(), packet.getLevel());
            }else if(p.getClass()==PacketPlotPos.class){//If it's a change and it only gives us a position, we can't see it anymore
                PacketPlotPos packet = (PacketPlotPos) p;
                localPlayer.onPlotChange(packet.getPos(), Plot.Air, null);
                world.setPlot(packet.getPos(), Plot.Air);
            }else{
                throw new AssertionError("Unknown packet type on channel 'world.plot':  "+p.toString());
            }
        }
    }
    private void processMenuChannel(String s, ChanneledPacketSet subchannelSet) {
        if("singleplayer".equals(s)){
            processMenuSingleplayer(subchannelSet);
        }else{
            throw new AssertionError("Unknown channel menu"+(s==null?"":"."+s)+"!");
        }
    }
    private void processMenuSingleplayer(ChanneledPacketSet subchannelSet){
        Packet p;
        while((p=subchannelSet.get(null))!=null){
            if(p.getClass()==PacketConfig.class){
                WorldData d = new WorldData().load(((PacketConfig)p).value);
                if(gui.menu.getClass()==MenuSingleplayer.class){
                    ((MenuSingleplayer)gui.menu).missionList.add(new ListComponentWorld(d));
                }
            }else{
                throw new AssertionError("Unknown packet type on channel 'menu.singleplayer':  "+p.toString());
            }
        }
    }
    public void onMenuSingleplayer(MenuSingleplayer menu) {
        connection.send(new PacketString("REQUEST_WORLDLIST"), "menu.singleplayer");
        singleplayerMenu = menu;
    }
    public void onCreateSingleplayer(WorldData data) {
        connection.send(new PacketConfig(data.save()), "menu.singleplayer.create");
    }
    public void requestWorldPause() {
        connection.send(new PacketString("PAUSE_REQUEST"), "world.time");
    }
    private String getUsername() {
        if(username!=null){
            return username;
        }
        MenuUsername m = new MenuUsername(this, gui.menu);
        gui.open(m);
        return m.getUsername();
    }
    private String getSessionID() {
        if(sessionID==null||sessionID.isEmpty()||SIDexpiration<System.currentTimeMillis()+10_000){
            throw new UnsupportedOperationException("Not supported yet.");//TODO execute login to the login server, set the sessionID, and return it
        }else{
            return sessionID;
        }
    }
    public void deleteWorld(ListComponentWorld comp) {
        connection.send(new PacketConfig(comp.data.save()), "menu.singleplayer.delete");
    }
    public void playWorld(ListComponentWorld comp) {
        connection.send(new PacketConfig(comp.data.save()), "menu.singleplayer.play");
    }
}
