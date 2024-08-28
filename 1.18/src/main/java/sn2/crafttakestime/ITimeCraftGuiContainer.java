package sn2.crafttakestime;

import net.minecraft.world.inventory.Slot;

public interface ITimeCraftGuiContainer {
    void handleCraftFinished(Slot slotIn, int slotId);
}
