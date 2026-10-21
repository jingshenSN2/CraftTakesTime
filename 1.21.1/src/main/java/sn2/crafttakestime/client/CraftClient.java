package sn2.crafttakestime.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import sn2.crafttakestime.network.ProgressPayload;

@Mod(value = "crafttakestime", dist = Dist.CLIENT)
public final class CraftClient {
    private static AbstractContainerMenu activeMenu;
    private static ProgressPayload progress;
    private static long lastUpdate;
    private static CraftingSound loop;
    private static final SoundEvent CRAFTING = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("crafttakestime", "crafting"));
    private static final SoundEvent FINISH = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("crafttakestime", "finish"));

    public CraftClient(IEventBus modBus) {
        ProgressPayload.setClientHandler(CraftClient::accept);
        NeoForge.EVENT_BUS.addListener(CraftClient::render);
        NeoForge.EVENT_BUS.addListener(CraftClient::tick);
    }
    private static void accept(ProgressPayload payload) {
        var mc = Minecraft.getInstance();
        if (mc.player == null || mc.player.containerMenu.containerId != payload.menuId()
                || !(mc.screen instanceof AbstractContainerScreen<?> screen) || screen.getMenu() != mc.player.containerMenu) return;
        if (payload.total() <= 0) {
            clear();
            if (payload.sound()) mc.getSoundManager().play(SimpleSoundInstance.forUI(FINISH, 1, 0.1f));
            return;
        }
        activeMenu = mc.player.containerMenu;
        progress = payload;
        lastUpdate = System.nanoTime();
        if (payload.sound() && payload.total() >= 10 && loop == null) {
            loop = new CraftingSound();
            mc.getSoundManager().play(loop);
        }
    }
    private static void clear() {
        progress = null; activeMenu = null;
        if (loop != null) { Minecraft.getInstance().getSoundManager().stop(loop); loop = null; }
    }
    private static void tick(ClientTickEvent.Post event) {
        var mc = Minecraft.getInstance();
        if (activeMenu != null && (mc.player == null || mc.player.containerMenu != activeMenu
                || !(mc.screen instanceof AbstractContainerScreen<?> screen) || screen.getMenu() != activeMenu)) clear();
    }
    private static void render(ScreenEvent.Render.Post event) {
        if (progress == null || !(event.getScreen() instanceof AbstractContainerScreen<?> screen)
                || screen.getMenu() != activeMenu || progress.slot() < 0 || progress.slot() >= activeMenu.slots.size()) return;
        var slot = activeMenu.getSlot(progress.slot());
        var g = event.getGuiGraphics();
        int x = screen.getGuiLeft() + slot.x;
        int y = screen.getGuiTop() + slot.y;
        // Interpolate at most one tick; a stalled server never completes a craft on the client.
        double elapsed = progress.elapsed() + Math.min(1, (System.nanoTime() - lastUpdate) / 50_000_000.0);
        int width = (int) (16 * Math.min(1, elapsed / progress.total()));
        g.fill(x, y, x + 16, y + 16, 0x66000000);
        g.fill(x - 1, y + 17, x + 17, y + 21, 0xff202020);
        g.fill(x, y + 18, x + width, y + 20, 0xff63d471);
    }
    private static final class CraftingSound extends AbstractTickableSoundInstance {
        CraftingSound() {
            super(CRAFTING, SoundSource.MASTER, SoundInstance.createUnseededRandom());
            looping = true; relative = true; volume = 0.1f; attenuation = SoundInstance.Attenuation.NONE;
        }
        @Override public void tick() {
            if (progress == null || !progress.sound()) stop();
        }
    }
}
