package sn2.timecraft;

import net.minecraft.inventory.Slot;

public interface ITimeCraftGuiContainer {
    void handleCraftFinished(Slot slotIn, int slotId);
}
