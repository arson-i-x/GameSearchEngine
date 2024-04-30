package com.searchengine;

import java.io.IOException;
import javax.swing.JOptionPane;
import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import javafx.application.Platform;

public class GameSearchApplication {

    // how many times user can say no
    protected static final int MAX_DISLIKES = 10;

    // Stores Steam Web API Client
    public static final SteamWebApiClient SteamClient = new SteamWebApiClient.SteamWebApiClientBuilder
                                        ("CF994D1D2070B5344BF6DF7337BDB2AB").build();

    protected GameSearch SearchInstance;
    protected UserData userData;
    protected int iteration;
    protected Game GameToPresent;

    public GameSearchApplication()
    {
        Database.init();
        this.iteration = 0;
        this.userData = new UserData();
        this.SearchInstance = new GameSearch(userData);
    }

    public void BuildUserSearch (String steamID) throws IOException, SteamApiException
    {
        this.iteration = 0;
        this.SearchInstance.setUserData(User.Login(steamID).getUserData());
    }

    public Game nextGame() 
    {
        if (iteration < 0) {
            JOptionPane.showMessageDialog(null,"NO USER DATA");
            System.exit(1);
        } 

        iteration++;

        // if user has said no to too many games, 
        // clear out array and restart searching
        if (iteration > MAX_DISLIKES) {
            JOptionPane.showMessageDialog(null,"REVISING SEARCH");
            SearchInstance.getUserData().removeSomeGames();
            SearchInstance.clearCache();
        } 

        // choose next best game from source
        return SearchInstance.ChooseNext(SearchInstance.getUserData(), GameToPresent);
    }

    public void download() 
    {
        Platform.exit();
    }
            
    public void like(Game game) 
    {
        iteration = 0;  
        SearchInstance.getUserData().addGame(game); 
        SearchInstance.recache(game); // removes games that need to be recalculated
        GameToPresent = null;  // Set last game to null so similar games will show up
    }

    public UserData getUserData() 
    {
        return userData;
    }
    
    public static void main (String[] args)     // tests application through terminal input and returns best game
    {
        //test
    }    
}
