package panetina.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerListEntry.class)
public interface PlayerListEntryInvoker {
    @Invoker("<init>")
    static PlayerListEntry create(GameProfile profile) {
        throw new AssertionError();
    }
}