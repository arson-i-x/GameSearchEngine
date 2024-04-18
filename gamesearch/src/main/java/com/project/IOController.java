package com.project;

import com.lukaspradel.steamapi.core.exception.SteamApiException;

public class IOController {
    boolean initiated_download;
    boolean likes_game;
    int iterations;
    static String userID;

    // private constructor method
    private IOController (int iterations, boolean likes_game, boolean initiated_download) 
    {
        this.initiated_download = initiated_download;
        this.likes_game = likes_game;
        this.iterations = iterations;
    }

    public static void GameNotFound () 
    {
        System.out.println("Game not found");
    }

    public static boolean LoginQuery () throws SteamApiException {
        System.out.println("Would you like to login? Y/N");
        String answer = GameSearchApplication.InputScanner.nextLine();
        if (answer.equals("Y") ||
            answer.equals("y")) {
            System.out.println("Enter your SteamID: ");
            userID = GameSearchApplication.InputScanner.nextLine();
            UserLogin.Login(userID);
            return true;
            } else {
                System.out.println("Creating user library data....\n....");
                return false;
            }

    }

    public static void GameAdded (Game Query) 
    {
        System.out.println(Query.name + " added to library!");
    }

    // Queries data base for user input
    public static Game UserQuery (Database database) throws SteamApiException {
        if (LoginQuery()){
            Game Game = database.Query(UserLogin.Login(userID));
            return Game;
        }
        // STD IO REPLACED
        System.out.println("Enter Name of game or -1 if finished");
        String GameName = GameSearchApplication.InputScanner.nextLine();  // Read user input
        Game Game = database.Query(GameName);
        return Game;
    }

    // returns an input controller with associated user input
    public static IOController PresentGameToUser (Game Game, int iterations) 
    {
        //
        // Show the game to user and wait for input. Store their input
        //
        System.out.println("Your Game is: " + Game.name + "     Would you like to download it? Y/N");
        String answer1 = GameSearchApplication.InputScanner.nextLine();  // Read user input
        boolean initiated_download = answer1.equals("Y") || answer1.equals("y") ? true : false;
        if (!initiated_download) {
            System.out.println("Do you want to add this game to search? Y/N");
            String answer2 = GameSearchApplication.InputScanner.nextLine();  // Read user input
            boolean likes_game = answer2.equals("Y") || answer2.equals("y") ? true : false;
            return new IOController(iterations, likes_game, initiated_download);
        } else {
            return new IOController(0, false, true);
        }
    }
}
