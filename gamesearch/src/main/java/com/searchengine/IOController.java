package com.searchengine;

import java.util.InputMismatchException;
import java.util.Scanner;

public class IOController {

    // READS FROM SYSTEM.IN AND RETURNS AN INT ASSOCIATED TO EXIT CONDITIONS
    private static final Scanner inputScanner = new Scanner(System.in);
    
    // Stores state as an int
    static final int EXIT = 2;
    static final int LIKE = 1;
    static final int SEARCH = 0;

    // stores the first thing entered into SYSTEM.IN
    static Object cachedObject;
    static int state;


    public static Object LoginQuery () // reads login object from input either a long steamid or a game name
    {
        System.out.println("Enter your SteamID to insert your entire library OR enter names of games to create a library");
        String input = inputScanner.nextLine();

        try {
            Long id = Long.parseLong(input);
            return id;
        } catch (NumberFormatException nfe) {
            //ERROR(nfe.getMessage());
            return input;
        }     
    }

    static void GameAdded (Game Query) // prints name of game added to library
    {
        System.out.println(Query.getName() + " added to library!");
    }

    static String UserQuery () // Queries data base for user input
    {
        // STD IO REPLACED
        System.out.println("Enter Name of game or -1 if finished");
        String input = inputScanner.nextLine();
        return input;  // Read user input
    }

    static Object getCache ()  // gets cached object that user input 
    {
        return cachedObject == null ? null : cachedObject;
    }

    static void cache (Object obj)  // caches object in IO 
    {
        cachedObject = obj;
    }

    static void PresentGameToUser (Game Game)  // changes io state according to input 
    {
        //  Show the game to user and wait for input. Store their input in IOController
        System.out.println("Your Game is: " + Game.getName() + " " + Game.getURL() + " " + "  Would you like to download it? Y/N");
        
        String download = inputScanner.nextLine();  // Read user input
        if (download.equals("Y") || download.equals("y")) {
            state = EXIT;
            return;
        }

        System.out.println("Do you want to add this game to search? Y/N");
        
        String likes = inputScanner.nextLine();  // Read user input
        if (likes.equals("Y") || likes.equals("y")) {
            state = LIKE;
            return;
        }

        state = SEARCH;
    }

    public static void ERROR(String errorMessage) 
    {
        //inputScanner.nextLine();
        System.out.println("ERROR: " + errorMessage);
    }

    public static void EXIT(Game game) 
    {
        closeInputReader();
        if (game == null) {
            ERROR("NO GAME FOUND");
        } else {
            System.out.println("Game found: " + game.getName() + "   Link to download ->" + game.getURL());
        }
    }

    public static void EXIT(String message) 
    {
        closeInputReader();
        System.out.println(message);
    }

    // closes scanner to prevent leaks
    public static void closeInputReader () 
    {
        inputScanner.close();
    }
}
