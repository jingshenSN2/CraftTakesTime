package sn2.timecraft.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import sn2.timecraft.config.ConfigLoader;
import sn2.timecraft.core.CraftManager;
import sn2.timecraft.networking.PacketCraftConfig;
import sn2.timecraft.networking.TimeCraftPacketHandler;

@Mod.EventBusSubscriber()
public class Events {

    @SubscribeEvent
    public static void onWorldLoad(Load event) {
        if (!event.getWorld().isRemote) {
            ConfigLoader.loadConfig();
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        PacketCraftConfig packet = new PacketCraftConfig(CraftManager.getInstance().getConfig());
        TimeCraftPacketHandler.INSTANCE.sendTo(packet, (EntityPlayerMP) player);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer()) {
            return;
        }
        CraftManager.getInstance().tick();
    }
}
