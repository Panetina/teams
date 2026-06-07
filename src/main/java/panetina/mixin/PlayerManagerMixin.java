package panetina.mixin;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import panetina.team.TeamData;
import panetina.team.TeamStorage;
import panetina.util.TeamColorUtil;

import java.util.List;
import java.util.Optional;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        TeamData team = TeamStorage.getInstance().getTeamOfPlayer(player.getUuid());
        if (team != null && player.getServer() != null) {
            String displayName = player.getDisplayName() != null
                    ? player.getDisplayName().getString()
                    : player.getName().getString();
            Text coloredName = Text.literal(displayName)
                    .styled(style -> style.withColor(TeamColorUtil.parseColor(team.getColor())));
            player.setCustomName(coloredName);
            player.setCustomNameVisible(true);

            TrackedData<Optional<Text>> key = EntityAccessor.getCustomNameTrackedData();
            Optional<Text> value = player.getDataTracker().get(key);
            DataTracker.SerializedEntry<Optional<Text>> entry = DataTracker.SerializedEntry.of(key, value);
            EntityTrackerUpdateS2CPacket trackerPacket = new EntityTrackerUpdateS2CPacket(player.getId(), List.of(entry));

            PlayerListS2CPacket tabPacket = new PlayerListS2CPacket(
                    PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME,
                    player
            );

            for (ServerPlayerEntity other : player.getServer().getPlayerManager().getPlayerList()) {
                other.networkHandler.sendPacket(trackerPacket);
                other.networkHandler.sendPacket(tabPacket);
            }
        }
    }
}