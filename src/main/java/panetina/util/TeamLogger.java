package panetina.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TeamLogger {
    private static final Logger LOGGER = LogManager.getLogger("TeamsMod");

    public static void logTeamChat(String prefix, String nickname, String message) {
        LOGGER.info("[TEAM CHAT][{}][{}]: {}", prefix, nickname, message);
    }

    public static void logAdmin(String msg) {
        LOGGER.info("[TEAM ADMIN] {}", msg);
    }

    public static void logGeneral(String msg) {
        LOGGER.info("[TEAM] {}", msg);
    }
}