package sn2.crafttakestime.util;

import lombok.experimental.UtilityClass;
import net.minecraft.client.entity.player.ClientPlayerEntity;

@UtilityClass
public class CraftingSpeedHelper {
    public static float getCraftingSpeed(ClientPlayerEntity player) {
        float speed = 1F;
        speed += 0.02F * Math.min(200, player.experienceLevel);
        return speed;
    }
}
