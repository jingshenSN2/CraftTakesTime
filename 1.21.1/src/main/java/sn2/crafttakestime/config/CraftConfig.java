package sn2.crafttakestime.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.LoggerFactory;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Server-owned settings. Restart the server after editing. */
public final class CraftConfig {
    public boolean enableCraftingSound = true;
    public double globalCraftingTimeMultiplier = 1;
    public Map<String, Double> containerCraftingTimeMultipliers = new LinkedHashMap<>(Map.of(
        "minecraft:inventory", 1.0, "minecraft:crafting", 1.0,
        "minecraft:anvil", 1.0, "minecraft:smithing", 1.0, "minecraft:stonecutter", 1.0));
    public ItemConfig ingredientConfig = new ItemConfig();
    public ItemConfig outputConfig = new ItemConfig();
    public static final class ItemConfig {
        public Map<String, Double> modCraftingTimeMultipliers = new LinkedHashMap<>();
        public Map<String, Double> itemCraftingTimeMultipliers = new LinkedHashMap<>();
        double multiplier(ItemStack stack) {
            var id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            return modCraftingTimeMultipliers.getOrDefault(id.getNamespace(), 1.0)
                * itemCraftingTimeMultipliers.getOrDefault(id.toString(), 1.0);
        }
        void validate() {
            validateMap(modCraftingTimeMultipliers);
            validateMap(itemCraftingTimeMultipliers);
        }
    }
    private static void validateMap(Map<String, Double> map) {
        if (map == null) throw new IllegalArgumentException("Multiplier map must be an object");
        map.forEach((key, value) -> {
            if (value == null || !Double.isFinite(value) || value < 0 || value > 1_000_000)
                throw new IllegalArgumentException("Invalid multiplier: " + key);
        });
    }
    public static CraftConfig load() {
        var path = FMLPaths.CONFIGDIR.get().resolve("crafttakestime/craft_time_config.json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) Files.writeString(path, gson.toJson(new CraftConfig()));
            CraftConfig config = gson.fromJson(Files.readString(path), CraftConfig.class);
            if (config == null || !Double.isFinite(config.globalCraftingTimeMultiplier)
                    || config.globalCraftingTimeMultiplier < 0 || config.globalCraftingTimeMultiplier > 1_000_000)
                throw new IllegalArgumentException("Invalid global multiplier");
            validateMap(config.containerCraftingTimeMultipliers);
            config.ingredientConfig.validate(); config.outputConfig.validate();
            return config;
        } catch (Exception e) {
            LoggerFactory.getLogger("crafttakestime").error("Cannot load {}; using defaults without overwriting the file", path, e);
            return new CraftConfig();
        }
    }
    public int ticks(List<ItemStack> inputs, ItemStack output, String menuId, int level) {
        return ticks(inputs, output, menuId, level, 1);
    }
    public int ticks(List<ItemStack> inputs, ItemStack output, String menuId, int level, double attributeSpeed) {
        double ingredients = inputs.stream().filter(s -> !s.isEmpty()).mapToDouble(ingredientConfig::multiplier).sum();
        return CraftTime.ticks(ingredients, outputConfig.multiplier(output), globalCraftingTimeMultiplier,
            containerCraftingTimeMultipliers.getOrDefault(menuId, 1.0), level, attributeSpeed);
    }
}
