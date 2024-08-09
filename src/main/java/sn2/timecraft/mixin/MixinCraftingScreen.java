package sn2.timecraft.mixin;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sn2.timecraft.ITimeCraftPlayer;
import sn2.timecraft.util.CraftingDifficultyHelper;

import java.util.ArrayList;

@Mixin(GuiCrafting.class)
public abstract class MixinCraftingScreen extends GuiContainer implements IRecipeShownListener {

    private static final ResourceLocation CRAFT_OVERLAY_TEXTURE = new ResourceLocation(
            "timecraft:textures/gui/crafting_table.png");
    @Shadow
    private GuiRecipeBook recipeBookGui;
    private ITimeCraftPlayer player;

    public MixinCraftingScreen(InventoryPlayer playerInv, World worldIn, BlockPos blockPosition) {
        super(new ContainerWorkbench(playerInv, worldIn, blockPosition));
        this.recipeBookGui = new GuiRecipeBook();
    }

    @Inject(method = "drawGuiContainerBackgroundLayer", at = @At("TAIL"), cancellable = true)
    protected void timecraft$drawGuiContainerBackgroundLayer(float delta, int mouseX, int mouseY, CallbackInfo info) {
        this.player = (ITimeCraftPlayer) this.mc.player;
        this.mc.getTextureManager().bindTexture(CRAFT_OVERLAY_TEXTURE);
        int i = this.guiLeft;
        int j = (this.height - this.ySize) / 2;
        if (player.isCrafting() && player.getCraftPeriod() > 0) {
            int l = (int) (player.getCraftTime() * 24.0F / player.getCraftPeriod());
            MixinCraftingScreen.drawModalRectWithCustomSizedTexture(i + 89, j + 35, 0, 0, l + 1, 16, 24, 17);
        }
    }

    @Inject(method = "updateScreen", at = @At("TAIL"), cancellable = true)
    public void timecraft$updateScreen(CallbackInfo info) {
        this.player = (ITimeCraftPlayer) this.mc.player;
        ItemStack resultStack = this.inventorySlots.getSlot(0).getStack();
        boolean finished = player.tick(resultStack);
        if (finished) {
            ArrayList<Item> old_recipe = CraftingDifficultyHelper.getItemFromMatrix(this.inventorySlots, true);
            super.handleMouseClick(this.inventorySlots.getSlot(0), 0, 0, ClickType.PICKUP);
            ArrayList<Item> new_recipe = CraftingDifficultyHelper.getItemFromMatrix(this.inventorySlots, true);
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
        if (invSlot > 0 && invSlot < 10) {
            player.stopCraft();
        }
        if (invSlot == 0) {
            if (!player.isCrafting()) {
                player.setCraftPeriod(CraftingDifficultyHelper.getCraftingDifficultyFromMatrix(this.inventorySlots, true));
                player.startCraft();
            }
            info.cancel();
        }
    }

    @Inject(method = "onGuiClosed", at = @At("HEAD"), cancellable = true)
    public void timecraft$onClose(CallbackInfo info) {
        player.stopCraft();
    }
}
