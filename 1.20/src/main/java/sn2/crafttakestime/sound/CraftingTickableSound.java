package sn2.crafttakestime.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import sn2.crafttakestime.common.core.CraftManager;

public class CraftingTickableSound extends AbstractTickableSoundInstance {

    public CraftingTickableSound(BlockPos pos) {
        super(SoundEventRegistry.craftingSound.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.looping = true;
        this.delay = 3;
        this.volume = 1.0F;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    //
    @Override
    public void tick() {
        if (!CraftManager.getInstance().isCrafting()) {
            this.stop();
        }
    }
}
