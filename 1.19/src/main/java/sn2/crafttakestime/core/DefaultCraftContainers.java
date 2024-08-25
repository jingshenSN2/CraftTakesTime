package sn2.crafttakestime.core;

import lombok.Data;
import sn2.crafttakestime.config.ContainerConfig;
import sn2.crafttakestime.util.SlotRange;

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
                .guiContainerClassName("net.minecraft.client.gui.screens.inventory.InventoryScreen")
                .overlayTexture("crafttakestime:textures/gui/crafting_overlay_inventory.png")
                .overlayX(134)
                .overlayY(29)
                .overlayWidth(18)
                .overlayHeight(15)
                .build());
        // minecraft inventory with Curios API
        registerCraftContainer(ContainerConfig.builder()
                .containerName("minecraft:inventory")
                .ingredientSlots(SlotRange.fromString("1-4"))
                .guiContainerClassName("top.theillusivec4.curios.client.gui.CuriosScreen")
                .overlayTexture("crafttakestime:textures/gui/crafting_overlay_inventory.png")
                .overlayX(134)
                .overlayY(29)
                .overlayWidth(18)
                .overlayHeight(15)
                .build());
        // minecraft crafting table
        registerCraftContainer(ContainerConfig.builder()
                .containerName("minecraft:crafting_table")
                .guiContainerClassName("net.minecraft.client.gui.screens.inventory.CraftingScreen")
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
