package panetina.border;

import net.minecraft.network.packet.s2c.play.WorldBorderInitializeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.border.WorldBorder;
import panetina.team.TeamData;
import panetina.team.TeamStorage;

public class TeamBorderManager {

    /**
     * Sends a per-player world border packet.
     * If the player's team is merged, sends the current global border instead.
     * Otherwise, sends the team's specific border centered at spawn.
     */
    public static void syncBorderToPlayer(ServerPlayerEntity player) {
        TeamData team = TeamStorage.getInstance().getTeamOfPlayer(player.getUuid());

        // If the player is not in a team, or their team is merged → use global border
        if (team == null || team.isMerged()) {
            WorldBorder globalBorder = player.getServerWorld().getWorldBorder();
            player.networkHandler.sendPacket(new WorldBorderInitializeS2CPacket(globalBorder));
            return;
        }

        // Team-specific border centered at spawn
        double centerX = team.getSpawn().x;
        double centerZ = team.getSpawn().z;
        double radius = team.getBorderRadius();

        WorldBorder border = new WorldBorder();
        border.setCenter(centerX, centerZ);
        border.setSize(radius * 2);
        border.setWarningBlocks(5);
        border.setWarningTime(15);

        player.networkHandler.sendPacket(new WorldBorderInitializeS2CPacket(border));
    }
}