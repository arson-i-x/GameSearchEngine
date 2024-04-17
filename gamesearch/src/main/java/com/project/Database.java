package com.project;
import java.io.*;
import java.util.*;
import org.apache.commons.csv.*;
import me.xdrop.fuzzywuzzy.FuzzySearch;

public class Database {
    
    static final int DATABASE_MAX = 1000;
    static List<CSVRecord> RecordList;
    HashMap<String, Game> GameTable;

    //final static int MAXIMUM_GAMELIST_SIZE = 2000; // LIMIT DATABASE SIZE TO 100 GAMES FOR NOW

    // constructs a database from default path
    public Database () 
    {
        RecordList = ParseCSV("gamesearch/src/main/resources/GameDataset.csv");
        this.GameTable = toGameTable();
    }

    // constructs database with specific path
    public Database (String path) 
    {
        RecordList = ParseCSV(path);
        this.GameTable = toGameTable();
    }

    // Query Database with name of game and return Game Object
    public Game Query(String GameName) 
    {
        // if done searching return an empty game object
        if (GameName.equals("-1")) {
            return new Game(null);
        }
        /* Method to use Game name and get misspellings */
        if (GameName.length() < 4) {
            return this.GameTable.get(GameName);
        }

        // stores lowercase and uppercase version of user input
        String UpperName = GameName.toUpperCase();
        String LowerName = GameName.toLowerCase();
        String correctedName = GameName;
        int bestRatio = 0;

        // compare user input to names in table and return best match
        for (String Name : this.GameTable.keySet()) {
            int upperRatio = FuzzySearch.ratio(Name.toUpperCase(), UpperName);
            int lowerRatio = FuzzySearch.ratio(Name.toLowerCase(), LowerName);
            if (upperRatio > bestRatio && upperRatio > 90 && upperRatio > lowerRatio) {
                bestRatio = upperRatio;
                correctedName = Name;
            } else if (lowerRatio > bestRatio && lowerRatio > 90) {
                bestRatio = lowerRatio;
                correctedName = Name;
            }
        }

        return this.GameTable.get(correctedName);
    }

    // method to convert Database records to list of Game objects
    private HashMap<String, Game> toGameTable () 
    {
        HashMap<String, Game> AllGames = new LinkedHashMap<String, Game>();
        boolean first = true;
        int iterations = 0;
        for (CSVRecord record : Database.RecordList) {
            if (iterations > DATABASE_MAX) {
                break;
            }
            if (first) { first = false; continue; }
            Game newGame = new Game(record);
            AllGames.put(newGame.name, newGame);
            iterations++;
        }
        return AllGames;
    }

    // returns a list of records from CSV file using given string path
    private static List<CSVRecord> ParseCSV (String path) 
    {

    
            List<CSVRecord> RecordList = new ArrayList<CSVRecord>();
    
            // try to parse database records
            try {   
                Reader in = new FileReader(path); 
                Iterable<CSVRecord> records = CSVFormat.RFC4180.builder().setHeader("App ID","Title","Reviews Total","Reviews Score Fancy","Release Date","Reviews D7","Reviews D30","Reviews D90","Launch Price","Tags","name_slug","Revenue Estimated","Modified Tags","Steam Page").build().parse(in);
                for (CSVRecord record : records) {
                    RecordList.add(record);
                }
            } catch (IOException exception) {
                System.out.println("ERROR PARSING DATABASE");
                exception.printStackTrace();
            }
    
            return RecordList;
    }

    // gets random game title from table
    public Game GetRandomGame () 
    {
        Game randomGame = null;
        Random rand = new Random();
        int i = rand.nextInt(this.GameTable.size() - 1);
        int iterations = 0;
        for (Game game : this.GameTable.values()) {
            if (iterations == i) {randomGame = game; break;}
            iterations++;
        }
        return randomGame;
    }

    // tests database structure. Use this to ensure filepath is set correctly
    public static void main (String[] args) 
    {
        List<CSVRecord> newRecords = Database.ParseCSV("gamesearch/src/main/resources/GameDataset.csv");
        int[] appids = new int[5];
        String[] names = new String[5];
        String[] tags = new String[5];
        int index = 0;
        boolean first = true;
        for (CSVRecord record : newRecords) {
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
                System.out.println(appids[index]);
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