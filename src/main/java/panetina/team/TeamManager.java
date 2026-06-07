package panetina.team;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import panetina.Teams;
import panetina.border.TeamBorderManager;
import panetina.mixin.EntityAccessor;
import panetina.util.TeamColorUtil;
import panetina.util.TeamLogger;

import java.util.Optional;
import java.util.UUID;

public class TeamManager {

    public static void addPlayer(ServerPlayerEntity player, String teamId) {
        if (player == null) return;
        UUID uuid = player.getUuid();
        TeamStorage storage = TeamStorage.getInstance();
        storage.removePlayerFromTeam(uuid);
        storage.addPlayerToTeam(uuid, teamId);
        TeamData team = storage.getTeamById(teamId);
        if (team != null) {
            TeamData.SpawnLocation spawn = team.getSpawn();
            player.teleport(player.getServerWorld(), spawn.x, spawn.y, spawn.z, player.getYaw(), player.getPitch());
            player.sendMessage(Text.literal("You joined ").append(Text.literal(team.getName()).formatted(Formatting.GREEN)), false);

            String displayName = player.getDisplayName() != null ? player.getDisplayName().getString() : player.getName().getString();
            Text colouredName = Text.literal(displayName)
                    .styled(style -> style.withColor(TeamColorUtil.parseColor(team.getColor())));

            // Direct data tracker update – automatically syncs to all clients
            TrackedData<Optional<Text>> key = EntityAccessor.getCustomNameTrackedData();
            player.getDataTracker().set(key, Optional.of(colouredName));
            player.setCustomNameVisible(true);
        }
        TeamBorderManager.syncBorderToPlayer(player);
        refreshTabList(player);

        MinecraftServer server = player.getServer();
        if (server != null) {
            Teams.sendTeamDataToAllOnline(server);
        }
    }

    public static void removePlayer(ServerPlayerEntity player) {
        if (player == null) return;
        UUID uuid = player.getUuid();
        TeamStorage storage = TeamStorage.getInstance();
        storage.removePlayerFromTeam(uuid);
        player.sendMessage(Text.literal("You have been removed from your team.").formatted(Formatting.RED), false);

        TrackedData<Optional<Text>> key = EntityAccessor.getCustomNameTrackedData();
        player.getDataTracker().set(key, Optional.empty());
        player.setCustomNameVisible(false);

        TeamBorderManager.syncBorderToPlayer(player);
        refreshTabList(player);

        MinecraftServer server = player.getServer();
        if (server != null) {
            Teams.sendTeamDataToAllOnline(server);
        }
    }

    public static void sendTeamMessage(ServerPlayerEntity sender, String message) {
        if (sender == null || sender.getServer() == null) return;

        TeamStorage storage = TeamStorage.getInstance();
        TeamData team = storage.getTeamOfPlayer(sender.getUuid());
        if (team == null) {
            sender.sendMessage(Text.literal("You are not in a team.").formatted(Formatting.RED), false);
            return;
        }
        Text prefix = Text.literal(team.getPrefix()).styled(style ->
                style.withColor(TeamColorUtil.parseColor(team.getColor())));
        String displayName = sender.getDisplayName() != null ? sender.getDisplayName().getString() : sender.getName().getString();
        Text fullMessage = prefix.copy().append(Text.literal(" " + displayName + ": ").formatted(Formatting.WHITE))
                .append(Text.literal(message).formatted(Formatting.GRAY));

        MinecraftServer server = sender.getServer();
        if (server != null && server.getPlayerManager() != null) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (team.equals(storage.getTeamOfPlayer(player.getUuid()))) {
                    player.sendMessage(fullMessage, false);
                }
            }
        }
        TeamLogger.logTeamChat(team.getPrefix(), displayName, message);
    }

    public static void giveToTeam(String teamId, ItemStack stack, MinecraftServer server) {
        if (server == null || server.getPlayerManager() == null) return;

        TeamStorage storage = TeamStorage.getInstance();
        TeamData team = storage.getTeamById(teamId);
        if (team == null) return;

        RegistryWrapper.WrapperLookup registryLookup = server.getRegistryManager();
        String nbtString = stack.encode(registryLookup).toString();

        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            if (team.equals(storage.getTeamOfPlayer(player.getUuid()))) {
                ItemStack copy = stack.copy();
                if (!player.getInventory().insertStack(copy)) {
                    player.dropItem(copy, false);
                }
            }
        }

        for (UUID uuid : team.getMembers()) {
            if (server.getPlayerManager().getPlayer(uuid) == null) {
                TeamMember member = storage.getOrCreateMember(uuid);
                member.addPendingReward(nbtString);
            }
        }
        storage.saveToFile();
    }

    public static void deliverPendingRewards(ServerPlayerEntity player, MinecraftServer server) {
        if (player == null || server == null || server.getPlayerManager() == null) return;

        TeamStorage storage = TeamStorage.getInstance();
        TeamMember member = storage.getOrCreateMember(player.getUuid());
        RegistryWrapper.WrapperLookup registryLookup = server.getRegistryManager();

        for (String nbtStr : member.getPendingRewards()) {
            try {
                NbtElement element = StringNbtReader.parse(nbtStr);
                if (element instanceof NbtCompound compound) {
                    ItemStack.fromNbt(registryLookup, compound).ifPresent(stack -> {
                        if (!stack.isEmpty()) {
                            if (!player.getInventory().insertStack(stack)) {
                                player.dropItem(stack, false);
                            }
                        }
                    });
                }
            } catch (Exception ignored) {}
        }
        member.clearPendingRewards();
        storage.saveToFile();
    }

    public static void refreshTabList(ServerPlayerEntity changedPlayer) {
        if (changedPlayer == null || changedPlayer.getServer() == null) return;

        MinecraftServer server = changedPlayer.getServer();
        if (server.getPlayerManager() == null) return;

        PlayerListS2CPacket packet = new PlayerListS2CPacket(
                PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME,
                changedPlayer
        );
        for (ServerPlayerEntity other : server.getPlayerManager().getPlayerList()) {
            if (other != null && other.networkHandler != null) {
                other.networkHandler.sendPacket(packet);
            }
        }
    }
}