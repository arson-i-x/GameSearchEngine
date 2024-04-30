package com.searchengine;

import java.util.*;


public class GameSearch {

    // how many times the algorithm will loop also controls accuracy of matches
    private static final int MAX_ITERATIONS = Database.size(); 
    
    public UserData UserData;  // holds users games
    private Map<Game, Long> cache; // holds weight of games already calculated

    public GameSearch () // Searches a game database using Userdata as input
    {
        this.UserData = new UserData();
        this.cache = new LinkedHashMap<>();
    }

    public GameSearch (UserData data) // Searches a game database using Userdata as input
    {
        this.UserData = data;
        this.cache = new LinkedHashMap<>();
    }

    public UserData getUserData() 
    {
        return this.UserData;
    }

    public void setUserData(UserData data) 
    {
        this.UserData = data;
    }

    public void recache(Game thisGame)     // removes game from cache if it contains tags matching thisGame 
    {
        Iterator<Game> iter = cache.keySet().iterator();

        while (iter.hasNext()) {
            Game game = iter.next();
            
            if (!Collections.disjoint((Collection<String>)game.getTags(), 
            (Collection<String>)thisGame.getTags())) 
            { // Returns true if games have no matches
                iter.remove();
            }
        }
    }

    public void clearCache() // removes all games from cache
    {
        cache.clear();
    }

    public Game ChooseNext (UserData tempUserData, Game lastGame) // chooses next game from the database by tag matches to user games 
    {
        // if user data is empty then return random game
        if (tempUserData.getGames().isEmpty()) {
            IOController.MESSAGE("Returning random game.");
            return Database.getRandomGame();
        }

        Game nextGame = null; // initializer

        long maxWeight = Long.MIN_VALUE; // minimum # of matches
        
        int iteration = 0;  // breaks when past MAX_ITERATIONS

        for (Game thisGame : Database.getAllGames()) {

            long currWeight = 0;

            if (thisGame.similarTo(lastGame) || thisGame.IsRemoved() || this.UserData.getGames().containsKey(thisGame.getGameID())) { 
                continue; // prevents returning owned or removed games
            } 
            
            if (nextGame != null && (nextGame.getPopularity() > thisGame.getPopularity() && nextGame.getReviewScore() > thisGame.getReviewScore())) {  
                //continue; // if nextGame got better reviews and is more popular than thisGame then skip thisGame  TODO SKIP??                                          
            }

            // CHECK CACHE FOR WEIGHT BEFORE CALCULATING ANYTHING ELSE
            if (cache.containsKey(thisGame)) {
                currWeight = cache.get(thisGame);
                if (currWeight > maxWeight) {
                    nextGame = thisGame;
                    maxWeight = currWeight;
                    /*      DEBUG  */     IOController.MESSAGE("Next cached game is "+ thisGame.getName() + "with weight " + currWeight);
                }
                continue;
            }

            // check each user game and compare its tags to this game by tag weights (hours played)
            /*for (Long SourceID: tempUserData.getGames().keySet()) {
               
                Game Source = Database.getGame(SourceID);
                
                if (Source == null || Source.getGameID() == thisGame.getGameID()) { continue; }
                
                for (String tag : Source.getTags()) {
                    if (thisGame.getTags().contains(tag)) {
                        currWeight+= tempUserData.getTagWeight(tag);  
                    } else {
                        currWeight-= tempUserData.getTagWeight(tag); 
                    }
                }
            }*/

            // faster method to search, less deterministic
            for (String tag : UserData.getTags().keySet()) {
                if (thisGame.getTags().contains(tag)) {
                    currWeight+= tempUserData.getTagWeight(tag);  
                } else {
                    currWeight-= tempUserData.getTagWeight(tag); 
                }
            }

            // cache this games weight and increment
            cache.put(thisGame, currWeight);
            iteration++;

            // if combined weight is biggest found
            // update thisGame to return
            if (currWeight > maxWeight) {
                maxWeight = currWeight;
                nextGame = thisGame;
                iteration = 0;
                /*      DEBUG  */     IOController.MESSAGE("Next game is "+ nextGame.getName() + "with weight " + currWeight);
            }

            if (iteration > MAX_ITERATIONS) {
               break;
            }
        }

        // if nothing found at this point 
        // there is a big problem. The user
        // has likely searched every game in the 
        // data base so throw an error and exit program
        if (nextGame == null) {
            EXIT("NO MORE GAMES IN DATABASE!");
        } else {
            // remove this game
            nextGame.RemoveGame();
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

        System.out.println(NewGraph.ChooseNext(UserData, null).getName());
    }
}
