package sn2.timecraft;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Map;

import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class TimeCraftLoadingPlugin implements IFMLLoadingPlugin {

	public TimeCraftLoadingPlugin() {
		MixinBootstrap.init();
		Mixins.addConfiguration("mixins.timecraft.json");

		/*
		 * CodeSource codeSource =
		 * this.getClass().getProtectionDomain().getCodeSource(); if (codeSource !=
		 * null) { URL location = codeSource.getLocation(); try { File file = new
		 * File(location.toURI()); if (file.isFile()) {
		 * CoreModManager.getReparseableCoremods().remove(file.getName()); } } catch
		 * (URISyntaxException ignored) { } }
		 */
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
