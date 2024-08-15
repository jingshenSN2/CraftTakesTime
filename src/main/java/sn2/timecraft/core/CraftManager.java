package sn2.timecraft.core;

import lombok.Data;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sn2.timecraft.ITimeCraftGuiContainer;
import sn2.timecraft.config.ContainerConfig;
import sn2.timecraft.config.CraftConfig;
import sn2.timecraft.sound.CraftingTickableSound;
import sn2.timecraft.sound.SoundEventRegistry;
import sn2.timecraft.util.CraftingSpeedHelper;

import java.util.ArrayList;
import java.util.List;

@Data
public class CraftManager {

    private static final Logger log = LogManager.getLogger(CraftManager.class);
    private static final float BASE_CRAFTING_TIME_PER_ITEM = 20F;
    // Singleton
    private static CraftManager INSTANCE;
    private CraftConfig config;
    private GuiContainer currentGuiContainer;
    private boolean crafting = false;
    private int waitCounter = 0;
    private float currentCraftTime = 0;
    private float craftPeriod = 0;
    private ItemStack resultStack;

    private CraftManager() {
    }

    public static CraftManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CraftManager();
        }
        return INSTANCE;
    }

    public void unsetGuiContainer() {
        this.currentGuiContainer = null;
        this.stopCraft();
    }

    public CraftContainerProperties getCraftContainerProperties() {
        if (this.currentGuiContainer == null) {
            return null;
        }
        CraftContainerProperties properties = CraftContainers.getInstance()
                .getCraftContainerProperties(this.currentGuiContainer.getClass().getName());
        if (properties == null ||
                (config.getContainers().get(properties.getContainerName()) != null &&
                        !config.getContainers().get(properties.getContainerName()).isEnabled())) {
            return null;
        }
        return properties;
    }

    public boolean initCraft(GuiContainer gui, int invSlot) {
        this.setCurrentGuiContainer(gui);
        CraftContainerProperties properties = this.getCraftContainerProperties();
        if (config.isDebug()) {
            log.info("Inv slot {}, gui class {}, properties {}",
                    invSlot, this.getCurrentGuiContainer().getClass().getName(), properties);
        }

        if (properties == null) {
            return false;
        }

        // Check if clicking the result slot
        int outputSlot = properties.getOutputSlot();
        if (invSlot != outputSlot) {
            stopCraft();
            return false;
        }

        // Check if the result slot is empty
        if (this.currentGuiContainer.inventorySlots.getSlot(outputSlot).getStack().isEmpty()) {
            return false;
        }

        // Check if the player is already crafting
        if (!isCrafting()) {
            craftPeriod = getCraftingTime(
                    this.currentGuiContainer.inventorySlots, outputSlot, properties.getIngredientSlots(), properties);

            if (craftPeriod >= 10F && config.isEnableCraftingSound()) {
                EntityPlayer player = Minecraft.getMinecraft().player;
                if (player != null) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(
                            new CraftingTickableSound(player.getPosition()));
                }
            }
            startCraft();
        }
        return true;
    }

    private void startCraft() {
        this.crafting = true;
        this.currentCraftTime = 0;
    }

    private void stopCraft() {
        this.crafting = false;
        this.currentCraftTime = 0;
    }

    public void tick() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null) {
            return;
        }

        CraftContainerProperties properties = this.getCraftContainerProperties();
        if (properties == null) {
            return;
        }

        if (this.isCrafting()) {
            int outputSlot = properties.getOutputSlot();
            List<Integer> ingredientSlots = properties.getIngredientSlots();

            ItemStack resultStack = this.currentGuiContainer
                    .inventorySlots.getSlot(outputSlot).getStack();

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

            ItemStack cursorStack = player.inventory.getItemStack();
            if (cursorStack.getItem() != Items.AIR) {
                if (!cursorStack.isItemEqual(resultStack)
                        || cursorStack.getCount() + resultStack.getCount() > cursorStack.getMaxStackSize()) {
                    this.stopCraft();
                    return;
                }
            }
            if (this.getCurrentCraftTime() < this.getCraftPeriod()) {
                this.currentCraftTime += CraftingSpeedHelper.getCraftingSpeed(player);
            } else if (this.getCurrentCraftTime() >= this.getCraftPeriod()) {
                if (config.isEnableCraftingSound()) {
                    player.playSound(SoundEventRegistry.finishSound, 0.1F, 1f);
                }

                // Record the old recipe before picking up the result item
                List<Item> oldRecipe = getIngredientItems(
                        this.getCurrentGuiContainer().inventorySlots, ingredientSlots);

                ((ITimeCraftGuiContainer) this.currentGuiContainer).handleCraftFinished(
                        this.getCurrentGuiContainer().inventorySlots.getSlot(outputSlot), outputSlot);

                // Compare the old recipe with the new recipe
                List<Item> newRecipe = getIngredientItems(
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

    private List<Item> getIngredientItems(Container handler, List<Integer> ingredientSlots) {
        List<Item> items = new ArrayList<Item>();
        for (int i : ingredientSlots) {
            items.add(handler.getSlot(i).getStack().getItem());
        }
        log.info("Ingredient items: {}", items);
        return items;
    }

    private Item getOutputItem(Container handler, int outputSlot) {
        return handler.getSlot(outputSlot).getStack().getItem();
    }

    private float getCraftingTime(Container handler,
                                  int outputSlot,
                                  List<Integer> ingredientSlots,
                                  CraftContainerProperties properties) {
        // Global multiplier
        float globalMultiplier = config.getGlobalCraftingTimeMultiplier();

        // Container multiplier
        float containerMultiplier = 1F;
        ContainerConfig containerConfig = config.getContainers().get(properties.getContainerName());
        if (containerConfig != null) {
            containerMultiplier = containerConfig.getCraftingTimeMultiplier();
        }

        // Ingredient multiplier
        List<Item> ingredients = getIngredientItems(handler, ingredientSlots);
        float ingredientDifficulty = 0F;
        for (Item item : ingredients) {
            if (item == Items.AIR) {
                continue;
            }
            ResourceLocation registry = item.getRegistryName();
            if (registry == null) {
                continue;
            }
            float modMultiplier = config.getIngredientConfig().getModCraftingTimeMultipliers()
                    .getOrDefault(registry.getNamespace(), 1F);
            float itemMultiplier = config.getIngredientConfig().getItemCraftingTimeMultipliers()
                    .getOrDefault(registry.toString(), 1F);
            ingredientDifficulty += modMultiplier * itemMultiplier;
        }

        // Output multiplier
        Item outputItem = getOutputItem(handler, outputSlot);
        ResourceLocation outputRegistry = outputItem.getRegistryName();
        float outputMultiplier = 1F;
        if (outputRegistry != null) {
            float modMultiplier = config.getOutputConfig().getModCraftingTimeMultipliers()
                    .getOrDefault(outputRegistry.getNamespace(), 1F);
            float itemMultiplier = config.getOutputConfig().getItemCraftingTimeMultipliers()
                    .getOrDefault(outputRegistry.toString(), 1F);
            outputMultiplier = modMultiplier * itemMultiplier;
        }

        // Final crafting time
        return BASE_CRAFTING_TIME_PER_ITEM * ingredientDifficulty * outputMultiplier
                * containerMultiplier * globalMultiplier;
    }
}
