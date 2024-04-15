package com.project;

public class GameSearchApplication {
    public static void ExecuteProgram () 
    {
        // make a database
        Database database = new Database();

        // make fake user data
        System.out.println("Enter names of Game titles to create library. When finished, simply enter DONE");
        UserData userData = new UserData(UserData.CreateUserData(database));

        // make a graph with database
        WeightedGraph Graph = new WeightedGraph(database);

        // continue searching graph 
        // until exit condition is met
        Graph.Search(userData);
    }
    
    // Exit with a game to download
    public static void ExitProgram (Game GameToDownload) {
        System.out.println("Link to download -> " + GameToDownload.URL);
        System.exit(0);
    }

    // Exit with an Error Message
    public static void ExitProgram (String ExitMessage) {
        System.out.println(ExitMessage);
        System.exit(0);
    }

    public static void main (String[] args) 
    {
        ExecuteProgram();
    }    
}
