package sn2.timecraft;

import java.util.Map;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class TimeCraftLoadingPlugin implements IFMLLoadingPlugin {

	public TimeCraftLoadingPlugin() {
		MixinBootstrap.init();
		Mixins.addConfiguration("mixins.timecraft.json");
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] {};
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
