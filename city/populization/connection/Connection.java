package city.populization.connection;
import simplelibrary.Queue;
import java.util.ArrayList;
import java.util.HashMap;
import simplelibrary.net.ConnectionManager;
import simplelibrary.net.packet.Packet;
public class Connection {
    public final ConnectionManager connection;
    private ChanneledPacketSet stored = new ChanneledPacketSet();
    private final Object waitor = new Object();
    public Connection(ConnectionManager connection){
        this.connection = connection;
        Thread t = new Thread(){
            public void run(){
                ConnectionManager connection = Connection.this.connection;
                while(!connection.isClosed()||!connection.inboundPackets.isEmpty()){
                    Packet p = connection.receive();
                    synchronized(waitor){
                        stored.add(p);
                        waitor.notifyAll();
                    }
                }
            }
        };
        t.setName("Connection Receiver Thread");
        t.start();
    }
    public Queue<String> getReceivedChannels(){
        return stored.getReceivedChannels();
    }
    public void send(String channel, Packet p){
        if(channel==null){
            connection.send(p);
        }else{
            connection.send(new PacketChanneled(channel, p));
        }
    }
    public void send(Packet p, String... channels){
        for(int i = channels.length-1; i>=0; i--){
            String s = channels[i];
            if(s!=null){
                p = new PacketChanneled(s, p);
            }
        }
        connection.send(p);
    }
    public Packet receive(String channel){
        synchronized(waitor){
            while(!stored.getReceivedChannels().toList().contains(channel)&&!connection.isClosed()||!connection.inboundPackets.isEmpty()){
                try {
                    waitor.wait();
                } catch (InterruptedException ex) {}
            }
            if(!stored.getReceivedChannels().toList().contains(channel)){
                return null;
            }else{
                return stored.get(channel);
            }
        }
    }
    public Packet get(String channel){
        return stored.get(channel);
    }
    public ChanneledPacketSet readChannel(String channel){
        return stored.subchannelSet(channel);
    }
    public ChanneledPacketSet readAll(){
        synchronized(waitor){
            ChanneledPacketSet s = stored;
            stored = new ChanneledPacketSet();
            return s;
        }
    }
}
