package sn2.crafttakestime.common.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class ItemConfig {
    private Map<String, Float> modCraftingTimeMultipliers;
    private Map<String, Float> itemCraftingTimeMultipliers;
}
