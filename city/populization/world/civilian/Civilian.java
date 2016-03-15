package city.populization.world.civilian;
import city.populization.world.Player;
import city.populization.world.World;
import city.populization.world.plot.PlotOwner;
import city.populization.world.plot.PlotPos;
import java.util.Random;
public class Civilian extends PlotOwner{
    private final Life life;
    private final Lifestyle lifestyle;
    private final FamilyTree.TreeMember familyTree;
    private final boolean isFemale;
    private final String surname;
    private final String givenName;
    private PlotPos home;
    private float x;
    private float y;
    private float z;
    public Player owner;//Whose town this civilian belongs to.  This can change.
    private boolean alive = true;
    private int deadTime = 0;
    private double energy;//Positive reflects energetic, active- can't sleep.  Negative reflects tired.
    private long rest;//How long they've been sleeping for.  They can't sleep more than 1/3 day at a time.  Increments every sleeping tick, decrements to 0 every waking tick.
    private boolean isSleeping;
    private final World world;
    private boolean isHome;
    private double peakEnergy;
    public Civilian(World world, Life life, FamilyTree.TreeMember familyTree, Random rand) {
        this.world = world;
        this.life = life;
        this.familyTree = familyTree;
        this.isFemale = familyTree.isFemale;
        this.surname = familyTree.surname;
        this.givenName = familyTree.name;
        rest = Math.max(0, rand.nextInt(Life.ticksPerDay*2/3)-Life.ticksPerDay/3);
        energy = rand.nextInt(Life.ticksPerDay/4);
        peakEnergy = energy+rand.nextInt(Life.ticksPerDay/12);
        isSleeping = rand.nextInt(10)==1;
        lifestyle = new Lifestyle(life, rand);
    }
    @Override
    public String getName() {
        return givenName+" "+surname;
    }
    public void setHome(PlotPos plot) {
        this.home = plot;
        world.addOccupant(plot, this);
    }
    public void teleport(PlotPos plot) {
        this.x = plot.x;
        this.y = plot.y;
        this.z = plot.z;
        isHome = plot.equals(home);
    }
    public void tick() {
        if(alive&&!life.lifeStep()){
            die();
        }
        if(alive){
            lifestyle.tick();
            doTick();
        }else{
            deadTime++;
            if(deadTime>Life.ticksPerDay){
                world.civilians.remove(this);
                world.passOnHome(home, this);
            }
        }
    }
    private void die() {
        alive = false;
        //For now, that's all they need to do
        //TODO:  Terminate employment
    }
    private void doTick() {
        if(isSleeping){
            rest++;
            lifestyle.sleeping();
            if(energy<Life.ticksPerDay/10){
                energy = (energy-Life.ticksPerDay/10)*0.95+Life.ticksPerDay/10;//If they were tired, they recover faster; if they don't have much energy, they get more faster
            }
            energy+=lifestyle.getSleepingEnergy()+life.getActivityHealthFactor();//The more active the lifestyle, the more energy they recover from sleep...  or the more active the person, too
            if(rest>=Life.ticksPerDay/3||energy>peakEnergy+10){
                wakeUp();
            }
            //TODO:  If they need to get up to make it to something on time, their alarm goes off
            //TODO:  If something happens nearby, light sleepers (those with really efficient sleep) may wake
        }else{
            rest = Math.max(0, rest-1);
            energy-=0.1;//They slowly lose energy through the course of the day, just by being awake
            /*
            TODO:  If unemployed and income is too low, get a job.
            TODO:  If employed, do job- show up, clock in, work, have lunch, clock out, do ... whatever
            TODO:  Movement update- if traveling, keep going!
            TODO:  Discretionary update
                If hungry, eat
                If the groceries are low, go shopping
                If lifestyle is active, go be active
                If interested, visit someone else' house
                Maybe meet someone at a store or worksite
                If it's election day, vote
            */
        }
    }
    private void wakeUp() {
        isSleeping = false;
        peakEnergy = Math.max(peakEnergy, energy);
    }
}
