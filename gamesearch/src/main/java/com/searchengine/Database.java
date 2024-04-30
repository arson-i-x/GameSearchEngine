package com.searchengine;

import java.io.*;
import java.util.*;

import org.apache.commons.csv.*;
import me.xdrop.fuzzywuzzy.FuzzySearch;

final public class Database {
    
    private static final List<CSVRecord> RecordList = new ArrayList<>();     // list of csv records in this database
    private static final HashMap<Long, Game> GameTable = new LinkedHashMap<>();  // Hashmap of GameIDs and Game Objects

    public static void init()  // constructs a database from default path 
    {
        parseCSV("gamesearch/src/main/resources/GameDataset.csv");
    }

    static Game getRandomGame() // gets random game from table
    {
        Random rand = new Random();
        int i = rand.nextInt(GameTable.size() - 1); 
        return getAllGames().get(i);
    }

    public static int size () 
    {
        return GameTable.size();
    }

    public static Game getGame(Long id)      // gets game with this ID from table
    {
        return GameTable.get(id);
    }

    static List<Game> getAllGames() // gets all game object in the database 
    {
        List<Game> games = new LinkedList<>((GameTable.values().stream()).toList());
        Collections.shuffle(games); // Shuffles gamelist on each call
        return games;
    }

    static List<CSVRecord> getRecords()   // gets all records in the database 
    {
        return RecordList;
    }

    static Game query(String GameName) // Query Database with name of game and return Game Object    {
    {
        // if done searching return an empty game object
        if (GameName.equals("-done") || GameName.equals("")) {
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
            if (upperRatio > bestRatio && upperRatio > 85 && upperRatio > lowerRatio) { // higher ratio means better match
                bestRatio = upperRatio;
                bestGameID = Integer.parseInt(record.get("App ID"));
            } else if (lowerRatio > bestRatio && lowerRatio > 85) { // if lower is better than upper it is choosen
                bestRatio = lowerRatio;
                bestGameID = Integer.parseInt(record.get("App ID"));
            }
        }

        // compare user input to names in table and return best match
        return getGame(bestGameID);
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
        Map<Integer, Map.Entry<String, String>> map = new HashMap<>(); // Dennis - Created Map
//        int[] appids = new int[5];
//        String[] names = new String[5];
//        String[] tags = new String[5];
        int index = 0;
        boolean first = true;
        for (CSVRecord record : Database.RecordList) {
            if (first) { first = false; continue; }
//            appids[index] = Integer.parseInt(record.get("App ID"));
//            names[index] = record.get("Title");
//            tags[index] = record.get("Tags");
            // Dennis - Put values into map
            map.put(Integer.parseInt(record.get("App ID")), new AbstractMap.SimpleEntry<>(record.get("Title"), record.get("Tags")));
            index++;
            if (index == 5) {
                break;
            }
        }

//        index = 0;
//        for (String taglist : tags) {
//            //System.out.println(appids[index]);
//            System.out.println(names[index]);
//            String[] SplitTags = taglist.split(",");
//            for (String tag : SplitTags) {
//                System.out.print(tag + " ");
//            }
//            System.out.println("\n");
//            index++;
//        }
        // Dennis - Print out values in map.
        for (Map.Entry<Integer, Map.Entry<String, String>> entry : map.entrySet()) {
            System.out.println("Game ID = " + entry.getKey() + "\nGame Name = " + entry.getValue().getKey() + "\nTags = " + entry.getValue().getValue());
        }
    }
}