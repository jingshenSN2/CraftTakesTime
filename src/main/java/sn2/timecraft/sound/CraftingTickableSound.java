package sn2.timecraft.sound;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import sn2.timecraft.core.CraftManager;

public class CraftingTickableSound extends MovingSound {

    public CraftingTickableSound(BlockPos pos) {
        super(SoundEventRegistry.craftingSound, SoundCategory.PLAYERS);
        this.repeat = true;
        this.repeatDelay = 3;
        this.volume = 1.0F;
        this.xPosF = (float) pos.getX();
        this.yPosF = (float) pos.getY();
        this.zPosF = (float) pos.getZ();
    }

    @Override
    public void update() {
        if (!CraftManager.getInstance().isCrafting()) {
            this.donePlaying = true;
            this.volume = 0;
        }
    }
}
