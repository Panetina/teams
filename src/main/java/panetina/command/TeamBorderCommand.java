package panetina.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import panetina.border.TeamBorderManager;
import panetina.team.TeamData;
import panetina.team.TeamStorage;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeamBorderCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("teamborder")
                .requires(source -> source.hasPermissionLevel(4))
                .then(argument("team", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            TeamStorage.getInstance().getAllTeams().forEach(t -> builder.suggest(t.getId()));
                            return builder.buildFuture();
                        })
                        // /teamborder <team> set <radius>
                        .then(literal("set")
                                .then(argument("radius", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            String teamId = StringArgumentType.getString(ctx, "team");
                                            int radius = IntegerArgumentType.getInteger(ctx, "radius");
                                            TeamData team = TeamStorage.getInstance().getTeamById(teamId);
                                            if (team != null) {
                                                team.setBorderRadius(radius);
                                                TeamStorage.getInstance().saveToFile();
                                                refreshTeamBorder(ctx.getSource(), teamId);
                                                ctx.getSource().sendFeedback(() -> Text.literal("Border for " + teamId + " set to " + radius), true);
                                            }
                                            return 1;
                                        })
                                )
                        )
                        // /teamborder <team> add <amount>
                        .then(literal("add")
                                .then(argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            String teamId = StringArgumentType.getString(ctx, "team");
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            TeamData team = TeamStorage.getInstance().getTeamById(teamId);
                                            if (team != null) {
                                                int newRadius = team.getBorderRadius() + amount;
                                                team.setBorderRadius(newRadius);
                                                TeamStorage.getInstance().saveToFile();
                                                refreshTeamBorder(ctx.getSource(), teamId);
                                                ctx.getSource().sendFeedback(() -> Text.literal("Border for " + teamId + " increased to " + newRadius), true);
                                            }
                                            return 1;
                                        })
                                )
                        )
                        // /teamborder <team> merge
                        .then(literal("merge")
                                .executes(ctx -> {
                                    String teamId = StringArgumentType.getString(ctx, "team");
                                    TeamData team = TeamStorage.getInstance().getTeamById(teamId);
                                    if (team != null) {
                                        team.setMerged(true);
                                        TeamStorage.getInstance().saveToFile();
                                        refreshTeamBorder(ctx.getSource(), teamId);
                                        ctx.getSource().sendFeedback(() -> Text.literal("Team " + teamId + " merged – using global border."), true);
                                    }
                                    return 1;
                                })
                        )
                        // /teamborder <team> unmerge
                        .then(literal("unmerge")
                                .executes(ctx -> {
                                    String teamId = StringArgumentType.getString(ctx, "team");
                                    TeamData team = TeamStorage.getInstance().getTeamById(teamId);
                                    if (team != null) {
                                        team.setMerged(false);
                                        TeamStorage.getInstance().saveToFile();
                                        refreshTeamBorder(ctx.getSource(), teamId);
                                        ctx.getSource().sendFeedback(() -> Text.literal("Team " + teamId + " unmerged – using team border again."), true);
                                    }
                                    return 1;
                                })
                        )
                )
        );
    }

    private static void refreshTeamBorder(ServerCommandSource source, String teamId) {
        for (ServerPlayerEntity player : source.getServer().getPlayerManager().getPlayerList()) {
            if (teamId.equals(TeamStorage.getInstance().getTeamIdForPlayer(player.getUuid()))) {
                TeamBorderManager.syncBorderToPlayer(player);
            }
        }
    }
}