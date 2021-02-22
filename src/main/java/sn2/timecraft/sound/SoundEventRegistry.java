package sn2.timecraft.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class SoundEventRegistry {

	public static final ResourceLocation craftingSoundID = new ResourceLocation("timecraft:crafting");
    public static SoundEvent craftingSound = new SoundEvent(craftingSoundID).setRegistryName(craftingSoundID);
	public static final ResourceLocation finishSoundID = new ResourceLocation("timecraft:finish");
    public static SoundEvent finishSound = new SoundEvent(finishSoundID).setRegistryName(finishSoundID);
}
