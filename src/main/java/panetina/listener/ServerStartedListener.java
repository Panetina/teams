package panetina.listener;

import net.minecraft.server.MinecraftServer;
import panetina.team.TeamStorage;

public class ServerStartedListener {
    public static void onServerStarted(MinecraftServer server) {
        TeamStorage.getInstance().loadFromFile();
        server.getOverworld().getWorldBorder().setSize(200.0);
    }
}