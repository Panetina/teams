package panetina.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import panetina.client.ClientTeamData;
import panetina.util.TeamColorUtil;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "collectPlayerEntries", at = @At("HEAD"), cancellable = true)
    private void groupByTeamWithHeaders(CallbackInfoReturnable<List<PlayerListEntry>> cir) {
        if (this.client.player == null || this.client.getNetworkHandler() == null) return;

        Collection<PlayerListEntry> allEntries = this.client.getNetworkHandler().getPlayerList();
        if (allEntries.isEmpty()) return;

        Map<String, List<PlayerListEntry>> teamGroups = new LinkedHashMap<>();
        List<PlayerListEntry> noTeam = new ArrayList<>();

        for (PlayerListEntry entry : allEntries) {
            UUID uuid = entry.getProfile().getId();
            String teamId = ClientTeamData.getTeamId(uuid);
            if (teamId != null) {
                teamGroups.computeIfAbsent(teamId, k -> new ArrayList<>()).add(entry);
            } else {
                noTeam.add(entry);
            }
        }

        List<PlayerListEntry> result = new ArrayList<>();

        for (Map.Entry<String, List<PlayerListEntry>> group : teamGroups.entrySet()) {
            String teamId = group.getKey();
            String teamName = ClientTeamData.getTeamName(teamId);
            String teamColor = ClientTeamData.getTeamColor(teamId);

            // Use team color for header, fallback to gold if invalid
            TextColor color = TeamColorUtil.parseColor(teamColor);
            Text headerText = Text.literal(" " + teamName + " ")
                    .styled(style -> style.withBold(true).withColor(color));

            UUID headerUUID = UUID.nameUUIDFromBytes(("teamheader_" + teamId).getBytes(StandardCharsets.UTF_8));
            GameProfile dummyProfile = new GameProfile(headerUUID, "§n" + teamName);
            PlayerListEntry header = new PlayerListEntry(dummyProfile, false);
            header.setDisplayName(headerText);
            result.add(header);
            result.addAll(group.getValue());
        }

        if (!noTeam.isEmpty()) {
            UUID noTeamUUID = UUID.nameUUIDFromBytes("teamheader_none".getBytes(StandardCharsets.UTF_8));
            GameProfile dummyProfile = new GameProfile(noTeamUUID, "§nOthers");
            PlayerListEntry header = new PlayerListEntry(dummyProfile, false);
            header.setDisplayName(Text.literal(" No Team ").formatted(Formatting.BOLD, Formatting.GRAY));
            result.add(header);
            result.addAll(noTeam);
        }

        cir.setReturnValue(result);
    }
}