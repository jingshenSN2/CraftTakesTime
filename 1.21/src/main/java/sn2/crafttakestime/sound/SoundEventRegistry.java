package sn2.crafttakestime.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static sn2.crafttakestime.CraftTakesTime.MODID;

public class SoundEventRegistry {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);
    public static RegistryObject<SoundEvent> craftingSound = SOUNDS.register("crafting", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "crafting")));
    public static RegistryObject<SoundEvent> finishSound = SOUNDS.register("finish", () ->
            SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "finish")));
}
