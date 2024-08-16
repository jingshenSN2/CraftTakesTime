package sn2.crafttakestime.mixin;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sn2.crafttakestime.ITimeCraftGuiContainer;
import sn2.crafttakestime.core.CraftContainerProperties;
import sn2.crafttakestime.core.CraftManager;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends GuiScreen implements ITimeCraftGuiContainer {

    private static final Logger log = LogManager.getLogger(MixinGuiContainer.class);
    private final GuiContainer self = (GuiContainer) (Object) this;
    @Shadow
    protected int guiLeft;
    @Shadow
    protected int guiTop;
    private boolean finished = false;

    @Shadow
    protected abstract void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type);

    @Override
    public void handleCraftFinished(Slot slotIn, int slotId) {
        this.finished = true;
        this.handleMouseClick(slotIn, slotId, 0, ClickType.PICKUP);
    }

    @Inject(method = "drawScreen", at = @At("TAIL"))
    public void crafttakestime$drawScreen(int mouseX, int mouseY, float partialTicks,
                                          CallbackInfo ci) {
        try {
            CraftManager manager = CraftManager.getInstance();
            CraftContainerProperties properties = manager.getCraftContainerProperties();

            // Skip if the properties are not set for this container
            if (properties == null) {
                return;
            }

            // Skip if the player is not crafting
            if (!manager.isCrafting()) {
                return;
            }

            // Draw the crafting overlay
            if (properties.isDrawCraftingOverlay() && manager.getCraftPeriod() > 0) {
                String[] namespaceAndPath = properties.getOverlayTexture().split(":");
                ResourceLocation craftOverlay = new ResourceLocation(namespaceAndPath[0], namespaceAndPath[1]);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(craftOverlay);
                float percentage = manager.getCurrentCraftTime() / manager.getCraftPeriod();
                int progressWidth = (int) (percentage * properties.getOverlayWidth());
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableLighting();
                drawModalRectWithCustomSizedTexture(
                        this.guiLeft + properties.getOverlayX(),
                        this.guiTop + properties.getOverlayY(),
                        0.0F, 0.0F,
                        progressWidth,
                        properties.getOverlayHeight(),
                        properties.getOverlayWidth(),
                        properties.getOverlayHeight());
                GlStateManager.enableLighting();
                GlStateManager.enableRescaleNormal();
            }
        } catch (Exception e) {
            log.error("Failed to draw the crafting overlay", e);
        }
    }

    @Inject(method = "handleMouseClick", at = @At("HEAD"), cancellable = true)
    public void crafttakestime$handleMouseClick(Slot slot, int invSlot, int clickData, ClickType actionType,
                                                CallbackInfo info) {
        // Skip if the player is just finished crafting, so that the player can pick up the result item
        if (finished) {
            finished = false;
            return;
        }
        try {
            if (CraftManager.getInstance().initCraft(self, invSlot)) {
                info.cancel();
            }
        } catch (Exception e) {
            log.error("Failed to handle the mouse click", e);
        }
    }
}