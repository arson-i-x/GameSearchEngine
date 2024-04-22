package com.searchengine;

import java.util.*;

public class UserData {

    private HashSet<Long> UserGames;        // These should be kept separate for weighting by hours played
    private HashMap<String, Long> UserTags; 
    private long hoursPlayedTotal;

    // empty userdata constructor
    public UserData () 
    {
        this.UserGames = new HashSet<>(); 
        this.UserTags = new HashMap<>();
        hoursPlayedTotal = 0;
    }

    public UserData (UserData data) 
    {
        this.UserGames = new HashSet<>(data.getGames());
        this.UserTags = new HashMap<>(data.getTags());
        this.hoursPlayedTotal = data.hoursPlayedTotal;
    }

    // Creates game list by querying database
    public static UserData CreateUserData (String query) 
    {   
        UserData UserData = new UserData();

        // asks user for input and adds game if found
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
            UserData.addGame(game);
            query = IOController.UserQuery();
        }

        return UserData;
    }

    // Creates userdata from id list
    public static UserData CreateUserData (HashMap<Long, Long> idList) 
    {
        UserData userData = new UserData();

        for (Long gameid : idList.keySet()) {
            userData.UserGames.add(gameid);
            Game game = Database.getGame(gameid);
            if (game != null) {
                long hoursPlayed = idList.get(gameid);
                userData.hoursPlayedTotal += hoursPlayed;
                for (String tag : game.getTags().keySet()) {
                    userData.UserTags.put(tag, (userData.UserTags.containsKey(tag)) ?       // hashes tag and hours played of the tag
                                            (userData.UserTags.get(tag) + hoursPlayed) : 
                                            (hoursPlayed));
                }
            }   
        }
        return userData;
    }

    void addGame (Game game) 
    {
        UserGames.add(game.getGameID());
        UserTags.putAll(game.getTags());
        hoursPlayedTotal += 1;
        IOController.GameAdded(game);
    }  

    public Set<Long> getGames()
    {
        return UserGames;
    }

    public HashMap<String, Long> getTags() 
    {
        return UserTags;
    }

    public Long getTagWeight (String tag) 
    {
        return UserTags.get(tag);
    }
}