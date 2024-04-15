package com.project;
import java.util.*;
import org.apache.commons.csv.CSVRecord;

public class Game {
    int gameID;
    String name;
    String price;
    String URL;
    HashSet<String> tags;
    HashMap<Integer, Edge> edges;
    boolean removed;
    
    // constructs a game object using a CSV record and index
    public Game (CSVRecord record) 
    {
        // makes an empty game object
        if (record == null) {
            this.gameID = -1;
            return;
        }

        // Game ID and Name
        this.gameID = Integer.parseInt(record.get("App ID"));
        this.name = record.get("Title");

        // Game tags
        this.tags = new HashSet<String>();
        String tagString = record.get("Tags");
        String[] tags = tagString.split(",");
        for (String tag : tags) {
            this.tags.add(tag);
        }

        // Game Info
        this.URL = record.get("Steam Page");
        this.price = record.get("Launch Price");
        this.edges = new HashMap<Integer, Edge>();
        this.removed = false;
    }

    // sets a game as removed and gets rid of all edges
    public void RemoveGame () 
    {
        // set this game as removed
        this.removed = true;

        // remove edge weights from every other game that points to this game
        for (int GameID : this.edges.keySet()) {
            HashMap<Integer, Edge> OtherGameEdges = this.edges.get(GameID).Game.edges;
            if (OtherGameEdges.containsKey(this.gameID)) {
                OtherGameEdges.get(this.gameID).Weight = 0;
            }
        }
    }

    public int getHoursPlayed () 
    {
        int hoursPlayed = 1;
        //USE APP ID TO GET USER HOURS PLAYED
        return hoursPlayed;
    }

    // tests data structure
    public static void main (String[] args) 
    {
        Game[] games = new Game[5];
        Database database = new Database();
        List<Game> AllGames = database.GameList;
        int i = 0;
        int first = 1;
        for (CSVRecord record : database.RecordList) {
            if (first == 1) { first = 0; continue; }
            games[i] = new Game(record);
            i++;
            if (i == 5) {
                break;
            }
        }

        int limit = 0;
        for (Game game : AllGames) {
            if (limit == 30) {break;}
            System.out.println(game.name + " " + game.gameID + " Tags: ");
            for (String tag : game.tags) {
                System.out.print("'" + tag + "' ");
            }
            System.out.println();
            limit++;
        }
    }
}
