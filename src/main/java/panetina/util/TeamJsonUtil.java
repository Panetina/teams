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

        teams.add(createTeam1());
        teams.add(createTeam2());
        teams.add(createTeam3());

        config.teams = teams;
        return config;
    }

    private static TeamData createTeam1() {
        TeamData team = new TeamData();
        team.setId("team1");
        team.setName("Kingdom of Oak");
        team.setPrefix("[OAK]");
        team.setColor("green");
        team.setSpawn(new TeamData.SpawnLocation(0, 64, 0));
        team.setBorderRadius(64);
        team.setMembers(new ArrayList<>());
        team.setCommands(new ArrayList<>());
        return team;
    }

    private static TeamData createTeam2() {
        TeamData team = new TeamData();
        team.setId("team2");
        team.setName("Iron Empire");
        team.setPrefix("[IRON]");
        team.setColor("gray");
        team.setSpawn(new TeamData.SpawnLocation(1000, 64, 0));
        team.setBorderRadius(64);
        team.setMembers(new ArrayList<>());
        team.setCommands(new ArrayList<>());
        return team;
    }

    private static TeamData createTeam3() {
        TeamData team = new TeamData();
        team.setId("team3");
        team.setName("Blue Dominion");
        team.setPrefix("[BLUE]");
        team.setColor("blue");
        team.setSpawn(new TeamData.SpawnLocation(0, 64, 1000));
        team.setBorderRadius(64);
        team.setMembers(new ArrayList<>());

        List<String> commands = new ArrayList<>();
        commands.add("effect give @s minecraft:water_breathing 60 1");
        commands.add("give @s minecraft:blue_wool 1");
        commands.add("playsound minecraft:entity.player.levelup ambient @s ~ ~ ~");
        commands.add("team join blue {player}");
        team.setCommands(commands);

        return team;
    }
}