package sn2.timecraft.core;

import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Data
public class CraftContainers {

    // Singleton
    private static CraftContainers INSTANCE;
    private final Map<String, CraftContainerProperties> craftContainers = new HashMap<>();
    private final Map<String, String> containerNameToGuiName = new HashMap<>();

    private CraftContainers() {
        registerCraftContainer(CraftContainerProperties.builder()
                .containerName("minecraft:inventory")
                .ingredientSlots(Arrays.asList(1, 2, 3, 4))
                .guiContainerClassName("net.minecraft.client.gui.inventory.GuiInventory")
                .overlayTexture("timecraft:textures/gui/crafting_overlay_inventory.png")
                .overlayX(134)
                .overlayY(29)
                .overlayWidth(18)
                .overlayHeight(15)
                .build());
        registerCraftContainer(CraftContainerProperties.builder()
                .containerName("minecraft:crafting_table")
                .guiContainerClassName("net.minecraft.client.gui.inventory.GuiCrafting")
                .build());
        registerCraftContainer(CraftContainerProperties.builder()
                .containerName("craftingstation:crafting_station")
                .guiContainerClassName("com.tfar.craftingstation.client.CraftingStationScreen")
                .build());
        registerCraftContainer(CraftContainerProperties.builder()
                .containerName("sevendaystomine:workbench")
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
        containerNameToGuiName.put(properties.getContainerName(), properties.getGuiContainerClassName());
    }

    public CraftContainerProperties getCraftContainerProperties(String name) {
        if (name.contains(":")) {
            // If the name is a block name, get the GUI class name first
            return craftContainers.get(containerNameToGuiName.get(name));
        }
        return craftContainers.get(name);
    }
}
