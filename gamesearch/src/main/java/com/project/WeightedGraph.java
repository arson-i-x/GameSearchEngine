package com.project;

import java.util.*;


public class WeightedGraph {
    
    // how many times user can say no
    final int MAX_ITERATIONS = 3;
    
    // stores all games in database
    Database Database;
    
    // constructs weighted graph using Database as input
    public WeightedGraph (Database gameData) 
    {
        this.Database = gameData;
        this.addWeights();
    }

    // adds edges to each game
    private void addWeights () 
    {
        // Could possibly find initial source during this step
        
        // THIS IS EXTREMELY SLOW AND WILL PROBABLY BE REIMPLEMENTED
        for (Game Game1 : this.Database.GameList) {
            for (Game Game2 : this.Database.GameList) {
                if (Game1.gameID == Game2.gameID) { continue; }
                int matches = 0;
                for (String tag : Game1.tags) {
                    if (Game2.tags.contains(tag)) {
                        matches++;
                    }
                }
                Game1.edges.put(Game2.gameID, new Edge(Game2, matches));
            }
        }
    }

    // search and present games. Scan from input and enter next control state according to input.
    public void Search (UserData UserData) 
    {
        // variables
        int iterations = 0;
        InputController UserInput;
        List<Game> S = new ArrayList<Game>();
        Game Source = this.ChooseSourceVertex(UserData);
        S.add(Source);
        Game GameToPresent = Source;

        // algorithm
        while (true)
        {
            // if no game found search again
            if (GameToPresent == null) {
                break;
            }

            // if user doesn't own this game Output it and get user Input
            if (!UserData.UserGames.contains(GameToPresent.gameID)) { 
                Scanner scanner = new Scanner(System.in);
                UserInput = InputController.PresentGameToUser(GameToPresent, scanner, iterations);
                
                if (UserInput.initiated_download) {
                    scanner.close();
                    GameSearchApplication.ExitProgram(GameToPresent);  
                    return;
                }

                if (UserInput.likes_game) {
                    S.add(GameToPresent);
                } else {
                    iterations++;
                }
            }
            
            // remove this game
            GameToPresent.RemoveGame();
    
            // if user has said no to last x games, break
            if (iterations > MAX_ITERATIONS) {
                break;
            }

            // choose next best game from source
            GameToPresent =  this.ChooseNext(S, Source);
        }

        // find new source vertex
        System.out.println("/**** REVISING SEARCH ****/");
        Search(UserData);
    }

    // choose game in graph with most user tag matches, or a random game if library is empty
    public Game ChooseSourceVertex (UserData UserData)
    {
        // if library or usertags is empty get a random game
        if (UserData.UserGames.isEmpty() || UserData.UserTags.isEmpty()) {
            Random rand = new Random();
            return this.Database.GameList.get(rand.nextInt(this.Database.GameList.size()));
        }

        // compare each game to UserTags and return one with most matches
        Game bestGame = null;
        int bestCount = 0;
        for (Game G : this.Database.GameList) {
            if (G.removed) { continue; } // prevents removed games from being source vertex
            int count = 0;
            for (String tag : G.tags) {
                if (UserData.UserTags.containsKey(tag)) {
                    count++;
                }
                if (count > bestCount) {
                    bestCount = count;
                    bestGame = G;
                    break;
                }
            }
        }

        // if nothing found at this point 
        // there is a big problem. The user
        // has likely searched every game in the 
        //data base so throw an error and exit program
        try {
            if (bestGame == null) {
                throw new NullPointerException();
            }
        } catch (Exception e) {
            System.out.println("No more games");
            System.exit(1);
        }
        
        return bestGame;
    }

    // chooses next game with most amount of tag matches
    public Game ChooseNext (List<Game> S, Game Source) 
    {
        Game nextGame = null;
        int maxWeight = 0;
        HashMap<Integer, Edge> SourceEdges = Source.edges;

        // for each Game in S
        for (Game G : S) {
            if (G.gameID == Source.gameID) { continue; }
            HashMap<Integer, Edge> GEdges = G.edges;
            
            // Check all its edges and update maxWeight 
            // with combined weight of this and source
            for (int GameID : GEdges.keySet()) {
                Edge currEdge = GEdges.get(GameID);
                int sourceWeight = 0;
                if (SourceEdges.containsKey(GameID)) {
                    sourceWeight = SourceEdges.get(GameID).Weight;
                }
                int thisWeight = sourceWeight + currEdge.Weight;
                if (thisWeight > maxWeight) {
                    maxWeight = thisWeight;
                    nextGame = currEdge.Game;
                }
            }
        }

        //System.out.println("c");
        // check source edges for higher max
        for (int GameID : SourceEdges.keySet()) {
            Edge currEdge = SourceEdges.get(GameID);
            int thisWeight = currEdge.Weight;
            if (thisWeight > maxWeight) {
                maxWeight = thisWeight;
                nextGame = currEdge.Game;
            }
        }
        if (nextGame == null) {
            nextGame = Source.removed ? Source : null;
        }
        return nextGame;
    }

    // tests unit
    public static void main (String[] args) 
    {
        Database database = new Database();
        UserData userData = new UserData("gamesearch/src/main/resources/UserDataExample.csv");
        WeightedGraph NewGraph = new WeightedGraph(database);
        
        System.out.println("User library\n");
        for (Integer game : userData.UserGames) {
            
            System.out.println(game);
        }
        NewGraph.Search(userData);
    }
}
