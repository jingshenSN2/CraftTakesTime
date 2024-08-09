package sn2.timecraft.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CraftContainers {

    private static final CraftContainerProperties PLAYER_INVENTORY =
            CraftContainerProperties.builder()
                    .resultSlot(0)
                    .ingredientSlots(Arrays.asList(1, 2, 3, 4))
                    .guiContainerClassName("net.minecraft.client.gui.inventory.GuiInventory")
                    .drawCraftingOverlay(true)
                    .overlayX(134)
                    .overlayY(29)
                    .overlayWidth(18)
                    .overlayHeight(15)
                    .build();

    private static final CraftContainerProperties CRAFTING_TABLE =
            CraftContainerProperties.builder()
                    .resultSlot(0)
                    .ingredientSlots(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                    .guiContainerClassName("net.minecraft.client.gui.inventory.GuiCrafting")
                    .drawCraftingOverlay(true)
                    .overlayX(89)
                    .overlayY(35)
                    .overlayWidth(24)
                    .overlayHeight(17)
                    .build();

    private static final CraftContainerProperties CRAFT_STATION =
            CraftContainerProperties.builder()
                    .resultSlot(0)
                    .ingredientSlots(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                    .guiContainerClassName("com.tfar.craftingstation.client.CraftingStationScreen")
                    .drawCraftingOverlay(false)
                    .build();

    // Singleton
    private static CraftContainers INSTANCE;
    private final Map<String, CraftContainerProperties> craftContainers = new HashMap<>();

    private CraftContainers() {
        registerCraftContainer(PLAYER_INVENTORY);
        registerCraftContainer(CRAFTING_TABLE);
    }

    public static CraftContainers getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CraftContainers();
        }
        return INSTANCE;
    }

    public void registerCraftContainer(CraftContainerProperties properties) {
        craftContainers.put(properties.getGuiContainerClassName(), properties);
    }

    public CraftContainerProperties getCraftContainerProperties(String guiContainerClassName) {
        return craftContainers.get(guiContainerClassName);
    }
}
