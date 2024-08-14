package sn2.timecraft.config;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class CraftConfig {
    private boolean enableCraftingSound;
    private float globalCraftingTimeMultiplier;
    private Map<String, ContainerConfig> containers;
    private ItemConfig ingredientConfig;
    private ItemConfig outputConfig;
}
