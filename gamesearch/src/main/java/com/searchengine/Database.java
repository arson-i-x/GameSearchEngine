package com.searchengine;

import java.io.*;
import java.util.*;
import org.apache.commons.csv.*;
import me.xdrop.fuzzywuzzy.FuzzySearch;

final public class Database {
    
    private static final List<CSVRecord> RecordList = new ArrayList<CSVRecord>();     // list of csv records in this database
    private static final HashMap<Long, Game> GameTable = new HashMap<Long, Game>();  // Hashmap of GameIDs and Game Objects

    public static void init()  // constructs a database from default path 
    {
        parseCSV("gamesearch/src/main/resources/GameDataset.csv");
    }

    static Game getRandomGame() // gets random game from table
    {
        Game randomGame = null;
        Random rand = new Random();
        int i = rand.nextInt(GameTable.size() - 1);
        int iterations = 0;
        for (Game game : GameTable.values()) {
            if (iterations == i) {
                randomGame = game; 
                break;
            } else {
                iterations++;
            }
        }
        return randomGame;
    }

    public static Game getGame(Long id)      // gets game with this ID from table
    {
        return GameTable.get(id);
    }

    static Collection<Game> getAllGames() // gets all game object in the database 
    {
        return GameTable.values();
    }

    static List<CSVRecord> getRecords()   // gets all records in the database 
    {
        return RecordList;
    }

    static Game query(String GameName) // Query Database with name of game and return Game Object    {
    {
        // if done searching return an empty game object
        if (GameName.equals("-1")) {
            return new Game(null);
        }

        /* Method to use Game name and get misspellings */
        
        // stores lowercase and uppercase version of user input
        String UpperName = GameName.toUpperCase();
        String LowerName = GameName.toLowerCase();
        long bestGameID = -1;
        int bestRatio = 0;
        
        // check each record for the given name
        for (CSVRecord record : RecordList) {                                   
            String name = record.get("Title");                             
            int upperRatio = FuzzySearch.ratio(name.toUpperCase(), UpperName);  // uses fuzzy search to store a best ratio match
            int lowerRatio = FuzzySearch.ratio(name.toLowerCase(), LowerName);  // and corrected version of the name
            if (upperRatio > bestRatio && upperRatio > 50 && upperRatio > lowerRatio) { // higher ratio means better match
                bestRatio = upperRatio;
                bestGameID = Integer.parseInt(record.get("App ID"));
            } else if (lowerRatio > bestRatio && lowerRatio > 50) { // if lower is better than upper it is choosen
                bestRatio = lowerRatio;
                bestGameID = Integer.parseInt(record.get("App ID"));
            }
        }

        // compare user input to names in table and return best match
        return Database.getGame(bestGameID);
    }

    static void parseCSV(String path)   // parses a CSV file into database using given string path as input
    {
        // clears database for new entries
        RecordList.clear();
        GameTable.clear();
        // Uses CSV library to parse records with header format
        try {   
            Reader in = new FileReader(path); 
            Iterable<CSVRecord> records = CSVFormat.RFC4180.builder().setHeader("App ID",
                                                                        "Title",
                                                                        "Reviews Total",
                                                                        "Reviews Score Fancy",
                                                                        "Release Date",
                                                                        "Reviews D7",
                                                                        "Reviews D30",
                                                                        "Reviews D90",
                                                                        "Launch Price",
                                                                        "Tags",
                                                                        "name_slug",
                                                                        "Revenue Estimated",
                                                                        "Modified Tags",
                                                                        "Steam Page").build().parse(in);
            boolean first = true;
            for (CSVRecord record : records) { // put records into database, skipping header
                if (first) { first = false; continue; }
                RecordList.add(record);
                Game game = new Game(record);
                GameTable.put(game.getGameID(), game);
            }
        } catch (IOException exception) {
            GameSearch.EXIT(exception.getMessage());
        }
    }

    public static void main(String[] args)      // tests database structure. Use this to ensure filepath is set correctly
    {
        Database.init();
        int[] appids = new int[5];
        String[] names = new String[5];
        String[] tags = new String[5];
        int index = 0;
        boolean first = true;
        for (CSVRecord record : Database.RecordList) {
            if (first) { first = false; continue; }
            appids[index] = Integer.parseInt(record.get("App ID"));
            names[index] = record.get("Title");
            tags[index] = record.get("Tags");
            index++;
            if (index == 5) {
                break;
            }
        }
        
        index = 0;
        for (String taglist : tags) {
                //System.out.println(appids[index]);
                System.out.println(names[index]);
            String[] SplitTags = taglist.split(",");
            for (String tagg : SplitTags) {
                System.out.print(tagg + " ");
            }
            System.out.println();
            index++;
        }
    }
}