package com.project;
import java.util.*;

public class WeightedGraph {
    
    // how many times user can say no
    final int MAX_ITERATIONS = 5;

    // how many games to graph
    final int MAXIMUM_GAMELIST_SIZE = 100;
    
    // stores all games in database
    Database Database;
    
    // constructs weighted graph using Database as input
    public WeightedGraph (Database gameData) 
    {
        this.Database = gameData;
    }

    // adds edges to a game object
    private void addWeights (Game Game1) 
    {
        if (Game1.removed || Game1.weighted) {
            return;
        }
        
        for (Game Game2 : this.Database.GameTable.values()) {
            //System.out.println(Game2.name);
            if (Game1.gameID == Game2.gameID) { continue; }
            int matches = 0;
            for (String tag : Game2.tags) {
                if (Game1.tags.contains(tag)) {
                    matches++;
                }
            }
            Game1.edges.put(Game2.gameID, new Edge(Game2, matches));
        }
        Game1.weighted = true;
    }

    // search and present games. Scan from input and enter next control state according to input.
    public void Search (UserData UserData) 
    {
        // variables
        int iterations = 0;
        IOController UserInput;
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
                UserInput = IOController.PresentGameToUser(GameToPresent, iterations);
                
                if (UserInput.initiated_download) {
                    GameSearchApplication.ExitProgram(GameToPresent);  
                    return;
                }

                if (UserInput.likes_game) {
                    S.add(GameToPresent);
                    iterations = 0;
                } else {
                    iterations++;

                    // remove too similar games
                    /*for (Game G : this.Database.GameTable.values()) {
                        if (FuzzySearch.ratio(G.name, GameToPresent.name) > 90) {
                            G.RemoveGame();
                        }
                    }*/
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
    private Game ChooseSourceVertex (UserData UserData)
    {
        Game bestGame = null;
        // if library or usertags is empty get a random game
        if (UserData.UserGames == null || UserData.UserGames.isEmpty()) {
            bestGame = this.Database.GetRandomGame();
            this.addWeights(bestGame);
            return bestGame;
        }

        // compare each game to UserTags and return one with most matches
        int bestCount = 0;
        for (Game G : this.Database.GameTable.values()) {
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
        this.addWeights(bestGame);
        return bestGame;
    }

    // chooses next game with most amount of tag matches
    private Game ChooseNext (List<Game> S, Game Source) 
    {
        Game nextGame = null;
        int maxWeight = 0;

        // for each Game in S
        for (Game G : S) {
            if (G.gameID == Source.gameID) { continue; }
            this.addWeights(G);
            HashMap<Integer, Edge> GEdges = G.edges;
            
            // Check all its edges and update maxWeight 
            // with combined weight of this and source
            for (int GameID : GEdges.keySet()) {
                Edge currEdge = GEdges.get(GameID);
                int sourceWeight = 0;
                if (Source.edges.containsKey(GameID)) {
                    sourceWeight = Source.edges.get(GameID).Weight;
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
        for (int GameID : Source.edges.keySet()) {
            Edge currEdge = Source.edges.get(GameID);
            int thisWeight = currEdge.Weight;
            if (thisWeight > maxWeight) {
                maxWeight = thisWeight;
                nextGame = currEdge.Game;
            }
        }
        if (nextGame == null) {
            nextGame = Source.removed ? Source : null;
            Source.RemoveGame();
        }
        this.addWeights(nextGame);
        return nextGame;
    }

    // tests unit
    public static void main (String[] args) 
    {
        Database database = new Database();
        WeightedGraph NewGraph = new WeightedGraph(database);
        
        System.out.println("User library\n");
        for (Game game : NewGraph.Database.GameTable.values()) {
            
            System.out.println(game.name);
        }
    }
}
