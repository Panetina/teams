package panetina.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import panetina.Teams;

@SuppressWarnings("unused")
public record TeamSyncPayload(String data) implements CustomPayload {
    public static final Id<TeamSyncPayload> ID = new Id<>(Identifier.of(Teams.MOD_ID, "team_sync"));
    public static final PacketCodec<PacketByteBuf, TeamSyncPayload> CODEC = PacketCodec.of(
            (value, buf) -> buf.writeString(value.data),
            buf -> new TeamSyncPayload(buf.readString())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}