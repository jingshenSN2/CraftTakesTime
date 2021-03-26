package sn2.timecraft.sound;

import net.minecraft.client.audio.TickableSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import sn2.timecraft.ITimeCraftPlayer;

public class CraftingTickableSound extends TickableSound {
	   private ITimeCraftPlayer player;
	   private float last_craft_time;
	   public boolean stop;

	   public CraftingTickableSound(ITimeCraftPlayer player, BlockPos pos) {
	      super(SoundEventRegistry.craftingSound.get(), SoundCategory.PLAYERS);
	      this.player = player;
	      this.repeat = true;
	      this.repeatDelay = 3;
	      this.volume = 1.0F;
	      this.x = (double)((float)pos.getX());
	      this.y = (double)((float)pos.getY());
	      this.z = (double)((float)pos.getZ());
	      this.last_craft_time = 0;
	      this.stop = false;
	   }
	   
	   public void tick() {
		   float new_craft_time = player.getCraftTime();
		   if (this.stop || !this.player.isCrafting()) {
			   this.finishPlaying();
			   return;
		   }
		   if (this.last_craft_time == new_craft_time) {
			   this.volume = 0.0F;
		   } else {
			   this.volume = 1.0F;
		   }
		   this.last_craft_time = new_craft_time;
	   }
}
