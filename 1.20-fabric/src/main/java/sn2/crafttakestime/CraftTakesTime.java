package sn2.crafttakestime;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import sn2.crafttakestime.common.core.CraftManager;
import sn2.crafttakestime.impl.MC120Adapter;
import sn2.crafttakestime.sound.SoundEventRegistry;

public class CraftTakesTime implements ModInitializer {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "crafttakestime";

    @Override
    public void onInitialize() {
        Registry.register(Registries.SOUND_EVENT,
                SoundEventRegistry.craftingSound.getId().toString(), SoundEventRegistry.craftingSound);
        Registry.register(Registries.SOUND_EVENT,
                SoundEventRegistry.finishSound.getId().toString(), SoundEventRegistry.finishSound);
        
        CraftManager.getInstance().setMinecraftAdapter(new MC120Adapter());
        CraftManager.getInstance().loadConfig();
    }
}
