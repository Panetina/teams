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

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "getPlayerListName", at = @At("RETURN"), cancellable = true)
    private void colorPlayerListName(CallbackInfoReturnable<Text> cir) {
        ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;
        TeamData team = TeamStorage.getInstance().getTeamOfPlayer(self.getUuid());
        if (team != null) {
            Text original = cir.getReturnValue();
            String name = original != null ? original.getString() : self.getName().getString();
            Text colored = Text.literal(name)
                    .styled(style -> style.withColor(TeamColorUtil.parseColor(team.getColor())));
            cir.setReturnValue(colored);
        }
    }
}