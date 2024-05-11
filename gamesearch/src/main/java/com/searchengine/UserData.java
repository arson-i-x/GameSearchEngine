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
    private static final File userdataFile = new File(GameSearch.path.getAbsolutePath()+"/userdata.ser");
    private Map<Long,Long> userGames;  // stores game ids/hours played of game
    private long hoursPlayedTotal;
    private int size;

    // empty userdata constructor
    public UserData () 
    {
        this.userGames = new LinkedHashMap<>(); 
        hoursPlayedTotal = 0;
        size = 0;
    }

    public UserData (UserData data) 
    {
        this.userGames = new LinkedHashMap<>(data.getGames());
        this.hoursPlayedTotal = data.hoursPlayedTotal;
        this.size = data.size;
    }

    public int size () 
    {
        return this.size;
    }

    public static UserData CreateUserData (HashMap<Long, Long> idList) // Creates userdata from id list 
    {
        UserData userData = new UserData();

        for (Long gameid : idList.keySet()) {
            Game game = Database.getGame(gameid.toString());
            if (game == null) { continue; }
            long hoursPlayed = idList.get(gameid);
            userData.addGame(game, hoursPlayed, false);
            userData.size++;
        }
        writeUserDataToFile(userData);
        return userData;
    }

    private void addGame (Game game, long hours, boolean writeToFile) // adds a game with specific amt of hours
    {
        putHours(game, hours);
        long hoursPlayed = Math.abs(hours);
        hoursPlayedTotal += hoursPlayed;
        Log.GameAdded(game, this);
        if (writeToFile) {
            writeUserDataToFile(this);
        }
    }  

    public Map<String, Long> getTags() 
    {
        Map<String, Long> usertags = new HashMap<>();
        for (Long id : userGames.keySet()) {
            Game game = Database.getGame(id.toString());
            int rank = game.getTags().size();
            int i = rank;
            Long hoursPlayed = Math.abs(userGames.get(id));
            for (String tag : game.getTags()) {
                if (usertags.containsKey(tag)) {
                    usertags.put(tag, usertags.get(tag) + hoursPlayed*i/rank);
                    //Log.MESSAGE((UserTags.get(tag) + hoursPlayed*i/rank) + " is new weight for " + tag + " in game " + game.getName());
                } else {
                    usertags.put(tag, hoursPlayed*i/rank);
                    //Log.MESSAGE(hoursPlayed*i/rank + " is weight for " + tag + " in game " + game.getName());
                }
                i--;
            }
        }
        return usertags;
    }

    public void addGame(String query) throws RuntimeException // add game to library by querying database
    {   
        try {
            Game game = Database.query(query);
            this.addGame(game, getAveragePlaytime(), true);    
        } catch (NameNotFoundException e) {
            throw new RuntimeException("GAME NOT FOUND");
        }
    } 

    public void likeGame (Game game) // adds a game with avg amt of hours
    {
        addGame(game, -getAveragePlaytime(), true);
    }  

    public void addGame (Game game) // adds a game with no hours
    {
        addGame(game, (long)0, true);
    }

    public boolean isEmpty() 
    {
        return this.userGames.isEmpty();
    }

    public Long getTotalHoursPlayed() 
    {
        return hoursPlayedTotal;
    }

    public Long getAveragePlaytime () 
    {
        if (size == 0 || hoursPlayedTotal == 0) {
            return (long)1000000000; 
        }
        
        List<Long> sortedGames = getGamesInOrder().reversed();
        long hours = 0;
        for (int i = 0; i < 3; i++) {
            
            hours += getHours(sortedGames.get(i));
            Log.MESSAGE(Database.getGame(sortedGames.get(i).toString()).getName()+"  "+hours+ "  " +hours/3);
        }
        return hours/3;
    }

    public Map<Long, Long> getGames()
    {
        return userGames;
    }

    public List<Long> getGamesInOrder()
    {
        // sort tags by playtime
        List<Long> sortedGames = new LinkedList<>(userGames.keySet().stream().toList());
        Collections.sort(sortedGames, new Comparator<Long>() {
            @Override
            public int compare(Long id1, Long id2) {
                if (getHours(id2) < getHours(id1)) {
                    return 1;
                } else if (getHours(id2) > getHours(id1)) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        return sortedGames;
    }

    

    public List<Game> removeSomeGames () 
    {
        List<Game> removedGames = new ArrayList<>();

        List<Long> sortedGames = getGamesInOrder();

        for (Long id : sortedGames) {
            Game game = Database.getGame(id.toString());
            Log.MESSAGE(game.getName() + " hours played = " + getHours(id));
        }
        
        // remove 1/3 of lowest played games 
        for (int i = 0; i < userGames.size()/3; i++) {
            Game game = Database.getGame(sortedGames.get(i).toString());
            Log.MESSAGE("REMOVING GAME " + game.getName());
            putHours(game, (long)0); // remove game by making play time 0
            removedGames.add(game);
        }

        for (Long id : getGames().keySet()) {
            Log.MESSAGE("Library now contains: " + Database.getGame(id.toString()).getName());
        }

        return removedGames;
    }
 
    public static void writeUserDataToFile(UserData data) 
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
            Log.ERROR(i.getMessage());
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
            Log.ERROR("USERDATA CLASS NOT FOUND");
            return new UserData();
        }
        if (data == null) {
            Log.ERROR("NO USERDATA FOUND, GENERATING FILE");
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

    public void putHours(Game game, Long hours) 
    {
        userGames.put(game.getGameID(), hours);
    }

    public long getHours(long id) 
    {
        return userGames.get(id);
    }
}