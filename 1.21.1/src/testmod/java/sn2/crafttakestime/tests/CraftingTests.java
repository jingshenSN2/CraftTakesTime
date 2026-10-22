package sn2.crafttakestime.tests;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import sn2.crafttakestime.CraftTakesTime;
import sn2.crafttakestime.config.CraftTime;
import sn2.crafttakestime.data.CraftingData;
import sn2.crafttakestime.server.CraftJobs;
import java.util.*;
import java.util.function.Consumer;

@net.neoforged.neoforge.gametest.GameTestHolder("crafttakestime_tests")
@Mod("crafttakestime_tests")
public final class CraftingTests {
    private static final String ID = "crafttakestime_tests";
    private static final Map<String, Consumer<GameTestHelper>> TESTS = new LinkedHashMap<>();
    static {
        TESTS.put("anvil_cost_and_rename", CraftingTests::anvil);
        TESTS.put("smithing_upgrade", CraftingTests::smithing);
        TESTS.put("stonecutter_timed", CraftingTests::stonecutter);
        TESTS.put("duration_policy", CraftingTests::duration);
        TESTS.put("inventory_single", h -> single(h, false));
        TESTS.put("inventory_right_click", h -> single(h, true));
        TESTS.put("shift_each_recipe_waits", CraftingTests::shift);
        TESTS.put("input_change_cancels", CraftingTests::changed);
        TESTS.put("closing_cancels", CraftingTests::closed);
        TESTS.put("full_inventory", CraftingTests::full);
        TESTS.put("alternate_clicks_blocked", CraftingTests::alternate);
        TESTS.put("cursor_capacity", CraftingTests::capacity);
        TESTS.put("attribute_speed", CraftingTests::attribute);
        TESTS.put("table_and_bucket_remainders", CraftingTests::remainders);
        TESTS.put("ingredient_output_multipliers", CraftingTests::multipliers);
        TESTS.put("persistent_item_component", CraftingTests::component);
        TESTS.put("datapack_reload", DatapackReloadTest::run);
    }
    public CraftingTests(IEventBus bus) {
        bus.addListener((RegisterGameTestsEvent event) -> event.register(CraftingTests.class));
    }
    @GameTestGenerator
    public static Collection<TestFunction> generateTests() {
        return TESTS.entrySet().stream().map(entry -> new TestFunction(ID, entry.getKey(),
            ID + ":empty", 400, 0, true, entry.getValue())).toList();
    }
    private static void check(boolean value, String message) { if (!value) throw new IllegalStateException(message); }
    private static ServerPlayer player(GameTestHelper h) {
        return new FakePlayer(h.getLevel(), new GameProfile(UUID.randomUUID(), "ctt-test"));
    }
    private static InventoryMenu logs(ServerPlayer p, int count) {
        var menu = p.inventoryMenu;
        p.containerMenu = menu;
        menu.getSlot(1).set(new ItemStack(Items.OAK_LOG, count));
        menu.slotsChanged(menu.getSlot(1).container);
        check(menu.getSlot(0).getItem().is(Items.OAK_PLANKS), "Recipe preview must exist");
        return menu;
    }
    private static void ticks(ServerPlayer p, int n) {
        for (int i = 0; i < n; i++) CraftJobs.tick(new PlayerTickEvent.Post(p));
    }
    private static void duration(GameTestHelper h) {
        check(CraftTime.ticks(1, 1, 1, 1, 0) == 20, "One log = 20 ticks");
        check(CraftTime.ticks(4, 1, 1, 1, 0) == 80, "Four ingredients = 80 ticks");
        check(CraftTime.ticks(4, 2, 1, 1, 0) == 160, "Output multiplier");
        check(CraftTime.ticks(1, 1, 1, 1, 200) == 4, "XP cap");
        check(CraftTime.ticks(1, 1, 1, 1, 999) == 4, "Levels above cap");
        check(CraftTime.ticks(1, 1, 0, 1, 0) == 1, "Zero still waits one tick");
        check(CraftTime.ticks(0.205, 1, 1, 1, 0, 2.2) == 2, "Round once after applying fractional attribute speed");
        h.succeed();
    }
    private static void single(GameTestHelper h, boolean right) {
        var p = player(h); var menu = logs(p, 2);
        menu.clicked(0, right ? 1 : 0, ClickType.PICKUP, p);
        check(menu.getCarried().isEmpty(), "Click cannot immediately craft");
        ticks(p, 19);
        check(menu.getCarried().isEmpty(), "Cannot craft early");
        ticks(p, 1);
        check(menu.getCarried().is(Items.OAK_PLANKS) && menu.getCarried().getCount() == 4, "One complete recipe");
        check(menu.getSlot(1).getItem().getCount() == 1, "Consumes one log");
        ticks(p, 40);
        check(menu.getCarried().getCount() == 4, "Single click does not repeat");
        h.succeed();
    }
    private static void shift(GameTestHelper h) {
        var p = player(h); var menu = logs(p, 3);
        menu.clicked(0, 0, ClickType.QUICK_MOVE, p);
        ticks(p, 20);
        check(p.getInventory().countItem(Items.OAK_PLANKS) == 4, "First wait crafts one recipe");
        check(menu.getSlot(1).getItem().getCount() == 2, "Shift must not consume all inputs");
        ticks(p, 19);
        check(p.getInventory().countItem(Items.OAK_PLANKS) == 4, "Second recipe waits independently");
        ticks(p, 21);
        check(p.getInventory().countItem(Items.OAK_PLANKS) == 12, "Three timed batches");
        check(menu.getSlot(1).getItem().isEmpty(), "All inputs consumed exactly once");
        h.succeed();
    }
    private static void changed(GameTestHelper h) {
        var p = player(h); var menu = logs(p, 2);
        menu.clicked(0, 0, ClickType.PICKUP, p); ticks(p, 10);
        menu.getSlot(1).set(new ItemStack(Items.OAK_LOG, 3)); ticks(p, 30);
        check(menu.getCarried().isEmpty() && menu.getSlot(1).getItem().getCount() == 3, "Input change cancels without consumption");
        h.succeed();
    }
    private static void closed(GameTestHelper h) {
        var p = player(h); var menu = logs(p, 1);
        menu.clicked(0, 0, ClickType.PICKUP, p); ticks(p, 10);
        p.containerMenu = new InventoryMenu(p.getInventory(), true, p); ticks(p, 30);
        check(menu.getCarried().isEmpty() && menu.getSlot(1).getItem().getCount() == 1, "Old menu cannot complete");
        h.succeed();
    }
    private static void full(GameTestHelper h) {
        var p = player(h); var menu = logs(p, 1);
        for (int i = 0; i < 36; i++) p.getInventory().setItem(i, new ItemStack(Items.STONE, 64));
        menu.clicked(0, 0, ClickType.QUICK_MOVE, p); ticks(p, 40);
        check(menu.getSlot(1).getItem().getCount() == 1 && menu.getCarried().isEmpty(), "Full inventory preserves ingredients");
        h.succeed();
    }
    private static void alternate(GameTestHelper h) {
        var p = player(h); var menu = logs(p, 2);
        for (var action : List.of(ClickType.SWAP, ClickType.THROW, ClickType.CLONE, ClickType.QUICK_CRAFT)) {
            menu.clicked(0, 0, action, p); ticks(p, 25);
            check(menu.getSlot(1).getItem().getCount() == 2 && menu.getCarried().isEmpty(), "Bypass blocked: " + action);
        }
        check(!menu.getSlot(0).mayPickup(p), "Untimed result may not be collected");
        menu.setCarried(new ItemStack(Items.OAK_PLANKS));
        menu.clicked(10, 0, ClickType.PICKUP_ALL, p);
        check(menu.getSlot(1).getItem().getCount() == 2, "Double-click cannot collect recipe results");
        h.succeed();
    }
    private static void capacity(GameTestHelper h) {
        var p = player(h); var menu = logs(p, 1);
        menu.setCarried(new ItemStack(Items.OAK_PLANKS, 62));
        menu.clicked(0, 0, ClickType.PICKUP, p); ticks(p, 40);
        check(menu.getCarried().getCount() == 62 && menu.getSlot(1).getItem().getCount() == 1, "Partial output is never taken");
        h.succeed();
    }
    private static void attribute(GameTestHelper h) {
        var p = player(h); var menu = logs(p, 1);
        p.getAttribute(CraftTakesTime.CRAFTING_SPEED).setBaseValue(2);
        menu.clicked(0, 0, ClickType.PICKUP, p); ticks(p, 9);
        check(menu.getCarried().isEmpty(), "Double speed still waits"); ticks(p, 1);
        check(menu.getCarried().getCount() == 4, "Double speed finishes in 10 ticks");
        h.succeed();
    }
    private static void remainders(GameTestHelper h) {
        var p = player(h);
        var pos = h.absolutePos(new net.minecraft.core.BlockPos(1, 1, 1));
        h.getLevel().setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.CRAFTING_TABLE.defaultBlockState());
        p.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        var menu = new CraftingMenu(1, p.getInventory(), ContainerLevelAccess.create(h.getLevel(), pos));
        p.containerMenu = menu;
        var items = List.of(Items.MILK_BUCKET, Items.MILK_BUCKET, Items.MILK_BUCKET,
            Items.SUGAR, Items.EGG, Items.SUGAR, Items.WHEAT, Items.WHEAT, Items.WHEAT);
        for (int i = 0; i < 9; i++) menu.getSlot(i + 1).set(new ItemStack(items.get(i)));
        menu.slotsChanged(menu.getSlot(1).container);
        check(menu.getSlot(0).getItem().is(Items.CAKE), "Cake preview");
        menu.clicked(0, 0, ClickType.PICKUP, p); ticks(p, 179);
        check(menu.getCarried().isEmpty(), "Nine ingredient recipe waits 180 ticks"); ticks(p, 1);
        check(menu.getCarried().is(Items.CAKE), "Cake crafted through vanilla path");
        for (int i = 1; i <= 3; i++) check(menu.getSlot(i).getItem().is(Items.BUCKET), "Milk bucket returned");
        h.succeed();
    }
    private static void multipliers(GameTestHelper h) {
        var p = player(h);
        var menu = new GrindstoneMenu(1, p.getInventory());
        var shard = new ItemStack(Items.AMETHYST_SHARD, 64);
        var emerald = new ItemStack(Items.EMERALD);
        check(CraftingData.itemTime(shard).ingredientMultiplier() == 2, "Native item tag data map loaded");
        check(CraftingData.itemTime(emerald).outputMultiplier() == 3, "Native output item data map loaded");
        check(CraftingData.ticks(p, menu, List.of(shard), emerald) == 480, "Item, output and menu data maps multiply");
        shard.set(CraftingData.TIME_COMPONENT.get(), new CraftingData.ItemTime(0.5, 1));
        check(CraftingData.ticks(p, menu, List.of(shard), emerald) == 120,
            "Per-stack component overrides item data map; stack count does not affect duration");
        check(CraftingData.ItemTime.CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE,
            com.google.gson.JsonParser.parseString("{\"ingredient_multiplier\":-1}")).error().isPresent(),
            "Invalid multiplier rejected by native codec");
        h.succeed();
    }
    private static void component(GameTestHelper h) {
        var p = player(h); var menu = logs(p, 2);
        var stack = menu.getSlot(1).getItem();
        stack.set(CraftingData.TIME_COMPONENT.get(), new CraftingData.ItemTime(1.5, 1));
        var ops = h.getLevel().registryAccess().createSerializationContext(com.mojang.serialization.JsonOps.INSTANCE);
        var encoded = ItemStack.CODEC.encodeStart(ops, stack).getOrThrow();
        var decoded = ItemStack.CODEC.parse(ops, encoded).getOrThrow();
        check(ItemStack.matches(stack, decoded), "Per-stack component survives native save codec");
        menu.clicked(0, 0, ClickType.PICKUP, p); ticks(p, 29);
        check(menu.getCarried().isEmpty(), "Component controls real crafting job"); ticks(p, 1);
        check(menu.getCarried().getCount() == 4, "Component finishes at 30 ticks");
        menu.setCarried(ItemStack.EMPTY);
        menu.clicked(0, 0, ClickType.PICKUP, p); ticks(p, 5);
        menu.getSlot(1).getItem().set(CraftingData.TIME_COMPONENT.get(), new CraftingData.ItemTime(2, 1));
        ticks(p, 50);
        check(menu.getCarried().isEmpty() && menu.getSlot(1).getItem().getCount() == 1,
            "Changing input components cancels without consuming materials");
        h.succeed();
    }
    private static void anvil(GameTestHelper h) {
        var p = player(h); p.experienceLevel = 20;
        var menu = new AnvilMenu(1, p.getInventory(), ContainerLevelAccess.NULL); p.containerMenu = menu;
        menu.getSlot(0).set(new ItemStack(Items.DIAMOND_SWORD)); menu.setItemName("Timed Sword");
        check(!menu.getSlot(2).getItem().isEmpty(), "Rename preview");
        int cost = menu.getCost();
        check(cost > 0, "Rename costs XP");
        check(menu.getSlot(2).tryRemove(1, 64, p).isEmpty(), "Anvil cannot be collected through safeTake bypass");
        menu.clicked(2, 0, ClickType.PICKUP, p);
        int time = CraftingData.ticks(p, menu, java.util.List.of(new ItemStack(Items.DIAMOND_SWORD)),
            menu.getSlot(2).getItem());
        ticks(p, time - 1);
        check(menu.getCarried().isEmpty() && p.experienceLevel == 20, "No early output or XP cost"); ticks(p, 1);
        check(menu.getCarried().getHoverName().getString().equals("Timed Sword"), "Rename retained");
        check(p.experienceLevel == 20 - cost && menu.getSlot(0).getItem().isEmpty(), "Vanilla XP and input consumed");
        h.succeed();
    }
    private static void smithing(GameTestHelper h) {
        var p = player(h); var menu = new SmithingMenu(1, p.getInventory(), ContainerLevelAccess.NULL); p.containerMenu = menu;
        menu.getSlot(0).set(new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
        menu.getSlot(1).set(new ItemStack(Items.DIAMOND_SWORD));
        menu.getSlot(2).set(new ItemStack(Items.NETHERITE_INGOT)); menu.createResult();
        check(menu.getSlot(3).getItem().is(Items.NETHERITE_SWORD), "Smithing preview");
        check(menu.getSlot(3).tryRemove(1, 64, p).isEmpty(), "Smithing result extraction guarded");
        menu.clicked(3, 0, ClickType.PICKUP, p); ticks(p, 59);
        check(menu.getCarried().isEmpty(), "Smithing waits"); ticks(p, 1);
        check(menu.getCarried().is(Items.NETHERITE_SWORD), "Vanilla smithing result");
        for (int i = 0; i < 3; i++) check(menu.getSlot(i).getItem().isEmpty(), "Smithing inputs consumed");
        h.succeed();
    }
    private static void stonecutter(GameTestHelper h) {
        var p = player(h); var menu = new StonecutterMenu(1, p.getInventory(), ContainerLevelAccess.NULL); p.containerMenu = menu;
        menu.getSlot(0).set(new ItemStack(Items.STONE, 2)); menu.slotsChanged(menu.container);
        check(menu.clickMenuButton(p, 0), "Select stonecutting recipe");
        var output = menu.getSlot(1).getItem().copy(); check(!output.isEmpty(), "Stonecutting preview");
        menu.clicked(1, 0, ClickType.PICKUP, p); ticks(p, 19);
        check(menu.getCarried().isEmpty(), "Stonecutter waits"); ticks(p, 1);
        check(ItemStack.matches(output, menu.getCarried()), "Selected recipe result");
        check(menu.getSlot(0).getItem().getCount() == 1, "One input consumed"); h.succeed();
    }

}
