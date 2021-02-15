package sn2.timecraft.util;

import harmonised.pmmo.skills.Skill;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;

public class CraftingSpeedHelper {
	
	
	public static float getCraftingSpeed(EntityPlayer player) {
		float speed = 1F;
		speed += 0.02F * Math.min(200, player.experienceLevel);
		if (Loader.isModLoaded("pmmo")) {
			speed += 0.05F * Math.min(150, Skill.getLevel(Skill.CRAFTING.toString(), player));
		}
		return speed;
	}
	
}
