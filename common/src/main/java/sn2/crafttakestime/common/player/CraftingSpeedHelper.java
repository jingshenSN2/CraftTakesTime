package sn2.crafttakestime.common.player;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CraftingSpeedHelper {
    public static float getCraftingSpeed(int experienceLevel) {
        float speed = 1F;
        speed += 0.02F * Math.min(200, experienceLevel);
        return speed;
    }
}
