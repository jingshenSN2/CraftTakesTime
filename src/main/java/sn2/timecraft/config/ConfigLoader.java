package sn2.timecraft.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sn2.timecraft.Constants;
import sn2.timecraft.core.CraftContainers;
import sn2.timecraft.core.Ingredients;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.HashMap;

public class ConfigLoader {

    private static final Logger log = LogManager.getLogger(ConfigLoader.class);
    public static Path cfgPath = Loader.instance().getConfigDir().toPath().resolve("crafttakestime");
    public static String GLOBAL = "global_multiplier";
    public static String CONTAINERS = "containers";
    public static String MODS = "mods";
    public static String MOD_MULTIPLIER = "mod_multiplier";
    public static String ITEMS = "items";

    public ConfigLoader() {
    }

    public static void loadConfig() {
        Ingredients instance = Ingredients.getInstance();
        File cfgFile = cfgPath.resolve(Constants.CONFIG_FILENAME).toFile();
        if (!cfgFile.exists()) {
            genSampleConfig();
        }
        try {
            FileInputStream fis = new FileInputStream(cfgFile);
            byte[] data = new byte[(int) cfgFile.length()];
            fis.read(data);
            fis.close();
            String jsonString = new String(data, "UTF-8");
            JsonParser parser = new JsonParser();
            JsonObject object = (JsonObject) parser.parse(jsonString);

            // Load global multiplier
            float global_multiplier = object.getAsJsonPrimitive(GLOBAL).getAsFloat();

            // Load container multipliers
            JsonObject container_list = object.getAsJsonObject(CONTAINERS);
            container_list.entrySet().forEach(e -> {
                String container = e.getKey();
                float value = e.getValue().getAsFloat();
                CraftContainers.getInstance().getCraftContainerProperties(container).setContainerMultiplier(value);
            });

            // Load item multipliers
            JsonObject mod_list = object.getAsJsonObject(MODS);
            mod_list.entrySet().forEach(e -> {
                String namespace = e.getKey() + ':';
                JsonObject mod = (JsonObject) e.getValue();
                float mod_multiplier = mod.getAsJsonPrimitive(MOD_MULTIPLIER).getAsFloat() * global_multiplier;
                JsonObject items = mod.getAsJsonObject(ITEMS);
                items.entrySet().forEach(i -> {
                    String item = namespace + i.getKey();
                    int id = Item.getIdFromItem(Item.getByNameOrId(item));
                    float value = i.getValue().getAsFloat() * mod_multiplier;
                    instance.setDifficulty(id, value);
                });
            });
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

        // Set global multiplier to 1
        JsonObject all = new JsonObject();
        all.addProperty(GLOBAL, 1F);

        // Get all containers
        // { "containers": { "minecraft:inventory": 1F } }
        JsonObject container_list = new JsonObject();
        CraftContainers.getInstance().getCraftContainers().forEach((name, properties) ->
                container_list.addProperty(properties.getContainerName(), 1F));
        all.add(CONTAINERS, container_list);

        // Get all items
        JsonObject mod_list = new JsonObject();
        all.add(MODS, mod_list);

        HashMap<String, JsonObject> nameSpaceMap = new HashMap<>();
        ForgeRegistries.ITEMS.getKeys().forEach(rkey -> {
            String namespace = rkey.getNamespace();
            String path = rkey.getPath();
            if (!nameSpaceMap.containsKey(namespace)) {
                JsonObject array = new JsonObject();
                nameSpaceMap.put(namespace, array);
            }
            nameSpaceMap.get(namespace).addProperty(path, 20F);
        });
        nameSpaceMap.forEach((name, array) -> {
            JsonObject mod = new JsonObject();
            mod.addProperty(MOD_MULTIPLIER, 1F);
            mod.add(ITEMS, array);
            mod_list.add(name, mod);
        });

        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            File cfgSampleFile = cfgPath.resolve(Constants.CONFIG_FILENAME).toFile();
            FileWriter writer = new FileWriter(cfgSampleFile);
            writer.write(gson.toJson(all));
            writer.close();
        } catch (Exception e) {
            log.error("Failed to generate sample config file");
        }
    }
}
