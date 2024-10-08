package sn2.crafttakestime.common.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import sn2.crafttakestime.common.slot.SlotRange;

@Data
@Builder
@AllArgsConstructor
public class ContainerConfig {
    /**
     * The block name of the GUI container
     */
    private String containerName;

    /**
     * Whether the container is enabled for crafting time
     */
    @Builder.Default
    private boolean enabled = true;

    /**
     * The class name of the GUI container
     */
    private String guiContainerClassName;

    /**
     * The multiplier for the crafting time
     */
    @Builder.Default
    private float craftingTimeMultiplier = 1.0F;

    /**
     * The multiplier for the crafting time
     */
    @Builder.Default
    private float containerMultiplier = 1.0f;

    /**
     * The slot index of the output item
     */
    @Builder.Default
    private int outputSlot = 0;

    /**
     * The slot indices of the ingredients
     */
    @Builder.Default
    private SlotRange ingredientSlots = SlotRange.builder().build();

    /**
     * Whether to draw the crafting overlay
     */
    @Builder.Default
    private boolean drawCraftingOverlay = true;

    /**
     * The texture resource location of the crafting overlay
     */
    @Builder.Default
    private String overlayTexture = "crafttakestime:textures/gui/crafting_overlay_default.png";

    /**
     * The x-coordinate of the crafting overlay, relative to the GUI container guiLeft
     */
    @Builder.Default
    private int overlayX = 89;

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
