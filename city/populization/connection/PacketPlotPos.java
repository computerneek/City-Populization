package city.populization.connection;
import city.populization.world.plot.PlotPos;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import simplelibrary.net.ConnectionManager;
import simplelibrary.net.packet.Packet;
import simplelibrary.net.packet.PacketPing;
public class PacketPlotPos implements Packet {
    private PlotPos p;
    private static PacketPlotPos instance;
    public PacketPlotPos(){
        p = new PlotPos(0, 0, 0);
        if(instance==null){
            instance = this;
        }
    }
    public PacketPlotPos(PlotPos p){
        if(p==null){
            throw new IllegalArgumentException("Argument cannot be null!");
        }
        this.p = p;
        if(instance==null){
            newInstance();
        }
    }
    @Override
    public Packet newInstance() {
        return new PacketPlotPos();
    }
    @Override
    public void readPacketData(DataInputStream in) throws IOException {
        p = new PlotPos(in.readInt(), in.readInt(), in.readInt());
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
        out.writeInt(p.x);
        out.writeInt(p.y);
        out.writeInt(p.z);
    }
    public PlotPos getPos(){
        return p;
    }
}
