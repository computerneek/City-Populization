package CityPopulization.menu;
import CityPopulization.Core;
import CityPopulization.VersionManager;
import CityPopulization.world.GameDifficulty;
import CityPopulization.world.WinningCondition;
import CityPopulization.world.World;
import CityPopulization.world.WorldInfo;
import CityPopulization.world.player.Player;
import CityPopulization.world.player.Race;
import CityPopulization.world.plot.Template;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import org.lwjgl.input.Keyboard;
import simplelibrary.net.ConnectionManager;
import simplelibrary.net.packet.Packet;
import simplelibrary.net.packet.PacketConfig;
import simplelibrary.net.packet.PacketInteger;
import simplelibrary.net.packet.PacketString;
import simplelibrary.opengl.gui.GUI;
import simplelibrary.opengl.gui.Menu;
import simplelibrary.opengl.gui.components.MenuComponentButton;
import simplelibrary.opengl.gui.components.MenuComponentOptionButton;
import simplelibrary.opengl.gui.components.MenuComponentSlider;
import simplelibrary.opengl.gui.components.MenuComponentTextBox;
public class MenuOpenServer extends Menu{
    public WinningCondition theGoal = WinningCondition.get("None");
    public final MenuComponentOptionButton template;
    public final MenuComponentButton goal;
    public final MenuComponentOptionButton gameSpeed;
    public final MenuComponentOptionButton gameMode;
    public final MenuComponentOptionButton gameDifficulty;
    public final MenuComponentOptionButton settingsStyle;
    public final MenuComponentSlider playerDistance;
    private final Race[] races;
    private final String[] racesS;
    private final Template[] templates;
    private final String[] templatesS;
    private final GameDifficulty[] difficulties;
    private final String[] difficultiesS;
    private final MenuComponentTextBox name;
    private final MenuComponentOptionButton race;
    private final MenuComponentButton ready;
    private final MenuComponentButton quit;
    private ConnectionManager connection;
    private boolean server;
    private ArrayList<Client> connections = new ArrayList<>();
    private int tick;
    public MenuOpenServer(GUI gui, Menu parent, ConnectionManager connection){
        super(gui, parent);
        races = Race.values();
        racesS = new String[races.length];
        for(int i = 0; i<races.length; i++){
            racesS[i]=races[i].getName();
        }
        templates = Template.values();
        templatesS = new String[templates.length];
        for(int i = 0; i<templates.length; i++){
            templatesS[i] = templates[i].getName();
        }
        difficulties = GameDifficulty.values();
        difficultiesS = new String[difficulties.length];
        for(int i = 0; i<difficulties.length; i++){
            difficultiesS[i] = difficulties[i].getName();
        }
        name = add(new MenuComponentTextBox(-0.8, -0.98, 1.6, 0.16, "Player"+System.currentTimeMillis()%1000, true));
        race = add(new MenuComponentOptionButton(-0.8, -0.78, 1.6, 0.16, "Race", true, 0, racesS));
        template = add(new MenuComponentOptionButton(-0.8, -0.58, 1.6, 0.16, "Template", connection==null, 0, templatesS));
        goal = add(new MenuComponentButton(-0.8, -0.38, 1.6, 0.16, "Goal", connection==null));
        gameSpeed = add(new MenuComponentOptionButton(-0.8, -0.18, 1.6, 0.16, "Game speed", connection==null, 0, "1x", "2x", "4x", "8x", "16x", "32x", "64x"));
        gameMode = add(new MenuComponentOptionButton(-0.8, 0.02, 1.6, 0.16, "Mode", connection==null, 0, "Standard", "Sandbox"));
        gameDifficulty = add(new MenuComponentOptionButton(-0.8, 0.22, 1.6, 0.16, "Difficulty", connection==null, 5, difficultiesS));
        settingsStyle = add(new MenuComponentOptionButton(-0.8, 0.42, 1.6, 0.16, "Settings", /*connection==null*/false, 0, "Host", "Preference"));
        playerDistance = add(new MenuComponentSlider(-0.8, 0.62, 1.56, 0.16, 10, 400, 100, connection==null));
        ready = add(new MenuComponentButton(-1.58, 0.82, 1.56, 0.16, "Ready", true));
        quit = add(new MenuComponentButton(0.02, 0.82, 1.56, 0.16, "Quit", true));
        if(connection==null){
            this.connection = openServer();
            server = true;
        }else{
            this.connection = connection;
            server = false;
            startClientThread();
        }
    }
    @Override
    public void renderBackground(){
        ready.enabled = !name.text.isEmpty()&&(server?!connections.isEmpty():true);
    }
    @Override
    public void keyboardEvent(char character, int key, boolean pressed, boolean repeat){
        if(key==Keyboard.KEY_F11&&pressed&&!repeat){
            Core.helper.setFullscreen(!Core.helper.isFullscreen());
        }
        super.keyboardEvent(character, key, pressed, repeat);
    }
    @Override
    public void buttonClicked(MenuComponentButton button){
        if(button==goal){
            goal();
        }else if(button==ready){
            ready();
        }else if(button==quit){
            quit();
        }
    }
    @Override
    public void tick(){
        tick++;
        if(tick%20==0){
            synchronized(connections){
                for(Iterator<Client> it=connections.iterator(); it.hasNext();){
                    Client client=it.next();
                    if(!client.isConnected()){
                        it.remove();
                    }else{
                        client.send(this);
                    }
                }
            }
        }
    }
    private ConnectionManager openServer(){
        try{
            ConnectionManager cm = ConnectionManager.createServerSide(25565, 10, 1000, ConnectionManager.TYPE_PACKET);
            startServerListener(cm);
            return cm;
        }catch(IOException ex){
            throw new RuntimeException(ex);
        }
    }
    private void startServerListener(final ConnectionManager cm){
        new Thread(){
            public void run(){
                try{
                    while(!cm.isClosed()&&Core.helper.running){
                        if(cm.connections.isEmpty()){
                            try{
                                Thread.sleep(50);
                            }catch(InterruptedException ex){}
                            continue;
                        }
                        ConnectionManager client = cm.connections.remove(0);
                        Client aclient = new Client(client);
                        connections.add(aclient);
                        aclient.send(MenuOpenServer.this);
                    }
                    cm.close();
                }catch(IOException ex){}
            }
        }.start();
    }
    private void quit(){
        try{
            connection.close();
            if(server){
                for(Client client : connections){
                    client.close();
                }
            }
        }catch(IOException ex){}
        gui.open(parent);
    }
    private void ready(){
        ready.label = "Waiting for others";
        ready.enabled = false;
        if(server){
            new Thread(){
                public void run(){
                    while(gui.menu==MenuOpenServer.this){
                        boolean ready = true;
                        synchronized(connections){
                            for(Client client : connections){
                                if(!client.ready){
                                    ready = false;
                                    break;
                                }
                            }
                        }
                        if(!ready){
                            try{
                                Thread.sleep(50);
                            }catch(InterruptedException ex){}
                        }else{
                            try{
                                connection.close();
                            }catch(IOException ex){}
                            break;
                        }
                    }
                    if(gui.menu==MenuOpenServer.this){
                        WorldInfo info = new WorldInfo(null);
                        info.name = name.text;
                        info.type = "Multiplayer";
                        info.played = getNow();
                        info.created = getNow();
                        info.size = "9 plots";
                        info.template = template.getSelectedString();
                        info.version = VersionManager.currentVersion;
                        World world = new World();
                        world.info = info;
                        world.setTemplate(templates[template.getIndex()]);
                        world.setRace(races[race.getIndex()]);
                        world.setGoal(theGoal);
                        world.setGameSpeed((int)Math.pow(2, gameSpeed.getIndex()));
                        world.setDifficulty(difficulties[gameDifficulty.getIndex()]);
                        world.getLocalPlayer().setSandbox(gameMode.getIndex()==1);
                        world.paused = true;
                        Core.playWorld(world);
                        world.summonInitialWorker(1);
                        Random rand = new Random();
                        synchronized(connections){
                            for(int i = 0; i<connections.size(); i++){
                                Client client = connections.get(i);
                                addClientToWorld(world, client, playerDistance.getValue()*(0.75+0.5*rand.nextDouble()*(i+1)), rand);
                            }
                        }
                        try{
                            Thread.sleep(500);
                        }catch(InterruptedException ex){}
                        world.paused = false;
                    }
                }
            }.start();
        }else{
            connection.send(new PacketString("Ready"));
        }
    }
    private void startClientThread(){
        new Thread(){
            public void run(){
                doThread();
            }
        }.start();
    }
    private void goal(){
        gui.open(new MenuOpenServerGoal(gui, this));
    }
    private void addClientToWorld(World world, Client client, double distance, Random r){
        double degrees = r.nextDouble()*360;
        double radians = Math.toRadians(degrees);
        int x = (int)(Math.cos(radians)*distance);
        int y = (int)(Math.sin(radians)*distance);
        Player player = client.race.createPlayer(world);
        player.client = client;
        player.offsetX = x;
        player.offsetY = y;
        player.setSandbox(world.getLocalPlayer().sandbox);
        world.otherPlayers.add(player);
        client.setPlayer(player);
        client.prepare();
        player.summonInitialWorkers(1);
    }
    private void doThread(){
        while(!connection.isClosed()){
            while(!connection.inboundPackets.isEmpty()){
                processPacket(connection.inboundPackets.dequeue());
                if(gui.menu!=this){
                    return;
                }
            }
            try{
                Thread.sleep(50);
            }catch(InterruptedException ex){}
        }
    }
    private void processPacket(Packet packet){
        if(packet instanceof PacketString){
            String line = ((PacketString)packet).value;
            switch(line){
                case "Template":
                    template.setIndex(((PacketInteger)connection.receive()).value);
                    break;
                case "Speed":
                    gameSpeed.setIndex(((PacketInteger)connection.receive()).value);
                    break;
                case "Mode":
                    gameMode.setIndex(((PacketInteger)connection.receive()).value);
                    break;
                case "Difficulty":
                    gameDifficulty.setIndex(((PacketInteger)connection.receive()).value);
                    break;
                case "Style":
                    settingsStyle.setIndex(((PacketInteger)connection.receive()).value);
                    break;
                case "Distance":
                    playerDistance.setValue(((PacketInteger)connection.receive()).value);
                    synchronized(connection){
                        connection.send(new PacketString("Race"));
                        connection.send(new PacketString(race.getSelectedString()));
                    }
                    break;
                case "Goal":
                    theGoal = WinningCondition.load(((PacketConfig)connection.receive()).value);
                    break;
                case "Prepare":
                {
                    WorldInfo info = new WorldInfo(null);
                    info.name = name.text;
                    info.type = "Multiplayer";
                    info.played = getNow();
                    info.created = getNow();
                    info.size = "9 plots";
                    info.template = template.getSelectedString();
                    info.version = VersionManager.currentVersion;
                    World world = new World();
                    world.info = info;
                    world.setTemplate(templates[template.getIndex()]);
                    world.setRace(races[race.getIndex()]);
                    world.setGoal(theGoal);
                    world.setGameSpeed((int)Math.pow(2, gameSpeed.getIndex()));
                    world.setDifficulty(difficulties[gameDifficulty.getIndex()]);
                    world.getLocalPlayer().setSandbox(gameMode.getIndex()==1);
                    world.paused = true;
                    world.setRemote(connection);
                    Core.playWorld(world);
                    return;
                }
                default:
                    throw new AssertionError(line);
            }
        }else{
            throw new UnsupportedOperationException(packet.getClass().getName());
        }
    }
    public static String getNow(){
        GregorianCalendar calendar = new GregorianCalendar();
        StringBuilder buff = new StringBuilder(13);
        buff.append(calendar.getDisplayName(Calendar.MONTH, GregorianCalendar.SHORT, Locale.US));
        buff.append(" ");
        buff.append(calendar.get(Calendar.DAY_OF_MONTH));
        buff.append(", ");
        buff.append(calendar.get(Calendar.YEAR));
        buff.append("  ");
        buff.append(calendar.get(Calendar.HOUR)+(calendar.get(Calendar.HOUR)==0?12:0));
        buff.append(":");
        buff.append(calendar.get(Calendar.MINUTE));
        return buff.toString();
    }
}
