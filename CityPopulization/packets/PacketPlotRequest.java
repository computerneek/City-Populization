package CityPopulization.packets;
import CityPopulization.world.plot.Plot;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import simplelibrary.net.packet.Packet;
public class PacketPlotRequest implements Packet{
    private static PacketPlotRequest baseInstance;
    public int x;
    public int y;
    public int z;
    public PacketPlotRequest(){
        if(baseInstance==null){
            baseInstance = this;
        }
    }
    public PacketPlotRequest(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    @Override
    public Packet newInstance(){
        return new PacketPlotRequest();
    }
    @Override
    public void readPacketData(DataInputStream in) throws IOException{
        x = in.readInt();
        y = in.readInt();
        z = in.readInt();
    }
    @Override
    public Packet baseInstance(){
        return baseInstance;
    }
    @Override
    public void writePacketData(DataOutputStream out) throws IOException{
        out.writeInt(x);
        out.writeInt(y);
        out.writeInt(z);
    }
    @Override
    public String toString(){
        return getClass().getName()+"(x="+x+"&y="+y+"&z="+z+")";
    }
}
