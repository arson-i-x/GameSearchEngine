package com.searchengine;

import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.ownedgames.GetOwnedGames;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.GetOwnedGamesRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

// com.searchengine.User class uses Steam API to create userdata from login
public class User {
    // get Steam client from GameSearchAppplication Instance
    private static final SteamWebApiClient client = GameSearchApplication.SteamClient;
    private UserData userdata;
    private boolean loginsuccess;

    public User(UserData userData)  
    {
        this.userdata = userData;
        if (!userData.getGames().isEmpty()) {
            loginsuccess = true;
        }
    }

    // takes ID or game name as input and creates associated userdata
    public static User Login (String steamID) throws SteamApiException, IOException {
        Object ID = IOController.LoginQuery(steamID);
        if (ID instanceof Long) {
            steamID = ID.toString();
            GetOwnedGamesRequest req = new GetOwnedGamesRequest.GetOwnedGamesRequestBuilder(steamID).includeAppInfo(true).includePlayedFreeGames(true).buildRequest();
            GetOwnedGames games = client.processRequest(req);
            HashMap<Long, Long> gameIDList = new HashMap<>();
            List<com.lukaspradel.steamapi.data.json.ownedgames.Game> gameList = games.getResponse().getGames();
            for (com.lukaspradel.steamapi.data.json.ownedgames.Game game : gameList) {
                //System.out.println(game.getAppid());
                gameIDList.put(game.getAppid(), game.getPlaytimeForever()); // Brandon - Also hashes user playtime
            }
            return new User(UserData.CreateUserData(gameIDList));
        } else {
            throw new IOException();
        }
    }

    void setLoginSuccess (boolean state) 
    {
        loginsuccess = state;
    }

    boolean getLoginSuccess () 
    {
        return loginsuccess;
    }

    public UserData getUserData () 
    {
        if (userdata == null) {
            GameSearch.EXIT("NO USER DATA FOUND");
        }
        return userdata;
    }

    Set<Long> getGames()
    {   
        return userdata.getGames().keySet();
    }
}
