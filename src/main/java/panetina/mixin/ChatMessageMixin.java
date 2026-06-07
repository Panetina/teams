package panetina.mixin;

import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import panetina.team.TeamData;
import panetina.team.TeamStorage;
import panetina.util.TeamColorUtil;

@Mixin(ServerPlayNetworkHandler.class)
public class ChatMessageMixin {

    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("TeamsMod");

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "handleDecoratedMessage", at = @At("HEAD"), cancellable = true)
    private void colorChatMessage(SignedMessage signedMessage, CallbackInfo ci) {
        if (this.player == null || this.player.getServer() == null || this.player.getServer().getPlayerManager() == null) {
            return;
        }

        TeamData team = TeamStorage.getInstance().getTeamOfPlayer(this.player.getUuid());

        // Log normal chat to console for all players (admins can see everything)
        String playerName = this.player.getName().getString();
        String message = signedMessage.getContent().getString();
        LOGGER.info("<{}> {}", playerName, message);

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

            // Send colored message to ALL players (global chat with colored name)
            for (ServerPlayerEntity online : this.player.getServer().getPlayerManager().getPlayerList()) {
                if (online != null) {
                    online.sendMessage(newMessage, false);
                }
            }
            ci.cancel(); // Cancel vanilla broadcast to use our colored version
        }
        // If not in team, let vanilla handle it (don't cancel)
    }
}