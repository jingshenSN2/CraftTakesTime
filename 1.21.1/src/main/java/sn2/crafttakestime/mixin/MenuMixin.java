package sn2.crafttakestime.mixin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sn2.crafttakestime.server.CraftJobs;
@Mixin(AbstractContainerMenu.class)
public abstract class MenuMixin {
    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    private void crafttakestime$click(int index, int button, ClickType input, Player player, CallbackInfo ci) {
        if (CraftJobs.intercept((AbstractContainerMenu)(Object)this, index, button, input, player)) ci.cancel();
    }
}
