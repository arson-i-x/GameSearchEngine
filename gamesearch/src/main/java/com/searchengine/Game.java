package com.searchengine;
import java.io.Serializable;
import java.util.*;
import org.apache.commons.csv.CSVRecord;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class Game {
    private Long gameID;
    private String name;
    private int price;
    private int reviewRatio;
    private int reviewCount;
    private String URL;
    private String priceString;
    private HashSet<String> tags;
    private boolean removed;
    private ReleaseDate date;
    private Long hoursPlayed;

    // constructs a game object using a CSV record
    public Game () 
    {
        this.gameID = (long)-1;
    }
    
    public Game (CSVRecord record) 
    {
        // Game ID and Name
        gameID = Long.parseLong(record.get("App ID"));
        name = record.get("Title");

        // places game tags into hashmap by rank order within game
        tags = new LinkedHashSet<>();
        String tagString = record.get("Tags");
        String[] tempTagList = tagString.split(",");
        tags.addAll(Arrays.asList(tempTagList));

        // Release date
        String tempstring = record.get("Release Date");
        String[] templist = tempstring.split("-");
        int i = 1;
        for (String date : templist) {
            switch (i) {
                case 1:
                    this.date = new ReleaseDate();
                    this.date.year = Integer.parseInt(date);
                    i++;
                    break;
                case 2:
                    this.date.month = Integer.parseInt(date);
                    i++;
                    break;
                case 3:
                    this.date.day = Integer.parseInt(date);
                    break;
            }

        }
        
        // gets review count
        reviewCount = Integer.parseInt(record.get("Reviews Total"));

        // Splits input into ratio e.g. (88,64%) -> (88)
        String reviews = record.get("Reviews Score Fancy");
        reviews = reviews.split("%")[0];
        reviewRatio = Integer.parseInt(reviews.split(",")[0]);

        // Gets int value from dollar amount
        priceString = record.get("Launch Price");
        String temp = priceString.replaceAll("[^0-9/-]+", "");
        price = Integer.parseInt(temp);

        // Game info
        URL = record.get("Steam Page");
        removed = false;
    }

    public Game (Game game) 
    {
        this.URL = game.URL;
        this.date = game.date;
        this.gameID = game.gameID;
        this.hoursPlayed = game.hoursPlayed;
        this.name = game.name;
        this.price = game.price;
        this.priceString = game.priceString;
        this.reviewCount = game.reviewCount;
        this.reviewRatio = game.reviewRatio;
        this.removed = game.removed;
        this.tags = game.tags;
    }

    @Override
    public int hashCode() 
    {
        return gameID.hashCode();
    }

    public void RemoveGame () 
    {
        removed = true;
    }

    public void InsertGame () 
    {
        removed = false;
    }

    public boolean IsRemoved () 
    {
        return removed;
    }
    
    public long getGameID () 
    {
        return gameID;
    }

    public String getName() 
    {
        return name;
    }

    public int getReviewScore () 
    {
        return reviewRatio;
    }

    public int getPopularity () 
    {
        return reviewCount;
    }

    public int getPrice () 
    {
        return price;
    }

    public String getPriceString () 
    {
        return priceString;
    }
    
    public ReleaseDate getReleaseDate () 
    {
        return date;
    }

    public String getURL() 
    {
        return URL;
    }

    public HashSet<String> getTags() 
    {
        return tags;
    }
    
    public boolean similarTo (Game otherGame) 
    {
        if (otherGame == null) {
            return false;
        }
        /*if (this.getTags().containsAll(otherGame.getTags())) {
            return true;
        }*/
        return FuzzySearch.ratio(name, otherGame.getName()) > 75;
    }

    public int similarity (UserData data) 
    {
        Set<String> usertags = data.getTags().keySet();
        if (usertags.containsAll(this.getTags())) {
            return 100;
        }
        List<String> newlist = new ArrayList<>(this.getTags());
        newlist.retainAll(usertags);
        return newlist.size()/usertags.size();
    }

    private final class ReleaseDate implements Comparable<ReleaseDate>, Serializable  // release dates can be compared easily using this class
    {
        int year;
        int month;
        int day;

        public int compareTo (ReleaseDate otherReleaseDate) 
        {
            int otherDate = otherReleaseDate.year*200 + otherReleaseDate.month*100 + otherReleaseDate.day;
            int thisDate = this.year*200 + this.month*100 + this.day;
            return thisDate - otherDate;
        }
    }
    
    // tests data structure
    public static void main (String[] args) 
    {
        Database.init();
        Collection<Game> AllGames = Database.getAllGames();
        int limit = 0;
        for (Game game : AllGames) {
            if (limit == 30) {break;}
            System.out.println("Game Name: " + game.name + "\nGame Release: " + game.date.year + "-" + game.date.month + "-" + game.date.day );
            for (String tag : game.tags) {
                System.out.print("|" + tag + "| ");
            }
            System.out.println(" " + game.price);
            limit++;
        }
    }
}
