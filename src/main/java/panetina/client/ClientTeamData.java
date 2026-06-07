package panetina.client;

import java.util.*;

public class ClientTeamData {
    private static final Map<UUID, String> playerTeams = new HashMap<>();
    private static final Map<String, String> teamNames = new HashMap<>();
    private static final Map<String, String> teamColors = new HashMap<>();

    public static void update(Map<UUID, String> assignments, Map<String, String> names, Map<String, String> colors) {
        playerTeams.clear();
        playerTeams.putAll(assignments);
        teamNames.clear();
        teamNames.putAll(names);
        teamColors.clear();
        teamColors.putAll(colors);
    }

    public static String getTeamId(UUID player) {
        return playerTeams.get(player);
    }

    public static String getTeamName(String teamId) {
        return teamNames.getOrDefault(teamId, teamId);
    }

    public static String getTeamColor(String teamId) {
        return teamColors.getOrDefault(teamId, "#FFFFFF");
    }
}