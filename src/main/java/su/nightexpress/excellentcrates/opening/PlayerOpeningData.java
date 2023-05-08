package su.nightexpress.excellentcrates.opening;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.opening.animation.AnimationTask;
import su.nightexpress.excellentcrates.opening.slider.SliderTask;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class PlayerOpeningData {

    public static final Map<Player, PlayerOpeningData> PLAYERS = new WeakHashMap<>();

    private final Player player;
    private final Crate crate;
    private final Inventory inventory;
    private final Map<String, SliderTask> sliderTasks;
    private final Map<String, AnimationTask> animationTasks;

    private PlayerOpeningData(@NotNull Player player, @NotNull Crate crate, @NotNull Inventory inventory) {
        this.player = player;
        this.crate = crate;
        this.inventory = inventory;
        this.sliderTasks = new LinkedHashMap<>();
        this.animationTasks = new LinkedHashMap<>();
    }

    public static @NotNull PlayerOpeningData create(@NotNull Player player, @NotNull Crate crate, @NotNull Inventory inventory) {
        PlayerOpeningData has = get(player);
        if (has != null) throw new IllegalStateException("Player is already opening a crate!");

        PlayerOpeningData data = new PlayerOpeningData(player, crate, inventory);
        PLAYERS.put(player, data);
        return data;
    }

    public static @Nullable PlayerOpeningData get(@NotNull Player player) {
        return PLAYERS.get(player);
    }

    public static void clean(@NotNull Player player) {
        PlayerOpeningData data = get(player);
        if (data == null) return;

        data.stop(true);
        PLAYERS.remove(player);
    }

    public void stop(boolean force) {
        this.getSliderTasks().values().forEach(task -> task.stop(force));
        this.getAnimationTasks().values().forEach(task -> task.stop(force));
    }

    public boolean canSkip() {
        return this.getSliderTasks().values().stream().allMatch(SliderTask::canSkip) && this.getAnimationTasks().values().stream().allMatch(AnimationTask::canSkip);
    }

    public boolean isActive() {
        return this.getSliderTasks().values().stream().allMatch(task -> task.isStarted() && !task.isCancelled());
    }

    public boolean isCompleted() {
        return this.getSliderTasks().values().stream().allMatch(task -> task.isStarted() && task.isCancelled());
    }

    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Crate getCrate() {
        return crate;
    }

    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public @NotNull Map<String, SliderTask> getSliderTasks() {
        return sliderTasks;
    }

    public @NotNull Map<String, AnimationTask> getAnimationTasks() {
        return animationTasks;
    }
}
