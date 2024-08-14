package sn2.timecraft.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ContainerConfig {
    private boolean enabled;
    private float craftingTimeMultiplier;
}
