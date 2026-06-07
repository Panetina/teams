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

        if (team == null) return;

        // Get the current display name (could be from nickname mod)
        Text currentName = cir.getReturnValue();
        if (currentName == null) {
            currentName = self.getName();
        }

        // Extract just the string content, ignoring any existing color
        String nameString = currentName.getString();

        // Apply team color to the plain string
        Text coloredName = Text.literal(nameString)
                .styled(style -> style.withColor(TeamColorUtil.parseColor(team.getColor())));

        cir.setReturnValue(coloredName);
    }
}