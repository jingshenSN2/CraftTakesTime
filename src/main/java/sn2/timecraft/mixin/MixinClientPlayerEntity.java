package sn2.timecraft.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import sn2.timecraft.ITimeCraftPlayer;
import sn2.timecraft.sound.CraftingTickableSound;
import sn2.timecraft.sound.SoundEventRegistry;
import sn2.timecraft.util.CraftingSpeedHelper;

@Mixin(EntityPlayerSP.class)
public class MixinClientPlayerEntity extends AbstractClientPlayer implements ITimeCraftPlayer {

    public boolean isCrafting = false;
    public float currentCraftTime = 0;
    public float craftPeriod = 0;

    public MixinClientPlayerEntity(World world, GameProfile profile) {
        super(world, profile);
    }

    @Override
    public boolean isCrafting() {
        return this.isCrafting;
    }

    @Override
    public void setCrafting(boolean isCrafting) {
        this.isCrafting = isCrafting;
    }

    @Override
    public float getCraftTime() {
        return this.currentCraftTime;
    }

    @Override
    public void setCraftTime(float craftTime) {
        this.currentCraftTime = craftTime;
    }

    @Override
    public float getCraftPeriod() {
        return this.craftPeriod;
    }

    @Override
    public void setCraftPeriod(float craftPeriod) {
        this.craftPeriod = craftPeriod;
    }

    @Override
    public void stopCraft() {
        this.isCrafting = false;
        this.currentCraftTime = 0;
    }

    @Override
    public void startCraft() {
        this.isCrafting = true;
        this.currentCraftTime = 0;
        if (craftPeriod >= 10F) {
            Minecraft.getMinecraft().getSoundHandler().playSound(new CraftingTickableSound(this, this.getPosition()));
        }
    }

    @Override
    public boolean tick(ItemStack resultStack) {
        if (this.isCrafting()) {
            ItemStack cursorStack = this.inventory.getItemStack();
            if (cursorStack.getItem() != Items.AIR) {
                if (!cursorStack.isItemEqual(resultStack)
                        || cursorStack.getCount() + resultStack.getCount() > cursorStack.getMaxStackSize()) {
                    return false;
                }
            }
            if (this.getCraftTime() < this.getCraftPeriod()) {
                this.currentCraftTime += CraftingSpeedHelper.getCraftingSpeed(this);
            } else if (this.getCraftTime() >= this.getCraftPeriod()) {
                this.playSound(SoundEventRegistry.finishSound, 0.1F, 1f);
                return true;
            }
        }
        return false;
    }

}
