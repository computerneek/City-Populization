package CityPopulization.packets;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import simplelibrary.net.packet.Packet;
public class PacketCash implements Packet{
    private static PacketCash baseInstance;
    public long cash;
    public PacketCash(){
        if(baseInstance==null){
            baseInstance = this;
        }
    }
    public PacketCash(long cash){
        this.cash = cash;
    }
    @Override
    public Packet newInstance(){
        return new PacketCash();
    }
    @Override
    public void readPacketData(DataInputStream in) throws IOException{
        cash = in.readLong();
    }
    @Override
    public Packet baseInstance(){
        return baseInstance;
    }
    @Override
    public void writePacketData(DataOutputStream out) throws IOException{
        out.writeLong(cash);
    }
    @Override
    public String toString(){
        return getClass().getName()+"(cash="+cash+")";
    }
}
