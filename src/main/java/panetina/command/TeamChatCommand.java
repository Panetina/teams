package panetina.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import panetina.team.TeamManager;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeamChatCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("t")
                .then(argument("message", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                            String message = StringArgumentType.getString(ctx, "message");
                            TeamManager.sendTeamMessage(player, message);
                            return 1;
                        })
                )
        );
    }
}