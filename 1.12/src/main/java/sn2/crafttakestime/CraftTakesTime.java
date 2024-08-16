package sn2.crafttakestime;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import sn2.crafttakestime.networking.TimeCraftPacketHandler;
import sn2.crafttakestime.sound.SoundEventRegistry;

@Mod(modid = "crafttakestime")
public class CraftTakesTime {

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        TimeCraftPacketHandler.registerMessage();
        ForgeRegistries.SOUND_EVENTS.register(SoundEventRegistry.craftingSound);
        ForgeRegistries.SOUND_EVENTS.register(SoundEventRegistry.finishSound);
    }
}
