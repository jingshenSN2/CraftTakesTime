package sn2.crafttakestime.tests;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** Opt-in startup probe; not included in the distributed mod. */
@Mod(value = "crafttakestime_tests", dist = Dist.CLIENT)
public final class ClientSmoke {
    private int ticks;
    public ClientSmoke() {
        if (Boolean.getBoolean("crafttakestime.clientSmoke"))
            NeoForge.EVENT_BUS.addListener(this::tick);
    }
    private void tick(ClientTickEvent.Post event) {
        if (++ticks >= 100) {
            if (Minecraft.getInstance().gui.screen().getClass().getSimpleName().equals("LoadingErrorScreen"))
                throw new IllegalStateException("Client stopped at the mod loading warning/error screen");
            org.slf4j.LoggerFactory.getLogger("crafttakestime-tests").info("CTT_CLIENT_SMOKE_PASSED: 100 client ticks, screen={}",
                Minecraft.getInstance().gui.screen().getClass().getSimpleName());
            Minecraft.getInstance().stop();
        }
    }
}
