package sn2.timecraft;

import net.minecraft.item.ItemStack;

public interface ITimeCraftPlayer {

    public boolean isCrafting();

    public void setCrafting(boolean isCrafting);

    public float getCraftTime();

    public void setCraftTime(float craftTime);

    public float getCraftPeriod();

    public void setCraftPeriod(float craftPeriod);

    public void stopCraft();

    public void startCraft();

    public boolean tick(ItemStack resultStack);

}
