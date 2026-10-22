package sn2.crafttakestime;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import sn2.crafttakestime.data.CraftingData;
import sn2.crafttakestime.network.ProgressPayload;
import sn2.crafttakestime.server.CraftJobs;

@Mod(CraftTakesTime.MOD_ID)
public final class CraftTakesTime {
    public static final String MOD_ID = "crafttakestime";
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, MOD_ID);
    public static final Holder<Attribute> CRAFTING_SPEED = ATTRIBUTES.register("crafting_speed", () ->
        new RangedAttribute("attribute.crafttakestime.crafting_speed", 1, 0.01, 1024).setSyncable(true));

    public CraftTakesTime(IEventBus modBus) {
        ATTRIBUTES.register(modBus);
        CraftingData.register(modBus);
        NeoForge.EVENT_BUS.addListener(CraftingData::updated);
        modBus.addListener((EntityAttributeModificationEvent event) -> event.add(EntityType.PLAYER, CRAFTING_SPEED));
        modBus.addListener((RegisterPayloadHandlersEvent event) -> event.registrar("1")
            .playToClient(ProgressPayload.TYPE, ProgressPayload.CODEC, (payload, context) -> ProgressPayload.receive(payload)));
        NeoForge.EVENT_BUS.addListener(CraftJobs::tick);
        NeoForge.EVENT_BUS.addListener(CraftJobs::logout);
    }
}
