package sn2.crafttakestime.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
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
    private AbstractContainerScreen containerScreen;

    @Override
    public Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get().resolve(MODID);
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
        this.containerScreen = (AbstractContainerScreen) screen;
    }

    @Override
    public Object getItems(SlotRange range) {
        List<Item> items = new ArrayList<>();
        if (this.containerScreen == null) {
            return items;
        }
        for (int i : range) {
            items.add(this.containerScreen.getMenu().getSlot(i).getItem().getItem());
        }
        return items;
    }

    @Override
    public boolean isSlotEmpty(int slotIndex) {
        if (this.containerScreen == null) {
            return true;
        }
        return this.containerScreen.getMenu().getSlot(slotIndex).getItem().isEmpty();
    }

    @Override
    public ItemRegistry getSlotItemRegistry(int slotIndex) {
        if (this.containerScreen == null) {
            return null;
        }
        Item item = this.containerScreen.getMenu().getSlot(slotIndex).getItem().getItem();
        ResourceLocation resourceLocation = ForgeRegistries.ITEMS.getKey(item);
        if (resourceLocation != null) {
            return ItemRegistry.builder()
                    .name(resourceLocation.toString())
                    .modId(resourceLocation.getNamespace())
                    .build();
        }
        return null;
    }

    @Override
    public boolean shouldStopCrafting(int outputSlot) {
        if (this.containerScreen == null) {
            return true;
        }
        ItemStack outputStack = this.containerScreen.getMenu().getSlot(outputSlot).getItem();
        ItemStack carriedStack = this.containerScreen.getMenu().getCarried();
        return !carriedStack.isEmpty() &&
                (!ItemStack.isSameItem(outputStack, carriedStack) ||
                        outputStack.getCount() + carriedStack.getCount() > carriedStack.getMaxStackSize());
    }

    @Override
    public void handleCraftFinished(int outputSlot) {
        if (this.containerScreen == null) {
            return;
        }
        ((ITimeCraftGuiContainer) this.containerScreen).handleCraftFinished(
                this.containerScreen.getMenu().getSlot(outputSlot), outputSlot);
    }

    @Override
    public boolean hasPlayer() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null;
    }

    @Override
    public void playCraftingSound() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            Minecraft.getInstance().getSoundManager().play(
                    new CraftingTickableSound(player.getOnPos()));
        }
    }

    @Override
    public void playFinishSound() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            player.playSound(SoundEventRegistry.finishSound.get(), 0.1F, 1f);
        }
    }

    @Override
    public float getCraftingSpeed() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            return CraftingSpeedHelper.getCraftingSpeed(player.experienceLevel);
        }
        return 0;
    }
}
