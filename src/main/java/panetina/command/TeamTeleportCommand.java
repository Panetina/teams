package panetina.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import panetina.team.TeamData;
import panetina.team.TeamStorage;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeamTeleportCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // /teamtp <team>  →  tp to team spawn
        dispatcher.register(literal("teamtp")
                .requires(source -> source.hasPermissionLevel(4))
                .then(argument("team", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            TeamStorage.getInstance().getAllTeams().forEach(t -> builder.suggest(t.getId()));
                            return builder.buildFuture();
                        })
                        // /teamtp <team>
                        .executes(ctx -> {
                            String teamId = StringArgumentType.getString(ctx, "team");
                            int count = teleportToSpawn(teamId, ctx.getSource());
                            ctx.getSource().sendFeedback(() -> Text.literal("Teleported " + count + " player(s) of team " + teamId + " to their spawn"), true);
                            return 1;
                        })
                        // /teamtp <team> spawn  (explicit)
                        .then(literal("spawn")
                                .executes(ctx -> {
                                    String teamId = StringArgumentType.getString(ctx, "team");
                                    int count = teleportToSpawn(teamId, ctx.getSource());
                                    ctx.getSource().sendFeedback(() -> Text.literal("Teleported " + count + " player(s) of team " + teamId + " to their spawn"), true);
                                    return 1;
                                })
                        )
                        // /teamtp <team> coords <x> <y> <z>
                        .then(literal("coords")
                                .then(argument("x", DoubleArgumentType.doubleArg())
                                        .then(argument("y", DoubleArgumentType.doubleArg())
                                                .then(argument("z", DoubleArgumentType.doubleArg())
                                                        .executes(ctx -> {
                                                            String teamId = StringArgumentType.getString(ctx, "team");
                                                            double x = DoubleArgumentType.getDouble(ctx, "x");
                                                            double y = DoubleArgumentType.getDouble(ctx, "y");
                                                            double z = DoubleArgumentType.getDouble(ctx, "z");
                                                            int count = teleportToCoords(teamId, x, y, z, ctx.getSource());
                                                            ctx.getSource().sendFeedback(() -> Text.literal("Teleported " + count + " player(s) of team " + teamId + " to " + x + " " + y + " " + z), true);
                                                            return 1;
                                                        })
                                                )
                                        )
                                )
                        )
                )
        );
    }

    private static int teleportToSpawn(String teamId, ServerCommandSource source) {
        TeamData team = TeamStorage.getInstance().getTeamById(teamId);
        if (team == null) return 0;
        TeamData.SpawnLocation spawn = team.getSpawn();
        int count = 0;
        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
            if (teamId.equals(TeamStorage.getInstance().getTeamIdForPlayer(player.getUuid()))) {
                player.teleport(player.getServerWorld(), spawn.x, spawn.y, spawn.z, player.getYaw(), player.getPitch());
                count++;
            }
        }
        return count;
    }

    private static int teleportToCoords(String teamId, double x, double y, double z, ServerCommandSource source) {
        int count = 0;
        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
            if (teamId.equals(TeamStorage.getInstance().getTeamIdForPlayer(player.getUuid()))) {
                player.teleport(player.getServerWorld(), x, y, z, player.getYaw(), player.getPitch());
                count++;
            }
        }
        return count;
    }
}