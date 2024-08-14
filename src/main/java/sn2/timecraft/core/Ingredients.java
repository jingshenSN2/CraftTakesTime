package sn2.timecraft.core;

import lombok.Data;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
public class Ingredients {

    // Singleton
    private static Ingredients INSTANCE;
    private HashMap<Integer, Float> difficultyMap = new HashMap<>();

    private Ingredients() {
    }

    public static Ingredients getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Ingredients();
        }
        return INSTANCE;
    }

    public void setDifficulty(int ingredientId, float difficulty) {
        difficultyMap.put(ingredientId, difficulty);
    }

    public float getDifficulty(Item item) {
        int ingredientId = Item.getIdFromItem(item);
        if (difficultyMap.containsKey(ingredientId)) {
            return difficultyMap.get(ingredientId);
        }
        return 20F;
    }

    public float getCraftingDifficultyFromIngredients(
            List<Item> items, float basicDifficulty, float itemDifficultyMultiplier, float containerMultiplier) {
        float itemDifficulty = 0F;
        for (Item item : items) {
            if (item == Items.AIR)
                continue;
            itemDifficulty += getDifficulty(item);
        }
        return (basicDifficulty + itemDifficulty * itemDifficultyMultiplier) * containerMultiplier;
    }

    public List<Item> getIngredientItems(Container handler, List<Integer> ingredientSlots) {
        List<Item> items = new ArrayList<Item>();
        for (int i : ingredientSlots) {
            items.add(handler.getSlot(i).getStack().getItem());
        }
        return items;
    }
}
