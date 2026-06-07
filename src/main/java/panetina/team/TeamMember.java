package panetina.team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamMember {
    private UUID uuid;
    private List<String> pendingRewards = new ArrayList<>(); // SNBT strings

    public TeamMember() {}
    public TeamMember(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() { return uuid; }
    public List<String> getPendingRewards() { return pendingRewards; }
    public void addPendingReward(String itemNbt) { pendingRewards.add(itemNbt); }
    public void clearPendingRewards() { pendingRewards.clear(); }
}