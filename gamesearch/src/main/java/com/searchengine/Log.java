package com.searchengine;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.Configurator;

public class Log 
{
    private static final Logger log = LogManager.getLogger(Log.class);

    public static void allLogs() 
    {
        Configurator.setLevel(log, Level.ALL);
    }

    static void GameAdded (Game Query, UserData File)
    {
        log.info(Query.getName() + " has been added to search with hours: "+ File.getGames().get(Query.getGameID()));
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
        log.error(errorMessage);
    }

    public static void MESSAGE(Object message)
    {
        log.info(message.toString());
    }
    
    public static void EXIT(Game game) 
    {
        if (game == null) {
            ERROR("NO GAME FOUND");
        } else {
            log.info("Game found: " + game.getName() + "   Link to download ->" + game.getURL());
        }
    }

    public static void EXIT(String message) 
    {
        ERROR("ERROR " + message);
    }
}
