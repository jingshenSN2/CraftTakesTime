package sn2.crafttakestime.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import java.util.List;

/**
 * Adapter for menus with a virtual recipe result. All operations run on the game thread.
 * completeOne must execute exactly one recipe and place its output on the carried stack,
 * retaining the menu's remainder/event behavior.
 * Output may only change when the menu is accessed through the guarded click path.
 */
public interface CraftingAdapter {
    boolean supports(AbstractContainerMenu menu);
    int resultSlot(AbstractContainerMenu menu);
    List<ItemStack> inputs(AbstractContainerMenu menu);
    String menuId(AbstractContainerMenu menu);
    default boolean canCraft(ServerPlayer player, AbstractContainerMenu menu) { return true; }
    void completeOne(ServerPlayer player, AbstractContainerMenu menu);
}
