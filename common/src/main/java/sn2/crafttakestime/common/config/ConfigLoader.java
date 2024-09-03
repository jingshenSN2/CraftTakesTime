package sn2.crafttakestime.common.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import sn2.crafttakestime.common.slot.SlotRange;
import sn2.crafttakestime.common.slot.SlotRangeAdapter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class ConfigLoader {

    public static final String CONFIG_FILENAME = "craft_time_config.json";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(SlotRange.class, new SlotRangeAdapter())
            .setPrettyPrinting()
            .create();

    private final Path configPath;
    private final List<ContainerConfig> defaultCraftContainers;

    public CraftConfig loadConfig() {
        File cfgFile = configPath.resolve(CONFIG_FILENAME).toFile();
        if (!cfgFile.exists()) {
            generateSampleConfig();
        }
        try {
            // Read file to Config object
            byte[] configBytes = Files.readAllBytes(configPath.resolve(CONFIG_FILENAME));
            CraftConfig config = gson.fromJson(new String(configBytes), CraftConfig.class);
            // Check if there are new containers
            checkNewContainersAndUpdateConfig(config);
            // Set config
            return config;
        } catch (Exception e) {
            log.error("Failed to load config file, will use default values");
        }
        return null;
    }

    private void generateSampleConfig() {
        // Make dir
        File cfgDir = configPath.toFile();
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
                .containers(defaultCraftContainers)
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

    private void checkNewContainersAndUpdateConfig(CraftConfig config) {
        Set<String> guiNamesInConfig = config.getContainers().stream()
                .map(ContainerConfig::getGuiContainerClassName)
                .collect(Collectors.toSet());
        defaultCraftContainers.forEach(container -> {
            if (!guiNamesInConfig.contains(container.getGuiContainerClassName())) {
                log.warn("New container found: {}", container.getGuiContainerClassName());
                config.getContainers().add(container);
            }
        });
        saveConfig(config);
    }

    private void saveConfig(CraftConfig config) {
        // save to file
        try {
            Files.write(configPath.resolve(CONFIG_FILENAME),
                    gson.toJson(config).getBytes());
        } catch (Exception e) {
            log.error("Failed to save config file");
        }
    }
}
