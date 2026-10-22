package sn2.crafttakestime.tests;

import com.mojang.authlib.GameProfile;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import sn2.crafttakestime.data.CraftingData;
import sn2.crafttakestime.server.CraftJobs;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/** Exercises the same native reload pipeline as /reload using a disposable world datapack. */
final class DatapackReloadTest {
    private static void check(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
    static void run(GameTestHelper h) {
        var server = h.getLevel().getServer();
        var repository = server.getPackRepository();
        var original = List.copyOf(repository.getSelectedIds());
        var pack = server.getWorldPath(LevelResource.DATAPACK_DIR).resolve("ctt-reload-fixture");
        var p = new FakePlayer(h.getLevel(), new GameProfile(UUID.randomUUID(), "ctt-reload"));
        var menu = p.inventoryMenu;
        menu.getSlot(1).set(new ItemStack(Items.OAK_LOG, 2));
        menu.slotsChanged(menu.getSlot(1).container);
        menu.clicked(0, 0, ContainerInput.PICKUP, p);
        try {
            Files.createDirectories(pack);
            Files.writeString(pack.resolve("pack.mcmeta"), """
                {"pack":{"min_format":[121,0],"max_format":[121,0],"description":"CTT reload fixture"}}
                """);
            var file = pack.resolve("data/crafttakestime/data_maps/entity_type/crafting_settings.json");
            Files.createDirectories(file.getParent());
            Files.writeString(file, """
                {"values":{"minecraft:player":{"global_multiplier":7,"inventory_multiplier":2,
                  "crafting_sound":false,"ingredient_mod_multipliers":{"minecraft":0.5},
                  "output_mod_multipliers":{"minecraft":2}}}}
                """);
            repository.reload();
            var selected = new ArrayList<>(original);
            selected.add("file/ctt-reload-fixture");
            // Minecraft pumps its server executor while loading when called on the server thread.
            server.reloadResources(selected).join();
            check(CraftingData.settings(p).globalMultiplier() == 7, "External datapack applied by native reload");
            check(!CraftingData.settings(p).craftingSound(), "Datapack sound setting applied");
            for (int i = 0; i < 40; i++) CraftJobs.tick(new PlayerTickEvent.Post(p));
            check(menu.getCarried().isEmpty() && menu.getSlot(1).getItem().getCount() == 2,
                "Reload cancels existing task without consuming inputs");
            check(CraftingData.ticks(p, menu, List.of(menu.getSlot(1).getItem()), menu.getSlot(0).getItem()) == 280,
                "Global, inventory and namespace multipliers compose");
            menu.clicked(0, 0, ContainerInput.PICKUP, p);
            for (int i = 0; i < 279; i++) CraftJobs.tick(new PlayerTickEvent.Post(p));
            check(menu.getCarried().isEmpty(), "New recipe waits for reloaded duration");
            CraftJobs.tick(new PlayerTickEvent.Post(p));
            check(menu.getCarried().getCount() == 4 && menu.getSlot(1).getItem().getCount() == 1,
                "New job uses native datapack settings and consumes one recipe");
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Cannot prepare test datapack", e);
        } finally {
            server.reloadResources(original).join();
            try (var files = Files.walk(pack)) {
                for (var path : files.sorted(Comparator.reverseOrder()).toList()) Files.delete(path);
            } catch (java.io.IOException e) { throw new IllegalStateException("Cannot clean test datapack", e); }
        }
        check(CraftingData.settings(p).equals(CraftingData.PlayerSettings.DEFAULT), "Disabling pack restores defaults");
        h.succeed();
    }
}
