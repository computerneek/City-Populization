package city.populization.world.civilian;
import java.util.Random;
public class Lifestyle {
    private double activity;
    private final Life life;
    Random rand;
    public Lifestyle(Life life, Random rand) {
        //Lifestyle variances are more severe in older ages
        this.life = life;
        this.rand = new Random(rand.nextLong());
        double modifier = Math.sqrt(life.getAge())+0.25;
        //Now we generate initial values
        activity = rand.nextDouble()*modifier;
    }
    public void tick() {
        float activity = life.getActivity();
        if(activity>this.activity){//If they're very active, their lifestyle gradually becomes more active too.  Very gradually.
            this.activity = this.activity+(0.001*(activity-this.activity));
        }else{
            this.activity = this.activity-(0.001*(this.activity-activity));//If they're not active, their lifestyle becomes less active.  Very gradually.
        }
    }
    public double getSleepingEnergy(){
        return Math.sqrt(activity)*getSleepEfficiency();
    }
    public void sleeping() {}
    private double getSleepEfficiency() {
        return life.getHealthFactor()/2;//The healthier they are, the better they sleep...  and the more time they can spend being healthy.
    }
}
