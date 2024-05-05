package com.searchengine;

import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.ownedgames.GetOwnedGames;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.GetOwnedGamesRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// com.searchengine.User class uses Steam API to create userdata from login
public class User 
{
    private static final SteamWebApiClient client = GameSearch.SteamClient;
    private UserData userdata;

    public User()  
    {
        this.userdata = UserData.getUserDataFromFile();
    }

    public User(UserData userData)  
    {
        this.userdata = userData;
    }

    // takes ID or game name as input and creates associated userdata
    public void login(String input) throws SteamApiException, IOException {
        if (input.length() != 17) {
            throw new IOException("PLEASE ENTER YOUR STEAM ID");
        }
        GetOwnedGamesRequest req = new GetOwnedGamesRequest
            .GetOwnedGamesRequestBuilder(input)
            .includeAppInfo(true)
            .includePlayedFreeGames(true)
            .buildRequest();
        GetOwnedGames games = client.processRequest(req);
        HashMap<Long, Long> gameIDList = new HashMap<>();
        List<com.lukaspradel.steamapi.data.json.ownedgames.Game> gameList = games.getResponse().getGames();
        for (com.lukaspradel.steamapi.data.json.ownedgames.Game game : gameList) {
            gameIDList.put(game.getAppid(), game.getPlaytimeForever()); // Brandon - Also hashes user playtime
        }
        if (gameIDList.size() == 0) {
            throw new SteamApiException("PLEASE ENTER YOUR STEAM ID\nUSER LIBRARY MAY BE PRIVATE");
        }
        this.userdata = UserData.CreateUserData(gameIDList);
    }

    public UserData getUserData () 
    {
        return userdata;
    }

    Map<Long,Long> getGames()
    {   
        return userdata.getGames();
    }
}
