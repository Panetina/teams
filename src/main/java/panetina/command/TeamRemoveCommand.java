package panetina.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import panetina.team.TeamManager;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeamRemoveCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("teamremove")
                .requires(source -> source.hasPermissionLevel(4))
                .then(argument("player", EntityArgumentType.player())
                        .executes(ctx -> {
                            ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");
                            TeamManager.removePlayer(player);

                            String playerName = player.getDisplayName() != null
                                    ? player.getDisplayName().getString()
                                    : player.getName().getString();

                            ctx.getSource().sendFeedback(() -> Text.literal("Removed " + playerName + " from their team"), true);
                            return 1;
                        })
                )
        );
    }
}