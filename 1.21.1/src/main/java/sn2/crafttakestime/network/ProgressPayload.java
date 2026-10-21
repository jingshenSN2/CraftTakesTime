package sn2.crafttakestime.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ProgressPayload(int menuId, int slot, int elapsed, int total, boolean sound) implements CustomPacketPayload {
    public static final Type<ProgressPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("crafttakestime", "progress"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ProgressPayload> CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, ProgressPayload::menuId,
        ByteBufCodecs.VAR_INT, ProgressPayload::slot,
        ByteBufCodecs.VAR_INT, ProgressPayload::elapsed,
        ByteBufCodecs.VAR_INT, ProgressPayload::total,
        ByteBufCodecs.BOOL, ProgressPayload::sound, ProgressPayload::new);
    // Installed by the client entry point; dedicated servers do not reference client-only classes.
    private static java.util.function.Consumer<ProgressPayload> clientHandler = ignored -> {};
    public static void setClientHandler(java.util.function.Consumer<ProgressPayload> handler) { clientHandler = handler; }
    public static void receive(ProgressPayload payload) { clientHandler.accept(payload); }
    @Override public Type<ProgressPayload> type() { return TYPE; }
}
