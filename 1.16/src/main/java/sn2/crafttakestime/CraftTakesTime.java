package sn2.crafttakestime;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sn2.crafttakestime.networking.TimeCraftPacketHandler;
import sn2.crafttakestime.sound.SoundEventRegistry;

@Mod("crafttakestime")
public class CraftTakesTime {
    public CraftTakesTime() {
        TimeCraftPacketHandler.registerMessage();
        SoundEventRegistry.SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
