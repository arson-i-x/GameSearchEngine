package com.searchengine;

import java.io.*;
import java.util.*;
import javax.naming.NameNotFoundException;
import org.apache.commons.csv.*;

import me.xdrop.fuzzywuzzy.FuzzySearch;

final public class Database {
    
    private static final Map<String, Game> GameTable = new LinkedHashMap<>();

    public static void init()  // constructs a database from default path 
    {
        parseCSV("gamesearch/src/main/resources/GameDataset.csv");
    }

    public static Game getRandomGame() // gets random game from table
    {
        return getAllGames().get(0);
    }

    public static int size () 
    {
        return GameTable.size()/2;
    }

    public static List<Game> getAllGames() // gets all game object in the database
    {
        //List<Game> games = new ArrayList<>(GameTable.values());
        //Collections.shuffle(games); // Shuffles gamelist on each call
        return new ArrayList<>(GameTable.values());
    }

    public static Set<String> getAllNames()
    {
        return GameTable.keySet();
    }

    public static Game getGame(String game)
    {
        return GameTable.get(game);
    }

    public static Game query(String name) throws NameNotFoundException // Query Database with name of game and return Game Object
    {
        Game game = null;

        // try finding the name directly
        // if not found, fuzzysearch
        if (GameTable.containsKey(name)) {
            return GameTable.get(name);
        }

        // fuzzy search
        game = fuzzyquery(name);
        
        // if still not found
        // throw error
        if (game == null) {
            throw new NameNotFoundException();
        } else {
            Log.MESSAGE("Name found: " + game.getName());
            return game;
        }
    }

    static Game fuzzyquery(String GameName) 
    {
        if (GameName.equals("-done") || GameName.equals("")) {
            return new Game();
        }

        // stores lowercase and uppercase version of user input
        String UpperName = GameName.toUpperCase();
        String LowerName = GameName.toLowerCase();
        Game bestGame = null;
        int bestRatio = 0;
        
        // check each record for the given name
        for (String name : Database.GameTable.keySet()) {                                                          
            int upperRatio = FuzzySearch.ratio(name.toUpperCase(), UpperName);  // uses fuzzy search to store a best ratio match
            int lowerRatio = FuzzySearch.ratio(name.toLowerCase(), LowerName);  // and corrected version of the name
            if (upperRatio > bestRatio && upperRatio > 85 && upperRatio > lowerRatio) { // higher ratio means better match
                bestRatio = upperRatio;
                bestGame = Database.GameTable.get(name);
            } else if (lowerRatio > bestRatio && lowerRatio > 85) { // if lower is better than upper it is choosen
                bestRatio = lowerRatio;
                bestGame = Database.GameTable.get(name);
            }
        }

        return bestGame;
    }

    static void parseCSV(String path)   // parses a CSV file into database using given string path as input
    {
        // clears database for new entries
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
                putNewGame(record.get("Title"), record.get("App ID"), new Game(record));
            }
        } catch (IOException exception) {
            Log.EXIT(exception.getMessage());
        }
    }

    private static void putNewGame(String title, String id, Game game) 
    {
        GameTable.put(id, game);
        GameTable.put(title, game);
    }

    public static void main(String[] args) // tests database structure. Use this to ensure filepath is set correctly
    {
        Database.init();
        Log.MESSAGE("DATABASE INITIALIZE: RANDOM GAME IS --> " + Database.getRandomGame().getName());
    }
}