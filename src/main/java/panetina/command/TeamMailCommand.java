package panetina.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import panetina.team.TeamData;
import panetina.team.TeamStorage;
import panetina.util.TeamColorUtil;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeamMailCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("teammail")
                .requires(source -> source.hasPermissionLevel(4))
                .then(argument("team", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            TeamStorage.getInstance().getAllTeams().forEach(t -> builder.suggest(t.getId()));
                            return builder.buildFuture();
                        })
                        .then(argument("message", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    String teamId = StringArgumentType.getString(ctx, "team");
                                    String message = StringArgumentType.getString(ctx, "message");
                                    TeamData team = TeamStorage.getInstance().getTeamById(teamId);

                                    if (team == null) {
                                        ctx.getSource().sendError(Text.literal("Team " + teamId + " does not exist."));
                                        return 0;
                                    }

                                    Text prefix = Text.literal(team.getPrefix()).styled(style ->
                                            style.withColor(TeamColorUtil.parseColor(team.getColor())));
                                    Text fullMsg = prefix.copy()
                                            .append(Text.literal(" [ADMIN] " + message).formatted(Formatting.YELLOW));

                                    int count = 0;
                                    for (ServerPlayerEntity player : ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
                                        if (teamId.equals(TeamStorage.getInstance().getTeamIdForPlayer(player.getUuid()))) {
                                            player.sendMessage(fullMsg, false);   // chat
                                            player.sendMessage(fullMsg, true);    // action bar overlay
                                            count++;
                                        }
                                    }

                                    final int sent = count;
                                    ctx.getSource().sendFeedback(() -> Text.literal("[Teammail] Sent to " + sent + " online member(s) of " + team.getName()), true);
                                    return 1;
                                })
                        )
                )
        );
    }
}