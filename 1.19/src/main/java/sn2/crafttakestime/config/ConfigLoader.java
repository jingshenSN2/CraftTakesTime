package sn2.crafttakestime.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sn2.crafttakestime.core.CraftManager;
import sn2.crafttakestime.core.DefaultCraftContainers;
import sn2.crafttakestime.util.SlotRange;
import sn2.crafttakestime.util.SlotRangeAdapter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

@UtilityClass
public class ConfigLoader {

    public static final String CONFIG_FILENAME = "craft_time_config.json";
    public static final Path CONFIG_PATH = FMLPaths.GAMEDIR.get().resolve("config").resolve("crafttakestime");
    private static final Logger log = LogManager.getLogger(ConfigLoader.class);
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(SlotRange.class, new SlotRangeAdapter())
            .setPrettyPrinting()
            .create();

    public static void loadConfig() {
        File cfgFile = CONFIG_PATH.resolve(CONFIG_FILENAME).toFile();
        if (!cfgFile.exists()) {
            genSampleConfig();
        }
        try {
            // Read file to Config object
            byte[] configBytes = Files.readAllBytes(CONFIG_PATH.resolve(CONFIG_FILENAME));
            CraftConfig config = gson.fromJson(new String(configBytes), CraftConfig.class);
            // Set config
            CraftManager.getInstance().setConfig(config);
        } catch (Exception e) {
            log.error("Failed to load config file, will use default values");
        }
    }

    private static void genSampleConfig() {
        // Make dir
        File cfgDir = CONFIG_PATH.toFile();
        if (!cfgDir.exists()) {
            if (!cfgDir.mkdirs()) {
                log.error("Failed to create config directory");
                return;
            }
        }

        CraftConfig config = CraftConfig.builder()
                .debug(false)
                .enableCraftingSound(true)
                .globalCraftingTimeMultiplier(1F)
                .containers(DefaultCraftContainers.getInstance().getCraftContainers())
                .ingredientConfig(ItemConfig.builder()
                        .modCraftingTimeMultipliers(new HashMap<>())
                        .itemCraftingTimeMultipliers(new HashMap<>())
                        .build())
                .outputConfig(ItemConfig.builder()
                        .modCraftingTimeMultipliers(new HashMap<>())
                        .itemCraftingTimeMultipliers(new HashMap<>())
                        .build())
                .build();

        // add sample items
        config.getIngredientConfig().getModCraftingTimeMultipliers().put("minecraft", 1F);
        config.getIngredientConfig().getItemCraftingTimeMultipliers().put("minecraft:stick", 1F);
        config.getOutputConfig().getModCraftingTimeMultipliers().put("minecraft", 1F);
        config.getOutputConfig().getItemCraftingTimeMultipliers().put("minecraft:stick", 1F);

        // save to file
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(SlotRange.class, new SlotRangeAdapter())
                .setPrettyPrinting().create();
        try {
            Files.write(CONFIG_PATH.resolve(CONFIG_FILENAME),
                    gson.toJson(config).getBytes());
        } catch (Exception e) {
            log.error("Failed to generate sample config file");
        }
    }
}
