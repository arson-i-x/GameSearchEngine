package com.searchengine;

import java.io.IOException;
import java.util.Locale;

import javax.swing.JOptionPane;

import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import UI.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class GameSearchApplication {

    // how many times user can say no
    protected static final int MAX_DISLIKES = 10;

    // Stores Steam Web API Client
    public static final SteamWebApiClient SteamClient = new SteamWebApiClient.SteamWebApiClientBuilder
                                        ("CF994D1D2070B5344BF6DF7337BDB2AB").build();

    protected GameSearch SearchInstance;
    protected UserData userData;
    protected int iteration;
    protected UserData workingData;
    protected Game GameToPresent;

    public GameSearchApplication()
    {
        Database.init();
        this.iteration = -1;
    }

    public void BuildUserSearch (String steamID) throws IOException, SteamApiException
    {
        this.iteration = 0;
        this.userData = User.Login(steamID).getUserData();
        this.SearchInstance = new GameSearch(this.userData);
        this.workingData = new UserData(this.userData);
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
            workingData.removeSomeGames();
            SearchInstance.clearCache();
        } 

        // choose next best game from source
        return SearchInstance.ChooseNext(workingData, GameToPresent);
    }

    public void download() 
    {
        Platform.exit();
    }
            
    public void like(Game game) 
    {
        iteration = 0;  
        workingData.addGame(game); 
        SearchInstance.recache(game); // removes games that need to be recalculated
        GameToPresent = null;
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
