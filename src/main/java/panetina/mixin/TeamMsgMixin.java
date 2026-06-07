package panetina.mixin;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeamMsgCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TeamMsgCommand.class)
public class TeamMsgMixin {
    @Inject(method = "register", at = @At("HEAD"), cancellable = true)
    private static void disableTeamMsg(CommandDispatcher<ServerCommandSource> dispatcher, CallbackInfo ci) {
        ci.cancel();
    }
}