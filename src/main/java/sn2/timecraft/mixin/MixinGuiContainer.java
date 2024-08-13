package sn2.timecraft.mixin;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sn2.timecraft.ITimeCraftGuiContainer;
import sn2.timecraft.ITimeCraftPlayer;
import sn2.timecraft.core.CraftContainerProperties;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends GuiScreen implements ITimeCraftGuiContainer {

    private static final Logger log = LogManager.getLogger(MixinGuiContainer.class);
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

    @Override
    public void handleCraftFinished(Slot slotIn, int slotId) {
        this.finished = true;
        this.handleMouseClick(slotIn, slotId, 0, ClickType.PICKUP);
    }

    @Inject(method = "initGui*", at = @At("TAIL"))
    public void timecraft$initGui(CallbackInfo ci) {
        player = (ITimeCraftPlayer) this.mc.player;
        player.getCraftManager().setGuiContainer((GuiContainer) (Object) this);
    }

    @Inject(method = "drawScreen", at = @At("TAIL"))
    public void timecraft$drawScreen(int mouseX, int mouseY, float partialTicks,
                                     CallbackInfo ci) {
        if (player == null) {
            return;
        }

        CraftContainerProperties properties = player.getCraftManager().getCraftContainerProperties();

        // Skip if the properties are not set for this container
        if (properties == null) {
            return;
        }

        // Skip if the player is not crafting
        if (!player.getCraftManager().isCrafting()) {
            return;
        }

        // Draw the crafting overlay
        if (properties.isDrawCraftingOverlay() && player.getCraftManager().getCraftPeriod() > 0) {
            ResourceLocation craftOverlay = new ResourceLocation(properties.getOverlayTexture());
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(craftOverlay);
            float percentage = player.getCraftManager().getCurrentCraftTime() / player.getCraftManager().getCraftPeriod();
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
        if (player.getCraftManager().initCraft(invSlot)) {
            info.cancel();
        }
    }

    @Inject(method = "onGuiClosed", at = @At("HEAD"))
    public void timecraft$onClose(CallbackInfo info) {
        player = (ITimeCraftPlayer) this.mc.player;
        player.getCraftManager().unsetGuiContainer();
    }
}
