package sn2.crafttakestime;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import sn2.crafttakestime.common.core.CraftManager;
import sn2.crafttakestime.impl.MC112Adapter;
import sn2.crafttakestime.networking.TimeCraftPacketHandler;
import sn2.crafttakestime.sound.SoundEventRegistry;

@Mod(modid = CraftTakesTime.MODID)
public class CraftTakesTime {
    public static final String MODID = "crafttakestime";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        TimeCraftPacketHandler.registerMessage();
        ForgeRegistries.SOUND_EVENTS.register(SoundEventRegistry.craftingSound);
        ForgeRegistries.SOUND_EVENTS.register(SoundEventRegistry.finishSound);
        CraftManager.getInstance().setMinecraftAdapter(new MC112Adapter());
    }
}
