package city.populization.connection;
import city.populization.world.Direction;
import city.populization.world.Player;
import city.populization.world.plot.PlotOwner;
import city.populization.world.plot.PlotPos;
import city.populization.world.World;
import city.populization.world.plot.Plot;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import simplelibrary.net.packet.Packet;
public class PacketPlot implements Packet {
    private PlotPos p;
    private static PacketPlot instance;
    private Direction heading;
    private String owner;
    private boolean ownerIsPlayer;
    private int level;
    private String type;
    public PacketPlot(){
        p = new PlotPos(0, 0, 0);
        heading = Direction.NOWHERE;
        owner = "";
        type = Plot.Air.name;
        if(instance==null){
            instance = this;
        }
    }
    public PacketPlot(World world, PlotPos p, PlotPos toSend, Player player){
        if(p==null){
            throw new IllegalArgumentException("Argument cannot be null!");
        }
        this.p = toSend;
        this.level = world.getLevel(p);
        this.heading = world.getHeading(p);
        PlotOwner o = world.getOwner(p);
        this.owner = o==null||o==player?"":o.getName();
        this.ownerIsPlayer = o!=null&&o instanceof Player;
        this.type = world.getPlot(p).name;
        if(instance==null){
            newInstance();
        }
    }
    @Override
    public Packet newInstance() {
        return new PacketPlot();
    }
    @Override
    public void readPacketData(DataInputStream in) throws IOException {
        p = new PlotPos(in.readInt(), in.readInt(), in.readInt());
        heading = Direction.byCoords(in.readByte(), in.readByte(), in.readByte());
        owner = in.readUTF();
        ownerIsPlayer = in.readBoolean();
        level = in.readInt();
        type = in.readUTF();
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
        out.writeByte(heading.x);
        out.writeByte(heading.y);
        out.writeByte(heading.z);
        out.writeUTF(owner);
        out.writeBoolean(ownerIsPlayer);
        out.writeInt(level);
        out.writeUTF(type);
    }
    public PlotPos getPos(){
        return p;
    }
    public Direction getHeading(){
        return heading;
    }
    public PlotOwner getOwner(Player local){
        if(owner.isEmpty()) return ownerIsPlayer?local:null;
        return new PlotOwner() {
            @Override
            public String getName() {
                return owner;
            }
            public boolean isPlayer(){
                return ownerIsPlayer;
            }
        };
    }
    public int getLevel(){
        return level;
    }
    public Plot getType(){
        return Plot.byName(type);
    }
}
