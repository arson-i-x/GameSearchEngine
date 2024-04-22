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
        UserData S = new UserData(userData);
        int iteration = 1;
        Game GameToPresent = null;

        // algorithm runs until exit condition met
        while (true)
        {
            // choose next best game from source
            GameToPresent = ChooseNext(S, GameToPresent);

            // remove this game
            GameToPresent.RemoveGame();

            // present this game
            IOController.PresentGameToUser(GameToPresent);

            // read input state
            switch (IOController.getState()) {
                case IOController.EXIT:
                    GameSearch.EXIT(GameToPresent);
                case IOController.LIKE:
                    S.addGame(GameToPresent);
            }

            // reset io state
            IOController.resetState();

            // if user has said no to too many games, 
            // clear out array and restart searching
            if (iteration > MAX_ITERATIONS) {
                IOController.MESSAGE("Revising search algorithm");
                Search();
            }
        }
    }

    private Game ChooseNext (UserData S, Game lastGame)     // chooses next game from the database by tag matches to user games 
    {
        // if user data is empty then return random game
        if (S.getGames().isEmpty()) {
            IOController.MESSAGE("Returning random game.");
            return Database.getRandomGame();
        }

        // chooses next game in database by highest # of tag matches.
        // using user data and games already selected to match tags
        Game nextGame = null;
        long maxWeight = Long.MIN_VALUE; // minimum # of matches
        
        
        
        for (Game thisGame : Database.getAllGames()) {
            
            if (lastGame != null && lastGame.similarTo(thisGame)) { 
                thisGame.RemoveGame(); //       TODO do we actually wanna remove this if the name is similar??
                continue;             // this will prevent sequels from appearing too often if the user doesn't like them 
            }

            if (thisGame.IsRemoved() || userData.getGames().contains(thisGame.getGameID())) { continue; } // prevents returning owned or removed games
            
            if (nextGame != null && nextGame.getReviewScore() > thisGame.getReviewScore() && nextGame.getPopularity() > thisGame.getPopularity()) {  
                //System.out.println("REVIEW SCORE 1 " + thisGame.getReviewScore() + "> REVIEW SCORE 2 " + nextGame.getReviewScore());
                // if thisGame got better reviews and is more popular than nextGame then skip nextGame
                continue;                                                   
            }
            
            long currWeight = 0;
            
            for (Long SourceID: S.getGames()) {
               
                Game Source = Database.getGame(SourceID);
                
                if (Source == null) { continue; }
                
                for (String tag : Source.getTags().keySet()) {
                    if (thisGame.getTags().containsKey(tag)) {
                        currWeight+= S.getTagWeight(tag);    // Brandon - GETS TAG WEIGHT BY HOURS PLAYED
                    } else {
                        currWeight-= S.getTags().get(tag);   // REMOVES WEIGHT BY TAG RANK IN GAME'S TAG LIST
                    }
                    
                }

            }

            // if combined weight is biggest found
            // update thisGame to return
            if (currWeight > maxWeight) {
                maxWeight = currWeight;
                nextGame = thisGame;
                System.out.println("Next game is "+ nextGame.getName());
            }
        }

        // if nothing found at this point 
        // there is a big problem. The user
        // has likely searched every game in the 
        //data base so throw an error and exit program
        if (nextGame == null) {
            EXIT("NO MORE GAMES IN DATABASE!");
        }

        return nextGame;
    }

    public static void EXIT(Game game) 
    {
        IOController.EXIT(game);
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
