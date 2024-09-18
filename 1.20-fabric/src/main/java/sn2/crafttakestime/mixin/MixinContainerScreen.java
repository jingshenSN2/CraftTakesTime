package sn2.crafttakestime.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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

@Mixin(HandledScreen.class)
public abstract class MixinContainerScreen<T extends ScreenHandler> extends Screen
        implements ITimeCraftGuiContainer, ScreenHandlerProvider<T> {

    private static final Logger log = LogManager.getLogger(MixinContainerScreen.class);
    private final HandledScreen<T> self = (HandledScreen<T>) (Object) this;
    @Shadow
    protected int x;
    @Shadow
    protected int y;
    private boolean finished = false;

    protected MixinContainerScreen(Text title) {
        super(title);
    }

    @Shadow
    protected abstract void onMouseClick(Slot slotIn, int slotId, int mouseButton, SlotActionType type);

    @Override
    public void handleCraftFinished(Slot slotIn, int slotId) {
        this.finished = true;
        this.onMouseClick(slotIn, slotId, 0, SlotActionType.PICKUP);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void crafttakestime$render(DrawContext context, int mouseX, int mouseY, float partialTicks,
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
                Identifier craftOverlay = new Identifier(namespaceAndPath[0], namespaceAndPath[1]);
                float percentage = manager.getCurrentCraftTime() / manager.getCraftPeriod();
                int progressWidth = (int) (percentage * containerConfig.getOverlayWidth());
                context.drawTexture(
                        craftOverlay,
                        this.x + containerConfig.getOverlayX(),
                        this.y + containerConfig.getOverlayY(),
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

    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at = @At("HEAD"), cancellable = true)
    public void crafttakestime$onMouseClick(Slot slot, int invSlot, int clickData, SlotActionType actionType,
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

    @Inject(method = "close", at = @At("TAIL"))
    public void crafttakestime$onClose(CallbackInfo ci) {
        CraftManager.getInstance().unsetGuiContainer();
    }
}
