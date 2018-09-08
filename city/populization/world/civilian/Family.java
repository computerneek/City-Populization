package city.populization.world.civilian;
import city.populization.world.plot.PlotPos;
import simplelibrary.Queue;
public class Family {
    FamilyTree.TreeMember head;
    public Family(FamilyTree.TreeMember head){
        this.head = head;
    }
    public Civilian getHead() {
        return head.civilian;
    }
    public Family setHome(PlotPos plot) {
        Queue<FamilyTree.TreeMember> lst = new Queue<>();
        if(head.civilian!=null) lst.enqueue(head);
        if(head.marriage!=null&&head.marriage.other(head).civilian!=null){
            lst.enqueue(head.marriage.other(head));
        }
        for(FamilyTree.TreeMember c : head.children){
            if(c.marriage==null&&c.civilian!=null){
                lst.enqueue(c);
            }
        }
        for(FamilyTree.TreeMember t : lst.toList()){
            if(!t.civilian.owner.getWorld().civilians.contains(t.civilian)){
                t.civilian.owner.getWorld().civilians.add(t.civilian);
            }
            t.civilian.setHome(plot);
        }
        return this;
    }
    public void teleport(PlotPos plot) {
        Queue<FamilyTree.TreeMember> lst = new Queue<>();
        if(head.civilian!=null) lst.enqueue(head);
        if(head.marriage!=null&&head.marriage.other(head).civilian!=null){
            lst.enqueue(head.marriage.other(head));
        }
        for(FamilyTree.TreeMember c : head.children){
            if(c.marriage==null&&c.civilian!=null){
                lst.enqueue(c);
            }
        }
        for(FamilyTree.TreeMember t : lst.toList()){
            t.civilian.teleport(plot);
        }
    }
}
