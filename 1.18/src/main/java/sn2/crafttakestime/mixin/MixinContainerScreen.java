package sn2.crafttakestime.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sn2.crafttakestime.ITimeCraftGuiContainer;
import sn2.crafttakestime.common.config.ContainerConfig;
import sn2.crafttakestime.common.core.CraftManager;

@Mixin(AbstractContainerScreen.class)
public abstract class MixinContainerScreen<T extends AbstractContainerMenu> extends Screen implements ITimeCraftGuiContainer {

    private static final Logger log = LogManager.getLogger(MixinContainerScreen.class);
    private final AbstractContainerScreen<T> self = (AbstractContainerScreen<T>) (Object) this;
    @Shadow
    protected int leftPos;
    @Shadow
    protected int topPos;
    private boolean finished = false;

    protected MixinContainerScreen(Component p_96550_) {
        super(p_96550_);
    }

    @Shadow
    protected abstract void slotClicked(Slot slotIn, int slotId, int mouseButton, ClickType type);

    @Override
    public void handleCraftFinished(Slot slotIn, int slotId) {
        this.finished = true;
        this.slotClicked(slotIn, slotId, 0, ClickType.PICKUP);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void crafttakestime$render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks,
                                      CallbackInfo ci) {
        try {
            CraftManager manager = CraftManager.getInstance();
            ContainerConfig containerConfig = manager.getCraftContainerConfig();

            // Skip if the properties are not set for this container
            if (!containerConfig.isEnabled()) {
                return;
            }

            // Skip if the player is not crafting
            if (!manager.isCrafting()) {
                return;
            }

            // Draw the crafting overlay
            if (containerConfig.isDrawCraftingOverlay() && manager.getCraftPeriod() > 0) {
                String[] namespaceAndPath = containerConfig.getOverlayTexture().split(":");
                ResourceLocation craftOverlay = new ResourceLocation(namespaceAndPath[0], namespaceAndPath[1]);
                RenderSystem.setShaderTexture(0, craftOverlay);
                float percentage = manager.getCurrentCraftTime() / manager.getCraftPeriod();
                int progressWidth = (int) (percentage * containerConfig.getOverlayWidth());
                blit(matrixStack,
                        this.leftPos + containerConfig.getOverlayX(),
                        this.topPos + containerConfig.getOverlayY(),
                        0.0F, 0.0F,
                        progressWidth,
                        containerConfig.getOverlayHeight(),
                        containerConfig.getOverlayWidth(),
                        containerConfig.getOverlayHeight());
            }
        } catch (Exception e) {
            log.error("Failed to draw the crafting overlay", e);
        }
    }

    @Inject(method = "slotClicked", at = @At("HEAD"), cancellable = true)
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

    @Inject(method = "onClose", at = @At("TAIL"))
    public void crafttakestime$onClose(CallbackInfo ci) {
        CraftManager.getInstance().unsetGuiContainer();
    }
}
