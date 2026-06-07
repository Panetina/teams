package panetina.team;

import panetina.util.TeamJsonUtil;

import java.util.*;

public class TeamStorage {
    private static TeamStorage instance;
    private Map<String, TeamData> teams = new LinkedHashMap<>();
    private Map<UUID, String> playerTeamMap = new HashMap<>();
    private List<TeamMember> memberData = new ArrayList<>();

    public static TeamStorage getInstance() {
        if (instance == null) instance = new TeamStorage();
        return instance;
    }

    public void loadFromFile() {
        TeamsConfig config = TeamJsonUtil.loadTeams();
        teams.clear();
        playerTeamMap.clear();
        memberData.clear();
        if (config != null && config.teams != null) {
            for (TeamData team : config.teams) {
                teams.put(team.getId(), team);
                for (UUID uuid : team.getMembers()) {
                    playerTeamMap.put(uuid, team.getId());
                }
            }
        }
    }

    public void saveToFile() {
        TeamsConfig config = new TeamsConfig();
        config.teams = new ArrayList<>(teams.values());
        TeamJsonUtil.saveTeams(config);
    }

    public TeamData getTeamById(String id) {
        return teams.get(id);
    }

    public String getTeamIdForPlayer(UUID uuid) {
        return playerTeamMap.get(uuid);
    }

    public TeamData getTeamOfPlayer(UUID uuid) {
        String id = getTeamIdForPlayer(uuid);
        return id != null ? teams.get(id) : null;
    }

    public void addPlayerToTeam(UUID uuid, String teamId) {
        TeamData team = teams.get(teamId);
        if (team != null) {
            team.addMember(uuid);
            playerTeamMap.put(uuid, teamId);
            getOrCreateMember(uuid);
            saveToFile();
        }
    }

    public void removePlayerFromTeam(UUID uuid) {
        String teamId = playerTeamMap.remove(uuid);
        if (teamId != null) {
            TeamData team = teams.get(teamId);
            if (team != null) {
                team.removeMember(uuid);
            }
            saveToFile();
        }
    }

    public TeamMember getOrCreateMember(UUID uuid) {
        for (TeamMember m : memberData) {
            if (m.getUuid().equals(uuid)) return m;
        }
        TeamMember newMember = new TeamMember(uuid);
        memberData.add(newMember);
        return newMember;
    }

    public Collection<TeamData> getAllTeams() {
        return teams.values();
    }

    public static class TeamsConfig {
        public List<TeamData> teams;
    }
}