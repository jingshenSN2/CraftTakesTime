package sn2.timecraft.core;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CraftContainerProperties {
    /**
     * The slot index of the result item
     */
    private int resultSlot;

    /**
     * The slot indices of the ingredients
     */
    private List<Integer> ingredientSlots;

    /**
     * The class name of the GUI container
     */
    private String guiContainerClassName;

    /**
     * Whether to draw the crafting overlay
     */
    private boolean drawCraftingOverlay;

    /**
     * The x-coordinate of the crafting overlay, relative to the GUI container guiLeft
     */
    private int overlayX;

    /**
     * The y-coordinate of the crafting overlay, relative to the GUI container guiTop
     */
    private int overlayY;

    /**
     * The width of the crafting overlay
     */
    private int overlayWidth;

    /**
     * The height of the crafting overlay
     */
    private int overlayHeight;
}
