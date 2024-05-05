package com.searchengine;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;
import com.lukaspradel.steamapi.core.exception.SteamApiException;

public class TerminalApplication extends GameSearch
{
    private static final Scanner inputScanner = new Scanner(System.in);
    private static final PrintStream outputStream = new PrintStream(System.out);

    private TerminalApplication() 
    {
        this.clearUserData();
        putOutput("Enter Steam ID or Search Games");
        this.BuildUserSearchFromTerminal();
    }

    public static String getInput() 
    {
        return inputScanner.nextLine();
    }

    public static void putOutput(String output) 
    {
        outputStream.println(output);
    }
    
    public void BuildUserSearchFromTerminal ()
    {
        // init gamesearch
        Database.init();
        String id = getInput();

        // user login
        try {
            login(id);
        } catch (SteamApiException steamApiException) {
            putOutput("STEAM API ERROR\nMAKE SURE LIBRARY IS SET TO PUBLIC");
            System.exit(1);
        } catch (IOException re) {
            this.BuildUserSearchFromTerminal(id);
        }
    }

    private void putGame(String input) 
    {
        try {
            getUserData().addGame(input);   
        } catch (RuntimeException e) {
            putOutput(e.getMessage());
            e.printStackTrace();
        }  
    }

    public void BuildUserSearchFromTerminal (String input)
    {
        boolean first = true;
        while (true) {
            if (first) { 
                first = false; 
                putGame(input);
                continue; 
            }
            input = getInput();
            if (input.equals("") || input.equals("-done"))
            putGame(input);
        }
    }

    public void PresentGameToUser (Game Game)  // changes io state according to input
    {
        //  Show the game to user and wait for input. Store their input in LogController
        putOutput("Your Game is: " + Game.getName() + " " + Game.getURL() + " " + "  Would you like to download it? Y/N");

        String download = inputScanner.nextLine();  // Read user input
        if (download.equals("Y") || download.equals("y")) {
            Log.EXIT(Game);
            putOutput("GAME FOUND " + Game.getName() + " URL--->" + Game.getURL());
            System.exit(0);
        }

        outputStream.println("Do you want to add this game to search? Y/N");
        
        String likes = inputScanner.nextLine();  // Read user input
        if (likes.equals("Y") || likes.equals("y")) {
            iteration = 0;  
            getUserData().likeGame(GameToPresent); 
            recache(GameToPresent); // removes games that need to be recalculated
            GameToPresent = null;
            return;
        }
    }

    private void runSearch() 
    {
        // choose next best game from source
        GameToPresent = search();

        // remove this game
        GameToPresent.RemoveGame();

        // present this game
        PresentGameToUser(GameToPresent);

        // if user has said no to too many games, 
        // clear out array and restart searching
        if (iteration > MAX_DISLIKES) {
            Log.MESSAGE("Revising search algorithm");
            UserData.removeSomeGames();
            clearCache();
            iteration = 0;
            runInTerminal();
        } else {
            iteration++;
        }
    }

    public void runInTerminal () 
    {
        while (true) {
            runSearch();
        }
    }

    public static void main (String[] args)     // tests application through terminal input
    {
        TerminalApplication app = new TerminalApplication();

        app.runInTerminal();
    }    
}