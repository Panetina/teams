package panetina.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import panetina.team.TeamData;
import panetina.team.TeamStorage;
import panetina.util.TeamColorUtil;

@Mixin(value = ServerPlayerEntity.class, priority = 5000) // Very high priority = runs last
public class TabListColorFixMixin {

    @Inject(method = "getPlayerListName", at = @At("RETURN"), cancellable = true)
    private void forceTeamColor(CallbackInfoReturnable<Text> cir) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
        TeamData team = TeamStorage.getInstance().getTeamOfPlayer(self.getUuid());

        if (team == null) return;

        Text current = cir.getReturnValue();
        if (current == null) return;

        // Get plain text (remove any existing formatting)
        String plainName = current.getString();

        // Force apply team color
        Text colored = Text.literal(plainName)
                .styled(style -> style.withColor(TeamColorUtil.parseColor(team.getColor())));

        cir.setReturnValue(colored);
    }
}