package sn2.timecraft.config;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.HashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import sn2.timecraft.Constants;

public class ConfigLoader {

	public HashMap<Integer, Float> difficultyMap = new HashMap<>();
	public static Path cfgPath = Loader.instance().getConfigDir().toPath();
	public static String GLOBAL = "global_multiplier";
	public static String MODS = "mods";
	public static String MOD_MULTIPLIER = "mod_multiplier";
	public static String ITEMS = "items";

	public ConfigLoader() {
	}

	public static void genSampleConfig() {
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

		JsonObject all = new JsonObject();
		all.addProperty(GLOBAL, 1F);
		JsonObject mod_list = new JsonObject();
		all.add(MODS, mod_list);
		nameSpaceMap.forEach((name, array) -> {
			JsonObject mod = new JsonObject();
			mod.addProperty(MOD_MULTIPLIER, 1F);
			mod.add(ITEMS, array);
			mod_list.add(name, mod);
		});

		try {
			File cfgSampleFile = cfgPath.resolve(Constants.CONFIG_FILENAME).toFile();
			if (cfgSampleFile.exists()) {
				cfgSampleFile = cfgPath.resolve(Constants.SAMPLE_CONFIG_FILENAME).toFile();
			}
			FileWriter writer = new FileWriter(cfgSampleFile);
			writer.write(all.toString());
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void parserFrom(String jsonString) {
		JsonParser parser = new JsonParser();
		JsonObject object = (JsonObject) parser.parse(jsonString);
		float global_multiplier = object.getAsJsonPrimitive(GLOBAL).getAsFloat();
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
				this.setDifficulty(id, value);
			});
		});
	}

	public float getDifficulty(Item item) {
		int rkey = Item.getIdFromItem(item);
		if (difficultyMap.containsKey(rkey)) {
			return difficultyMap.get(rkey);
		}
		return 20F;
	}

	public void setDifficulty(int item, float difficulty) {
		this.difficultyMap.put(item, difficulty);
	}
}
