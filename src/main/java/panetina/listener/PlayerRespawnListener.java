package panetina.listener;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import panetina.border.TeamBorderManager;
import panetina.team.TeamData;
import panetina.team.TeamStorage;

import java.util.Optional;

public class PlayerRespawnListener {
    public static void register() {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            ServerWorld world = newPlayer.getServerWorld();
            Optional<Vec3d> spawnPos = getBedSpawnPosition(newPlayer, world);
            if (spawnPos.isEmpty()) {
                TeamData team = TeamStorage.getInstance().getTeamOfPlayer(newPlayer.getUuid());
                if (team != null) {
                    TeamData.SpawnLocation spawn = team.getSpawn();
                    newPlayer.teleport(world, spawn.x, spawn.y, spawn.z, newPlayer.getYaw(), newPlayer.getPitch());
                }
            }
            // Re-send the border packet on respawn (new networkHandler after death)
            TeamBorderManager.syncBorderToPlayer(newPlayer);
        });
    }

    private static Optional<Vec3d> getBedSpawnPosition(ServerPlayerEntity player, ServerWorld world) {
        BlockPos bedPos = player.getSpawnPointPosition();
        if (bedPos != null) {
            BlockState state = world.getBlockState(bedPos);
            if (state.getBlock() instanceof BedBlock) {
                return Optional.of(Vec3d.ofCenter(bedPos).add(0, 0.2, 0));
            }
        }
        return Optional.empty();
    }
}