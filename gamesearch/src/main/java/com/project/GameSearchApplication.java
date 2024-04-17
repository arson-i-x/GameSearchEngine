package com.project;

import java.util.Scanner;

public class GameSearchApplication {

    public static Scanner InputScanner;

    // Main program execution. Reads from System.in
    public static void ExecuteProgram () 
    {
        // Where is input read from?
        InputScanner = new Scanner(System.in);

        // make a database with default directory
        Database database = new Database();

        // Query Login or create fake data
        UserData userData;
        if (IOController.LoginQuery()) {
            userData = new UserData();
        } else {
            userData = new UserData(UserData.CreateUserData(database));
        }
        
        // make a graph with database
        WeightedGraph Graph = new WeightedGraph(database);

        // begin searching graph 
        // until exit condition is met
        Graph.Search(userData);
    }
    
    // Exit with a game to download
    public static void ExitProgram (Game GameToDownload) {
        System.out.println("Link to download -> " + GameToDownload.URL);
        InputScanner.close();
        System.exit(0);
    }

    // Exit with an Error Message
    public static void ExitProgram (String ExitMessage) {
        System.out.println(ExitMessage);
        InputScanner.close();
        System.exit(1);
    }

    // Runs main application
    public static void main (String[] args) 
    {
        ExecuteProgram();
    }    
}
