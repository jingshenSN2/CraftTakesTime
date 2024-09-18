package sn2.crafttakestime.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import static sn2.crafttakestime.CraftTakesTime.MODID;

public class SoundEventRegistry {
    public static SoundEvent craftingSound = SoundEvent.of(new Identifier(MODID, "crafting"));
    public static SoundEvent finishSound = SoundEvent.of(new Identifier(MODID, "finish"));
}
