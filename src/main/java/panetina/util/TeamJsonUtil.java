package panetina.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import panetina.team.TeamData;
import panetina.team.TeamStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class TeamJsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeamJsonUtil.class);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG_PATH = "config/teams/teams.json";

    public static TeamStorage.TeamsConfig loadTeams() {
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_PATH);
        if (!file.exists()) {
            LOGGER.info("No teams config found, creating default config");
            TeamStorage.TeamsConfig defaultConfig = getDefaultConfig();
            saveTeams(defaultConfig);
            return defaultConfig;
        }
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, TeamStorage.TeamsConfig.class);
        } catch (IOException e) {
            LOGGER.error("Failed to load teams config from {}", CONFIG_PATH, e);
            return getDefaultConfig();
        }
    }

    public static void saveTeams(TeamStorage.TeamsConfig config) {
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_PATH);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            if (!created) {
                LOGGER.warn("Failed to create directory: {}", parentDir.getAbsolutePath());
            }
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            GSON.toJson(config, writer);
            LOGGER.info("Successfully saved teams config to {}", CONFIG_PATH);
        } catch (IOException e) {
            LOGGER.error("Failed to save teams config to {}", CONFIG_PATH, e);
        }
    }

    private static TeamStorage.TeamsConfig getDefaultConfig() {
        TeamStorage.TeamsConfig config = new TeamStorage.TeamsConfig();
        List<TeamData> teams = new ArrayList<>();

        TeamData team1 = new TeamData();
        team1.setId("team1");
        team1.setName("Kingdom of Oak");
        team1.setPrefix("[OAK]");
        team1.setColor("#55AA55");
        team1.setSpawn(new TeamData.SpawnLocation(0, 64, 0));
        team1.setBorderRadius(64);
        team1.setMembers(new ArrayList<>());

        TeamData team2 = new TeamData();
        team2.setId("team2");
        team2.setName("Iron Empire");
        team2.setPrefix("[IRON]");
        team2.setColor("#AAAAAA");
        team2.setSpawn(new TeamData.SpawnLocation(1000, 64, 0));
        team2.setBorderRadius(64);
        team2.setMembers(new ArrayList<>());

        TeamData team3 = new TeamData();
        team3.setId("team3");
        team3.setName("Blue Dominion");
        team3.setPrefix("[BLUE]");
        team3.setColor("#5555FF");
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