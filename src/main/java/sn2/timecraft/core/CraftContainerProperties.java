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
    @Builder.Default
    private int resultSlot = 0;

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
    @Builder.Default
    private boolean drawCraftingOverlay = true;

    /**
     * The texture resource location of the crafting overlay
     */
    @Builder.Default
    private String overlayTexture = "timecraft:textures/gui/crafting_overlay_default";

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
