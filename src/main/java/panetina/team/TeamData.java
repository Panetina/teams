package panetina.team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamData {
    private String id;
    private String name;
    private String prefix;
    private String color;
    private SpawnLocation spawn;
    private int borderRadius;
    private boolean merged = false;
    private List<UUID> members = new ArrayList<>();

    public static class SpawnLocation {
        public double x, y, z;

        // Only keep the parameterized constructor
        public SpawnLocation(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public TeamData() {}

    public TeamData(String id, String name, String prefix, String color, SpawnLocation spawn, int borderRadius) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.color = color;
        this.spawn = spawn;
        this.borderRadius = borderRadius;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPrefix() { return prefix; }
    public String getColor() { return color; }
    public SpawnLocation getSpawn() { return spawn; }
    public int getBorderRadius() { return borderRadius; }
    public List<UUID> getMembers() { return members; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public void setColor(String color) { this.color = color; }
    public void setSpawn(SpawnLocation spawn) { this.spawn = spawn; }
    public void setBorderRadius(int borderRadius) { this.borderRadius = borderRadius; }
    public void setMembers(List<UUID> members) { this.members = members; }

    public boolean isMerged() { return merged; }
    public void setMerged(boolean merged) { this.merged = merged; }

    public void addMember(UUID uuid) {
        if (!members.contains(uuid)) members.add(uuid);
    }
    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }
}