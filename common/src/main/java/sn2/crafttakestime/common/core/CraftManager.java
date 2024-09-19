package sn2.crafttakestime.common.core;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sn2.crafttakestime.common.config.ConfigLoader;
import sn2.crafttakestime.common.config.ContainerConfig;
import sn2.crafttakestime.common.config.CraftConfig;
import sn2.crafttakestime.common.slot.SlotRange;

@Data
public class CraftManager {

    private static final Logger log = LogManager.getLogger(CraftManager.class);
    private static final float BASE_CRAFTING_TIME_PER_ITEM = 20F;
    private static final ContainerConfig DISABLED_CONTAINER = ContainerConfig.builder().enabled(false).build();

    // Singleton
    private static CraftManager INSTANCE;
    private MinecraftAdapter minecraftAdapter;
    private CraftConfig config;
    private boolean crafting = false;
    private int waitCounter = 0;
    private float currentCraftTime = 0;
    private float craftPeriod = 0;

    private CraftManager() {
    }

    public static CraftManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CraftManager();
        }
        return INSTANCE;
    }

    public void loadConfig() {
        ConfigLoader configLoader = new ConfigLoader(
                this.minecraftAdapter.getConfigPath(),
                this.minecraftAdapter.getDefaultsCraftContainers());
        config = configLoader.loadConfig();
    }

    public void unsetGuiContainer() {
        this.minecraftAdapter.setContainerScreen(null);
        this.stopCraft();
    }

    public ContainerConfig getCraftContainerConfig() {
        Object containerScreen = this.minecraftAdapter.getContainerScreen();
        if (containerScreen == null) {
            return DISABLED_CONTAINER;
        }
        String guiClassName = this.minecraftAdapter.getContainerScreenClassName();
        return config.getContainers().stream().filter(container ->
                        container.getGuiContainerClassName().equals(guiClassName))
                .findFirst().orElse(DISABLED_CONTAINER);
    }

    public boolean initCraft(Object gui, int invSlot) {
        this.minecraftAdapter.setContainerScreen(gui);
        ContainerConfig containerConfig = this.getCraftContainerConfig();
        if (config.isDebug()) {
            log.info("Inv slot {}, gui class {}, containerConfig {}",
                    invSlot,
                    this.minecraftAdapter.getContainerScreenClassName(),
                    containerConfig);
        }

        if (!containerConfig.isEnabled()) {
            return false;
        }

        // Check if clicking the result slot
        int outputSlot = containerConfig.getOutputSlot();
        if (invSlot != outputSlot) {
            stopCraft();
            return false;
        }

        // Check if the result slot is empty
        if (this.minecraftAdapter.isSlotEmpty(outputSlot)) {
            return false;
        }

        // Check if the player is already crafting
        if (!isCrafting()) {
            craftPeriod = getCraftingTime(outputSlot, containerConfig.getIngredientSlots(), containerConfig);

            if (craftPeriod >= 10F && config.isEnableCraftingSound()) {
                this.minecraftAdapter.playCraftingSound();
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
        if (!this.minecraftAdapter.hasPlayer()) {
            return;
        }

        ContainerConfig containerConfig = this.getCraftContainerConfig();
        if (!containerConfig.isEnabled()) {
            return;
        }

        if (this.isCrafting()) {
            int outputSlot = containerConfig.getOutputSlot();
            SlotRange ingredientSlots = containerConfig.getIngredientSlots();

            // Stop crafting if the result slot is empty
            if (this.minecraftAdapter.isSlotEmpty(outputSlot)) {
                if (waitCounter < 5) {
                    waitCounter++;
                } else {
                    waitCounter = 0;
                    this.stopCraft();
                }
                return;
            }

            if (this.minecraftAdapter.shouldStopCrafting(outputSlot)) {
                this.stopCraft();
                return;
            }
            if (this.getCurrentCraftTime() < this.getCraftPeriod()) {
                this.currentCraftTime += this.minecraftAdapter.getCraftingSpeed();
            } else if (this.getCurrentCraftTime() >= this.getCraftPeriod()) {
                if (config.isEnableCraftingSound()) {
                    this.minecraftAdapter.playFinishSound();
                }

                // Record the old recipe before picking up the result item
                Object oldRecipe = this.minecraftAdapter.getItems(ingredientSlots);

                // Pick up the result item
                this.minecraftAdapter.handleCraftFinished(outputSlot);

                // Compare the old recipe with the new recipe
                Object newRecipe = this.minecraftAdapter.getItems(ingredientSlots);

                if (!oldRecipe.equals(newRecipe)) {
                    this.stopCraft();
                } else {
                    waitCounter = 0;
                    this.startCraft();
                }
            }
        }
    }

    private float getCraftingTime(int outputSlot,
                                  SlotRange ingredientSlots,
                                  ContainerConfig containerConfig) {
        // Global multiplier
        float globalMultiplier = config.getGlobalCraftingTimeMultiplier();

        // Container multiplier
        float containerMultiplier = 1F;
        if (containerConfig != null) {
            containerMultiplier = containerConfig.getCraftingTimeMultiplier();
        }

        // Ingredient multiplier
        float ingredientDifficulty = 0F;
        for (int idx : ingredientSlots) {
            if (this.minecraftAdapter.isSlotEmpty(idx)) {
                // Skip empty slots
                continue;
            }
            ItemRegistry registry = this.minecraftAdapter.getSlotItemRegistry(idx);
            if (registry == null) {
                continue;
            }
            float modMultiplier = config.getIngredientConfig().getModCraftingTimeMultipliers()
                    .getOrDefault(registry.getModId(), 1F);
            float itemMultiplier = config.getIngredientConfig().getItemCraftingTimeMultipliers()
                    .getOrDefault(registry.getName(), 1F);
            ingredientDifficulty += modMultiplier * itemMultiplier;
        }

        // Output multiplier
        ItemRegistry outputRegistry = this.minecraftAdapter.getSlotItemRegistry(outputSlot);
        float outputMultiplier = 1F;
        if (outputRegistry != null) {
            float modMultiplier = config.getOutputConfig().getModCraftingTimeMultipliers()
                    .getOrDefault(outputRegistry.getModId(), 1F);
            float itemMultiplier = config.getOutputConfig().getItemCraftingTimeMultipliers()
                    .getOrDefault(outputRegistry.getName(), 1F);
            outputMultiplier = modMultiplier * itemMultiplier;
        }

        // Final crafting time
        return BASE_CRAFTING_TIME_PER_ITEM * ingredientDifficulty * outputMultiplier
                * containerMultiplier * globalMultiplier;
    }
}
