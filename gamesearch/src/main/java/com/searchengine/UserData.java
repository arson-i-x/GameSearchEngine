package com.searchengine;

import java.util.*;

import javax.naming.NameNotFoundException;

public class UserData {

    private HashMap<Long, Long> UserGames;        // These should be kept separate for weighting by hours played
    private HashMap<String, Long> UserTags; 
    private long hoursPlayedTotal;

    // empty userdata constructor
    public UserData () 
    {
        this.UserGames = new HashMap<>(); 
        this.UserTags = new HashMap<>();
        hoursPlayedTotal = 0;
    }

    public UserData (UserData data) 
    {
        this.UserGames = new HashMap<>(data.getGames());
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
            Game game = Database.getGame(gameid);
            if (game != null) {
                long hoursPlayed = idList.get(gameid);
                userData.addGame(game, hoursPlayed);
                for (String tag : game.getTags()) {
                    userData.UserTags.put(tag, (userData.UserTags.containsKey(tag)) ?       // hashes tag and hours played of the tag
                                            (userData.UserTags.get(tag) + hoursPlayed) : 
                                            (hoursPlayed));
                }
            }   
        }
        return userData;
    }

    // add game to library by querying database. returns the games URL
    public String addGame(String query) throws NameNotFoundException
    {   
        Game game = Database.query(query);
                
        if (game == null) { 
            throw new NameNotFoundException(); 
        }

        this.addGame(game);    

        return game.getURL();
    }

    public void addGame (Game game, long hoursPlayed) // adds a game with specific amt of hours
    {
        UserGames.put(game.getGameID(), hoursPlayed);
        hoursPlayedTotal += hoursPlayed;
       
        game.getTags().forEach(tag -> {
            UserTags.put(tag, getTagWeight(tag) != null ? 
                        getTagWeight(tag) + hoursPlayedTotal 
                        : 
                        hoursPlayedTotal);
                        //IOController.MESSAGE(tag + " weight is " + getTagWeight(tag));
        });

        IOController.GameAdded(game);
    }  

    public void addGame (Game game) // adds a game with avg amt of hours
    {
        long hoursPlayed = UserGames.size() != 0 ? getAveragePlaytime() : 100;
        hoursPlayedTotal += hoursPlayed;
        UserGames.put(game.getGameID(), hoursPlayed);
        addTags(game.getTags());
        IOController.GameAdded(game);
    }  

    private void addTags(Set<String> tags) // simulates playtime based on current tag data
    {
        for (String tag : tags) {
            if (UserTags.containsKey(tag)) {

                IOController.MESSAGE("INCREASING " + tag + " WEIGHT " + getTagWeight(tag));
                UserTags.put(tag, getTagWeight(tag) + getTagWeight(tag)/tags.size());
                IOController.MESSAGE(" TO NEW WEIGHT" + getTagWeight(tag));
            }
            else {
                
                UserTags.put(tag, getAveragePlaytime());
                IOController.MESSAGE("MAKING " + tag + " WEIGHT " + getTagWeight(tag));
            }
        }
    }

    public boolean isEmpty() 
    {
        return this.UserGames.isEmpty();
    }

    public Long getTotalHoursPlayed() 
    {
        return hoursPlayedTotal;
    }

    public Long getAveragePlaytime () 
    {
        return hoursPlayedTotal / UserGames.size();
    }

    public HashMap<Long, Long> getGames()
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

    public void removeSomeGames () 
    {
        // sort tags by playtime
        List<Map.Entry<String,Long>> sortedTags = new LinkedList<>(UserTags.entrySet().stream().sorted(Map.Entry.comparingByValue()).toList());

        // make a list of tags and gameids to remove
        List<String> tagsToRemove = new LinkedList<>();
        List<Long> gamesToRemove = new LinkedList<>();
        
        // remove 1/3 of lowest tags 
        for (int i = 0; i < UserTags.size()/3; i++) {
            String tag = sortedTags.get(i).getKey();
            tagsToRemove.add(tag);
            UserTags.remove(tag);
        }

        // add games with tags to remove
        for (long gameid : getGames().keySet()) {
            for (String tag : Database.getGame(gameid).getTags()) {
                if (tagsToRemove.contains(tag)) {
                    gamesToRemove.add(gameid);
                }
            }
        }

        // removes games
        for (long gameid : gamesToRemove) {
            UserGames.remove(gameid);
            IOController.MESSAGE(Database.getGame(gameid).getName() + " REMOVED FROM LIBRARY!!");
        }

        for (long gameid : getGames().keySet()) {
            IOController.MESSAGE(Database.getGame(gameid).getName());
        }
    }
}