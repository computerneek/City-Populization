package city.populization.world;
import city.populization.world.plot.PlotOwner;
import city.populization.world.plot.PlotPos;
import city.populization.world.civilian.Family;
import city.populization.world.civilian.Civilian;
import city.populization.world.civilian.Life;
import city.populization.world.civilian.FamilyTree;
import city.populization.world.plot.Plot;
import city.populization.world.plot.type.PlotHouse;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.input.Keyboard;
import simplelibrary.Queue;
public enum Template {
    FLAT("Flat") {
        @Override
        public Zone generateZone(World world, ZoneCoords coords) {
            Zone zone = new Zone(world, coords);
            for(ChunkCoords c : zone){
                if(c.z==0){
                    generateSurfaceChunk(world, zone.getChunk(c), c);
                }else if(c.z<0){
                    generateSubterrainChunk(world, zone.getChunk(c), c);
                }
            }
            return zone;
        }
        private void generateSurfaceChunk(World world, Chunk chunk, ChunkCoords coords) {
            generateSubterrainChunk(world, chunk, coords);
            int height = (coords.getMinCoords()[2]+coords.getMaxCoords()[2])/2;
            for(PlotPos p : chunk){
                if(p.z==height){
                    world.setPlot(p, Plot.Grass);
                }else if(p.z>height){
                    world.setPlot(p, Plot.Air);
                }
            }
        }
        private void generateSubterrainChunk(World world, Chunk chunk, ChunkCoords coords) {
            ArrayList<PlotPos> lst = new ArrayList<>(1<<(coords.EXPONENT*3));
            for(PlotPos p : chunk){
                lst.add(p);
                world.setPlot(p, Plot.Stone);
            }
            Queue<Plot> toGenerate = new Queue<>();
            for(int i = 30; i>=0; i--){
                if(i<-coords.z+10) toGenerate.enqueue(Plot.Iron);
                if(i<-coords.z-3) toGenerate.enqueue(Plot.Diamond);
                if(i<-coords.z-1) toGenerate.enqueue(Plot.Gold);
                if(i<coords.z+10) toGenerate.enqueue(Plot.Dirt);
                if(i<coords.z+5) toGenerate.enqueue(Plot.Sand);
                if(i<coords.z+5) toGenerate.enqueue(Plot.Clay);
                if(i<-coords.z+20) toGenerate.enqueue(Plot.Coal);
            }
            Random rand = new Random(chunk.getChunkSeed());
            if(rand.nextInt(512)==1){
                generateCave(world, lst.get(rand.nextInt(lst.size())), rand);
            }
            if(rand.nextInt(128)==1){
                generateOilField(world, lst.get(rand.nextInt(lst.size())), rand);
            }
            while(!toGenerate.isEmpty()){
                PlotPos pos = lst.get(rand.nextInt(lst.size()));
                if(world.getPlot(pos)==Plot.Stone){
                    world.setPlot(pos, toGenerate.dequeue());
                }else{
                    toGenerate.dequeue();
                }
            }
        }
        private void generateOilField(World world, PlotPos get, Random rand) {
            Queue<PlotPos> toExpandFrom = new Queue<>();
            Queue<PlotPos> nextExpansion = new Queue<>();
            toExpandFrom.enqueue(get);
            if(world.getPlot(get)==Plot.Stone) world.setPlot(get, Plot.Oil);
            int toRun = (int) (rand.nextGaussian()*2+2);
            for(int i = 0; i<toRun; i++){
                while(!toExpandFrom.isEmpty()){
                    PlotPos pos = toExpandFrom.dequeue();
                    for(Direction d : Direction.values()){
                        if(rand.nextInt(10)==1){
                            PlotPos pos2 = pos.shift(d, (int) (rand.nextGaussian()/2+1));
                            if(world.getPlot(get)==Plot.Stone) world.setPlot(pos2, Plot.Oil);
                            nextExpansion.enqueue(pos2);
                        }
                    }
                }
                Queue<PlotPos> temp = nextExpansion;
                nextExpansion = toExpandFrom;
                toExpandFrom = temp;
            }
        }
        private void generateCave(World world, PlotPos get, Random rand){
            if(world.getPlot(get)==Plot.Stone) world.setPlot(get, Plot.Oil);
            int toRun = (int) (rand.nextGaussian()*10+rand.nextInt(10));
            Direction heading = Direction.all[rand.nextInt(Direction.all.length-2)+2];
            PlotPos pos = get;
            for(int i = 0; i<toRun; i++){
                for(Direction d : Direction.values()){
                    if(rand.nextBoolean()){
                        PlotPos pos2 = pos.shift(d, (int) (rand.nextGaussian()+1));
                        world.setPlot(pos2, Plot.Air);
                    }
                }
                pos = pos.shift(heading, 1);
                if(rand.nextBoolean()){
                    heading = heading.rotateLeft45();
                }else{
                    heading = heading.rotateRight45();
                }
                if(rand.nextBoolean()){
                }else if(rand.nextBoolean()){
                    heading = Direction.byCoords(heading.x, heading.y, heading.z+1);
                }else{
                    heading = Direction.byCoords(heading.x, heading.y, heading.z-1);
                }
            }
        }
        @Override
        public synchronized boolean generateStartingPosition(World world, PlotPos pos, Player player, Random rand) {
            pos = pos.shift(Direction.UP);
            player.setStartingZ(pos.z);
            Queue<PlotPos> used = new Queue<>();
            Queue<PlotPos> roads = new Queue<>();
            world.setPlot(pos, Plot.Road, player);
            roads.enqueue(pos);
            used.enqueue(pos);
            Queue<Plot> plotsToLocate = new Queue<>();
            plotsToLocate.enqueue(Plot.Warehouse);
            plotsToLocate.enqueue(Plot.Workshop);
            plotsToLocate.enqueue(Plot.Market);
            plotsToLocate.enqueue(Plot.TownHall);
            player.setCash(100);
            FamilyTree family = player.getFamilyTree();
            Queue<Family> q = family.createExtendedFamilyTree(rand);
            while(q.size()<5||family.count()<20||family.count()>50||q.size()>15){
                family.clear();
                q = family.createExtendedFamilyTree(rand);
            }
            for(int i = 0; i<family.count()/10+1; i++){
                plotsToLocate.enqueue(Plot.Farm);
            }
            for(int i = 0; i<q.size(); i++){
                plotsToLocate.enqueue(Plot.Hut);
            }
            while(plotsToLocate.size()>0){
                PlotPos road = roads.dequeue();
                if(road==null){
                    //If we've locked ourselves in a hole, fail
                    for(PlotPos p : used.toList()){
                        world.setPlot(p, Plot.Air);
                    }
                    family.clear();
                    return false;
                }
                ArrayList<Direction> lst = new ArrayList<>(4);
                for(Direction d : Direction.straightFlat){
                    if(world.getPlot(road.shift(d))==Plot.Air){
                        lst.add(d);
                    }
                }
                if(!lst.isEmpty()){
                    Direction d = lst.size()>1?lst.remove(rand.nextInt(lst.size())):lst.remove(0);
                    PlotPos plot = road.shift(d);
                    used.enqueue(plot);
                    if(rand.nextBoolean()){
                        world.setPlot(plot, Plot.Road, player);
                        roads.enqueue(plot);
                    }else{
                        Plot p = plotsToLocate.dequeue();
                        Family f = (p instanceof PlotHouse?q.dequeue():null);
                        world.setPlot(plot, p, (PlotOwner)(f==null?player:f.getHead()), d.reverse());
                        if(f!=null) f.setHome(plot).teleport(plot);
                    }
                    if(!lst.isEmpty()){
                        roads.enqueue(road);
                    }
                }
            }
            return true;
        }
    };
    private final String displayName;
    private Template(String displayName){
        this.displayName = displayName;
    }
    public String getName(){
        return displayName;
    }
    public abstract Zone generateZone(World world, ZoneCoords coords);
    public abstract boolean generateStartingPosition(World world, PlotPos pos, Player player, Random rand);
}
