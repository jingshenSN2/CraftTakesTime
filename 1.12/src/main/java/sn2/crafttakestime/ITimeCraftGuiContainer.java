package sn2.crafttakestime;

import net.minecraft.inventory.Slot;

public interface ITimeCraftGuiContainer {
    void handleCraftFinished(Slot slotIn, int slotId);
}
