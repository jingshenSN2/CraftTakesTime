package sn2.crafttakestime.tests;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import sn2.crafttakestime.CraftTakesTime;
import sn2.crafttakestime.network.ProgressPayload;

/** End-to-end development probe using the native container packet and real local connection. */
@Mod(value = "crafttakestime_tests", dist = Dist.CLIENT)
public final class ClientIntegration {
    private int ticks, phase, wait;
    private boolean sawProgress, tookScreenshot;
    public ClientIntegration() {
        if (Boolean.getBoolean("crafttakestime.clientIntegration")) NeoForge.EVENT_BUS.addListener(this::tick);
    }
    private void tick(ClientTickEvent.Post event) {
        var mc = Minecraft.getInstance();
        if (++ticks > 900) throw new IllegalStateException("CTT integration timed out in phase " + phase);
        if (mc.gui.screen() instanceof net.minecraft.client.gui.screens.BackupConfirmScreen screen) {
            for (var child : screen.children()) {
                if (child instanceof net.minecraft.client.gui.components.Button button
                        && button.getMessage().getString().equals(net.minecraft.network.chat.Component.translatable("selectWorld.backupJoinSkipButton").getString())) {
                    button.onPress(null); break;
                }
            }
        }
        if (mc.player == null || mc.gameMode == null || mc.getSingleplayerServer() == null) return;
        if (ticks % 100 == 0) org.slf4j.LoggerFactory.getLogger("ctt-integration").info(
            "phase={} screen={} output={} input={} creative={}", phase, mc.gui.screen(),
            mc.player.inventoryMenu.getSlot(0).getItem(), mc.player.inventoryMenu.getSlot(1).getItem(), mc.player.hasInfiniteMaterials());
        if (phase == 0) {
            phase = 1;
            var uuid = mc.player.getUUID();
            mc.getSingleplayerServer().execute(() -> {
                var p = mc.getSingleplayerServer().getPlayerList().getPlayer(uuid);
                p.setGameMode(GameType.SURVIVAL);
                p.getAbilities().invulnerable = true; p.setNoGravity(true); p.experienceLevel = 0;
                p.getInventory().clearContent();
                p.inventoryMenu.setCarried(ItemStack.EMPTY);
                p.inventoryMenu.getSlot(1).set(new ItemStack(Items.OAK_LOG, 2));
                p.inventoryMenu.slotsChanged(p.inventoryMenu.getSlot(1).container);
                p.inventoryMenu.broadcastChanges();
                org.slf4j.LoggerFactory.getLogger("ctt-integration").info("Server setup output={} input={}",
                    p.inventoryMenu.getSlot(0).getItem(), p.inventoryMenu.getSlot(1).getItem());
            });
        } else if (phase == 1 && mc.player.inventoryMenu.getSlot(0).getItem().is(Items.OAK_PLANKS)
                && !mc.player.hasInfiniteMaterials()) {
            mc.gui.setScreen(new InventoryScreen(mc.player));
            phase = 4; wait = 0;
        } else if (phase == 4 && ++wait >= 20) {
            if (mc.player.containerMenu != mc.player.inventoryMenu || !(mc.gui.screen() instanceof InventoryScreen))
                throw new IllegalStateException("Test fixture must use the survival inventory menu");
            mc.gameMode.handleContainerInput(0, 0, 0, ContainerInput.PICKUP, mc.player);
            if (!mc.player.inventoryMenu.getCarried().isEmpty()) throw new IllegalStateException("Client predicted immediate crafting");
            phase = 2;
        } else if (phase == 2) {
            try {
                var field = sn2.crafttakestime.client.CraftClient.class.getDeclaredField("progress");
                field.setAccessible(true);
                var progress = (ProgressPayload) field.get(null);
                if (progress != null && progress.total() == 60 && progress.elapsed() > 0) {
                    sawProgress = true;
                    if (progress.elapsed() >= 25 && !tookScreenshot) {
                        tookScreenshot = true;
                        Screenshot.grab(mc.gameDirectory, "crafttakestime-progress.png", mc.gameRenderer.mainRenderTarget(), 1, message -> {});
                    }
                }
            } catch (ReflectiveOperationException e) { throw new RuntimeException(e); }
            var carried = mc.player.inventoryMenu.getCarried();
            if (carried.is(Items.OAK_PLANKS)) {
                if (carried.getCount() != 4 || mc.player.inventoryMenu.getSlot(1).getItem().getCount() != 1 || !sawProgress)
                    throw new IllegalStateException("Wrong output, input count, or missing progress payload");
                org.slf4j.LoggerFactory.getLogger("crafttakestime-tests").info(
                    "CTT_CLIENT_INTEGRATION_PASSED: native click packet, 60-tick progress received, one log consumed, four planks on cursor");
                phase = 3; wait = 0;
            }
        } else if (phase == 3 && ++wait >= 20) {
            mc.stop();
        }
    }
}
