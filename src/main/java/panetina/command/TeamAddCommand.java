package panetina.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import panetina.team.TeamData;
import panetina.team.TeamManager;
import panetina.team.TeamStorage;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeamAddCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("teamadd")
                .requires(source -> source.hasPermissionLevel(4))
                .then(argument("player", EntityArgumentType.player())
                        .then(argument("team", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    TeamStorage.getInstance().getAllTeams().forEach(team -> builder.suggest(team.getId()));
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                                    String teamId = StringArgumentType.getString(ctx, "team");
                                    TeamData team = TeamStorage.getInstance().getTeamById(teamId);
                                    if (team == null) {
                                        ctx.getSource().sendError(Text.literal("Team " + teamId + " does not exist."));
                                        return 0;
                                    }
                                    TeamManager.addPlayer(player, teamId);
                                    ctx.getSource().sendFeedback(() -> Text.literal("Added " + player.getDisplayName().getString() + " to team " + teamId), true);
                                    return 1;
                                })
                        )
                )
        );
    }
}