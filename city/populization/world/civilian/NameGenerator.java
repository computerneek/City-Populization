package city.populization.world.civilian;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
public class NameGenerator {
    private static Set<String> workingSurnames = new HashSet<>();
    private static Set<String> workingMaleGivenNames = new HashSet<>();
    private static Set<String> workingFemaleGivenNames = new HashSet<>();
    private static Set<String> workingGivenNames = new HashSet<>();
    private static List<String> surnames;
    private static List<String> maleNames;
    private static List<String> femaleNames;
    private static List<String> givenNames;
    private static List<String> allFemale;
    private static List<String> allMale;
    public synchronized static List<String> getMaleGivenNames(){
        return maleNames;
    }
    public synchronized static List<String> getSurnames(){
        return surnames;
    }
    public synchronized static List<String> getFemaleGivenNames(){
        return femaleNames;
    }
    public synchronized static List<String> getGivenNames(){
        return givenNames;
    }
    public synchronized static void addSurname(String... surnames){
        for(String s : surnames){
            addSurname(s);
        }
    }
    public synchronized static void addSurname(String surname){
        workingSurnames.add(surname);
    }
    public synchronized static void addMaleName(String... names){
        for(String s : names){
            addMaleName(s);
        }
    }
    public synchronized static void addMaleName(String name){
        if(workingFemaleGivenNames.contains(name)){
            workingGivenNames.add(name);
            workingFemaleGivenNames.remove(name);
        }else if(!workingGivenNames.contains(name)){
            workingMaleGivenNames.add(name);
        }
    }
    public synchronized static void addFemaleName(String... names){
        for(String s : names){
            addFemaleName(s);
        }
    }
    public synchronized static void addFemaleName(String name){
        if(workingMaleGivenNames.contains(name)){
            workingGivenNames.add(name);
            workingMaleGivenNames.remove(name);
        }else if(!workingGivenNames.contains(name)){
            workingFemaleGivenNames.add(name);
        }
    }
    public synchronized static void addName(String... names){
        for(String s : names){
            addName(s);
        }
    }
    public synchronized static void addName(String name){
        workingGivenNames.add(name);
        workingFemaleGivenNames.remove(name);
        workingMaleGivenNames.remove(name);
    }
    public synchronized static void finalizeNames() {
        if(workingGivenNames==null||workingSurnames==null||workingMaleGivenNames==null||workingFemaleGivenNames==null){
            throw new IllegalStateException("Names already finalized!");
        }
        maleNames = Collections.unmodifiableList(new ArrayList<>(workingMaleGivenNames));
        surnames = Collections.unmodifiableList(new ArrayList<>(workingSurnames));
        femaleNames = Collections.unmodifiableList(new ArrayList<>(workingFemaleGivenNames));
        givenNames = Collections.unmodifiableList(new ArrayList<>(workingGivenNames));
        allFemale = new ArrayList<>(femaleNames);
        allMale = new ArrayList<>(maleNames);
        allFemale.addAll(givenNames);
        allMale.addAll(givenNames);
        allFemale = Collections.unmodifiableList(allFemale);
        allMale = Collections.unmodifiableList(allMale);
        workingSurnames = null;
        workingMaleGivenNames = null;
        workingFemaleGivenNames = null;
        workingGivenNames = null;
        System.out.println("Loaded "+surnames.size()+" surnames, "+maleNames.size()+" male names, "+femaleNames.size()+" female names, and "+givenNames.size()+" gender-neutral names.");
    }
    static{
        //<editor-fold defaultstate="collapsed" desc="A">
        addSurname("Aguilera", "Augenbagh");
        addFemaleName("Alexandra", "Alice", "Alicia", "Alisa", "Apryl", "Aria", "Avril");
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="B">
        addSurname("Beck", "Berry");
        addMaleName("Bryan");
        addFemaleName("Barbara", "Brianna");
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="C">
        addSurname("Church", "Clarkson");
        addFemaleName("Carrie", "Cathy", "Christina");
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="D">
        addSurname("Depalma", "Dolan");
        addMaleName("David", "Donald");
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="E">
        addMaleName("Eric");
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="F">
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="G">
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="H">
        addSurname("Hubler");
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="I">
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="J">
        addSurname("Josefik");
        addMaleName("James", "Jeremiah", "Jerry", "Jim", "John", "Jonathan");
        addFemaleName("Jade", "Jeanine", "Jennifer");
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="K">
        addSurname("Kirk");
        addMaleName("Kenneth");
        addFemaleName("Kelly", "Kelsey");
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="L">
        addSurname("Lambert", "Lavigne");
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="M">
        addSurname("Marie");
        addFemaleName("Marianna", "Marie", "Michelle", "Miranda");
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="N">
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="O">
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="P">
        addSurname("Powell");
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Q">
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="R">
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="S">
        addMaleName("Sterling");
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="T">
        addFemaleName("Trisha");
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="U">
        addSurname("Underwood", "Upton");
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="V">
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="W">
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="X">
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Y">
        //</editor-fold>
        //<editor-fold defaultstate="collapsed" desc="Z">
        addSurname("Zimmer");
        //</editor-fold>
    }
    public synchronized static String generateSurname(Random rand){
        return surnames.get(rand.nextInt(surnames.size()));
    }
    public synchronized static String generateName(Random rand, boolean isFemale){
        if(isFemale){
            return allFemale.get(rand.nextInt(allFemale.size()));
        }else{
            return allMale.get(rand.nextInt(allMale.size()));
        }
    }
}
