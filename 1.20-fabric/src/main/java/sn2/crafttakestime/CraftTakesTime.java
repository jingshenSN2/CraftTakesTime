package sn2.crafttakestime;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import sn2.crafttakestime.common.core.CraftManager;
import sn2.crafttakestime.impl.MC120Adapter;
import sn2.crafttakestime.networking.PacketCraftConfig;
import sn2.crafttakestime.sound.SoundEventRegistry;

public class CraftTakesTime implements ModInitializer {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "crafttakestime";

    @Override
    public void onInitialize() {
        Registry.register(Registries.SOUND_EVENT,
                SoundEventRegistry.craftingSound.getId().toString(), SoundEventRegistry.craftingSound);
        Registry.register(Registries.SOUND_EVENT,
                SoundEventRegistry.finishSound.getId().toString(), SoundEventRegistry.finishSound);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var cmd = dispatcher.register(
                    CommandManager.literal("crafttakestime")
                            .then(CommandManager.literal("config")
                                    .requires((commandSource) -> commandSource.hasPermissionLevel(2))
                                    .then(CommandManager.literal("reload")
                                            .executes((commandSource) -> {
                                                CraftManager.getInstance().loadConfig();
                                                PacketCraftConfig packet = new PacketCraftConfig(CraftManager.getInstance().getConfig());
                                                PlayerLookup.all(commandSource.getSource().getServer())
                                                        .forEach((serverPlayerEntity) -> {
                                                            ServerPlayNetworking.send(serverPlayerEntity, packet);
                                                        });
                                                commandSource.getSource().sendMessage(
                                                        Text.literal("Config reloaded and send to all players"));
                                                return 1;
                                            }))
                                    .then(CommandManager.literal("print")
                                            .executes((commandSource) -> {
                                                commandSource.getSource().sendMessage(
                                                        Text.literal(CraftManager.getInstance().getConfig().toString()));
                                                return 1;
                                            }))));
            dispatcher.register(CommandManager.literal("ctt").redirect(cmd));
        });

        CraftManager.getInstance().setMinecraftAdapter(new MC120Adapter());
        CraftManager.getInstance().loadConfig();
    }
}
