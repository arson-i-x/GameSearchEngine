package com.searchengine;

import java.io.IOException;
import java.util.*;
import javax.swing.JOptionPane;
import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;


public class GameSearch 
{
    protected static final int MAX_DISLIKES = 10;
    protected static final SteamWebApiClient SteamClient = new SteamWebApiClient
                                    .SteamWebApiClientBuilder
                                    ("CF994D1D2070B5344BF6DF7337BDB2AB")
                                    .build();

    List<Game> database;        // stores all games
    User user;                  // stores real user data
    UserData UserData;          // holds user selected games and data
    HashMap<Game, Long> cache;  // holds weight of games already calculated
    int iteration;              // current iteration of search
    Game GameToPresent;         // current game that is most relevant
    GameSearch otherSearchInstance; // another search instance for threading

    public GameSearch ()        // Search instance constructor
    {
        Database.init();
        this.database = Database.getAllGames();
        this.user = new User();
        this.UserData = user.getUserData();
        this.cache = new LinkedHashMap<>();
        this.iteration = 0;
        this.otherSearchInstance = new GameSearch(this);
    }

    private GameSearch (GameSearch instance) // Search instance constructor
    {
        this.user = instance.user;
        this.UserData = new UserData(user.getUserData());
        this.cache = new LinkedHashMap<>();
        this.iteration = instance.iteration;
    }

    public void login(String steamid) throws SteamApiException, IOException
    {
        user.login(steamid);
        UserData = new UserData(user.getUserData());
    }

    public UserData getUserData() 
    {
        return this.UserData;
    }

    public void clearLikes() 
    {
        Map<Long, Long> ids = UserData.getGames();
        for (Long id : ids.keySet()) {
            if (ids.get(id) < 0) {
                ids.put(id, (long)0);
                Log.MESSAGE(Database.getGame(id.toString()).getName()+" REMOVED");
            }
        }
        clearCache();
        Log.MESSAGE("LIKES DELETED");
    }

    public void clearUserData() 
    {
        clearLikes();
        com.searchengine.UserData.clearFile();
        this.user = new User();
        this.UserData = new UserData(user.getUserData());
        Log.MESSAGE("USERDATA DELETED");
    }

    void recache(Game thisGame)     // removes game from cache if it contains tags matching thisGame 
    {
        Iterator<Game> iter = cache.keySet().iterator();

        while (iter.hasNext()) {
            Game game = iter.next();
            
            for (String tag : thisGame.getTags()) {
                if (game.getTags().contains(tag)) {
                    iter.remove();
                    break;
                }
            }
        }
    }

    void clearCache() // removes all games from cache
    {
        cache.clear();
    }

    private long weight(Game thisGame, HashMap<String, Long> tags) // returns weight of a game by tag matches 
    {
        long currWeight = 0;

        // faster method to search, less deterministic
        for (String tag : tags.keySet()) {
            if (thisGame.getTags().contains(tag)) {
                currWeight+= tags.get(tag);  
            } else {
                currWeight-= tags.get(tag); 
            }
        }

        //currWeight = thisGame.similarity(UserData) * currWeight;

        cache.put(thisGame, currWeight);
        return currWeight;
    }

    protected Game nextGame() throws NoSuchElementException // chooses next game from the database by max weight
    {
        long maxWeight = Long.MIN_VALUE; // minimum # of matches
        HashMap<String, Long> tags = UserData.getTags(); // get tagmap
        for (Game thisGame : database) 
        {        
            long currWeight = 0;
            
            if (thisGame.similarTo(GameToPresent)) {
                continue; // skips if user didn't like last game and the name is similar
            } 
            if (GameToPresent != null && GameToPresent.getPopularity() > thisGame.getPopularity() && GameToPresent.getReviewScore() > thisGame.getReviewScore()) {
                //continue; // Skips based on popularity/rating
            } 
            if (UserData.getGames().containsKey(thisGame.getGameID())) {     
                continue; // skips if already owned
            } 

            // CHECK CACHE FOR WEIGHT BEFORE CALCULATING ANYTHING ELSE
            if (cache.containsKey(thisGame)) {
                currWeight = cache.get(thisGame);
            } else {
                currWeight = weight(thisGame, tags);
            }

            // update maxweight found
            if (currWeight > maxWeight) {
                maxWeight = currWeight;
                GameToPresent = thisGame;
                iteration = 0;
                Log.MESSAGE("Next Game is "+ GameToPresent.getName() + " with weight " + currWeight);
            }
        }
        
        if (GameToPresent != null) {
            UserData.addGame(GameToPresent, 0); // prevent game from showing up again
            Log.MESSAGE("Best game is "+ GameToPresent.getName());
        }
        
        
        return GameToPresent;
    }

    public Game search() 
    {
        iteration++;

        if (iteration > MAX_DISLIKES) {
            JOptionPane.showMessageDialog(null,"REVISING SEARCH");
            UserData.removeSomeGames();
            clearCache();
        } 

        // if user data is empty then return random game
        if (UserData.getGames().isEmpty()) {
            Log.MESSAGE("Returning random game.");
            return Database.getRandomGame();
        }

        return nextGame();
    } 

    public void like(Game game) 
    {
        iteration = 0;  
        UserData.likeGame(game); 
        recache(game);          // removes games that need to be recalculated
        GameToPresent = null;   // Set last game to null so similar games will show up
    } 

    public static void main (String[] args) // tests unit
    {
        Database.init();
        GameSearch search = new GameSearch();
        
        Log.MESSAGE("All games");
        for (Game game : Database.getAllGames()) {
            Log.MESSAGE(game.getName());
        }

        Log.EXIT(search.nextGame());
    }
}
