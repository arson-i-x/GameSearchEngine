package com.searchengine;
import java.util.*;

public class GameSearch {
    
    // how many times user can say no
    private static final int MAX_ITERATIONS = 5;
    
    private UserData userData;

    // constructs weighted graph using Database as input
    public GameSearch (UserData data) 
    {
        this.userData = data;
    }

    // search and present games
    public void Search () 
    {
        int iteration = 1;
        HashSet<Long> UserSelectedGames = userData.getGames();

        // algorithm runs until exit condition met
        while (true)
        {
            // choose next best game from source
            Game GameToPresent = ChooseNext();
            IOController.PresentGameToUser(GameToPresent);
            switch (IOController.state) {
                case IOController.EXIT:
                    IOController.cache(GameToPresent);
                    GameSearch.EXIT();
                case IOController.LIKE:
                    userData.addGame(GameToPresent);
                    IOController.state = IOController.SEARCH;
            }

            // remove this game
            GameToPresent.RemoveGame();
    
            // if user has said no to too many games, 
            // clear out array and restart searching
            if (iteration > MAX_ITERATIONS) {
                UserSelectedGames.clear();
            }
        }
    }

    // chooses next game from the database by tag matches to user games
    private Game ChooseNext () 
    {
        // if user data is empty then return random game
        if (userData.getGames().isEmpty()) {
            IOController.ERROR("Returning random game.");
            return Database.getRandomGame();
        }

        // chooses next game in database by highest # of tag matches.
        // using user data and games already selected to match tags
        Game thisGame = null;
        int maxWeight = Integer.MIN_VALUE; // minimum # of matches
        for (Game nextGame : Database.getAllGames()) {
            if (nextGame.IsRemoved() || userData.getGames().contains(nextGame.getGameID())) { continue; } // prevents returning owned or removed games
            if (thisGame != null && thisGame.getReviewScore() > nextGame.getReviewScore() && thisGame.getPopularity() > nextGame.getPopularity()) {  
                // if thisGame got better reviews and is more popular than nextGame then skip nextGame
                continue;                                                   
            }
            
            int currWeight = 0;
            
            
            
            // check Sources to this game
            for (Long SourceID: userData.getGames()) {
                Game Source = Database.getGame(SourceID);
                for (String tag : Source.getTags()) {
                    if (nextGame.getTags().contains(tag)) {
                        currWeight++;
                    } else {
                        currWeight--;
                    }
                }
            }



            // if combined weight is biggest found
            // update thisGame to return
            if (currWeight > maxWeight) {
                maxWeight = currWeight;
                thisGame = nextGame;
            }
        }

        // if nothing found at this point 
        // there is a big problem. The user
        // has likely searched every game in the 
        //data base so throw an error and exit program
        if (thisGame == null) {
            EXIT("NO MORE GAMES IN DATABASE!");
        }

        return thisGame;
    }

    public static void EXIT() 
    {
        IOController.EXIT((Game)IOController.getCache());
        System.exit(0);
    }

    public static void EXIT(String error) 
    {
        IOController.EXIT(error);
        System.exit(1);
    }

    // tests unit
    public static void main (String[] args) 
    {
        Database.init();
        UserData UserData = new UserData();
        GameSearch NewGraph = new GameSearch(UserData);
        
        System.out.println("All games");
        for (Game game : Database.getAllGames()) {
            
            System.out.println(game.getName());
        }

        NewGraph.Search();
    }
}
