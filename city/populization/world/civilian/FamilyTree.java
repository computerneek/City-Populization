package city.populization.world.civilian;
import city.populization.world.Player;
import city.populization.world.plot.PlotPos;
import city.populization.world.World;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import simplelibrary.Queue;
public class FamilyTree {
    private final World world;
    private ArrayList<TreeMember> tree = new ArrayList<>();
    private int people;
    private final Player owner;
    public FamilyTree(World world, Player owner){
        this.world = world;
        this.owner = owner;
    }
    public TreeMember createMember(boolean isFemale, long birthTime, Random rand){
        TreeMember m = new TreeMember(isFemale, null, null, NameGenerator.generateSurname(rand), NameGenerator.generateName(rand, isFemale), birthTime, rand);
        if(m.civilian!=null){
            m.civilian.owner = owner;
        }
        tree.add(m);
        people++;
        return m;
    }
    public TreeMember createMember(boolean isFemale, TreeMember mother, TreeMember father, long birthTime, Random rand){
        long minTime = Math.max(mother==null?Long.MIN_VALUE:mother.fertilityStart, father==null?Long.MIN_VALUE:father.fertilityStart);
        long maxTime = Math.min(mother==null?Long.MAX_VALUE:mother.fertilityEnd, father==null?Long.MAX_VALUE:father.fertilityEnd);
        birthTime = Math.max(Math.min(maxTime, birthTime), minTime);
        TreeMember m = new TreeMember(isFemale, father, mother, father==null?(mother==null?NameGenerator.generateSurname(rand):mother.surname):father.surname, NameGenerator.generateName(rand, isFemale), birthTime, rand);
        if(m.civilian!=null){
            m.civilian.owner = owner;
        }
        if(mother!=null) mother.children.add(m);
        if(father!=null) father.children.add(m);
        if(mother==null&&father==null) tree.add(m);
        people++;
        return m;
    }
    public int count(){
        Set<TreeMember> tree = new HashSet<>();
        Queue<TreeMember> q = new Queue<>();
        for(TreeMember t : this.tree){
            q.enqueue(t);
        }
        while(q.size()>0){
            TreeMember t = q.dequeue();
            if(t.civilian!=null) tree.add(t);
            if(t.marriage!=null&&t.marriage.other(t).civilian!=null) tree.add(t.marriage.other(t));
            for(TreeMember c : t.children){
                if(tree.add(c)) q.enqueue(c);
            }
        }
        int count = 0;
        for(TreeMember t : tree){
            if(t.civilian!=null) count++;
        }
        return count;
    }
    public Queue<Family> createExtendedFamilyTree(Random rand) {
        TreeMember father = createMember(false, -Life.ticksPerDay*200, rand);
        Queue<TreeMember> children = new Queue<>();
        Queue<Family> q = new Queue<>();
        children.enqueue(father);
        while(!children.isEmpty()){
            TreeMember person = children.dequeue();
            if(person.fertilityStart>=world.getTime()){
                continue;
            }
            TreeMember spouse = createMember(!person.isFemale, person.birthTime, rand);
            marry(person, spouse);
            if(person.civilian!=null||spouse.civilian!=null){
                if(person.marriage.husband.civilian==null) q.enqueue(new Family(person.marriage.wife));
                else q.enqueue(new Family(person.marriage.husband));
            }
            for(long i = Math.max(person.fertilityStart, spouse.fertilityStart); i<Math.min(person.fertilityEnd, spouse.fertilityEnd)&&i<world.getTime(); i++){
                if(rand.nextInt(24000*25)==1){
                    children.enqueue(createMember(rand.nextBoolean(), person.marriage.wife, person.marriage.husband, i, rand));
                }
            }
        }
        System.out.println("Generated "+q.size()+" families; "+count()+" people");
        return q;
    }
    public void clear() {
        tree.clear();
        people = 0;
    }
    private Marriage marry(TreeMember husband, TreeMember wife) {
        if(!wife.isFemale&&husband.isFemale){
            return marry(wife, husband);
        }else if(husband.isFemale||!wife.isFemale){
            return null;
        }else{
            Marriage m = new Marriage(husband, wife);
            husband.marriage = m;
            wife.marriage = m;
            return m;
        }
    }
    public class TreeMember {
        public final String surname;
        public final String name;
        public final boolean isFemale;
        public final TreeMember father;
        public final TreeMember mother;
        Marriage marriage;
        ArrayList<TreeMember> children = new ArrayList<>();
        Civilian civilian;
        private final long birthTime;
        private final long fertilityStart;
        private final long fertilityEnd;
        private TreeMember(boolean isFemale, TreeMember father, TreeMember mother, String surname, String name, long birthTime, Random rand){
            this.mother = mother;
            this.father = father;
            this.isFemale = isFemale;
            this.surname = surname;
            this.name = name;
            this.birthTime = birthTime;
            Life life = Life.atAge(world.getTime()-birthTime, world, rand, isFemale);
            if(life!=null){
                civilian = new Civilian(world, life, this, rand);
                fertilityStart = birthTime+life.adultAt;
                fertilityEnd = birthTime+life.elderlyAt;
            }else{
                fertilityStart = Life.getChildbearingStart(rand, isFemale)+birthTime;
                fertilityEnd = Life.getChildbearingDuration(rand, isFemale)+fertilityStart;
            }
        }
    }
    public static class Marriage {
        private final TreeMember husband;
        private final TreeMember wife;
        private Marriage(TreeMember husband, TreeMember wife){
            this.husband = husband;
            this.wife = wife;
        }
        TreeMember other(TreeMember one){
            if(one==husband) return wife;
            else return husband;
        }
    }
}
