package com.searchengine;

import java.io.IOException;

import com.lukaspradel.steamapi.core.exception.SteamApiException;


public class TerminalApplication extends GameSearchApplication {

    // how many times user can say no
    private static final int MAX_DISLIKES = 10;

    public TerminalApplication() 
    {
        IOController.MESSAGE("Enter Steam ID or Search Games");
        this.BuildUserSearchFromTerminal();
    }
    
    public void BuildUserSearchFromTerminal ()
    {
        // init database
        Database.init();

        String id = IOController.getTerminalInput();

        User newUser = new User(new UserData());

        // user login
        try {
            newUser = User.Login(id);
            if (!newUser.getLoginSuccess()) {
                IOController.putTerminalOutput("Make sure your Steam Library is set to public.");
            }
        } catch (SteamApiException steamApiException) {
            IOController.putTerminalOutput("STEAM API ERROR");
            System.exit(1);
        } catch (IOException re) {
            IOController.putTerminalOutput("PLEASE ENTER A STEAM ID");
        } finally {
            User user = newUser;
            this.userData = user.getUserData();
            SearchInstance = new GameSearch(this.userData);
        }
    }

    public void runInTerminal () 
    {
        Game GameToPresent = null;

        while (true) // algorithm runs until exit condition met
        {
            // choose next best game from source
            GameToPresent = SearchInstance.ChooseNext(SearchInstance.getUserData(), GameToPresent);

            // remove this game
            GameToPresent.RemoveGame();

            // present this game
            IOController.PresentGameToUser(GameToPresent);

            // read input state
            switch (IOController.getState()) {
                case IOController.EXIT:
                    GameSearch.EXIT(GameToPresent); 
                case IOController.LIKE:
                    iteration = 0;  
                    SearchInstance.getUserData().addGame(GameToPresent); 
                    SearchInstance.recache(GameToPresent); // removes games that need to be recalculated
                    GameToPresent = null;
            }

            // reset io state
            IOController.resetState();

            // if user has said no to too many games, 
            // clear out array and restart searching
            if (iteration > MAX_DISLIKES) {
                IOController.MESSAGE("Revising search algorithm");
                SearchInstance.getUserData().removeSomeGames();
                SearchInstance.clearCache();
                runInTerminal();
            } else {
                iteration++;
            }
        }
    }

    public static void main (String[] args)     // tests application through terminal input
    {
        TerminalApplication app = new TerminalApplication();

        app.runInTerminal();
    }    
}