package sn2.timecraft.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import sn2.timecraft.ITimeCraftPlayer;
import sn2.timecraft.sound.CraftingTickableSound;
import sn2.timecraft.sound.SoundEventRegistry;
import sn2.timecraft.util.CraftingSpeedHelper;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity extends AbstractClientPlayerEntity implements ITimeCraftPlayer {
	
	private CraftingTickableSound sound;
	
	public MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	public boolean is_crafting = false;
	public float craft_time = 0F;
	public float craft_period = 0F;

	@Override
	public void setCrafting(boolean is_crafting) {
		this.is_crafting = is_crafting;
	}

	@Override
	public boolean isCrafting() {
		return this.is_crafting;
	}

	@Override
	public void setCraftTime(float craft_time) {
		this.craft_time = craft_time;
	}

	@Override
	public float getCraftTime() {
		return this.craft_time;
	}

	@Override
	public void setCraftPeriod(float craft_period) {
		this.craft_period = craft_period;
	}

	@Override
	public float getCraftPeriod() {
		return this.craft_period;
	}
	
	@Override
	public void stopCraft() {
		this.is_crafting = false;
		this.craft_time = 0;
	}

	@Override
	public void startCraftWithNewPeriod(float craft_period) {
		this.sound.stop = true;
		this.craft_time = 0;
		this.craft_period = craft_period;
		this.is_crafting = true;
		if (craft_period >= 10F) {
			this.sound = new CraftingTickableSound(this, this.getPosition());
			// Minecraft.getInstance().getSoundHandler().play(this.sound);
		}
	}
	
	@Override
	public boolean tick(ItemStack resultStack) {
		if (this.isCrafting()) {
			ItemStack cursorStack = this.inventory.getItemStack();
			if (cursorStack.getItem() != Items.AIR) {
				if (!cursorStack.isItemEqual(resultStack)
						|| cursorStack.getCount() + resultStack.getCount() > cursorStack.getMaxStackSize()) {
					return false;
				}
			}
			if (this.getCraftTime() < this.getCraftPeriod()) {
				this.craft_time += CraftingSpeedHelper.getCraftingSpeed(this);
			}
			else if (this.getCraftTime() >= this.getCraftPeriod()) {
				this.playSound(SoundEventRegistry.finishSound.get(), SoundCategory.PLAYERS, 0.1F, 1f);
				this.startCraftWithNewPeriod(craft_period);
				return true;
			}
		}
		return false;
	}

}
