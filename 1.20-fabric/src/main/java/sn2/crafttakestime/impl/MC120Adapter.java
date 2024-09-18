package sn2.crafttakestime.impl;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import sn2.crafttakestime.ITimeCraftGuiContainer;
import sn2.crafttakestime.common.config.ContainerConfig;
import sn2.crafttakestime.common.core.ItemRegistry;
import sn2.crafttakestime.common.core.MinecraftAdapter;
import sn2.crafttakestime.common.player.CraftingSpeedHelper;
import sn2.crafttakestime.common.slot.SlotRange;
import sn2.crafttakestime.sound.CraftingTickableSound;
import sn2.crafttakestime.sound.SoundEventRegistry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static sn2.crafttakestime.CraftTakesTime.MODID;

public class MC120Adapter implements MinecraftAdapter {
    private HandledScreen containerScreen;

    @Override
    public Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().resolve(MODID);
    }

    @Override
    public List<ContainerConfig> getDefaultsCraftContainers() {
        return DefaultCraftContainers.getInstance().getCraftContainers();
    }

    @Override
    public Object getContainerScreen() {
        return containerScreen;
    }

    @Override
    public void setContainerScreen(Object screen) {
        this.containerScreen = (HandledScreen) screen;
    }

    @Override
    public Object getItems(SlotRange range) {
        List<Item> items = new ArrayList<>();
        if (this.containerScreen == null) {
            return items;
        }
        for (int i : range) {
            items.add(this.containerScreen.getScreenHandler().getSlot(i).getStack().getItem());
        }
        return items;
    }

    @Override
    public boolean isSlotEmpty(int slotIndex) {
        if (this.containerScreen == null) {
            return true;
        }
        return this.containerScreen.getScreenHandler().getSlot(slotIndex).getStack().isEmpty();
    }

    @Override
    public ItemRegistry getSlotItemRegistry(int slotIndex) {
        if (this.containerScreen == null) {
            return null;
        }
        Item item = this.containerScreen.getScreenHandler().getSlot(slotIndex).getStack().getItem();
        RegistryKey<Item> resourceLocation = Registries.ITEM.getKey(item).orElse(null);
        if (resourceLocation != null) {
            return ItemRegistry.builder()
                    .name(resourceLocation.getRegistry().toString())
                    .modId(resourceLocation.getRegistry().getNamespace())
                    .build();
        }
        return null;
    }

    @Override
    public boolean shouldStopCrafting(int outputSlot) {
        if (this.containerScreen == null) {
            return true;
        }
        ItemStack outputStack = this.containerScreen.getScreenHandler().getSlot(outputSlot).getStack();
        ItemStack carriedStack = this.containerScreen.getScreenHandler().getCursorStack();
        return !carriedStack.isEmpty() &&
                (!ItemStack.areItemsEqual(outputStack, carriedStack) ||
                        outputStack.getCount() + carriedStack.getCount() > carriedStack.getMaxCount());
    }

    @Override
    public void handleCraftFinished(int outputSlot) {
        if (this.containerScreen == null) {
            return;
        }
        ((ITimeCraftGuiContainer) this.containerScreen).handleCraftFinished(
                this.containerScreen.getScreenHandler().getSlot(outputSlot), outputSlot);
    }

    @Override
    public boolean hasPlayer() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        return player != null;
    }

    @Override
    public void playCraftingSound() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            MinecraftClient.getInstance().getSoundManager().play(
                    new CraftingTickableSound(player.getBlockPos()));
        }
    }

    @Override
    public void playFinishSound() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            player.playSound(SoundEventRegistry.finishSound, 0.1F, 1f);
        }
    }

    @Override
    public float getCraftingSpeed() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            return CraftingSpeedHelper.getCraftingSpeed(player.experienceLevel);
        }
        return 0;
    }
}
