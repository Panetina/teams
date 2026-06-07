package panetina;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import panetina.client.ClientTeamData;
import panetina.command.*;
import panetina.listener.PlayerJoinListener;
import panetina.listener.PlayerRespawnListener;
import panetina.listener.ServerStartedListener;
import panetina.network.TeamDataPayload;
import panetina.team.TeamData;
import panetina.team.TeamStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Teams implements ModInitializer, ClientModInitializer {
	public static final String MOD_ID = "teams";

	@Override
	public void onInitialize() {
		PayloadTypeRegistry.playS2C().register(TeamDataPayload.ID, TeamDataPayload.CODEC);

		ServerLifecycleEvents.SERVER_STARTED.register(ServerStartedListener::onServerStarted);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			TeamAddCommand.register(dispatcher);
			TeamRemoveCommand.register(dispatcher);
			TeamChatCommand.register(dispatcher);
			TeamBorderCommand.register(dispatcher);
			TeamGiveCommand.register(dispatcher, registryAccess);
			TeamTeleportCommand.register(dispatcher);
			TeamMailCommand.register(dispatcher);
		});

		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			PlayerJoinListener.onPlayerJoin(handler.player, server);
			sendTeamDataToPlayer(handler.player);
		});

		PlayerRespawnListener.register();
	}

	@Override
	@SuppressWarnings("resource")
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(TeamDataPayload.ID, (payload, context) ->
				context.client().execute(() -> ClientTeamData.update(payload.teamAssignments(), payload.teamNames(), payload.teamColors()))
		);
	}

	public static void sendTeamDataToPlayer(ServerPlayerEntity player) {
		Map<UUID, String> members = buildTeamMemberMap();
		Map<String, String> names = buildTeamNameMap();
		Map<String, String> colors = buildTeamColorMap();
		ServerPlayNetworking.send(player, new TeamDataPayload(members, names, colors));
	}

	public static void sendTeamDataToAllOnline(MinecraftServer server) {
		Map<UUID, String> members = buildTeamMemberMap();
		Map<String, String> names = buildTeamNameMap();
		Map<String, String> colors = buildTeamColorMap();
		TeamDataPayload payload = new TeamDataPayload(members, names, colors);
		for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
			ServerPlayNetworking.send(player, payload);
		}
	}

	private static Map<UUID, String> buildTeamMemberMap() {
		Map<UUID, String> map = new HashMap<>();
		for (TeamData team : TeamStorage.getInstance().getAllTeams()) {
			for (UUID uuid : team.getMembers()) {
				map.put(uuid, team.getId());
			}
		}
		return map;
	}

	private static Map<String, String> buildTeamNameMap() {
		Map<String, String> names = new HashMap<>();
		for (TeamData team : TeamStorage.getInstance().getAllTeams()) {
			names.put(team.getId(), team.getName());
		}
		return names;
	}

	private static Map<String, String> buildTeamColorMap() {
		Map<String, String> colors = new HashMap<>();
		for (TeamData team : TeamStorage.getInstance().getAllTeams()) {
			colors.put(team.getId(), team.getColor());
		}
		return colors;
	}
}