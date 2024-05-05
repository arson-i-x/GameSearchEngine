package com.searchengine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import javax.naming.NameNotFoundException;

public class UserData implements Serializable {
    private static final File userdataFile = new File("gamesearch/src/main/resources/userdata.ser");
    private HashMap<Long,Long> UserGames;  // stores game ids/hours played of game
    private HashMap<String, Long> tags;
    private long hoursPlayedTotal;

    // empty userdata constructor
    public UserData () 
    {
        this.UserGames = new HashMap<>(); 
        this.tags = new HashMap<>();
        hoursPlayedTotal = 0;
    }

    public UserData (UserData data) 
    {
        this.UserGames = new HashMap<>(data.getGames());
        this.tags = new HashMap<>(data.getTags());
        this.hoursPlayedTotal = data.hoursPlayedTotal;
    }

    public static UserData CreateUserData (HashMap<Long, Long> idList) // Creates userdata from id list 
    {
        UserData userData = new UserData();

        for (Long gameid : idList.keySet()) {
            Game game = Database.getGame(gameid.toString());
            if (game != null) {
                long hoursPlayed = idList.get(gameid);
                userData.addGame(game, hoursPlayed);
            }   
        }
        writeUserDataToFile(userData);
        return userData;
    }

    public void addGame(String query) throws RuntimeException // add game to library by querying database. returns the games URL
    {   
        try {
            Game game = Database.query(query);
            this.addGame(game, getAveragePlaytime());    
            writeUserDataToFile(this);
        } catch (NameNotFoundException e) {
            throw new RuntimeException("GAME NOT FOUND");
        }
    }

    public void addGame (Game game, long hoursPlayed) // adds a game with specific amt of hours
    {
        UserGames.put(game.getGameID(), hoursPlayed);
        hoursPlayed = Math.abs(hoursPlayed);
        hoursPlayedTotal += hoursPlayed;
        writeUserDataToFile(this);
        Log.GameAdded(game, this);
    }   

    public void likeGame (Game game) // adds a game with avg amt of hours
    {
        Long hoursPlayed = getAveragePlaytime();
        addGame(game, -hoursPlayed);
    }  

    public HashMap<String, Long> getTags() 
    {
        HashMap<String, Long> tags = new HashMap<>();
        for (Long gameid : UserGames.keySet()) {
            Game game = Database.getGame(gameid.toString());
            int rank = 0;
            for (String tag : game.getTags()) {
                rank++;
                if (tags.containsKey(tag)) {
                    tags.put(tag, tags.get(tag) + UserGames.get(gameid)/rank);
                } else {
                    tags.put(tag, UserGames.get(gameid)/rank);
                }
            }
        }
        return tags;
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
        if (UserGames.size() == 0) {
            return (long)1000000000; 
        }
        return hoursPlayedTotal / (UserGames.size() - 1);
    }

    public HashMap<Long, Long> getGames()
    {
        return UserGames;
    }

    public void removeSomeGames () 
    {
        // sort tags by playtime
        List<Long> sortedGames = new LinkedList<>(UserGames.keySet().stream().toList());
        Collections.sort(sortedGames, new Comparator<Long>() {
            @Override
            public int compare(Long id1, Long id2) {
                if (UserGames.get(id2) < UserGames.get(id1)) {
                    return 1;
                } else if (UserGames.get(id2) > UserGames.get(id1)) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        for (Long id : sortedGames) {
            Game game = Database.getGame(id.toString());
            Log.MESSAGE(game.getName() + " hours played = " + this.UserGames.get(id)); //TODO
        }
        
        // remove 1/3 of lowest played games 
        for (int i = 0; i < UserGames.size()/3; i++) {
            Game game = Database.getGame(sortedGames.get(i).toString());
            Log.MESSAGE("REMOVING GAME " + game.getName());
            UserGames.remove(game.getGameID());
        }

        for (Long id : getGames().keySet()) {
            Log.MESSAGE("Library contains: " + Database.getGame(id.toString()));
        }
    }
 
    private static void writeUserDataToFile(UserData data) 
    {
        try {
            userdataFile.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(userdataFile);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(data);
            out.close();
            fileOut.close();
            Log.MESSAGE("USERDATA SAVED TO FILE");
        } catch (IOException i) {
            Log.MESSAGE(i.getMessage());
        }
    }

    public static UserData getUserDataFromFile () 
    {
        UserData data = null;
        try {
            userdataFile.createNewFile();
            FileInputStream fileIn = new FileInputStream(userdataFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            data = (UserData) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            Log.MESSAGE("NO USERDATA FOUND, GENERATING FILE");
            return new UserData();
        } catch (ClassNotFoundException c) {
            Log.MESSAGE("USERDATA CLASS NOT FOUND");
            return new UserData();
        }
        if (data == null) {
            Log.MESSAGE("NO USERDATA FOUND, GENERATING FILE");
            return new UserData();
        } else {
            Log.userdata(data);
            return data;
        }
    }

    public static void clearFile() 
    {
        userdataFile.delete();
    }
}