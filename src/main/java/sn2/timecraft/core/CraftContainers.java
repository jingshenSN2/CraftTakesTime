package sn2.timecraft.core;

import lombok.Data;
import sn2.timecraft.util.RangeHelper;

import java.util.HashMap;
import java.util.Map;

@Data
public class CraftContainers {

    // Singleton
    private static CraftContainers INSTANCE;
    private final Map<String, CraftContainerProperties> craftContainers = new HashMap<>();

    private CraftContainers() {
        registerCraftContainer(CraftContainerProperties.builder()
                .containerName("minecraft:inventory")
                .ingredientSlots(RangeHelper.range(1, 4))
                .guiContainerClassName("net.minecraft.client.gui.inventory.GuiInventory")
                .overlayTexture("timecraft:textures/gui/crafting_overlay_inventory.png")
                .overlayX(134)
                .overlayY(29)
                .overlayWidth(18)
                .overlayHeight(15)
                .build());
        // minecraft crafting table
        registerCraftContainer(CraftContainerProperties.builder()
                .containerName("minecraft:crafting_table")
                .guiContainerClassName("net.minecraft.client.gui.inventory.GuiCrafting")
                .build());
        // minecraft crafting table with fastbench mod
        registerCraftContainer(CraftContainerProperties.builder()
                .containerName("minecraft:crafting_table")
                .guiContainerClassName("shadows.fastbench.gui.GuiFastBench")
                .build());
        registerCraftContainer(CraftContainerProperties.builder()
                .containerName("cyclicmagic:block_workbench")
                .guiContainerClassName("com.lothrazar.cyclicmagic.compat.fastbench.GuiFastWorkbench")
                .build());
        registerCraftContainer(CraftContainerProperties.builder()
                .containerName("slabmachines:workbench_slab")
                .guiContainerClassName("com.mrbysco.slabmachines.gui.workbench.fast.GuiFastWorkbenchSlab")
                .build());
        registerCraftContainer(CraftContainerProperties.builder()
                .containerName("slabmachines:crafting_station_slab")
                .guiContainerClassName("com.mrbysco.slabmachines.gui.compat.tcon.GuiCraftingStationSlab")
                .build());
        registerCraftContainer(CraftContainerProperties.builder()
                .containerName("craftingstation:crafting_station")
                .guiContainerClassName("com.tfar.craftingstation.client.CraftingStationScreen")
                .build());
        registerCraftContainer(CraftContainerProperties.builder()
                .containerName("sevendaystomine:workbench")
                .ingredientSlots(RangeHelper.range(1, 25))
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

    public CraftContainerProperties getCraftContainerProperties(String name) {
        return craftContainers.get(name);
    }
}
