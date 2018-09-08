package city.populization.connection;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import simplelibrary.net.ConnectionManager;
import simplelibrary.net.packet.Packet;
import simplelibrary.net.packet.PacketPing;
public class PacketChanneled implements Packet {
    private Packet p;
    private String channel;
    private static PacketChanneled instance;
    public PacketChanneled(){
        channel = "X";
        p = new PacketPing();
        if(instance==null){
            instance = this;
        }
    }
    public PacketChanneled(String channel, Packet p){
        if(channel==null||p==null){
            throw new IllegalArgumentException("Arguments cannot be null!");
        }
        this.channel = channel;
        this.p = p;
        if(instance==null){
            newInstance();
        }
    }
    @Override
    public Packet newInstance() {
        return new PacketChanneled();
    }
    @Override
    public void readPacketData(DataInputStream in) throws IOException {
        channel = in.readUTF();
        p = ConnectionManager.readPacket(in);
    }
    @Override
    public Packet baseInstance() {
        if(instance==null){
            newInstance();
        }
        return instance;
    }
    @Override
    public void writePacketData(DataOutputStream out) throws IOException {
        out.writeUTF(channel);
        ConnectionManager.sendPacket(out, p);
    }
    public String getChannel(){
        return channel;
    }
    public Packet getPacket(){
        return p;
    }
}
