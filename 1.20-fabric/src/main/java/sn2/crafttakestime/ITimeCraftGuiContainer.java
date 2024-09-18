package sn2.crafttakestime;

import net.minecraft.screen.slot.Slot;

public interface ITimeCraftGuiContainer {
    void handleCraftFinished(Slot slotIn, int slotId);
}
