package panetina.listener;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Formatting;
import panetina.team.TeamStorage;

public class ServerStartedListener {
    public static void onServerStarted(MinecraftServer server) {
        TeamStorage.getInstance().loadFromFile();

        // Create vanilla teams for all named colors if they don't exist
        createVanillaTeams(server);

        server.getOverworld().getWorldBorder().setSize(200.0);
    }

    private static void createVanillaTeams(MinecraftServer server) {
        Scoreboard scoreboard = server.getScoreboard();

        // All Minecraft named colors that work with scoreboard teams
        String[] colorNames = {
                "black", "dark_blue", "dark_green", "dark_aqua",
                "dark_red", "dark_purple", "gold", "gray",
                "dark_gray", "blue", "green", "aqua",
                "red", "light_purple", "yellow", "white"
        };

        for (String colorName : colorNames) {
            if (scoreboard.getTeam(colorName) == null) {
                Team team = scoreboard.addTeam(colorName);
                Formatting formatting = Formatting.byName(colorName);
                if (formatting != null) {
                    team.setColor(formatting);
                }
                team.setNameTagVisibilityRule(Team.VisibilityRule.ALWAYS);
                team.setDeathMessageVisibilityRule(Team.VisibilityRule.NEVER);
                team.setCollisionRule(Team.CollisionRule.ALWAYS);
                team.setFriendlyFireAllowed(false);
                team.setShowFriendlyInvisibles(false);
            }
        }
    }
}