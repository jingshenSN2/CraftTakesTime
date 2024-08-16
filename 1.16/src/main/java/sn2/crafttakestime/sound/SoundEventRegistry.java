package sn2.crafttakestime.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundEventRegistry {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "crafttakestime");
    public static RegistryObject<SoundEvent> craftingSound = SOUNDS.register("crafting", () -> new SoundEvent(new ResourceLocation("crafttakestime", "crafting")));
    public static RegistryObject<SoundEvent> finishSound = SOUNDS.register("finish", () -> new SoundEvent(new ResourceLocation("crafttakestime", "finish")));
}
