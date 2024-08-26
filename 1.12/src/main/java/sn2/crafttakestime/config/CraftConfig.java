package sn2.crafttakestime.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class CraftConfig {
    private boolean debug;
    private boolean enableCraftingSound;
    private float globalCraftingTimeMultiplier;
    private List<ContainerConfig> containers;
    private ItemConfig ingredientConfig;
    private ItemConfig outputConfig;
}
