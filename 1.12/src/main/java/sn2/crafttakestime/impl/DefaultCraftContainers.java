package sn2.crafttakestime.impl;

import lombok.Data;
import sn2.crafttakestime.common.config.ContainerConfig;
import sn2.crafttakestime.common.slot.SlotRange;

import java.util.ArrayList;
import java.util.List;

@Data
public class DefaultCraftContainers {

    // Singleton
    private static DefaultCraftContainers INSTANCE;
    private final List<ContainerConfig> craftContainers = new ArrayList<>();

    private DefaultCraftContainers() {
        // minecraft inventory
        registerCraftContainer(ContainerConfig.builder()
                .containerName("minecraft:inventory")
                .ingredientSlots(SlotRange.fromString("1-4"))
                .guiContainerClassName("net.minecraft.client.gui.inventory.GuiInventory")
                .overlayTexture("crafttakestime:textures/gui/crafting_overlay_inventory.png")
                .overlayX(134)
                .overlayY(29)
                .overlayWidth(18)
                .overlayHeight(15)
                .build());
        // minecraft crafting table
        registerCraftContainer(ContainerConfig.builder()
                .containerName("minecraft:crafting_table")
                .guiContainerClassName("net.minecraft.client.gui.inventory.GuiCrafting")
                .build());
        // minecraft crafting table with fastbench mod
        registerCraftContainer(ContainerConfig.builder()
                .containerName("minecraft:crafting_table")
                .guiContainerClassName("shadows.fastbench.gui.GuiFastBench")
                .build());
        registerCraftContainer(ContainerConfig.builder()
                .containerName("cyclicmagic:block_workbench")
                .guiContainerClassName("com.lothrazar.cyclicmagic.compat.fastbench.GuiFastWorkbench")
                .build());
        registerCraftContainer(ContainerConfig.builder()
                .containerName("slabmachines:workbench_slab")
                .guiContainerClassName("com.mrbysco.slabmachines.gui.workbench.fast.GuiFastWorkbenchSlab")
                .build());
        registerCraftContainer(ContainerConfig.builder()
                .containerName("slabmachines:crafting_station_slab")
                .guiContainerClassName("com.mrbysco.slabmachines.gui.compat.tcon.GuiCraftingStationSlab")
                .build());
        registerCraftContainer(ContainerConfig.builder()
                .containerName("craftingstation:crafting_station")
                .guiContainerClassName("com.tfar.craftingstation.client.CraftingStationScreen")
                .build());
        registerCraftContainer(ContainerConfig.builder()
                .containerName("tconstruct:craftingstation")
                .guiContainerClassName("slimeknights.tconstruct.tools.common.client.GuiCraftingStation")
                .build());
        registerCraftContainer(ContainerConfig.builder()
                .containerName("tconstruct:stenciltable")
                .outputSlot(1)
                .ingredientSlots(SlotRange.fromString("0"))
                .guiContainerClassName("slimeknights.tconstruct.tools.common.client.GuiStencilTable")
                .overlayX(72)
                .overlayY(35)
                .build());
        registerCraftContainer(ContainerConfig.builder()
                .containerName("tconstruct:toolforge")
                .outputSlot(6)
                .ingredientSlots(SlotRange.fromString("0-5"))
                .guiContainerClassName("slimeknights.tconstruct.tools.common.client.GuiToolForge")
                .overlayY(38)
                .build());
        registerCraftContainer(ContainerConfig.builder()
                .containerName("sevendaystomine:workbench")
                .ingredientSlots(SlotRange.fromString("1-25"))
                .guiContainerClassName("nuparu.sevendaystomine.client.gui.inventory.GuiWorkbench")
                .overlayX(100)
                .overlayY(44)
                .overlayWidth(24)
                .overlayHeight(16)
                .build());
    }

    public static DefaultCraftContainers getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DefaultCraftContainers();
        }
        return INSTANCE;
    }

    public void registerCraftContainer(ContainerConfig config) {
        craftContainers.add(config);
    }
}
