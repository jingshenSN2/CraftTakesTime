package sn2.timecraft.util;

import java.util.ArrayList;

import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import sn2.timecraft.TimeCraft;

public class CraftingDifficultyHelper {

	public static int getCraftingDifficultyFromMatrix(Container container, boolean is_craft_table) {
		ArrayList<Slot> slots = new ArrayList<Slot>();
		int index = is_craft_table ? 10 : 5;
		for (int i = 1; i < index; i++) {
			slots.add(container.getSlot(i));
		}
		int difficulty = getCraftingDifficultyFromMatrix(slots);
		return difficulty;
	}

	public static int getCraftingDifficultyFromMatrix(ArrayList<Slot> slots) {
		int basic_difficulty = 20;
		int item_difficulty = 0;
		for (Slot s : slots) {
			Item item = s.getStack().getItem();
			if (item == Items.AIR)
				continue;
			item_difficulty += TimeCraft.map.getDifficulty(item);
		}
		int difficulty = basic_difficulty + item_difficulty;
		return difficulty;
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
