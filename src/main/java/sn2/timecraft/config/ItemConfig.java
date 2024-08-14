package sn2.timecraft.config;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ItemConfig {
    private Map<String, Float> modCraftingTimeMultipliers;
    private Map<String, Float> itemCraftingTimeMultipliers;
}
