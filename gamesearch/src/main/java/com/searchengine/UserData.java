package com.searchengine;

import java.util.*;

public class UserData {

    private HashMap<Long, List<String>> UserInfo; // Dennis - Stores steam game ID and List of Tags

    // empty userdata constructor
    public UserData () 
    {
        this.UserInfo = new HashMap<>(); // Dennis - Init HashMap
    }

    // Creates user data from list of games
    private UserData (List<Game> UserSelectedGames) 
    {
        this.UserInfo = new HashMap<>(); // Dennis - Init HashMap
        // Dennis - Store game id's and their associated
        // tags into the hashmap UserInfo.
        for (Game game : UserSelectedGames) {
            UserInfo.put(game.getGameID(), new ArrayList<>(game.getTags()));
        }
    }

    // Creates game list by querying database
    public static UserData CreateUserData (String query) 
    {   
        // asks user for input and adds game if found
        List<Game> GamesList = new ArrayList<>();
        
        while (true) {

            Game game = Database.query(query);
            
            // if game not found output error
            if (game == null) { 
                IOController.ERROR("GAME NOT FOUND"); 
                query = IOController.UserQuery();
                continue; 
            }
            if (game.getGameID() == -1) {  
                break; 
            }
            IOController.GameAdded(game);
            GamesList.add(game);
            query = IOController.UserQuery();
        }

        return new UserData(GamesList);
    }

    // Creates userdata from id list
    public static UserData CreateUserData (List<Long> idList) 
    {
        // gets list of games by ID from database
        List<Game> GamesList = new ArrayList<>();
        for (Long id : idList) {
            if (Database.getGame(id) != null) {
                GamesList.add(Database.getGame(id));
            }
        }

        return new UserData(GamesList);
    }

    void addGame (Game game) 
    {
        UserInfo.put(game.getGameID(), new ArrayList<>(game.getTags()));
    }  

    public Set<Long> getGames()
    {
        return UserInfo.keySet();
    }
}