package sn2.crafttakestime.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import sn2.crafttakestime.CraftTakesTime;
import sn2.crafttakestime.config.CraftTime;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** Native datapack data maps and persistent per-stack components; no private config files. */
public final class CraftingData {
    public static final Codec<Double> MULTIPLIER = Codec.DOUBLE.validate(value ->
        Double.isFinite(value) && value >= 0 && value <= 1_000_000
            ? DataResult.success(value) : DataResult.error(() -> "Multiplier must be finite and between 0 and 1000000"));

    public record ItemTime(double ingredientMultiplier, double outputMultiplier) {
        public static final ItemTime DEFAULT = new ItemTime(1, 1);
        public static final Codec<ItemTime> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MULTIPLIER.optionalFieldOf("ingredient_multiplier", 1.0).forGetter(ItemTime::ingredientMultiplier),
            MULTIPLIER.optionalFieldOf("output_multiplier", 1.0).forGetter(ItemTime::outputMultiplier)
        ).apply(instance, ItemTime::new));
    }

    public record PlayerSettings(double globalMultiplier, double inventoryMultiplier, boolean craftingSound,
                                 Map<String, Double> ingredientModMultipliers, Map<String, Double> outputModMultipliers) {
        public static final PlayerSettings DEFAULT = new PlayerSettings(1, 1, true, Map.of(), Map.of());
        public static final Codec<PlayerSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            MULTIPLIER.optionalFieldOf("global_multiplier", 1.0).forGetter(PlayerSettings::globalMultiplier),
            MULTIPLIER.optionalFieldOf("inventory_multiplier", 1.0).forGetter(PlayerSettings::inventoryMultiplier),
            Codec.BOOL.optionalFieldOf("crafting_sound", true).forGetter(PlayerSettings::craftingSound),
            Codec.unboundedMap(Codec.STRING, MULTIPLIER).optionalFieldOf("ingredient_mod_multipliers", Map.of())
                .forGetter(PlayerSettings::ingredientModMultipliers),
            Codec.unboundedMap(Codec.STRING, MULTIPLIER).optionalFieldOf("output_mod_multipliers", Map.of())
                .forGetter(PlayerSettings::outputModMultipliers)
        ).apply(instance, PlayerSettings::new));
        public PlayerSettings {
            ingredientModMultipliers = Map.copyOf(ingredientModMultipliers);
            outputModMultipliers = Map.copyOf(outputModMultipliers);
        }
    }

    public static final DataMapType<Item, ItemTime> ITEM_TIMES = DataMapType.builder(
        Identifier.fromNamespaceAndPath(CraftTakesTime.MOD_ID, "crafting_time"), Registries.ITEM, ItemTime.CODEC)
        .synced(ItemTime.CODEC, true).build();
    public static final DataMapType<MenuType<?>, Double> MENU_TIMES = DataMapType.builder(
        Identifier.fromNamespaceAndPath(CraftTakesTime.MOD_ID, "crafting_time"), Registries.MENU, MULTIPLIER)
        .synced(MULTIPLIER, true).build();
    // The player entity type carries the common policy; InventoryMenu has no registered MenuType.
    public static final DataMapType<EntityType<?>, PlayerSettings> PLAYER_SETTINGS = DataMapType.builder(
        Identifier.fromNamespaceAndPath(CraftTakesTime.MOD_ID, "crafting_settings"), Registries.ENTITY_TYPE, PlayerSettings.CODEC)
        .synced(PlayerSettings.CODEC, true).build();
    private static final DeferredRegister<DataComponentType<?>> COMPONENTS =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, CraftTakesTime.MOD_ID);
    public static final Supplier<DataComponentType<ItemTime>> TIME_COMPONENT = COMPONENTS.register("crafting_time", () ->
        DataComponentType.<ItemTime>builder().persistent(ItemTime.CODEC)
            .networkSynchronized(ByteBufCodecs.fromCodec(ItemTime.CODEC)).build());
    private static volatile long revision;

    private CraftingData() {}
    public static void register(IEventBus bus) {
        COMPONENTS.register(bus);
        bus.addListener((RegisterDataMapTypesEvent event) -> {
            event.register(ITEM_TIMES);
            event.register(MENU_TIMES);
            event.register(PLAYER_SETTINGS);
        });
    }
    public static void updated(DataMapsUpdatedEvent event) {
        if (event.getCause() == DataMapsUpdatedEvent.UpdateCause.SERVER_RELOAD
                && (event.getRegistryKey() == Registries.ITEM || event.getRegistryKey() == Registries.MENU
                    || event.getRegistryKey() == Registries.ENTITY_TYPE)) revision++;
    }
    public static long revision() { return revision; }
    public static PlayerSettings settings(ServerPlayer player) {
        var settings = player.getType().builtInRegistryHolder().getData(PLAYER_SETTINGS);
        return settings == null ? PlayerSettings.DEFAULT : settings;
    }
    public static ItemTime itemTime(ItemStack stack) {
        var component = stack.get(TIME_COMPONENT.get());
        if (component != null) return component;
        var data = BuiltInRegistries.ITEM.wrapAsHolder(stack.getItem()).getData(ITEM_TIMES);
        return data == null ? ItemTime.DEFAULT : data;
    }
    public static int ticks(ServerPlayer player, AbstractContainerMenu menu, List<ItemStack> inputs, ItemStack output) {
        var settings = settings(player);
        double ingredients = inputs.stream().filter(stack -> !stack.isEmpty()).mapToDouble(stack ->
            itemTime(stack).ingredientMultiplier() * settings.ingredientModMultipliers().getOrDefault(
                BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace(), 1.0)).sum();
        double product = itemTime(output).outputMultiplier() * settings.outputModMultipliers().getOrDefault(
            BuiltInRegistries.ITEM.getKey(output.getItem()).getNamespace(), 1.0);
        Double menuTime = menu instanceof InventoryMenu ? settings.inventoryMultiplier()
            : BuiltInRegistries.MENU.wrapAsHolder(menu.getType()).getData(MENU_TIMES);
        return CraftTime.ticks(ingredients, product, settings.globalMultiplier(), menuTime == null ? 1 : menuTime,
            player.experienceLevel, player.getAttributeValue(CraftTakesTime.CRAFTING_SPEED));
    }
}
