package panetina.mixin;

import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import panetina.team.TeamData;
import panetina.team.TeamStorage;
import panetina.util.TeamColorUtil;

@Mixin(ServerPlayNetworkHandler.class)
public class ChatMessageMixin {

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "handleDecoratedMessage", at = @At("HEAD"), cancellable = true)
    private void colorChatMessage(SignedMessage signedMessage, CallbackInfo ci) {
        TeamData team = TeamStorage.getInstance().getTeamOfPlayer(this.player.getUuid());
        if (team != null) {
            String displayName = this.player.getDisplayName() != null
                    ? this.player.getDisplayName().getString()
                    : this.player.getName().getString();

            Text coloredName = Text.literal(displayName)
                    .styled(style -> style.withColor(TeamColorUtil.parseColor(team.getColor())));

            Text newMessage = Text.literal("<")
                    .append(coloredName)
                    .append(Text.literal("> "))
                    .append(signedMessage.getContent());

            // Send colored message to all players
            for (ServerPlayerEntity online : this.player.getServer().getPlayerManager().getPlayerList()) {
                online.sendMessage(newMessage, false);
            }
            ci.cancel(); // cancel vanilla broadcast to avoid duplicate
        }
    }
}