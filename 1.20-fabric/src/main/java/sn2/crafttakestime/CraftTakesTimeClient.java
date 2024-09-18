package sn2.crafttakestime;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import sn2.crafttakestime.common.core.CraftManager;
import sn2.crafttakestime.networking.PacketCraftConfig;

public class CraftTakesTimeClient implements ClientModInitializer {
    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(CraftTakesTimeClient.class);

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(PacketCraftConfig.TYPE.getId(),
                (client, handler, buf, responseSender) -> {
                    PacketCraftConfig packet = new PacketCraftConfig(buf);
                    CraftManager.getInstance().setConfig(packet.getConfig());
                });

        ClientTickEvents.END_WORLD_TICK.register(world -> {
            if (world != null) {
                CraftManager.getInstance().tick();
            }
        });
    }
}
