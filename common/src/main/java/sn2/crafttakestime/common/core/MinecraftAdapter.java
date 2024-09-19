package sn2.crafttakestime.common.core;

import sn2.crafttakestime.common.config.ContainerConfig;
import sn2.crafttakestime.common.slot.SlotRange;

import java.nio.file.Path;
import java.util.List;

public interface MinecraftAdapter {
    // Config related
    Path getConfigPath();

    List<ContainerConfig> getDefaultsCraftContainers();

    // ContainerScreen related
    Object getContainerScreen();

    void setContainerScreen(Object screen);

    default String getContainerScreenClassName() {
        return this.getContainerScreen().getClass().getName();
    }

    // Slot related
    Object getItems(SlotRange range);

    boolean isSlotEmpty(int slotIndex);

    ItemRegistry getSlotItemRegistry(int slotIndex);

    boolean shouldStopCrafting(int outputSlot);

    void handleCraftFinished(int outputSlot);

    // Player related
    boolean hasPlayer();

    void playCraftingSound();

    void playFinishSound();

    float getCraftingSpeed();
}
