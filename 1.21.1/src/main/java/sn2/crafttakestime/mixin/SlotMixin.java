package sn2.crafttakestime.mixin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sn2.crafttakestime.server.CraftJobs;
@Mixin(Slot.class)
public abstract class SlotMixin {
    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void crafttakestime$pickup(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (!CraftJobs.mayTake((Slot)(Object)this, player)) cir.setReturnValue(false);
    }
    @Inject(method = "tryRemove", at = @At("HEAD"), cancellable = true)
    private void crafttakestime$remove(int amount, int maximum, Player player,
            CallbackInfoReturnable<java.util.Optional<net.minecraft.world.item.ItemStack>> cir) {
        if (!CraftJobs.mayTake((Slot)(Object)this, player)) cir.setReturnValue(java.util.Optional.empty());
    }
}
