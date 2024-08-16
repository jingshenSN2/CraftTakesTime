package sn2.crafttakestime;

import net.minecraft.inventory.container.Slot;

public interface ITimeCraftGuiContainer {
    void handleCraftFinished(Slot slotIn, int slotId);
}
