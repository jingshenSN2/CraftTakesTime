package sn2.timecraft.util;

import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import sn2.timecraft.TimeCraft;

import java.util.ArrayList;
import java.util.List;

public class CraftingDifficultyHelper {

    public static float getCraftingDifficultyFromMatrix(Container container, boolean is_craft_table) {
        ArrayList<Slot> slots = new ArrayList<Slot>();
        int index = is_craft_table ? 10 : 5;
        for (int i = 1; i < index; i++) {
            slots.add(container.getSlot(i));
        }
        return getCraftingDifficultyFromMatrix(slots);
    }

    public static float getCraftingDifficultyFromMatrix(ArrayList<Slot> slots) {
        float basic_difficulty = 5F;
        float item_difficulty = 0F;
        for (Slot s : slots) {
            Item item = s.getStack().getItem();
            if (item == Items.AIR)
                continue;
            item_difficulty += TimeCraft.map.getDifficulty(item);
        }
        return basic_difficulty + item_difficulty;
    }

    public static float getCraftingDifficultyFromIngredients(
            List<Item> items, float basicDifficulty, float itemDifficultyMultiplier) {
        float itemDifficulty = 0F;
        for (Item item : items) {
            if (item == Items.AIR)
                continue;
            itemDifficulty += TimeCraft.map.getDifficulty(item);
        }
        return basicDifficulty + itemDifficulty * itemDifficultyMultiplier;
    }

    public static List<Item> getIngredientItems(Container handler, List<Integer> ingredientSlots) {
        List<Item> items = new ArrayList<Item>();
        for (int i : ingredientSlots) {
            items.add(handler.getSlot(i).getStack().getItem());
        }
        return items;
    }

    public static ArrayList<Item> getItemFromMatrix(Container handler, boolean is_craft_table) {
        ArrayList<Item> items = new ArrayList<Item>();
        int index = is_craft_table ? 10 : 5;
        for (int i = 1; i < index; i++) {
            items.add(handler.getSlot(i).getStack().getItem());
        }
        return items;
    }
}
