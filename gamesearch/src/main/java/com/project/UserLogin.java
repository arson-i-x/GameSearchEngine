package com.project;

import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.data.json.ownedgames.Game;
import com.lukaspradel.steamapi.data.json.ownedgames.GetOwnedGames;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.GetOwnedGamesRequest;

import java.util.ArrayList;
import java.util.List;

public class UserLogin {
    public static String Login(String args) throws SteamApiException{
        String steamID = args;
        SteamWebApiClient client = new
                // The key is my Steam web api key.
                SteamWebApiClient.SteamWebApiClientBuilder("CF994D1D2070B5344BF6DF7337BDB2AB").build();
        GetOwnedGamesRequest req = new GetOwnedGamesRequest.GetOwnedGamesRequestBuilder(steamID).includeAppInfo(true).buildRequest();

        GetOwnedGames games = client.processRequest(req);
        List<String> ownedGames = new ArrayList<>();
        int index = 0;

        System.out.println("Here are your games:");
        for (Game game: games.getResponse().getGames()){
            ownedGames.add(String.valueOf(game.getName()));
            System.out.println(ownedGames.get(index));
            index++;
        }
        return ownedGames.toString();
    }
}
