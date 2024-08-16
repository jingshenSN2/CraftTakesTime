package sn2.crafttakestime.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
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

@Mixin(ContainerScreen.class)
public abstract class MixinContainerScreen<T extends Container> extends Screen implements ITimeCraftGuiContainer {

    private static final Logger log = LogManager.getLogger(MixinContainerScreen.class);
    private final ContainerScreen<T> self = (ContainerScreen<T>) (Object) this;
    @Shadow
    protected int guiLeft;
    @Shadow
    protected int guiTop;
    private boolean finished = false;

    protected MixinContainerScreen(ITextComponent p_i51108_1_) {
        super(p_i51108_1_);
    }

    @Shadow
    protected abstract void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type);

    @Override
    public void handleCraftFinished(Slot slotIn, int slotId) {
        this.finished = true;
        this.handleMouseClick(slotIn, slotId, 0, ClickType.PICKUP);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void crafttakestime$render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks,
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
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.minecraft.getTextureManager().bindTexture(craftOverlay);
                float percentage = manager.getCurrentCraftTime() / manager.getCraftPeriod();
                int progressWidth = (int) (percentage * properties.getOverlayWidth());
                RenderSystem.disableRescaleNormal();
                RenderSystem.disableLighting();
                blit(matrixStack,
                        this.guiLeft + properties.getOverlayX(),
                        this.guiTop + properties.getOverlayY(),
                        0.0F, 0.0F,
                        progressWidth,
                        properties.getOverlayHeight(),
                        properties.getOverlayWidth(),
                        properties.getOverlayHeight());
                RenderSystem.enableLighting();
                RenderSystem.enableRescaleNormal();
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
