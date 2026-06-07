package panetina.listener;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import panetina.border.TeamBorderManager;
import panetina.team.TeamManager;

public class PlayerJoinListener {
    public static void onPlayerJoin(ServerPlayerEntity player, MinecraftServer server) {
        TeamManager.deliverPendingRewards(player, server);
        // Send the border packet immediately; global border is already huge
        TeamBorderManager.syncBorderToPlayer(player);
    }
}