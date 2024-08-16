package sn2.crafttakestime.networking;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class TimeCraftPacketHandler {

    public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE
            .newSimpleChannel("craft_takes_time");
    private static int packetId = 0;

    public static int nextID() {
        return packetId++;
    }

    public static void registerMessage() {
        INSTANCE.registerMessage(PacketCraftConfig.Handler.class, PacketCraftConfig.class, nextID(),
                Side.CLIENT);
    }
}
