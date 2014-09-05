package CityPopulization.packets;
import CityPopulization.world.plot.Plot;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import simplelibrary.config2.Config;
import simplelibrary.net.packet.Packet;
public class PacketPlot implements Packet{
    private static PacketPlot baseInstance;
    public Config value;
    public PacketPlot(){
        if(baseInstance==null){
            baseInstance = this;
        }
    }
    public PacketPlot(int x, int y, int z, Plot plot){
        value = plot.save();
        value.set("x", x);
        value.set("y", y);
        value.set("z", z);
    }
    @Override
    public Packet newInstance(){
        return new PacketPlot();
    }
    @Override
    public void readPacketData(DataInputStream in) throws IOException{
        value = Config.newConfig().load(in);
    }
    @Override
    public Packet baseInstance(){
        return baseInstance;
    }
    @Override
    public void writePacketData(DataOutputStream out) throws IOException{
        value.save(out);
    }
    @Override
    public String toString(){
        return getClass().getName()+"(value="+value+")";
    }
}
