package sn2.crafttakestime.util;

import lombok.experimental.UtilityClass;
import net.minecraft.client.player.LocalPlayer;

@UtilityClass
public class CraftingSpeedHelper {
    public static float getCraftingSpeed(LocalPlayer player) {
        float speed = 1F;
        speed += 0.02F * Math.min(200, player.experienceLevel);
        return speed;
    }
}
