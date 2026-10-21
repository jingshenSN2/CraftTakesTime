package sn2.crafttakestime.server;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import sn2.crafttakestime.CraftTakesTime;
import sn2.crafttakestime.api.CraftingAdapter;
import sn2.crafttakestime.api.CraftingAdapters;
import sn2.crafttakestime.network.ProgressPayload;
import java.util.*;

public final class CraftJobs {
    private static final Map<UUID, Job> JOBS = new HashMap<>();
    // Completion authorization is scoped to one synchronous menu call, never stored on the client.
    private static final ThreadLocal<AbstractContainerMenu> AUTHORIZED = new ThreadLocal<>();
    private static final class Job {
        final AbstractContainerMenu menu;
        final CraftingAdapter adapter;
        final List<ItemStack> inputs;
        final ItemStack output;
        final ItemStack cursor;
        final boolean repeat;
        final int total;
        int elapsed;
        Job(ServerPlayer player, AbstractContainerMenu menu, CraftingAdapter adapter, boolean repeat) {
            this.menu = menu; this.adapter = adapter; this.repeat = repeat;
            inputs = adapter.inputs(menu).stream().map(ItemStack::copy).toList();
            output = menu.getSlot(adapter.resultSlot(menu)).getItem().copy();
            cursor = menu.getCarried().copy();
            total = CraftTakesTime.CONFIG.ticks(inputs, output, adapter.menuId(menu), player.experienceLevel,
                player.getAttributeValue(CraftTakesTime.CRAFTING_SPEED));
        }
    }
    private CraftJobs() {}
    public static boolean authorized(AbstractContainerMenu menu) { return AUTHORIZED.get() == menu; }
    public static boolean mayTake(Slot slot, Player player) {
        var menu = player.containerMenu;
        if (authorized(menu)) return true;
        var adapter = CraftingAdapters.find(menu);
        return adapter == null || adapter.resultSlot(menu) < 0 || menu.getSlot(adapter.resultSlot(menu)) != slot;
    }
    /** Invoked on both logical sides; the normal container packet is the start request. */
    public static boolean intercept(AbstractContainerMenu menu, int index, int button, ContainerInput input, Player player) {
        if (authorized(menu)) return false;
        var adapter = CraftingAdapters.find(menu);
        if (adapter == null) return false;
        if (index != adapter.resultSlot(menu)) {
            // Explicit interactions interrupt the task; subsequent ticks also compare exact snapshots.
            if (player instanceof ServerPlayer serverPlayer) cancel(serverPlayer);
            return false;
        }
        if (player instanceof ServerPlayer serverPlayer && player.containerMenu == menu) {
            if ((input == ContainerInput.PICKUP || input == ContainerInput.QUICK_MOVE) && (button == 0 || button == 1)
                    && !menu.getSlot(index).getItem().isEmpty() && menu.stillValid(player) && adapter.canCraft(serverPlayer, menu)) {
                Job current = JOBS.get(player.getUUID());
                if (current == null || current.menu != menu) {
                    Job job = new Job(serverPlayer, menu, adapter, input == ContainerInput.QUICK_MOVE);
                    if (canReceive(serverPlayer, job)) { JOBS.put(player.getUUID(), job); sync(serverPlayer, job); }
                }
            } else cancel(serverPlayer);
            menu.broadcastChanges();
        }
        // Number keys, dropping, cloning and drag actions never extract an untimed recipe.
        return true;
    }
    private static boolean same(List<ItemStack> a, List<ItemStack> b, boolean counts) {
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) {
            if (counts ? !ItemStack.matches(a.get(i), b.get(i)) : !ItemStack.isSameItemSameComponents(a.get(i), b.get(i))) return false;
        }
        return true;
    }
    private static boolean valid(ServerPlayer player, Job job) {
        return player.isAlive() && !player.isSpectator() && player.containerMenu == job.menu && job.menu.stillValid(player)
            && job.adapter.canCraft(player, job.menu) && same(job.inputs, job.adapter.inputs(job.menu), true)
            && ItemStack.matches(job.output, job.menu.getSlot(job.adapter.resultSlot(job.menu)).getItem())
            && ItemStack.matches(job.cursor, job.menu.getCarried()) && canReceive(player, job);
    }
    private static boolean canReceive(ServerPlayer player, Job job) {
        ItemStack carried = job.menu.getCarried();
        if (job.repeat) {
            if (!carried.isEmpty()) return false;
            int space = 0;
            for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
                if (stack.isEmpty()) space += Math.min(job.output.getMaxStackSize(), player.getInventory().getMaxStackSize());
                else if (ItemStack.isSameItemSameComponents(stack, job.output))
                    space += Math.max(0, Math.min(stack.getMaxStackSize(), player.getInventory().getMaxStackSize()) - stack.getCount());
            }
            return space >= job.output.getCount();
        }
        return carried.isEmpty() || (ItemStack.isSameItemSameComponents(carried, job.output)
            && carried.getCount() + job.output.getCount() <= carried.getMaxStackSize());
    }
    public static void tick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        Job job = JOBS.get(player.getUUID());
        if (job == null) return;
        if (!valid(player, job)) { cancel(player); return; }
        if (++job.elapsed < job.total) { sync(player, job); return; }
        JOBS.remove(player.getUUID());
        ItemStack previousCursor = job.menu.getCarried().copy();
        AUTHORIZED.set(job.menu);
        try { job.adapter.completeOne(player, job.menu); }
        finally { AUTHORIZED.remove(); }
        ItemStack crafted = job.menu.getCarried();
        boolean success = !ItemStack.matches(previousCursor, crafted);
        if (job.repeat && success) {
            // PICKUP executes one recipe. QUICK_MOVE would loop through every available recipe.
            ItemStack remaining = crafted.copy();
            job.menu.setCarried(ItemStack.EMPTY);
            player.getInventory().add(remaining);
            // Crafting remainders or other event listeners may have occupied the reserved space.
            if (!remaining.isEmpty()) job.menu.setCarried(remaining);
        }
        job.menu.broadcastChanges();
        PacketDistributor.sendToPlayer(player, new ProgressPayload(job.menu.containerId, job.adapter.resultSlot(job.menu),
            job.total, 0, success && CraftTakesTime.CONFIG.enableCraftingSound));
        if (job.repeat && success && player.containerMenu == job.menu
                && same(job.inputs, job.adapter.inputs(job.menu), false)
                && ItemStack.matches(job.output, job.menu.getSlot(job.adapter.resultSlot(job.menu)).getItem())) {
            Job next = new Job(player, job.menu, job.adapter, true);
            if (canReceive(player, next)) { JOBS.put(player.getUUID(), next); sync(player, next); }
        }
    }
    private static void sync(ServerPlayer player, Job job) {
        PacketDistributor.sendToPlayer(player, new ProgressPayload(job.menu.containerId, job.adapter.resultSlot(job.menu),
            job.elapsed, job.total, CraftTakesTime.CONFIG.enableCraftingSound));
    }
    private static void cancel(ServerPlayer player) {
        Job old = JOBS.remove(player.getUUID());
        if (old != null) PacketDistributor.sendToPlayer(player, new ProgressPayload(old.menu.containerId,
            old.adapter.resultSlot(old.menu), 0, 0, false));
    }
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) { JOBS.remove(event.getEntity().getUUID()); }
}
