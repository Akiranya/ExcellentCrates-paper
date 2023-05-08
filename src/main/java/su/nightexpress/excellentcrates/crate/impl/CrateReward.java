package su.nightexpress.excellentcrates.crate.impl;

import cc.mewcraft.mewcore.item.api.PluginItemRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.manager.ICleanable;
import su.nexmedia.engine.api.placeholder.Placeholder;
import su.nexmedia.engine.api.placeholder.PlaceholderMap;
import su.nexmedia.engine.hooks.Hooks;
import su.nexmedia.engine.hooks.misc.PlaceholderHook;
import su.nexmedia.engine.lang.LangManager;
import su.nexmedia.engine.utils.*;
import su.nightexpress.excellentcrates.ExcellentCrates;
import su.nightexpress.excellentcrates.Perms;
import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.config.Lang;
import su.nightexpress.excellentcrates.crate.editor.CrateRewardMainEditor;
import su.nightexpress.excellentcrates.data.impl.CrateUser;
import su.nightexpress.excellentcrates.data.impl.UserRewardData;

import java.util.*;

public class CrateReward implements ICleanable, Placeholder {

    private final Crate crate;
    private final String id;

    private String name;
    private double chance;
    private Rarity rarity;
    private boolean broadcast;
    private int winLimitAmount;
    private long winLimitCooldown;
    private ItemStack preview;
    private List<ItemStack> items;
    private List<String> commands;
    private Set<String> ignoredForPermissions;

    private CrateRewardMainEditor editor;

    private final PlaceholderMap placeholderMap;

    public CrateReward(@NotNull Crate crate, @NotNull String id) {
        this(
            crate,
            id,

            "<green>" + StringUtil.capitalizeFully(id) + "</green>",
            25D,
            crate.plugin().getCrateManager().getMostCommonRarity(),
            false,

            -1,
            0L,

            new ItemStack(Material.EMERALD),
            new ArrayList<>(),
            new ArrayList<>(),
            new HashSet<>()
        );
    }

    public CrateReward(
        @NotNull Crate crate,
        @NotNull String id,

        @NotNull String name,
        double chance,
        @NotNull Rarity rarity,
        boolean broadcast,

        int winLimitAmount,
        long winLimitCooldown,

        @NotNull ItemStack preview,
        @NotNull List<ItemStack> items,
        @NotNull List<String> commands,
        @NotNull Set<String> ignoredForPermissions
    ) {
        this.crate = crate;
        this.id = id.toLowerCase();

        this.setName(name);
        this.setChance(chance);
        this.setRarity(rarity);
        this.setBroadcast(broadcast);

        this.setWinLimitAmount(winLimitAmount);
        this.setWinLimitCooldown(winLimitCooldown);

        this.setItems(items);
        this.setCommands(commands);
        this.setPreview(preview);
        this.setIgnoredForPermissions(ignoredForPermissions);

        this.placeholderMap = new PlaceholderMap()
            .add(Placeholders.REWARD_ID, this::getId)
            .add(Placeholders.REWARD_NAME, this::getName)
            .add(Placeholders.REWARD_CHANCE, () -> NumberUtil.format(this.getChance()))
            .add(Placeholders.REWARD_RARITY_NAME, () -> this.getRarity().getName())
            .add(Placeholders.REWARD_RARITY_CHANCE, () -> NumberUtil.format(this.getRarity().getChance()))
            .add(Placeholders.REWARD_BROADCAST, () -> LangManager.getBoolean(this.isBroadcast()))
            .add(Placeholders.REWARD_PREVIEW_NAME, () -> ComponentUtil.asMiniMessage(ItemUtil.getName(this.getPreview())))
            //.add(Placeholders.REWARD_PREVIEW_LORE, () -> String.join("\n", ItemUtil.getLore(this.getPreview())))
            //.add(Placeholders.REWARD_IGNORED_FOR_PERMISSIONS, () -> String.join("\n", this.getIgnoredForPermissions()))
            //.add(Placeholders.REWARD_COMMANDS, () -> String.join("\n", this.getCommands()))
            .add(Placeholders.REWARD_WIN_LIMIT_AMOUNT, () -> {
                if (!this.isWinLimitedAmount()) {return LangManager.getPlain(Lang.OTHER_INFINITY);}
                return String.valueOf(this.getWinLimitAmount());
            })
            .add(Placeholders.REWARD_WIN_LIMIT_COOLDOWN, () -> {
                if (!this.isWinLimitedCooldown()) {return LangManager.getPlain(Lang.OTHER_NO);}
                return this.getWinLimitCooldown() > 0
                    ? TimeUtil.formatTime(this.getWinLimitCooldown() * 1000L)
                    : LangManager.getPlain(Lang.OTHER_ONE_TIMED);
            })
        ;
        this.placeholderMap.getKeys().addAll(this.getRarity().getPlaceholders().getKeys());
    }

    @Override
    public @NotNull PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    public @NotNull ExcellentCrates plugin() {
        return this.getCrate().plugin();
    }

    public @NotNull CrateRewardMainEditor getEditor() {
        if (this.editor == null) {
            this.editor = new CrateRewardMainEditor(this);
        }
        return this.editor;
    }

