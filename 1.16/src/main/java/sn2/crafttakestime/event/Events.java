package sn2.crafttakestime.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import sn2.crafttakestime.config.ConfigLoader;
import sn2.crafttakestime.core.CraftManager;
import sn2.crafttakestime.networking.PacketCraftConfig;
import sn2.crafttakestime.networking.TimeCraftPacketHandler;

@Mod.EventBusSubscriber()
public class Events {

    @SubscribeEvent
    public static void onWorldLoad(Load event) {
        if (!event.getWorld().isRemote()) {
            ConfigLoader.loadConfig();
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();
        PacketCraftConfig packet = new PacketCraftConfig(CraftManager.getInstance().getConfig());
        TimeCraftPacketHandler.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player),
                packet);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer()) {
            return;
        }
        CraftManager.getInstance().tick();
    }
}
