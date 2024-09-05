package sn2.crafttakestime.event;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import sn2.crafttakestime.common.core.CraftManager;
import sn2.crafttakestime.networking.PacketCraftConfig;
import sn2.crafttakestime.networking.TimeCraftPacketHandler;

@Mod.EventBusSubscriber()
public class Events {

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        CraftManager.getInstance().loadConfig();
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        PacketCraftConfig packet = new PacketCraftConfig(CraftManager.getInstance().getConfig());
        TimeCraftPacketHandler.INSTANCE.send(
                PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                packet);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer()) {
            return;
        }
        CraftManager.getInstance().tick();
    }

    @SubscribeEvent
    public static void onRegisterCommand(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        var cmd = dispatcher.register(
                Commands.literal("crafttakestime")
                        .then(Commands.literal("config").requires((commandSource) -> commandSource.hasPermission(2))
                                .then(Commands.literal("reload")
                                        .executes((commandSource) -> {
                                            CraftManager.getInstance().loadConfig();
                                            PacketCraftConfig packet = new PacketCraftConfig(CraftManager.getInstance().getConfig());
                                            TimeCraftPacketHandler.INSTANCE.send(
                                                    PacketDistributor.ALL.noArg(),
                                                    packet);
                                            commandSource.getSource().sendSuccess(
                                                    () -> Component.literal("Config reloaded and send to all players"),
                                                    true);
                                            return 1;
                                        }))
                                .then(Commands.literal("print")
                                        .executes((commandSource) -> {
                                            commandSource.getSource().sendSuccess(
                                                    () -> Component.literal(CraftManager.getInstance().getConfig().toString()),
                                                    true);
                                            return 1;
                                        }))));
        dispatcher.register(Commands.literal("ctt").redirect(cmd));
    }
}
