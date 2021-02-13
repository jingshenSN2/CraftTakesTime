package sn2.timecraft;

import net.minecraft.item.ItemStack;

public interface ITimeCraftPlayer {

	public void setCrafting(boolean is_crafting);

	public boolean isCrafting();

	public void setCraftTime(float craft_time);

	public float getCraftTime();

	public void setCraftPeriod(float craft_period);

	public float getCraftPeriod();
	
	public void stopCraft();
	
	public void startCraftWithNewPeriod(float craft_period);

	public boolean tick(ItemStack resultStack);

}
