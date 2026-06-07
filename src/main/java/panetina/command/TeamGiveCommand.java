package panetina.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import panetina.team.TeamManager;
import panetina.team.TeamStorage;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TeamGiveCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("teamgive")
                .requires(source -> source.hasPermissionLevel(4))
                .then(argument("team", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            TeamStorage.getInstance().getAllTeams().forEach(t -> builder.suggest(t.getId()));
                            return builder.buildFuture();
                        })
                        .then(argument("item", ItemStackArgumentType.itemStack(registryAccess))
                                .then(argument("count", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            String teamId = StringArgumentType.getString(ctx, "team");
                                            ItemStack stack = ItemStackArgumentType.getItemStackArgument(ctx, "item").createStack(IntegerArgumentType.getInteger(ctx, "count"), false);
                                            TeamManager.giveToTeam(teamId, stack, ctx.getSource().getServer());
                                            ctx.getSource().sendFeedback(() -> Text.literal("Gave " + stack.getCount() + " " + stack.getItem().getName().getString() + " to team " + teamId), true);
                                            return 1;
                                        })
                                )
                        )
                )
        );
    }
}