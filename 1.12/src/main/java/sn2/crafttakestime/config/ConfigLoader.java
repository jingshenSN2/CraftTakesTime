package sn2.crafttakestime.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;
import net.minecraftforge.fml.common.Loader;
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
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class ConfigLoader {

    public static final String CONFIG_FILENAME = "craft_time_config.json";
    private static final Logger log = LogManager.getLogger(ConfigLoader.class);
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(SlotRange.class, new SlotRangeAdapter())
            .setPrettyPrinting()
            .create();
    public static Path CONFIG_PATH = Loader.instance().getConfigDir().toPath().resolve("crafttakestime");

    public static void loadConfig() {
        File cfgFile = CONFIG_PATH.resolve(CONFIG_FILENAME).toFile();
        if (!cfgFile.exists()) {
            generateSampleConfig();
        }
        try {
            // Read file to Config object
            byte[] configBytes = Files.readAllBytes(CONFIG_PATH.resolve(CONFIG_FILENAME));
            CraftConfig config = gson.fromJson(new String(configBytes), CraftConfig.class);
            // Check if there are new containers
            checkNewContainersAndUpdateConfig(config);
            // Set config
            CraftManager.getInstance().setConfig(config);
        } catch (Exception e) {
            log.error("Failed to load config file, will use default values");
        }
    }

    private static void generateSampleConfig() {
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
        saveConfig(config);
    }

    private static void checkNewContainersAndUpdateConfig(CraftConfig config) {
        Set<String> guiNamesInConfig = config.getContainers().stream()
                .map(ContainerConfig::getGuiContainerClassName)
                .collect(Collectors.toSet());
        DefaultCraftContainers.getInstance().getCraftContainers().forEach(container -> {
            if (!guiNamesInConfig.contains(container.getGuiContainerClassName())) {
                log.warn("New container found: {}", container.getGuiContainerClassName());
                config.getContainers().add(container);
            }
        });
        saveConfig(config);
    }

    public static void saveConfig(CraftConfig config) {
        try {
            Files.write(CONFIG_PATH.resolve(CONFIG_FILENAME),
                    gson.toJson(config).getBytes());
        } catch (Exception e) {
            log.error("Failed to save config file");
        }
    }
}