    @Override
    public void clear() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
    }

    public @NotNull String getId() {
        return this.id;
    }

    public @NotNull Crate getCrate() {
        return this.crate;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public double getChance() {
        return this.chance;
    }

    public void setChance(double chance) {
        this.chance = Math.max(0, chance);
    }

    public @NotNull Rarity getRarity() {
        return rarity;
    }

    public void setRarity(@NotNull Rarity rarity) {
        this.rarity = rarity;
    }

    public boolean isBroadcast() {
        return broadcast;
    }

    public void setBroadcast(boolean broadcast) {
        this.broadcast = broadcast;
    }

    public boolean isWinLimitedAmount() {
        return this.getWinLimitAmount() >= 0;
    }

    public boolean isWinLimitedCooldown() {
        return this.getWinLimitCooldown() != 0;
    }

    public boolean isWinLimitedOnce() {
        return this.getWinLimitAmount() == 1 || this.getWinLimitCooldown() < 0;
    }

    public int getWinLimitAmount() {
        return winLimitAmount;
    }

    public void setWinLimitAmount(int winLimitAmount) {
        this.winLimitAmount = winLimitAmount;
    }

    public long getWinLimitCooldown() {
        return winLimitCooldown;
    }

    public void setWinLimitCooldown(long winLimitCooldown) {
        this.winLimitCooldown = winLimitCooldown;
    }

    public boolean canWin(@NotNull Player player) {
        if (this.getIgnoredForPermissions().stream().anyMatch(player::hasPermission)) {
            return false;
        }
        if (this.isWinLimitedAmount() || this.isWinLimitedCooldown()) {
            CrateUser user = plugin().getUserManager().getUserData(player);
            UserRewardData winLimit = user.getRewardWinLimit(this);
            if (winLimit == null) return true;
            return winLimit.isExpired() && !winLimit.isDrained(this);
        }
        return true;
    }

    public @NotNull ItemStack getPreview() {
        return new ItemStack(this.preview);
    }

    public void setPreview(@NotNull ItemStack item) {
        this.preview = item.clone();
    }

    public @NotNull List<String> getCommands() {
        return this.commands;
    }

    public void setCommands(@NotNull List<String> commands) {
        this.commands = new ArrayList<>(commands);
        this.commands.removeIf(String::isEmpty);
    }

    public @NotNull Set<String> getIgnoredForPermissions() {
        return ignoredForPermissions;
    }

    public void setIgnoredForPermissions(@NotNull Set<String> ignoredForPermissions) {
        this.ignoredForPermissions = ignoredForPermissions;
    }

    public @NotNull List<ItemStack> getItems() {
        return this.items;
    }

    public void setItems(@NotNull List<ItemStack> items) {
        this.items = new ArrayList<>(items.stream().limit(27).toList());
        this.items.removeIf(item -> item == null || item.getType().isAir());

        // Custom plugin item integration - start
        ListIterator<ItemStack> it = this.items.listIterator();
        while (it.hasNext()) {
            ItemStack itemStack = it.next();
            itemStack = PluginItemRegistry.get().refreshItemStack(itemStack);
            it.set(itemStack);
        }
        // Custom plugin item integration - end
    }

    public void addItem(@NotNull ItemStack item) {
        this.items.add(PluginItemRegistry.get().refreshItemStack(item));
    }

    public void give(@NotNull Player player) {
        this.getItems().forEach(item -> {
            ItemStack give = item.clone();
            if (Config.CRATE_PLACEHOLDER_API_FOR_REWARDS.get()) {
                ItemUtil.setPlaceholderAPI(give, player);
            }
            PlayerUtil.addItem(player, give);
        });
        this.getCommands().forEach(command -> {
            if (Hooks.hasPlaceholderAPI()) {
                command = PlaceholderHook.setPlaceholders(player, command);
            }
            PlayerUtil.dispatchCommand(player, command);
        });

        this.plugin().getMessage(Lang.CRATE_OPEN_REWARD_INFO)
            .replace(this.getCrate().replacePlaceholders())
            .replace(this.replacePlaceholders())
            .send(player);

        if (this.isBroadcast()) {
            this.plugin().getMessage(Lang.CRATE_OPEN_REWARD_BROADCAST)
                .replace(Placeholders.Player.replacer(player))
                .replace(this.getCrate().replacePlaceholders())
                .replace(this.replacePlaceholders())
                .broadcast();
        }

        if (this.isWinLimitedAmount() || this.isWinLimitedCooldown()) {
            CrateUser user = plugin().getUserManager().getUserData(player);
            UserRewardData winLimit = user.getRewardWinLimit(this);
            if (winLimit == null) winLimit = new UserRewardData(0, 0);

            if (!player.hasPermission(Perms.BYPASS_REWARD_LIMIT_AMOUNT)) {
                if (this.isWinLimitedAmount()) winLimit.setAmount(winLimit.getAmount() + 1);
            }
            if (!player.hasPermission(Perms.BYPASS_REWARD_LIMIT_COOLDOWN)) {
                if (this.isWinLimitedCooldown()) {
                    winLimit.setExpireDate(this.getWinLimitCooldown() < 0 ? -1L : System.currentTimeMillis() + this.getWinLimitCooldown() * 1000L);
                }
            }
            user.setRewardWinLimit(this, winLimit);
        }
    }
}
