package sn2.timecraft.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sn2.timecraft.core.CraftContainers;
import sn2.timecraft.core.CraftManager;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.HashMap;

@UtilityClass
public class ConfigLoader {

    public static final String CONFIG_FILENAME = "craft_time_config.json";
    private static final Logger log = LogManager.getLogger(ConfigLoader.class);
    public static Path cfgPath = Loader.instance().getConfigDir().toPath().resolve("crafttakestime");

    public static void loadConfig() {
        File cfgFile = cfgPath.resolve(CONFIG_FILENAME).toFile();
        if (!cfgFile.exists()) {
            genSampleConfig();
        }
        try {
            FileInputStream fis = new FileInputStream(cfgFile);
            ObjectMapper mapper = new ObjectMapper();
            // Read file to Config object
            CraftConfig config = mapper.readValue(fis, CraftConfig.class);
            // Set config
            CraftManager.getInstance().setConfig(config);
        } catch (Exception e) {
            log.error("Failed to load config file, will use default values");
        }
    }

    private static void genSampleConfig() {
        // Make dir
        File cfgDir = cfgPath.toFile();
        if (!cfgDir.exists()) {
            if (!cfgDir.mkdirs()) {
                log.error("Failed to create config directory");
                return;
            }
        }

        CraftConfig config = CraftConfig.builder()
                .enableCraftingSound(true)
                .globalCraftingTimeMultiplier(1F)
                .containers(new HashMap<>())
                .ingredientConfig(ItemConfig.builder()
                        .modCraftingTimeMultipliers(new HashMap<>())
                        .itemCraftingTimeMultipliers(new HashMap<>())
                        .build())
                .outputConfig(ItemConfig.builder()
                        .modCraftingTimeMultipliers(new HashMap<>())
                        .itemCraftingTimeMultipliers(new HashMap<>())
                        .build())
                .build();

        // add all containers
        CraftContainers.getInstance().getCraftContainers().forEach((name, properties) -> {
            config.getContainers().put(name, ContainerConfig.builder()
                    .enabled(true)
                    .craftingTimeMultiplier(properties.getContainerMultiplier())
                    .build());
        });

        // add sample items
        config.getIngredientConfig().getModCraftingTimeMultipliers().put("minecraft", 1F);
        config.getIngredientConfig().getItemCraftingTimeMultipliers().put("minecraft:stick", 1F);
        config.getOutputConfig().getModCraftingTimeMultipliers().put("minecraft", 1F);
        config.getOutputConfig().getItemCraftingTimeMultipliers().put("minecraft:stick", 1F);

        // save to file, jackson
        ObjectMapper mapper = new ObjectMapper();
        mapper.writer().withDefaultPrettyPrinter();
        try {
            File cfgSampleFile = cfgPath.resolve(CONFIG_FILENAME).toFile();
            mapper.writeValue(cfgSampleFile, config);
        } catch (Exception e) {
            log.error("Failed to generate sample config file");
        }
    }
}
