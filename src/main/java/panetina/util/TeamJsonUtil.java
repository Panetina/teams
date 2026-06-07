package panetina.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import panetina.team.TeamData;
import panetina.team.TeamStorage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamJsonUtil {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_PATH = "config/teams/teams.json";

    public static TeamStorage.TeamsConfig loadTeams() {
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_PATH);
        if (!file.exists()) {
            TeamStorage.TeamsConfig defaultConfig = getDefaultConfig();
            saveTeams(defaultConfig);
            return defaultConfig;
        }
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, TeamStorage.TeamsConfig.class);
        } catch (IOException e) {
            e.printStackTrace();
            return getDefaultConfig();
        }
    }

    public static void saveTeams(TeamStorage.TeamsConfig config) {
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_PATH);
        file.getParentFile().mkdirs();
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static TeamStorage.TeamsConfig getDefaultConfig() {
        TeamStorage.TeamsConfig config = new TeamStorage.TeamsConfig();
        List<TeamData> teams = new ArrayList<>();

        // Team 1 - using named colors instead of hex
        TeamData team1 = new TeamData();
        team1.setId("team1");
        team1.setName("Kingdom of Oak");
        team1.setPrefix("[OAK]");
        team1.setColor("green"); // Changed from #55AA55
        team1.setSpawn(new TeamData.SpawnLocation(0, 64, 0));
        team1.setBorderRadius(64);
        team1.setMembers(new ArrayList<>());

        // Team 2
        TeamData team2 = new TeamData();
        team2.setId("team2");
        team2.setName("Iron Empire");
        team2.setPrefix("[IRON]");
        team2.setColor("gray"); // Changed from #AAAAAA
        team2.setSpawn(new TeamData.SpawnLocation(1000, 64, 0));
        team2.setBorderRadius(64);
        team2.setMembers(new ArrayList<>());

        // Team 3
        TeamData team3 = new TeamData();
        team3.setId("team3");
        team3.setName("Blue Dominion");
        team3.setPrefix("[BLUE]");
        team3.setColor("blue"); // Changed from #5555FF
        team3.setSpawn(new TeamData.SpawnLocation(0, 64, 1000));
        team3.setBorderRadius(64);
        team3.setMembers(new ArrayList<>());

        teams.add(team1);
        teams.add(team2);
        teams.add(team3);
        config.teams = teams;
        return config;
    }
}