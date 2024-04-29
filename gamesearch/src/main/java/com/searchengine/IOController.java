package com.searchengine;
import java.io.PrintStream;
import java.util.Scanner;

public class IOController {

    // READS FROM SYSTEM.IN AND RETURNS AN INT ASSOCIATED TO EXIT CONDITIONS
    // GUI WILL MOST LIKELY BE IMPLEMENTED INTO SYSTEM.IN and OUT
    private static final Scanner inputScanner = new Scanner(System.in);
    private static final PrintStream outputStream = new PrintStream(System.out);
    
    // Stores state as an int
    static final int EXIT = 2;
    static final int LIKE = 1;
    static final int SEARCH = 0;
    private static int state;

    public static String getTerminalInput() 
    {
        return inputScanner.nextLine();
    }

    public static void putTerminalOutput(String output) 
    {
        outputStream.println(output);
    }

    public static Object LoginQuery (String userInput) // reads login object from input either a long steamid or a game name
    {
        //outputStream.println("Enter your SteamID to insert your entire library OR enter names of games to create a library");
        //String input = inputScanner.nextLine();
        String input = userInput;
        try {
            Long id = Long.parseLong(input);
            return id;
        } catch (NumberFormatException nfe){
            return input;
        }
    }

    static int getState() 
    {
        return state;
    }
    
    static void resetState() 
    {
        state = SEARCH;
    }

    static void GameAdded (Game Query) // prints name of game added to library
    {
        outputStream.println(Query.getName() + " added to library!");
    }

    public static String UserQuery () // Queries data base for user input
    {
        // STD IO REPLACED
        outputStream.println("Enter Name of game or -done if finished");
        String input = inputScanner.nextLine();
        return input;  // Read user input
    }

    public static void PresentGameToUser (Game Game)  // changes io state according to input
    {
        //  Show the game to user and wait for input. Store their input in IOController
        outputStream.println("Your Game is: " + Game.getName() + " " + Game.getURL() + " " + "  Would you like to download it? Y/N");

        String download = inputScanner.nextLine();  // Read user input
        if (download.equals("Y") || download.equals("y")) {
            state = EXIT;
            return;
        }

        outputStream.println("Do you want to add this game to search? Y/N");
        
        String likes = inputScanner.nextLine();  // Read user input
        if (likes.equals("Y") || likes.equals("y")) {
            state = LIKE;
            return;
        }

        state = SEARCH;
    }

    public static void ERROR(Object errorMessage) 
    {
        //inputScanner.nextLine();
        outputStream.println("ERROR: " + errorMessage);
    }

    public static void MESSAGE(Object message)
    {
        outputStream.println(message);
    }
    
    public static void EXIT(Game game) 
    {
        closeInputReader();
        if (game == null) {
            ERROR("NO GAME FOUND");
        } else {
            outputStream.println("Game found: " + game.getName() + "   Link to download ->" + game.getURL());
        }
    }

    public static void EXIT(String message) 
    {
        closeInputReader();
        outputStream.println("ERROR " + message);
    }

    // closes scanner to prevent leaks
    public static void closeInputReader () 
    {
        inputScanner.close();
    }
}
