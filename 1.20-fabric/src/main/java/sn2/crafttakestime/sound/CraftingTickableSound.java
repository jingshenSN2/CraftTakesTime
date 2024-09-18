package sn2.crafttakestime.sound;

import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import sn2.crafttakestime.common.core.CraftManager;

public class CraftingTickableSound extends MovingSoundInstance {
    public CraftingTickableSound(BlockPos pos) {
        super(SoundEventRegistry.craftingSound, SoundCategory.PLAYERS, SoundInstance.createRandom());
        this.repeat = true;
        this.repeatDelay = 3;
        this.volume = 1.0F;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    @Override
    public void tick() {
        if (!CraftManager.getInstance().isCrafting()) {
            this.setDone();
        }
    }
}
