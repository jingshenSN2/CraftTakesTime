package sn2.crafttakestime.common.core;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemRegistry {
    private final String name;
    private final String modId;
}
