package com.searchengine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.io.FileUtils;

import com.lukaspradel.steamapi.core.exception.SteamApiException;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;

public class GameSearch 
{
    protected static final File path = new File(FileUtils.getUserDirectoryPath()+"/gamesearch/");
    protected static final int MAX_DISLIKES = 10;
    protected static final SteamWebApiClient SteamClient = new SteamWebApiClient
                                    .SteamWebApiClientBuilder
                                    ("CF994D1D2070B5344BF6DF7337BDB2AB")
                                    .build();

    User user;                  // stores real user data
    HashMap<Game, Long> cache;  // holds weight of games already calculated
    int iteration;              // current iteration of search
    Game GameToPresent;         // current game that is most relevant

    public GameSearch ()        // Search instance constructor
    {
        path.mkdirs();
        Database.init();
        this.user = new User();
        this.cache = new LinkedHashMap<>();
        this.iteration = 0;
    }

    public void login(String steamid) throws SteamApiException, IOException
    {
        user.login(steamid);
    }

    public void clearLikes() 
    {
        Map<Long, Long> ids = getUserData().getGames();
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

    private Long weight(Game thisGame, Map<String, Long> tags) // returns weight of a game by tag matches 
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

    Game nextGame() throws NoSuchElementException // chooses next game from the database by max weight
    {
        Game nextGame = null, lastGame = GameToPresent;
        iteration++;
        long maxWeight = Long.MIN_VALUE; // minimum # of matches
        Map<String, Long> tags = getUserData().getTags(); // get tagmap
        boolean check_next_game = true;
        for (Game thisGame : Database.getAllGames()) 
        {      
            if (!check_next_game) {
                check_next_game = true; // skip every other game
                continue;
            }
            check_next_game = false;

            long currWeight = 0;

            if (lastGame != null && lastGame.getPopularity() > thisGame.getPopularity() && lastGame.getReviewScore() > thisGame.getReviewScore()) {
                //Log.MESSAGE(thisGame.getName()+" SKIPPING");
                //continue; // Skips based on popularity/rating
            } 
            if (user.getUserData().getGames().containsKey(thisGame.getGameID())) {     
                //Log.MESSAGE(thisGame.getName()+" ALREADY OWNED: SKIPPING");
                continue; // skips if already owned
            } 

            // CHECK CACHE FOR WEIGHT BEFORE CALCULATING ANYTHING ELSE
            if (cache.containsKey(thisGame))  {
                currWeight = cache.get(thisGame);
            } else {
                currWeight = weight(thisGame, tags);// * thisGame.getPopularity()+1;
            }

            // update maxweight found
            if (currWeight > maxWeight) {
                if (thisGame.similarTo(lastGame)) {
                    getUserData().addGame(thisGame);
                    continue;
                }
                maxWeight = currWeight;
                nextGame = thisGame;
                Log.MESSAGE("Next Game is "+ thisGame.getName() + " with weight " + currWeight);// + " and similarity " + (thisGame.similarity(getUserData())+1));
            }
        }

        GameToPresent = nextGame;
        
        // if the game is still not found
        // throw an error which will exit
        // the program
        if (GameToPresent == null) {
            throw new NoSuchElementException();
        }

        getUserData().addGame(GameToPresent); // prevent game from showing up again
        return GameToPresent;
    }

    public UserData getUserData() 
    {
        return this.user.getUserData();
    }   

    public Game search() throws NoSuchElementException
    {
        if (iteration > MAX_DISLIKES) {
            Log.MESSAGE("REVISING SEARCH METHOD, REMOVING SOME GAMES");
            List<Game> removedGames = getUserData().removeSomeGames();
            for (Game game : removedGames) {
                recache(game);
            }
            iteration = 0;
        } 

        // if user data is empty then return random game
        if (getUserData().getGames().isEmpty()) {
            Log.MESSAGE("Returning random game.");
            return Database.getRandomGame();
        }

        return nextGame();
    } 

    public void like(Game game) 
    {
        iteration = 0;  
        user.getUserData().likeGame(game); 
        recache(game);          // removes games that need to be recalculated
        GameToPresent = null;   // Set last game to null so similar games will show up
    } 

    public static void main (String[] args) // tests unit
    {
        Database.init();
        GameSearch search = new GameSearch();
        Log.allLogs();
        Log.MESSAGE("All games");
        for (Game game : Database.getAllGames()) {
            Log.MESSAGE(game.getName());
        }
        Log.EXIT(search.nextGame());
    }
}
