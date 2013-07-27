package CityPopulization;
public class Direction{
    public static final String[] directions = new String[]{"Up", "Right", "Down", "Left"};
    public static String getName(int direction){
        return directions[direction-1];
    }
}
