package panetina.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import panetina.Teams;

import java.util.Map;
import java.util.UUID;

public record TeamDataPayload(Map<UUID, String> teamAssignments,
                              Map<String, String> teamNames,
                              Map<String, String> teamColors) implements CustomPayload {
    public static final Id<TeamDataPayload> ID = new Id<>(Identifier.of(Teams.MOD_ID, "team_data"));
    public static final PacketCodec<PacketByteBuf, TeamDataPayload> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeMap(value.teamAssignments,
                        (b, uuid) -> b.writeUuid(uuid),
                        PacketByteBuf::writeString);
                buf.writeMap(value.teamNames,
                        PacketByteBuf::writeString,
                        PacketByteBuf::writeString);
                buf.writeMap(value.teamColors,
                        PacketByteBuf::writeString,
                        PacketByteBuf::writeString);
            },
            buf -> new TeamDataPayload(
                    buf.readMap(b -> b.readUuid(), PacketByteBuf::readString),
                    buf.readMap(PacketByteBuf::readString, PacketByteBuf::readString),
                    buf.readMap(PacketByteBuf::readString, PacketByteBuf::readString)
            )
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}