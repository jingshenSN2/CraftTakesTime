package sn2.crafttakestime.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class CraftConfig {
    private boolean debug;
    private boolean enableCraftingSound;
    private float globalCraftingTimeMultiplier;
    private Map<String, ContainerConfig> containers;
    private ItemConfig ingredientConfig;
    private ItemConfig outputConfig;
}
