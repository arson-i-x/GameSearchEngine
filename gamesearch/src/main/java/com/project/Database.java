package com.project;
import java.io.*;
import java.util.*;
import org.apache.commons.csv.*;
import org.springframework.core.io.ClassPathResource;

public class Database {
    
    List<CSVRecord> RecordList;
    List<Game> GameList;
    HashMap<String, Game> NameTable;

    final static int MAXIMUM_GAMELIST_SIZE = 1000; // LIMIT DATABASE SIZE TO 100 GAMES FOR NOW

    

    // constructs a database from CSV file output
    public Database () 
    {
        this.RecordList = ParseCSV();
        this.GameList = this.DatabasetoGamelist(); 
        this.NameTable = new HashMap<String, Game>();
        for (Game G : this.GameList) {
            this.NameTable.put(G.name, G);
        }
    }

    public Database (String path) 
    {
        this.RecordList = ParseCSV(path);
        this.GameList = this.DatabasetoGamelist(); 
        this.NameTable = new HashMap<String, Game>();
        for (Game G : this.GameList) {
            this.NameTable.put(G.name, G);
        }
    }

    // Query Database with name of game and return Game Object
    public Game Query(String GameName) 
    {
        // if done searching return an empty game object
        if (GameName.equals("DONE")) {
            return new Game(null);
        }

        // otherwise return null
        return this.NameTable.get(GameName);
    }

    // method to convert Database to list of Game objects
    private List<Game> DatabasetoGamelist () 
    {
        List<Game> AllGames = new ArrayList<Game>();
        int index = 0;
        boolean first = true;
        for (CSVRecord record : this.RecordList) {
            if (first) { first = false; continue; }
            AllGames.add(new Game(record));
            index++;
            if (index == MAXIMUM_GAMELIST_SIZE) { break; }
        }
        return AllGames;
    }

    // returns a list of records from CSV file
    private static List<CSVRecord> ParseCSV () 
    {
        List<CSVRecord> RecordList = new ArrayList<CSVRecord>();

        // try to parse database records
        try {   
            ClassPathResource resource = new ClassPathResource("gamesearch/src/main/resources/GameDataset.csv");
            Reader in = new FileReader(resource.getPath()); 
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

    // tests database structure. Use this to ensure filepath is set correctly
    public static void main (String[] args) 
    {
        List<CSVRecord> newRecords = Database.ParseCSV();
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