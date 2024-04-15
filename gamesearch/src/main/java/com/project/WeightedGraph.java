package com.project;

import java.util.*;


public class WeightedGraph {
    
    List<Game> AllGames;
    
    public WeightedGraph (Database gameData) // constructs weighted graph using Database as input
    {
        this.AllGames = gameData.GameList;
        this.addWeights();
    }

    private void addWeights () 
    {
        // Could possibly find initial source during this step
        
        // THIS IS EXTREMELY SLOW AND WILL PROBABLY BE REIMPLEMENTED
        for (Game Game1 : this.AllGames) {
            for (Game Game2 : this.AllGames) {
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
        // how many times user can say no
        final int MAX_ITERATIONS = 10;

        // algorithm
        int iterations = 0;
        InputController UserInput;
        List<Game> S = new ArrayList<Game>();
        Game Source = this.ChooseSourceVertex(UserData);
        S.add(Source);

        while (true)
        {
            // Start with source and remove it
            Game GameToPresent = Source.removed ? this.ChooseNext(S, Source) : Source;
            if (GameToPresent == Source) {
                Source.RemoveGame();
            }
            // if no game found search again
            if (GameToPresent == null) {
                break;
            }

            // if user doesn't own this game return it
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

            // if user has said no to last 5 games break
            if (iterations > MAX_ITERATIONS) {
                break;
            }
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
            return this.AllGames.get(rand.nextInt(this.AllGames.size()));
        }

        // compare each game to UserTags and return one with most matches
        Game bestGame = null;
        int bestCount = 0;
        for (Game G : this.AllGames) {
            if (G.removed) { continue; } // prevents removed games from being source vertex
            if (bestGame == null && UserData.UserGames.contains(G.gameID)) {
                bestGame = G;
            } 
            int count = 0;
            for (String tag : G.tags) {
                if (UserData.UserTags.containsKey(tag)) {
                    count++;
                    if (count > bestCount) {
                        bestCount = count;
                        bestGame = G;
                        break;
                    }
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

    public Game ChooseNext (List<Game> S, Game Source) 
    {
        Game nextGame = null;
        int maxWeight = 0;
        HashMap<Integer, Edge> SourceEdges = Source.GetEdges();

        // for each Game in S
        for (Game G : S) {
            if (G.gameID == Source.gameID) { continue; }
            HashMap<Integer, Edge> GEdges = G.GetEdges();
            
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
            Source.RemoveGame();
        }

        try {
            if (nextGame == null) {throw new NullPointerException();}
        } catch (NullPointerException npe) {
            GameSearchApplication.ExitProgram("NO GAME FOUND");
        }
        return nextGame;
    }

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
