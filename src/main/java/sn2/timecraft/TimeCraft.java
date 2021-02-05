package sn2.timecraft;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import sn2.timecraft.config.ConfigLoader;
import sn2.timecraft.networking.TimeCraftPacketHandler;

@Mod(modid = "timecraft")
public class TimeCraft {

	public static ConfigLoader map = new ConfigLoader();
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent e) {
		TimeCraftPacketHandler.registerMessage();
	}
}
