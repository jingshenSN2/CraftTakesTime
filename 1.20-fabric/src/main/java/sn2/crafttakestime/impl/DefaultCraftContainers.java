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
                .guiContainerClassName("net.minecraft.client.gui.screen.ingame.InventoryScreen")
                .ingredientSlots(SlotRange.fromString("1-4"))
                .overlayTexture("crafttakestime:textures/gui/crafting_overlay_inventory.png")
                .overlayX(134)
                .overlayY(29)
                .overlayWidth(18)
                .overlayHeight(15)
                .build());
        registerCraftContainer(ContainerConfig.builder()
                .containerName("minecraft:inventory")
                .guiContainerClassName("net.minecraft.class_490")
                .ingredientSlots(SlotRange.fromString("1-4"))
                .overlayTexture("crafttakestime:textures/gui/crafting_overlay_inventory.png")
                .overlayX(134)
                .overlayY(29)
                .overlayWidth(18)
                .overlayHeight(15)
                .build());
        // minecraft inventory with Curios API
        registerCraftContainer(ContainerConfig.builder()
                .containerName("minecraft:inventory")
                .guiContainerClassName("top.theillusivec4.curios.client.gui.CuriosScreen")
                .ingredientSlots(SlotRange.fromString("1-4"))
                .overlayTexture("crafttakestime:textures/gui/crafting_overlay_inventory.png")
                .overlayX(134)
                .overlayY(29)
                .overlayWidth(18)
                .overlayHeight(15)
                .build());
        // minecraft crafting table
        registerCraftContainer(ContainerConfig.builder()
                .containerName("minecraft:crafting_table")
                .guiContainerClassName("net.minecraft.client.gui.screen.ingame.CraftingScreen")
                .build());
        registerCraftContainer(ContainerConfig.builder()
                .containerName("minecraft:crafting_table")
                .guiContainerClassName("net.minecraft.class_479")
                .build());
        // minecraft smithing table
        registerCraftContainer(ContainerConfig.builder()
                .containerName("minecraft:smithing_table")
                .guiContainerClassName("net.minecraft.client.gui.screen.ingame.SmithingScreen")
                .ingredientSlots(SlotRange.fromString("0-2"))
                .outputSlot(3)
                .overlayX(67)
                .overlayY(49)
                .build());
        registerCraftContainer(ContainerConfig.builder()
                .containerName("minecraft:smithing_table")
                .guiContainerClassName("net.minecraft.class_4895")
                .ingredientSlots(SlotRange.fromString("0-2"))
                .outputSlot(3)
                .overlayX(67)
                .overlayY(49)
                .build());
        // minecraft anvil screen
        registerCraftContainer(ContainerConfig.builder()
                .containerName("minecraft:anvil")
                .guiContainerClassName("net.minecraft.client.gui.screen.ingame.AnvilScreen")
                .ingredientSlots(SlotRange.fromString("0-1"))
                .outputSlot(2)
                .overlayX(101)
                .overlayY(48)
                .build());
        registerCraftContainer(ContainerConfig.builder()
                .containerName("minecraft:anvil")
                .guiContainerClassName("net.minecraft.class_471")
                .ingredientSlots(SlotRange.fromString("0-1"))
                .outputSlot(2)
                .overlayX(101)
                .overlayY(48)
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
