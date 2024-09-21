package sn2.crafttakestime.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
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

public class MC112Adapter implements MinecraftAdapter {
    private GuiContainer containerScreen;

    @Override
    public Path getConfigPath() {
        return Loader.instance().getConfigDir().toPath().resolve(MODID);
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
        this.containerScreen = (GuiContainer) screen;
    }

    @Override
    public Object getItems(SlotRange range) {
        List<Item> items = new ArrayList<>();
        if (this.containerScreen == null) {
            return items;
        }
        for (int i : range) {
            items.add(this.containerScreen.inventorySlots.getSlot(i).getStack().getItem());
        }
        return items;
    }

    @Override
    public boolean isSlotEmpty(int slotIndex) {
        if (this.containerScreen == null) {
            return true;
        }
        return this.containerScreen.inventorySlots.getSlot(slotIndex).getStack().isEmpty();
    }

    @Override
    public ItemRegistry getSlotItemRegistry(int slotIndex) {
        if (this.containerScreen == null) {
            return null;
        }
        Item item = this.containerScreen.inventorySlots.getSlot(slotIndex).getStack().getItem();
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
        ItemStack outputStack = this.containerScreen.inventorySlots.getSlot(outputSlot).getStack();
        ItemStack carriedStack = this.containerScreen.mc.player.inventory.getItemStack();
        return !carriedStack.isEmpty() &&
                (!outputStack.isItemEqual(carriedStack) ||
                        outputStack.getCount() + carriedStack.getCount() > carriedStack.getMaxStackSize());
    }

    @Override
    public void handleCraftFinished(int outputSlot) {
        if (this.containerScreen == null) {
            return;
        }
        ((ITimeCraftGuiContainer) this.containerScreen).handleCraftFinished(
                this.containerScreen.inventorySlots.getSlot(outputSlot), outputSlot);
    }

    @Override
    public boolean hasPlayer() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        return player != null;
    }

    @Override
    public void playCraftingSound() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            Minecraft.getMinecraft().getSoundHandler().playSound(
                    new CraftingTickableSound(player.getPosition()));
        }
    }

    @Override
    public void playFinishSound() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            player.playSound(SoundEventRegistry.finishSound, 0.1F, 1f);
        }
    }

    @Override
    public float getCraftingSpeed() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null) {
            return CraftingSpeedHelper.getCraftingSpeed(player.experienceLevel);
        }
        return 0;
    }
}
