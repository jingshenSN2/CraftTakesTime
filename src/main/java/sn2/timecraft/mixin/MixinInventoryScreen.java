package sn2.timecraft.mixin;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sn2.timecraft.ITimeCraftPlayer;
import sn2.timecraft.util.CraftingDifficultyHelper;

import java.util.ArrayList;

@Mixin(GuiInventory.class)
public abstract class MixinInventoryScreen extends InventoryEffectRenderer implements IRecipeShownListener {

    private static final ResourceLocation CRAFT_OVERLAY_TEXTURE = new ResourceLocation(
            "timecraft:textures/gui/inventory.png");
    private ITimeCraftPlayer player;

    public MixinInventoryScreen(EntityPlayer player) {
        super(player.inventoryContainer);
        this.allowUserInput = true;
    }

    @Inject(method = "drawGuiContainerBackgroundLayer", at = @At("TAIL"), cancellable = true)
    protected void timecraft$drawGuiContainerBackgroundLayer(float delta, int mouseX, int mouseY, CallbackInfo info) {
        this.player = (ITimeCraftPlayer) this.mc.player;
        this.mc.getTextureManager().bindTexture(CRAFT_OVERLAY_TEXTURE);
        int i = this.guiLeft;
        int j = this.guiTop;
        if (player.isCrafting() && player.getCraftPeriod() > 0) {
            int l = (int) (player.getCraftTime() * 17.0F / player.getCraftPeriod());
            MixinInventoryScreen.drawModalRectWithCustomSizedTexture(i + 134, j + 29, 0, 0, l + 1, 14, 18, 15);
        }
    }

    @Inject(method = "updateScreen", at = @At("TAIL"), cancellable = true)
    public void timecraft$updateScreen(CallbackInfo info) {
        this.player = (ITimeCraftPlayer) this.mc.player;
        ItemStack resultStack = this.inventorySlots.getSlot(0).getStack();
        boolean finished = player.tick(resultStack);
        if (finished) {
            ArrayList<Item> old_recipe = CraftingDifficultyHelper.getItemFromMatrix(this.inventorySlots, false);
            super.handleMouseClick(this.inventorySlots.getSlot(0), 0, 0, ClickType.PICKUP);
            ArrayList<Item> new_recipe = CraftingDifficultyHelper.getItemFromMatrix(this.inventorySlots, false);
            if (old_recipe.equals(new_recipe))
                player.startCraft();
            else
                player.stopCraft();
        }
    }

    @Inject(method = "handleMouseClick", at = @At("HEAD"), cancellable = true)
    public void timecraft$handleMouseClick(Slot slot, int invSlot, int clickData, ClickType actionType,
                                           CallbackInfo info) {
        if (slot != null) {
            invSlot = slot.slotNumber;
        }
        if (invSlot > 0 && invSlot < 5) {
            player.stopCraft();
        }
        if (invSlot == 0) {
            if (!player.isCrafting()) {
                player.setCraftPeriod(CraftingDifficultyHelper.getCraftingDifficultyFromMatrix(this.inventorySlots, false));
                player.startCraft();
            }
            info.cancel();
        }
    }

    @Inject(method = "onGuiClosed", at = @At("HEAD"), cancellable = true)
    public void timecraft$onClose(CallbackInfo info) {
        if (player != null) {
            player.stopCraft();
        }
    }
}
