package sn2.timecraft.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import sn2.timecraft.Constants;
import sn2.timecraft.ITimeCraftPlayer;
import sn2.timecraft.TimeCraft;
import sn2.timecraft.config.ConfigLoader;
import sn2.timecraft.networking.PacketCraftingDifficulty;
import sn2.timecraft.networking.TimeCraftPacketHandler;

import java.io.File;
import java.io.FileInputStream;

@Mod.EventBusSubscriber()
public class Events {

    @SubscribeEvent
    public static void onWorldLoad(Load event) {
        if (!event.getWorld().isRemote) {
            ConfigLoader.genSampleConfig();
            try {
                File cfgFile = ConfigLoader.cfgPath.resolve(Constants.CONFIG_FILENAME).toFile();
                FileInputStream inputFile = new FileInputStream(cfgFile);
                byte[] buf = new byte[inputFile.available()];
                inputFile.read(buf);
                inputFile.close();
                String json = new String(buf);
                TimeCraft.map.parserFrom(json);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        TimeCraft.map.difficultyMap.forEach((item, difficulty) -> {
            PacketCraftingDifficulty packet = new PacketCraftingDifficulty(item, difficulty);
            TimeCraftPacketHandler.INSTANCE.sendTo(packet, (EntityPlayerMP) player);
        });
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer()) {
            return;
        }
        ITimeCraftPlayer player = (ITimeCraftPlayer) event.player;
        player.getCraftManager().tick();
    }
}
