package sn2.timecraft.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CraftContainers {

    // Singleton
    private static CraftContainers INSTANCE;
    private final Map<String, CraftContainerProperties> craftContainers = new HashMap<>();

    private CraftContainers() {
        // minecraft:inventory
        registerCraftContainer(CraftContainerProperties.builder()
                .ingredientSlots(Arrays.asList(1, 2, 3, 4))
                .guiContainerClassName("net.minecraft.client.gui.inventory.GuiInventory")
                .overlayTexture("timecraft:textures/gui/crafting_overlay_inventory.png")
                .overlayX(134)
                .overlayY(29)
                .overlayWidth(18)
                .overlayHeight(15)
                .build());
        // minecraft:crafting_table
        registerCraftContainer(CraftContainerProperties.builder()
                .guiContainerClassName("net.minecraft.client.gui.inventory.GuiCrafting")
                .build());
        // craftingstation:crafting_station
        registerCraftContainer(CraftContainerProperties.builder()
                .guiContainerClassName("com.tfar.craftingstation.client.CraftingStationScreen")
                .build());
        // sevendaystomine:workbench
        registerCraftContainer(CraftContainerProperties.builder()
                .resultSlot(0)
                .ingredientSlots(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
                        , 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25))
                .guiContainerClassName("nuparu.sevendaystomine.client.gui.inventory.GuiWorkbench")
                .overlayX(100)
                .overlayY(44)
                .overlayWidth(24)
                .overlayHeight(16)
                .build());
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
