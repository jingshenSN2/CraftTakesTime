package sn2.crafttakestime.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundEventRegistry {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "crafttakestime");
    public static RegistryObject<SoundEvent> craftingSound = SOUNDS.register("crafting", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation("crafttakestime", "crafting")));
    public static RegistryObject<SoundEvent> finishSound = SOUNDS.register("finish", () ->
            SoundEvent.createVariableRangeEvent(new ResourceLocation("crafttakestime", "finish")));
}
