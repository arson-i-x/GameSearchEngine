package com.project;

import java.util.*;

public class UserData {
    HashSet<Integer> UserGames;          // Stores game by ID instead of Game Object so we can prevent returning owned games
    HashMap<String, Integer> UserTags;   // Stores Hashset of (tag,hours played) pairs in order that the User most likes

    // empty userdata constructor
    public UserData () 
    {
        this.UserGames = new HashSet<>();
        this.UserTags = new HashMap<>();
    }

    // userData constructor
    public UserData (String URL) 
    {

        /*IMPLEMENT LATER

        //USE API TO GET LIST OF USER GAME OBJECTS
        
        */

        //create a fake list from random database entries
        Database UserDatabase = new Database(URL);
        
        // make list of Game Object and HashSet of Game names
        Collection<Game> UserGames = UserDatabase.GameTable.values();
        this.UserGames = new HashSet<Integer>();
        this.UserTags = new HashMap<String, Integer>();

        for (Game G : UserGames) {
            this.UserGames.add(G.gameID);
            for (String tag : G.tags) {
                this.UserTags.put(tag, G.getHoursPlayed());
            }
        }
    }

    // creates user data from database table
    public UserData (List<Game> UserSelectedGames) 
    {
        this.UserGames = new HashSet<Integer>();
        this.UserTags = new HashMap<String, Integer>();
        for (Game game : UserSelectedGames) {
            this.UserGames.add(game.gameID);
            for (String tag : game.tags) {
                this.UserTags.put(tag, game.getHoursPlayed());
            }
        }
    }

    // Creates game list by querying database
    public static List<Game> CreateUserData (Database database) 
    {   
        boolean UserSearch = true;
        List<Game> GamesList = new ArrayList<Game>();
        while (UserSearch) {
            Game Query = IOController.UserQuery(database);
            if (Query == null) { IOController.GameNotFound(); continue; }
            if (Query.gameID == -1) {  UserSearch = false; }
            if (!UserSearch) { break; }
            IOController.GameAdded(Query);
            GamesList.add(Query);
        }
        return GamesList;
    }

}
