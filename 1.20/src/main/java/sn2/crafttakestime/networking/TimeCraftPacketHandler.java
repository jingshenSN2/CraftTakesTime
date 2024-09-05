package sn2.crafttakestime.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class TimeCraftPacketHandler {

    public static SimpleChannel INSTANCE = NetworkRegistry
            .newSimpleChannel(new ResourceLocation("craft_takes_time")
                    , () -> "1.0", (s) -> true, (s) -> true);
    private static int packetId = 0;

    public static int nextID() {
        return packetId++;
    }

    public static void registerMessage() {
        INSTANCE.registerMessage(nextID(),
                PacketCraftConfig.class,
                PacketCraftConfig::toBytes,
                PacketCraftConfig::fromBytes,
                PacketCraftConfig::handle);
    }
}