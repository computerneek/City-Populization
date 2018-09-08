package city.populization.connection;
import simplelibrary.Queue;
import java.util.HashMap;
import java.util.Map;
import simplelibrary.net.packet.Packet;
public class ChanneledPacketSet {
    private HashMap<String, Queue<Packet>> packets = new HashMap<>();
    private HashMap<String, ChanneledPacketSet> rechanneled = new HashMap<>();
    public synchronized int size(){
        int size = 0;
        for(Queue<Packet> q : packets.values()){
            size+=q.size();
        }
        for(ChanneledPacketSet q : rechanneled.values()){
            size+=q.size();
        }
        return size;
    }
    private Queue<String> processChannel(String channel){
        Queue<String> q = new Queue<>();
        if(channel==null){
            return q;
        }
        String s = "";
        for(int i = 0; i<channel.length(); i++){
            char c = channel.charAt(i);
            if(c=='.'){
                q.enqueue(s);
                s = "";
            }else{
                s+=c;
            }
        }
        q.enqueue(s);
        return q;
    }
    private synchronized void add(String channel, Packet packet){
        add(processChannel(channel), packet);
    }
    private void add(Queue<String> channel, Packet packet){
        verifyChannel(channel.peek());
        if(channel.size()>1){
            rechanneled.get(channel.dequeue()).add(channel, packet);
            return;
        }
        String c = channel.dequeue();
        if(packet instanceof PacketChanneled){
            if(c!=null){
                rechanneled.get(c).add(packet);
            }else{
                add(packet);
            }
        }else{
            packets.get(c).enqueue(packet);
        }
    }
    public synchronized void add(Packet packet){
        if(packet instanceof PacketChanneled){
            PacketChanneled p = (PacketChanneled)packet;
            add(p.getChannel(), p.getPacket());
        }else{
            add((String)null, packet);
        }
    }
    public synchronized Packet get(String channel){
        return get(processChannel(channel));
    }
    private synchronized Packet get(Queue<String> channel){
        verifyChannel(channel.peek());
        if(channel.size()>1){
            return rechanneled.get(channel.dequeue()).get(channel);
        }else{
            return packets.get(channel.dequeue()).dequeue();
        }
    }
    private void verifyChannel(String channel) {
        if(!packets.containsKey(channel)){
            packets.put(channel, new Queue<Packet>());
        }
        if(channel!=null&&!rechanneled.containsKey(channel)){
            rechanneled.put(channel, new ChanneledPacketSet());
        }
    }
    public synchronized ChanneledPacketSet subchannelSet(String channel){
        return subchannelSet(processChannel(channel));
    }
    private ChanneledPacketSet subchannelSet(Queue<String> channel){
        verifyChannel(channel.peek());
        if(channel.size()>1){
            return rechanneled.get(channel.dequeue()).subchannelSet(channel);
        }
        String c = channel.dequeue();
        ChanneledPacketSet subset = c==null?new ChanneledPacketSet():rechanneled.get(c);
        if(c!=null){
            rechanneled.put(c, new ChanneledPacketSet());
        }
        Queue<Packet> q = packets.get(c);
        Packet p;
        while((p = q.dequeue())!=null||q.size()>0){
            if(p!=null){
                subset.add(p);
            }
        }
        return subset;
    }
    public synchronized Queue<String> getReceivedChannels() {
        Queue<String> receivedChannels = new Queue<>();
        for(Map.Entry<String, Queue<Packet>> s : packets.entrySet()){
            if(s.getValue().size()>0||(rechanneled.containsKey(s.getKey())&&rechanneled.get(s.getKey()).getReceivedChannels().size()>0)){
                receivedChannels.enqueue(s.getKey());
            }
        }
        return receivedChannels;
    }
    public synchronized Queue<String> getAllReceivedChannels() {
        Queue<String> receivedChannels = getReceivedChannels();
        for(Map.Entry<String, ChanneledPacketSet> s : rechanneled.entrySet()){
            Queue<String> channels = s.getValue().getAllReceivedChannels();
            String c = s.getKey();
            while(channels.size()>0){
                receivedChannels.enqueue(c+"."+channels.dequeue());
            }
        }
        return receivedChannels;
    }
    public synchronized Queue<Packet> asList(String channel){
        return asList(processChannel(channel));
    }
    private Queue<Packet> asList(Queue<String> channel){
        verifyChannel(channel.peek());
        if(channel.size()>1){
            return rechanneled.get(channel.dequeue()).asList(channel);
        }else{
            return packets.get(channel.dequeue()).copy();
        }
    }
}
