package com.project;

import java.util.Scanner;

public class InputController {
    boolean initiated_download;
    boolean likes_game;
    int iterations;

    // private constructor method
    private InputController (int iterations, boolean likes_game, boolean initiated_download) 
    {
        this.initiated_download = initiated_download;
        this.likes_game = likes_game;
        this.iterations = iterations;
    }

    public static void GameNotFound () 
    {
        System.out.println("Game not found");
    }

    public static Game UserQuery (Database database, Scanner scanner) 
    {

        // STD IN
        System.out.println("Enter ID of game or -1 if finished");
        String GameID = scanner.nextLine();  // Read user input


        Game Game = database.Query(Integer.parseInt(GameID));
        return Game;
    }

    // returns an input controller with associated user input
    public static InputController PresentGameToUser (Game Game, Scanner scanner, int iterations) 
    {
        //
        // Show the game to user and wait for input. Store their input
        //
        System.out.println("Your Game is: " + Game.name + "     Would you like to download it? Y/N");
        String answer1 = scanner.nextLine();  // Read user input
        boolean initiated_download = answer1.equals("Y") || answer1.equals("y") ? true : false;
        if (!initiated_download) {
            System.out.println("Do you want to add this game to search? Y/N");
            String answer2 = scanner.nextLine();  // Read user input
            boolean likes_game = answer2.equals("Y") || answer2.equals("y") ? true : false;
            return new InputController(iterations, likes_game, initiated_download);
        } else {
            return new InputController(0, false, true);
        }
    }
}
