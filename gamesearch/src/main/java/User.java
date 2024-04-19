import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.ownedgames.GetOwnedGames;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.GetOwnedGamesRequest;
import com.searchengine.GameSearch;
import com.searchengine.IOController;
import com.searchengine.UserData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class User {
    // get Steam client from GameSearchApp Instance
    private static final SteamWebApiClient client = GameSearchApplication.SteamClient;
    private static String steamID;
    private static UserData userdata;
    private static boolean loginsuccess;

    // takes ID or game name as input and creates associated userdata
    static void Login () throws SteamApiException {
        Object ID = IOController.LoginQuery();
        if (ID instanceof Long) {
            steamID = ID.toString();
            //System.out.println(steamID);
            GetOwnedGamesRequest req = new GetOwnedGamesRequest.GetOwnedGamesRequestBuilder(steamID).includePlayedFreeGames(true).buildRequest();
            GetOwnedGames games = client.processRequest(req);
            List<Long> gameIDList = new ArrayList<Long>();
            List<com.lukaspradel.steamapi.data.json.ownedgames.Game> gameList = games.getResponse().getGames();
            if (gameList.isEmpty()) {
                System.out.println("error empty");
            }
            for (com.lukaspradel.steamapi.data.json.ownedgames.Game game : gameList) {
                loginsuccess = true;
                //System.out.println(game.getAppid());
                gameIDList.add(game.getAppid());
            }
            userdata = UserData.CreateUserData(gameIDList);
        } else {
            loginsuccess = true;
            userdata = UserData.CreateUserData(ID.toString());
        }
    }

    static void setLoginSuccess (boolean state) 
    {
        loginsuccess = state;
    }

    static boolean getLoginSuccess () 
    {
        return loginsuccess;
    }

    static void fakeUser () 
    {
        userdata = new UserData();
    }

    static UserData getUserData () 
    {
        if (userdata == null) {
            GameSearch.EXIT("NO USER DATA FOUND");
        }
        return userdata;
    }

    static HashSet<Long> getGames() 
    {
        return userdata.getGames();
    }
}
