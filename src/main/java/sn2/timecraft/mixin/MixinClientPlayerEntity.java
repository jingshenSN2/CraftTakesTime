package sn2.timecraft.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import sn2.timecraft.ITimeCraftPlayer;
import sn2.timecraft.core.CraftManager;

@Mixin(EntityPlayerSP.class)
public class MixinClientPlayerEntity extends AbstractClientPlayer implements ITimeCraftPlayer {

    private CraftManager craftManager = new CraftManager();

    public MixinClientPlayerEntity(World world, GameProfile profile) {
        super(world, profile);
    }

    @Override
    public CraftManager getCraftManager() {
        craftManager.setPlayer(this);
        return craftManager;
    }
}
