package sn2.crafttakestime.networking;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import static sn2.crafttakestime.CraftTakesTime.MODID;

public class TimeCraftPacketHandler {

    public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE
            .newSimpleChannel(MODID);
    private static int packetId = 0;

    public static int nextID() {
        return packetId++;
    }

    public static void registerMessage() {
        INSTANCE.registerMessage(PacketCraftConfig.Handler.class, PacketCraftConfig.class, nextID(),
                Side.CLIENT);
    }
}
