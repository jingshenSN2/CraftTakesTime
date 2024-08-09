package sn2.timecraft.mixin;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sn2.timecraft.ITimeCraftPlayer;
import sn2.timecraft.config.CraftContainerProperties;
import sn2.timecraft.config.CraftContainers;
import sn2.timecraft.util.CraftingDifficultyHelper;

import java.util.List;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends GuiScreen {

    private static final Logger log = LogManager.getLogger(MixinGuiContainer.class);

    private static final ResourceLocation CRAFT_OVERLAY_TEXTURE = new ResourceLocation(
            "textures/gui/craft_overlay.png");
    @Shadow
    public Container inventorySlots;
    @Shadow
    protected int guiLeft;
    @Shadow
    protected int guiTop;
    private ITimeCraftPlayer player;
    private boolean finished = false;

    public MixinGuiContainer(Container inventorySlotsIn) {
        super();
        this.allowUserInput = true;
    }

    @Shadow
    protected abstract void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type);

    @Inject(method = "drawScreen", at = @At("TAIL"), cancellable = true)
    public void timecraft$drawScreen(int mouseX, int mouseY, float partialTicks,
                                     CallbackInfo ci) {
        player = (ITimeCraftPlayer) this.mc.player;
        CraftContainerProperties properties = getCraftContainerProperties();
        int resultSlot = properties.getResultSlot();

        ItemStack resultStack = this.inventorySlots.getSlot(resultSlot).getStack();

        // Skip if the result slot is empty
        if (resultStack.isEmpty()) {
            return;
        }

        // Skip if the player is not crafting
        if (!player.isCrafting()) {
            return;
        }

        // Draw the crafting overlay
        if (properties.isDrawCraftingOverlay() && player.getCraftPeriod() > 0) {
            this.mc.getTextureManager().bindTexture(CRAFT_OVERLAY_TEXTURE);
            float percentage = player.getCraftTime() / player.getCraftPeriod();
            int progressWidth = (int) (percentage * properties.getOverlayWidth());
            MixinGuiContainer.drawModalRectWithCustomSizedTexture(
                    this.guiLeft + properties.getOverlayX(),
                    this.guiTop + properties.getOverlayY(),
                    0, 0,
                    progressWidth,
                    properties.getOverlayHeight(),
                    properties.getOverlayWidth(),
                    properties.getOverlayHeight());
        }
    }


    @Inject(method = "updateScreen", at = @At("TAIL"), cancellable = true)
    public void timecraft$updateScreen(CallbackInfo ci) {
        player = (ITimeCraftPlayer) this.mc.player;
        CraftContainerProperties properties = getCraftContainerProperties();
        int resultSlot = properties.getResultSlot();
        List<Integer> ingredientSlots = properties.getIngredientSlots();

        ItemStack resultStack = this.inventorySlots.getSlot(resultSlot).getStack();

        // Skip if the result slot is empty
        if (resultStack.isEmpty()) {
            return;
        }

        // Skip if the player is not crafting
        if (!player.isCrafting()) {
            return;
        }

        // Tick the player
        finished = player.tick(resultStack);

        if (finished) {
            // Record the old recipe before picking up the result item
            List<Item> oldRecipe = CraftingDifficultyHelper.getIngredientItems(
                    this.inventorySlots, ingredientSlots);

            this.handleMouseClick(this.inventorySlots.getSlot(resultSlot), resultSlot, 0, ClickType.PICKUP);

            // Compare the old recipe with the new recipe
            List<Item> newRecipe = CraftingDifficultyHelper.getIngredientItems(
                    this.inventorySlots, ingredientSlots);
            if (!oldRecipe.equals(newRecipe)) {
                player.stopCraft();
            } else {
                player.startCraft();
            }
        }
    }

    @Inject(method = "handleMouseClick", at = @At("HEAD"), cancellable = true)
    public void timecraft$handleMouseClick(Slot slot, int invSlot, int clickData, ClickType actionType,
                                           CallbackInfo info) {
        // Skip if the player is just finished crafting, so that the player can pick up the result item
        if (finished) {
            finished = false;
            return;
        }
        player = (ITimeCraftPlayer) this.mc.player;
        if (slot != null) {
            CraftContainerProperties properties = getCraftContainerProperties();
            if (properties != null) {
                if (properties.getResultSlot() == invSlot) {
                    log.debug("Result slot clicked, {}", slot);
                    ItemStack resultStack = this.inventorySlots.getSlot(invSlot).getStack();

                    // Skip if the result slot is empty
                    if (resultStack.isEmpty()) {
                        player.stopCraft();
                        return;
                    }

                    if (!player.isCrafting()) {
                        player.setCraftPeriod(CraftingDifficultyHelper.getCraftingDifficultyFromIngredients(
                                CraftingDifficultyHelper.getIngredientItems(
                                        this.inventorySlots, properties.getIngredientSlots()),
                                5F, 1F));
                        player.startCraft();
                    }
                    info.cancel();
                    return;
                }
            }
            player.stopCraft();
            log.debug("Inv slot {}, gui class {}, properties {}",
                    invSlot, this.getClass().getName(), properties);
        }
    }

    @Inject(method = "onGuiClosed", at = @At("HEAD"), cancellable = true)
    public void timecraft$onClose(CallbackInfo info) {
        if (player != null) {
            player.stopCraft();
        }
    }

    private CraftContainerProperties getCraftContainerProperties() {
        return CraftContainers.getInstance().getCraftContainerProperties(this.getClass().getName());
    }
}
