package sn2.crafttakestime.sound;

import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import sn2.crafttakestime.core.CraftManager;

public class CraftingTickableSound extends TickableSound {

    public CraftingTickableSound(BlockPos pos) {
        super(SoundEventRegistry.craftingSound.get(), SoundCategory.PLAYERS);
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
            this.finishPlaying();
        }
    }
}
