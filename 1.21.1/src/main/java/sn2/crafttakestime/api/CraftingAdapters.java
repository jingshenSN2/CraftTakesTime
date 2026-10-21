package sn2.crafttakestime.api;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public final class CraftingAdapters {
    private static final List<CraftingAdapter> ADAPTERS = new ArrayList<>();
    static {
        register(new CraftingAdapter() {
            public boolean supports(AbstractContainerMenu menu) {
                return menu.slots.stream().filter(slot -> slot instanceof ResultSlot).count() == 1
                    && menu.slots.stream().anyMatch(slot -> slot.container instanceof CraftingContainer);
            }
            public int resultSlot(AbstractContainerMenu menu) {
                return menu.slots.stream().filter(slot -> slot instanceof ResultSlot).findFirst().orElseThrow().index;
            }
            public List<ItemStack> inputs(AbstractContainerMenu menu) {
                return menu.slots.stream().filter(slot -> slot.container instanceof CraftingContainer)
                    .map(slot -> slot.getItem().copy()).toList();
            }
            public String menuId(AbstractContainerMenu menu) {
                return menu instanceof InventoryMenu ? "minecraft:inventory" : BuiltInRegistries.MENU.getKey(menu.getType()).toString();
            }
            public void completeOne(ServerPlayer player, AbstractContainerMenu menu) {
                menu.clicked(resultSlot(menu), 0, ClickType.PICKUP, player);
            }
        });
    }
    static {
        register(new CraftingAdapter() {
            public boolean supports(AbstractContainerMenu menu) {
                return menu instanceof AnvilMenu || menu instanceof SmithingMenu || menu instanceof StonecutterMenu;
            }
            public int resultSlot(AbstractContainerMenu menu) {
                return menu instanceof ItemCombinerMenu combiner ? combiner.getResultSlot() : StonecutterMenu.RESULT_SLOT;
            }
            public List<ItemStack> inputs(AbstractContainerMenu menu) {
                return java.util.stream.IntStream.range(0, resultSlot(menu)).mapToObj(i -> menu.getSlot(i).getItem().copy()).toList();
            }
            public String menuId(AbstractContainerMenu menu) {
                return BuiltInRegistries.MENU.getKey(menu.getType()).toString();
            }
            public boolean canCraft(ServerPlayer player, AbstractContainerMenu menu) {
                return !(menu instanceof AnvilMenu anvil) || (anvil.getCost() > 0
                    && (player.hasInfiniteMaterials() || player.experienceLevel >= anvil.getCost()));
            }
            public void completeOne(ServerPlayer player, AbstractContainerMenu menu) {
                menu.clicked(resultSlot(menu), 0, ClickType.PICKUP, player);
            }
        });
    }
    private CraftingAdapters() {}
    /** Register on both physical sides during setup, before players open menus. Last registration takes priority. */
    public static synchronized void register(CraftingAdapter adapter) { ADAPTERS.add(0, adapter); }
    public static CraftingAdapter find(AbstractContainerMenu menu) {
        for (CraftingAdapter adapter : ADAPTERS) if (adapter.supports(menu)) return adapter;
        return null;
    }
}
