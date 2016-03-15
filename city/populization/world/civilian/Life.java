package city.populization.world.civilian;
import city.populization.world.World;
import java.util.Random;
public class Life {
    /**
     * How long a pregnancy takes, from start to finish
     */
    private static final int PREGNANCY_DURATION = 9;
    /**
     * How long before a predicted pregnancy completion a woman goes on maternity leave
     */
    private static final int MATERNITY_TRIGGER = 7;
    /**
     * How long after pregnancy completion a woman's maternity leave lasts
     */
    private static final int MATERNITY_DURATION = 3;
    /**
     * How long anyone remains a baby
     */
    private static final int BABY_DURATION = 1;
    /**
     * How long anyone remains a toddler
     */
    private static final int TODDLER_DURATION = 2;
    /**
     * How long anyone remains a child
     */
    private static final int CHILD_DURATION = 5;
    /**
     * How long anyone remains a teen
     */
    private static final int TEEN_DURATION = 8;
    /**
     * How long anyone remains a junior
     */
    private static final int JUNIOR_DURATION = 2;
    /**
     * How long anyone remains an adult
     */
    private static final int ADULT_DURATION = 60;
    public static final int STAGE_BABY = 0;
    public static final int STAGE_TODDLER = 1;
    public static final int STAGE_CHILD = 2;
    public static final int STAGE_TEEN = 3;
    public static final int STAGE_JUNIOR = 4;
    public static final int STAGE_ADULT = 5;
    public static final int STAGE_ELDERLY = 6;
    public static final int STAGE_SENILE = 7;//Senile are troublemakers- cause accidents, arguments, etc.  Good thing they die off fast.
    public final int toddlerAt;
    public final int childAt;
    public final int teenAt;
    public final int juniorAt;
    public final int adultAt;
    public final int elderlyAt;
    private final int[] stageTimes;
    private float resilience;
    private float strength;
    //How sick someone is (part is ignored by resilience)
    private float disease;
    //How good someone's diet is, 0-1
    private float diet;
    //How active someone is
    private float activity;
    //The genetic factors involved for someone's health
    private float baseHealth;
    private float medicine;
    public static final int ticksPerDay = 24000;
    private final Random rand;
    private long age = 0;
    private float lifeLeft;
    private byte stage = 0;
    private byte actingStage = 0;
    private final boolean isFemale;
    private final World world;
    public static Life atAge(long age, World world, Random rand, boolean isFemale) {
        double days = (double)age/ticksPerDay;
        Life life = new Life(world, rand, isFemale);
        life.age = Math.max(0, age-30);
        life.lifeLeft-=life.age;
        while(life.age<age){
            if(!life.lifeStep()){
                return null;
            }
        }
        life.actingStage = life.stage;
        return life;
    }
    public static long getChildbearingStart(Random rand, boolean isFemale) {
        long time = decideTime(rand, BABY_DURATION, isFemale);
        time+=decideTime(rand, TODDLER_DURATION, isFemale);
        time+=decideTime(rand, CHILD_DURATION, isFemale);
        time+=decideTime(rand, TEEN_DURATION*(isFemale?1.1:1), isFemale);
        time+=decideTime(rand, JUNIOR_DURATION*(isFemale?1.2:1), isFemale);
        return time;
    }
    public static long getChildbearingDuration(Random rand, boolean isFemale) {
        return decideTime(rand, ADULT_DURATION*(isFemale?1.4:1), isFemale);
    }
    public Life(World world, Random r, boolean isFemale){
        this.world = world;
        rand = new Random(r.nextLong());
        this.isFemale = isFemale;
        toddlerAt = decideTime(rand, BABY_DURATION);
        childAt = decideTime(rand, TODDLER_DURATION)+toddlerAt;
        teenAt = decideTime(rand, CHILD_DURATION)+childAt;
        juniorAt = decideTime(rand, TEEN_DURATION*(isFemale?1.1:1))+teenAt;
        adultAt = decideTime(rand, JUNIOR_DURATION*(isFemale?1.2:1))+juniorAt;
        elderlyAt = decideTime(rand, ADULT_DURATION*(isFemale?1.4:1))+adultAt;
        lifeLeft = decideTime(rand, elderlyAt/ticksPerDay*(isFemale?1.8:1));
        //Senility is determined by an elderly's health dropping below 0.5.
        //If someone holds an extremely healthy ongoing lifestile, they would never hit senility- and hold a theoretically infinite life!
        stageTimes = new int[]{toddlerAt, childAt, teenAt, juniorAt, adultAt, elderlyAt};
        resilience = rand.nextFloat()*0.49f+0.5f;
        if(isFemale) increaseResilience(0.2);
        strength = (float) (rand.nextGaussian()+1);
        disease = 0;
        diet = rand.nextFloat()*0.25f+0.5f;
        baseHealth = (float) (Math.abs(rand.nextGaussian()+1)+0.5)*10;
    }
    private int decideTime(Random rand, double days){
        return decideTime(rand, days, isFemale);
    }
    private static int decideTime(Random rand, double days, boolean isFemale){
        return (int) (Math.max(Math.max(rand.nextDouble(), rand.nextDouble())/2+0.5, Math.abs(rand.nextGaussian()/2+1))*ticksPerDay*days*(isFemale?1.2:1));
    }
    public void growToStage(int stage){
        age = 0;
        for(int i = 0; i<stage&&i<stageTimes.length; i++){
            age=stageTimes[i];
        }
        this.stage = (byte) stage;
        if(stage<stageTimes.length){
            //Choose a random point in that stage
            age+=rand.nextInt(stageTimes[stage]-stageTimes[stage-1]);
        }else if(stage==stageTimes.length){
            //They're elderly, which has no cap, so make them randomly a little older.  They could go senile.
            age+=rand.nextInt(10*ticksPerDay);
        }else{
            this.stage = STAGE_ELDERLY;
            //Since STAGE_SENILE was entered, we push them until they're very likely to go senile.  We actually can't force that.
            while(age<elderlyAt*5/4){
                age+=rand.nextInt(ticksPerDay);
            }
            age+=rand.nextInt(20*ticksPerDay);
        }
    }
    public double getAgeHealthFactor(){
        int peak = elderlyAt*5/4;
        //Mere old age will never zero anyone's health, but it may pull it very low.
        //In old age, it is very difficult to hold good health, but it is possible- though only if a healthy life was lived before.
        double factor = Math.atan(1/(((age==peak?age+1:age)-peak)/100000f))+peak/(age+ticksPerDay)+0.01;
        //Health falls at the end of one's life.  At two minutes left, their health falls negative.
        double lifeLeftFactor = Math.log10((lifeLeft<1?1:lifeLeft)/(ticksPerDay/10));
        return factor*lifeLeftFactor;
    }
    public double getHealthFactor(){
        return 0.02*getAgeHealthFactor()*getDietHealthFactor()*getResilienceHealthFactor()*getActivityHealthFactor();
    }
    public double getUndeseasedHealth(){
        return baseHealth*getHealthFactor();
    }
    public double getTotalHealth(){
        return getUndeseasedHealth()+getDiseaseHealthAdjustment();
    }
    public double getDietHealthFactor() {
        return diet*diet*diet*3;
    }
    public double getResilienceHealthFactor() {
        return 4*resilience*resilience;
    }
    public double getActivityHealthFactor(){
        return Math.sqrt(activity)+0.1;//So we never zero the health
    }
    private double getDiseaseHealthAdjustment(){
        return Math.min(0, resilience-disease*(1-resilience));
    }
    private void increaseResilience(double d) {
        if(d>=1){
            d = 0.99;
        }
        resilience = (float) (1-(1-resilience)*(1-d));
    }
    private void reduceResilience(double d){
        if(d>=1){
            d = 0.99;
        }
        resilience = (float) (resilience*(1-d));
    }
    public boolean lifeStep(){
        age++;
        lifeLeft--;
        //World time; first half is day, second half is night
        int steps = (int) (world.getTime()%ticksPerDay);
        if(steps%(ticksPerDay/2)==0) actingStage = stage;
        //Their dietary health falls over time.  A 1-diet person will starve in around two weeks, if everything else holds.
        if(steps%(ticksPerDay/20)==0) diet = (float) (diet*0.99);
        //Their activeity-related health falls over time.  1-active person will take about 3-4 days to become a couch potato.
        if(steps%(ticksPerDay/80)==0) activity = (float) (activity*0.99);
        //Medicine wears off slowly.  It takes about 3-4 days for the maximum dose to do its job.
        if(steps%(ticksPerDay/80)==0) medicine = (float) (medicine*0.99);
        if(age%(ticksPerDay/4)==0) updateHealth();
        if(world.getTime()%(ticksPerDay/2)==0){
            actingStage = stage;
        }
        if(stage<stageTimes.length&&age>=stageTimes[stage]){
            stage++;
        }
        return lifeLeft>0;
    }
    private void updateHealth() {
        double health = getTotalHealth();
        if(health<1){
            //If health < 1, their health is failing, their lifespan falling
            reduceResilience(0.05);
            baseHealth*=Math.sqrt(Math.max(-1, health)+8)/3;
            lifeLeft*=Math.sqrt(Math.max(-1, health)+8)/3;
        }else if(health>10){
            //If health > 10, their health is soaring- they're so healthy they're automatically getting healthier!
            increaseResilience(0.01);
            baseHealth*=Math.sqrt(health+6)/4;
            lifeLeft*=Math.sqrt(health-1)/3;
        }
        if(disease>0){
            increaseResilience(0.1);
            if(disease<resilience/10){
                disease = 0;
            }else{
                //Any disease less than sqrt(2) is in complete remission and will deteriorate on its own
                //Any disease greater than 4 will overwhelm the maximum dose of medicine
                disease*=disease*0.5;
                //Activity makes disease worse- diverts body energy from fighting it
                disease*=1+activity;
                //Medicine tackles the disease directly, but can't do very much
                disease*=1-medicine/2;
                //Resilience is very valuable- very high resilience can fight off almost any disease, as long as it doesn't fall first
                disease*=1-resilience;
                //If they're healthy despite the illness, the illness abates a little more.  However, if they're unhealthy, it gets worse even faster!
                //5 is considered the threshhold between healthy and unhealthy.
                disease *= (1-(health-5)/100);
            }
        }
        if(activity>5){
            //They're really active- awesome!  And, healthy!
            strength*=Math.sqrt(activity+11)/4;
            baseHealth*=Math.sqrt(activity+20)/5;
            increaseResilience(0.01);
        }else if(activity<2){
            //They're inactive...   Their strength wanes away when not used.
            strength*=Math.sqrt(Math.max(-1, activity)+34)/6;
        }
    }
    public double getAge() {
        return (double)age/ticksPerDay;
    }
    public float getActivity() {
        return activity;
    }
}
