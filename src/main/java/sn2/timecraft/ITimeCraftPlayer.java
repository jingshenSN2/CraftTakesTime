package sn2.timecraft;

import net.minecraft.item.ItemStack;

public interface ITimeCraftPlayer {

	public void setCrafting(boolean is_crafting);

	public boolean isCrafting();

	public void setCraftTime(int craft_time);

	public int getCraftTime();

	public void setCraftPeriod(int craft_period);

	public int getCraftPeriod();
	
	public void stopCraft();
	
	public void startCraftWithNewPeriod(int craft_period);

	public boolean tick(ItemStack resultStack);

}
