import com.searchengine.*;
import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;

public class GameSearchApplication {

    // Stores Steam Web API Client
    public static final SteamWebApiClient SteamClient = new SteamWebApiClient.SteamWebApiClientBuilder
                                        ("CF994D1D2070B5344BF6DF7337BDB2AB").build();

    private GameSearch SearchInstance;

    private GameSearchApplication (GameSearch instance) 
    {
        SearchInstance = instance;
    }
    
    // Main program execution. Reads from System.in
    public static GameSearchApplication BuildUserSearch () 
    {
        // init database
        Database.init();

        // user login
        try {
            User.Login();
            if (!User.getLoginSuccess()) {
                IOController.ERROR("Make sure your Steam Library is set to public.");
            }
        } catch (SteamApiException steamApiException) {
            GameSearch.EXIT("STEAM API ERROR");
        }

        // print user library (debug)
//        for (long gameid : User.getGames()) {
//            //System.out.println(Database.getGame(gameid).getName());
//        }
        
        // make a graph with user data as input
        return new GameSearchApplication(new GameSearch(User.getUserData()));
    }

    public static GameSearchApplication BuildRandomSearch() 
    {
        // init database
        Database.init();

        // make fake user
        User.fakeUser();

        return new GameSearchApplication(new GameSearch(User.getUserData()));
    }

    public void GameSearch () 
    {
        SearchInstance.Search();
    }

    public static void main (String[] args) 
    {
        GameSearchApplication.BuildRandomSearch().GameSearch();
    }    
}
