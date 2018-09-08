package city.populization.core;
import simplelibrary.Queue;
import simplelibrary.config2.Config;
import simplelibrary.encryption.Encryption;
import simplelibrary.net.authentication.Authentication;
import simplelibrary.net.authentication.Authenticator;
import simplelibrary.net.packet.PacketString;
public class ServerAuthenticator extends Authenticator{
    private final ServerSide server;
    public ServerAuthenticator(ServerSide server) {
        this.server = server;
    }
    @Override
    public Authentication authenticate(Config authData, Encryption outbound, Encryption inbound) {
        Queue<String> usernames = new Queue<>();
        String username = authData.get("username");
        String originalName = username;
        String baseName = username;
        String password = authData.get("password");
        String sessionID = authData.get("sid");
        boolean isAuthed = password!=null&&password.equals(server.getPassword());
        if(!isAuthed&&sessionID!=null&&!sessionID.isEmpty()){
            usernames = authSID(username, sessionID);
        }
        int Try = -1;
        WHILE:while(true){
            if(Try>=0){
                username = getUsernameAddition(username, Try);
            }
            for(Client c : server.getClients()){
                if(username.equals(c.getUsername())){
                    //If the username is taken, either find the next one or vacate it
                    if(!isAuthed||c.isAuthorized()){
                        //This username was taken by someone of equal or greater permission.  Maybe the next username...?
                        usernames.enqueue(baseName);
                        baseName = username = usernames.dequeue();
                        if(username.equals(originalName)){
                            Try++;
                        }
                        continue WHILE;
                    }else{
                        c.connection.send(new PacketString("Game name taken by paid user!"), "disconnect.reason");
                        c.disconnect();
                        //Just vacated the username, use it
                        break WHILE;
                    }
                }
            }
            break;//If there was no conflict on this username, use it
        }
        Config result = Config.newConfig();
        result.set("username", username);
        result.set("isVerified", isAuthed);
        result.set("isAdmin", password.equals(server.getPassword()));
        return Authentication.authenticate(result);
    }
    private Queue<String> authSID(String username, String sessionID) {//TODO contact login server to authenticate the SID & username pair
        throw new UnsupportedOperationException("Not supported yet.");
    }
    String[] additions = {
        "<USER> The Second",//2 identicals
        "<USER> The Third",//3 identicals
        //Two or three identical names can be understandable...   Much more than that almost has to be deliberate.
        //Note that identical names are not possible between players- only spectators that are in offline mode.  The account name is used for players.
        "<USER> The Fourth, It Seems",//4
        "<USER> The Fifth, I Guess",//5
        "The Sixth <USER> Of Several",//6
        "The Seventh Is Too Many, <USER>",//7
        "The Eighth <USER>",//8
        "<USER> The Ninth",//9
        "<USER> The Tenth",//10
        "The Duplicate <USER>",//11
        "The Cloned <USER>",//12
        "The Unlucky <USER>",//13
        "The Fourteenth <USER>",//14
        "Maybe Fifteen Is Enough, <USER>",//15
        "The Sixteenth <USER>",//16
        "The Seventeenth <USER>",//17
        "The Eighteenth <USER>",//18
        "<USER> # 19 Already",//19
            //Someone's gonna have some laughs if they get large numbers of identical names....  Especially depending on what that name is!
        "<USER><USER> IsIs NumberNumber TwentyTwenty",//20
        "<USER> Is Half The Universe",//21
        "Maybe <USER> Has Logged In Enough Times By Now",//22
        "Seriously <USER>, There Are 23 Of You Now",//23
        "Twice Twelve <USER>",//24
        "Who Is <USER> Anyways",//25
        "Too Many <USER>",//26
        "<USER> Won't Stop Spawning",//27
        "There Are So Many <USER>",//28
        "Must Recount The <USER>",//29
        "I Count Thirty <USER>",//30
        "How Many <USER> Are There",//31
        "Large Quantities Of <USER>",//32
        "<USER> Overload",//33
        "Achievement Get <USER>",//34
        "I Lost My Town Hall In The <USER> Army",//35
        "<USER> Owns A Whole City By Themself",//36
        "<USER> Must Have A Cloning Device",//37
        "No Really, <USER>, That's Plenty Already",//38
        "Game Of <USER>",//39
        "<USER> To The Max",//40
        "Like A <USER>",//41
        "The Answer To Life, The Universe, And Everything Is <USER>",//42
        "There Are Seriously Too Many <USER>",//43
        "I Am Getting Tired Of Renaming <USER>",//44
        "Apocolypse Of <USER>"//45, final entry
    };
    private String getUsernameAddition(String username, int Try) {
        int mod = (Try+2)%10;
        return (Try<additions.length?additions[Try]:"Apocolyptic <USER> The "+(Try+2)+(mod==1?"st":(mod==2?"nd":(mod==3?"rd":"th")))).replaceAll("<USER>", username);
    }
}
