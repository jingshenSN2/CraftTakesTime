package sn2.timecraft.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import sn2.timecraft.ITimeCraftPlayer;

@Mixin(EntityPlayerSP.class)
public class MixinClientPlayerEntity extends AbstractClientPlayer implements ITimeCraftPlayer {

	public MixinClientPlayerEntity(World world, GameProfile profile) {
		super(world, profile);
	}

	public boolean is_crafting = false;
	public int craft_time = 0;
	public int craft_period = 0;

	@Override
	public void setCrafting(boolean is_crafting) {
		this.is_crafting = is_crafting;
	}

	@Override
	public boolean isCrafting() {
		return this.is_crafting;
	}

	@Override
	public void setCraftTime(int craft_time) {
		this.craft_time = craft_time;
	}

	@Override
	public int getCraftTime() {
		return this.craft_time;
	}

	@Override
	public void setCraftPeriod(int craft_period) {
		this.craft_period = craft_period;
	}

	@Override
	public int getCraftPeriod() {
		return this.craft_period;
	}

	@Override
	public void stopCraft() {
		this.is_crafting = false;
		this.craft_time = 0;
	}

	@Override
	public void startCraftWithNewPeriod(int craft_period) {
		this.craft_period = craft_period;
		this.is_crafting = true;
	}
	
	@Override
	public boolean tick(ItemStack resultStack) {
		if (this.isCrafting()) {
			ItemStack cursorStack = this.inventory.getItemStack();
			if (cursorStack.getItem() != Items.AIR) {
				if (!cursorStack.isItemEqual(resultStack)
						|| cursorStack.getCount() + resultStack.getCount() > cursorStack.getMaxStackSize()) {
					this.stopCraft();
				}
			}
			if (this.getCraftTime() < this.getCraftPeriod()) {
				this.craft_time++;
			}
			if (this.getCraftTime() >= this.getCraftPeriod()) {
				this.craft_time = 0;
				return true;
			}
		}
		return false;
	}

}
