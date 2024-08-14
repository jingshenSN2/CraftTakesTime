package sn2.timecraft.core;

import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class CraftContainerProperties {
    /**
     * The block name of the GUI container
     */
    private String containerName;

    /**
     * The class name of the GUI container
     */
    private String guiContainerClassName;

    /**
     * Whether the container is enabled for crafting time
     */
    @Builder.Default
    private boolean enabled = true;

    /**
     * The multiplier for the crafting time
     */
    @Builder.Default
    private float containerMultiplier = 1.0f;

    /**
     * The slot index of the result item
     */
    @Builder.Default
    private int resultSlot = 0;

    /**
     * The slot indices of the ingredients
     */
    @Builder.Default
    private List<Integer> ingredientSlots = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);

    /**
     * Whether to draw the crafting overlay
     */
    @Builder.Default
    private boolean drawCraftingOverlay = true;

    /**
     * The texture resource location of the crafting overlay
     */
    @Builder.Default
    private String overlayTexture = "timecraft:textures/gui/crafting_overlay_default.png";

    /**
     * The x-coordinate of the crafting overlay, relative to the GUI container guiLeft
     */
    @Builder.Default
    private int overlayX = 88;

    /**
     * The y-coordinate of the crafting overlay, relative to the GUI container guiTop
     */
    @Builder.Default
    private int overlayY = 35;

    /**
     * The width of the crafting overlay
     */
    @Builder.Default
    private int overlayWidth = 24;

    /**
     * The height of the crafting overlay
     */
    @Builder.Default
    private int overlayHeight = 16;
}
