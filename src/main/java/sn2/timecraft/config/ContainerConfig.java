package sn2.timecraft.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContainerConfig {
    private boolean enabled;
    private float craftingTimeMultiplier;
}
