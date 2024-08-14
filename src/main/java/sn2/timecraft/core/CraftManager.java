package sn2.timecraft.core;

import lombok.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sn2.timecraft.ITimeCraftGuiContainer;
import sn2.timecraft.ITimeCraftPlayer;
import sn2.timecraft.sound.CraftingTickableSound;
import sn2.timecraft.sound.SoundEventRegistry;
import sn2.timecraft.util.CraftingSpeedHelper;

import java.util.List;

@Data
public class CraftManager {

    private static final Logger log = LogManager.getLogger(CraftManager.class);

    private EntityPlayer player;
    private GuiContainer currentGuiContainer;

    private boolean crafting = false;
    private int waitCounter = 0;
    private float currentCraftTime = 0;
    private float craftPeriod = 0;
    private ItemStack resultStack;

    public CraftManager() {
    }

    public void setGuiContainer(GuiContainer guiContainer) {
        this.currentGuiContainer = guiContainer;
    }

    public void unsetGuiContainer() {
        this.currentGuiContainer = null;
        this.stopCraft();
    }

    public CraftContainerProperties getCraftContainerProperties() {
        if (this.currentGuiContainer == null) {
            return null;
        }
        return CraftContainers.getInstance()
                .getCraftContainerProperties(this.currentGuiContainer.getClass().getName());
    }

    public boolean initCraft(int invSlot) {
        CraftContainerProperties properties = this.getCraftContainerProperties();
        log.debug("Inv slot {}, gui class {}, properties {}",
                invSlot, this.getCurrentGuiContainer().getClass().getName(), properties);

        if (properties == null) {
            return false;
        }

        // Check if clicking the result slot
        int resultSlot = properties.getResultSlot();
        if (invSlot != resultSlot) {
            stopCraft();
            return false;
        }

        // Check if the result slot is empty
        if (this.currentGuiContainer.inventorySlots.getSlot(resultSlot).getStack().isEmpty()) {
            return false;
        }

        // Check if the player is already crafting
        if (!isCrafting()) {
            this.craftPeriod = Ingredients.getInstance().getCraftingDifficultyFromIngredients(
                    Ingredients.getInstance().getIngredientItems(
                            this.currentGuiContainer.inventorySlots, properties.getIngredientSlots()),
                    5F, 1F, properties.getContainerMultiplier());
            startCraft();
        }
        return true;
    }

    public void startCraft() {
        this.crafting = true;
        this.currentCraftTime = 0;
        if (craftPeriod >= 10F) {
            Minecraft.getMinecraft().getSoundHandler().playSound(
                    new CraftingTickableSound(
                            (ITimeCraftPlayer) this.player, this.player.getPosition()));
        }
    }

    public void stopCraft() {
        this.crafting = false;
        this.currentCraftTime = 0;
    }

    public void tick() {
        CraftContainerProperties properties = this.getCraftContainerProperties();
        if (properties == null) {
            return;
        }
        if (this.isCrafting()) {
            int resultSlot = properties.getResultSlot();
            List<Integer> ingredientSlots = properties.getIngredientSlots();

            ItemStack resultStack = this.currentGuiContainer
                    .inventorySlots.getSlot(resultSlot).getStack();

            // Stop crafting if the result slot is empty
            if (resultStack.isEmpty()) {
                if (waitCounter < 5) {
                    waitCounter++;
                } else {
                    waitCounter = 0;
                    this.stopCraft();
                }
                return;
            }

            ItemStack cursorStack = this.player.inventory.getItemStack();
            if (cursorStack.getItem() != Items.AIR) {
                if (!cursorStack.isItemEqual(resultStack)
                        || cursorStack.getCount() + resultStack.getCount() > cursorStack.getMaxStackSize()) {
                    this.stopCraft();
                    return;
                }
            }
            if (this.getCurrentCraftTime() < this.getCraftPeriod()) {
                this.currentCraftTime += CraftingSpeedHelper.getCraftingSpeed(this.player);
            } else if (this.getCurrentCraftTime() >= this.getCraftPeriod()) {
                this.player.playSound(SoundEventRegistry.finishSound, 0.1F, 1f);

                // Record the old recipe before picking up the result item
                List<Item> oldRecipe = Ingredients.getInstance().getIngredientItems(
                        this.currentGuiContainer.inventorySlots, ingredientSlots);

                ((ITimeCraftGuiContainer) this.currentGuiContainer).handleCraftFinished(
                        this.getCurrentGuiContainer().inventorySlots.getSlot(resultSlot), resultSlot);

                // Compare the old recipe with the new recipe
                List<Item> newRecipe = Ingredients.getInstance().getIngredientItems(
                        this.getCurrentGuiContainer().inventorySlots, ingredientSlots);
                if (!oldRecipe.equals(newRecipe)) {
                    this.stopCraft();
                } else {
                    waitCounter = 0;
                    this.startCraft();
                }
            }
        }
    }
}
