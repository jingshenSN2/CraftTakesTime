package sn2.crafttakestime.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

public class TimeCraftPacketHandler {

    public static SimpleChannel INSTANCE = ChannelBuilder.named(
                    ResourceLocation.fromNamespaceAndPath("crafttakestime", "main"))
            .networkProtocolVersion(1)
            .simpleChannel();

    public static void registerMessage() {
        INSTANCE.messageBuilder(PacketCraftConfig.class)
                .encoder(PacketCraftConfig::toBytes)
                .decoder(PacketCraftConfig::fromBytes)
                .consumer(PacketCraftConfig::handle)
                .add();
    }
}
