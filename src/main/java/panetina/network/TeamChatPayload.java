package panetina.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import panetina.Teams;

@SuppressWarnings("unused")
public record TeamChatPayload(String message) implements CustomPayload {
    public static final Id<TeamChatPayload> ID = new Id<>(Identifier.of(Teams.MOD_ID, "team_chat"));
    public static final PacketCodec<PacketByteBuf, TeamChatPayload> CODEC = PacketCodec.of(
            (value, buf) -> buf.writeString(value.message),
            buf -> new TeamChatPayload(buf.readString())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}