package sn2.timecraft.sound;

import net.minecraft.client.audio.MovingSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import sn2.timecraft.ITimeCraftPlayer;

public class CraftingTickableSound extends MovingSound {
    private final ITimeCraftPlayer player;

    public CraftingTickableSound(ITimeCraftPlayer player, BlockPos pos) {
        super(SoundEventRegistry.craftingSound, SoundCategory.PLAYERS);
        this.player = player;
        this.repeat = true;
        this.repeatDelay = 3;
        this.volume = 1.0F;
        this.xPosF = (float) pos.getX();
        this.yPosF = (float) pos.getY();
        this.zPosF = (float) pos.getZ();
    }

    @Override
    public void update() {
        if (!this.player.getCraftManager().isCrafting()) {
            this.donePlaying = true;
            this.volume = 0;
        }
    }
}
