package sn2.crafttakestime;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import sn2.crafttakestime.common.core.CraftManager;
import sn2.crafttakestime.impl.MC118Adapter;
import sn2.crafttakestime.networking.TimeCraftPacketHandler;
import sn2.crafttakestime.sound.SoundEventRegistry;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CraftTakesTime.MODID)
public class CraftTakesTime {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "crafttakestime";

    public CraftTakesTime() {
        TimeCraftPacketHandler.registerMessage();
        SoundEventRegistry.SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        CraftManager.getInstance().setMinecraftAdapter(new MC118Adapter());
    }
}
