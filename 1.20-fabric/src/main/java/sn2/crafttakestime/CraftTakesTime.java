package sn2.crafttakestime;

import net.fabricmc.api.ModInitializer;
import sn2.crafttakestime.common.core.CraftManager;
import sn2.crafttakestime.impl.MC120Adapter;

public class CraftTakesTime implements ModInitializer {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "crafttakestime";

    @Override
    public void onInitialize() {
        CraftManager.getInstance().setMinecraftAdapter(new MC120Adapter());
        CraftManager.getInstance().loadConfig();
    }
}
