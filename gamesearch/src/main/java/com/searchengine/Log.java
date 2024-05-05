package com.searchengine;
import java.io.PrintStream;
import java.util.Scanner;

public class Log 
{
    private static final Scanner inputScanner = new Scanner(System.in);
    private static final PrintStream outputStream = new PrintStream(System.out);

    static void GameAdded (Game Query, UserData File)
    {
        outputStream.println(Query.getName() + " has been added to search with hours: "+ File.getGames().get(Query.getGameID()));
    }

    static void userdata (UserData data)
    {
        if (data.isEmpty()) {
            MESSAGE("USERDATA FILE IS BLANK");
            return;
        }
        data.getGames().keySet().forEach(id -> {
            GameAdded(Database.getGame(id.toString()), data);
        });
        MESSAGE(data.getTotalHoursPlayed() + " hours played");
    }

    public static void ERROR(Object errorMessage) 
    {
        outputStream.println("ERROR: " + errorMessage);
    }

    public static void MESSAGE(Object message)
    {
        outputStream.println(message.toString());
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
