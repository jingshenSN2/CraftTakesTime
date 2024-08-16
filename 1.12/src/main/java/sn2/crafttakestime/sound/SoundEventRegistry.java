package sn2.crafttakestime.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class SoundEventRegistry {

    public static final ResourceLocation craftingSoundID = new ResourceLocation("crafttakestime:crafting");
    public static final ResourceLocation finishSoundID = new ResourceLocation("crafttakestime:finish");
    public static SoundEvent craftingSound = new SoundEvent(craftingSoundID).setRegistryName(craftingSoundID);
    public static SoundEvent finishSound = new SoundEvent(finishSoundID).setRegistryName(finishSoundID);
}
