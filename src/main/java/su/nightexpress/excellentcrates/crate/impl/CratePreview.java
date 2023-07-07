package su.nightexpress.excellentcrates.crate.impl;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.click.ClickHandler;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.utils.ComponentUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nexmedia.engine.utils.TimeUtil;
import su.nightexpress.excellentcrates.ExcellentCrates;
import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.excellentcrates.data.impl.CrateUser;
import su.nightexpress.excellentcrates.data.impl.UserRewardData;

import java.util.ArrayList;
import java.util.List;

public class CratePreview extends ConfigMenu<ExcellentCrates> implements AutoPaged<CrateReward> {

    private static final String PLACEHOLDER_WIN_LIMIT_AMOUNT = "%win_limit_amount%";
    private static final String PLACEHOLDER_WIN_LIMIT_COOLDOWN = "%win_limit_cooldown%";
    private static final String PLACEHOLDER_WIN_LIMIT_DRAINED = "%win_limit_drained%";

    private final Crate crate;
    private final int[] rewardSlots;
    private final String rewardName;
    private final List<String> rewardLore;
    private final List<String> rewardLoreLimitAmount;
    private final List<String> rewardLoreLimitCooldown; // Mewcraft - fix typo
    private final List<String> rewardLoreLimitDrained;
    private final boolean hideDrainedRewards;

    public CratePreview(@NotNull Crate crate, @NotNull JYML cfg) {
        super(crate.plugin(), cfg);
        this.crate = crate;

        this.hideDrainedRewards = cfg.getBoolean("Reward.Hide_Drained_Rewards");
        this.rewardSlots = cfg.getIntArray("Reward.Slots");
        this.rewardName = cfg.getString("Reward.Name", Placeholders.REWARD_PREVIEW_NAME); // Mewcraft - no legacy color
        this.rewardLore = cfg.getStringList("Reward.Lore.Default");
        this.rewardLoreLimitAmount = cfg.getStringList("Reward.Lore.Win_Limit.Amount");
        this.rewardLoreLimitCooldown = cfg.getStringList("Reward.Lore.Win_Limit.Cooldown"); // Mewcraft - fix type
        this.rewardLoreLimitDrained = cfg.getStringList("Reward.Lore.Win_Limit.Drained");

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, (viewer, event) -> {
                this.plugin.runTask(task -> viewer.getPlayer().closeInventory());
            })
            .addClick(MenuItemType.PAGE_NEXT, ClickHandler.forNextPage(this))
            .addClick(MenuItemType.PAGE_PREVIOUS, ClickHandler.forPreviousPage(this));

        this.load();
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        options.setTitle(this.crate.replacePlaceholders().apply(options.getTitle()));
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return this.rewardSlots;
    }

    @Override
    @NotNull
    public List<CrateReward> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.hideDrainedRewards ? crate.getRewards(player) : crate.getRewards());
    }

    @Override
    @NotNull
    public ItemStack getObjectStack(@NotNull Player player, @NotNull CrateReward reward) {
        ItemStack item = reward.getPreview();
        item.editMeta(meta -> {
            CrateUser user = plugin.getUserManager().getUserData(player);
            UserRewardData rewardData = user.getRewardWinLimit(reward);

            List<String> lore = new ArrayList<>(this.rewardLore);
            if (rewardData == null || rewardData.isDrained(reward) || !reward.isWinLimitedAmount())
                lore.remove(PLACEHOLDER_WIN_LIMIT_AMOUNT);
            if (rewardData == null || rewardData.isDrained(reward) || rewardData.isExpired())
                lore.remove(PLACEHOLDER_WIN_LIMIT_COOLDOWN);
            if (rewardData == null || !rewardData.isDrained(reward))
                lore.remove(PLACEHOLDER_WIN_LIMIT_DRAINED);

            lore = StringUtil.replacePlaceholderList(PLACEHOLDER_WIN_LIMIT_AMOUNT, lore, this.rewardLoreLimitAmount);
            lore = StringUtil.replacePlaceholderList(PLACEHOLDER_WIN_LIMIT_COOLDOWN, lore, this.rewardLoreLimitCooldown);
            lore = StringUtil.replacePlaceholderList(PLACEHOLDER_WIN_LIMIT_DRAINED, lore, this.rewardLoreLimitDrained);

            // Mewcraft - fix placeholder list
            lore = StringUtil.replacePlaceholderList(Placeholders.REWARD_PREVIEW_LORE, lore, ComponentUtil.asMiniMessage(ItemUtil.getLore(reward.getPreview())));
            lore = StringUtil.compressEmptyLines(lore);

            int amountLeft = rewardData == null ? reward.getWinLimitAmount() : reward.getWinLimitAmount() - rewardData.getAmount();
            long expireIn = rewardData == null ? 0L : rewardData.getExpireDate();

            lore.replaceAll(str -> str
                .replace(Placeholders.GENERIC_AMOUNT, String.valueOf(amountLeft))
                .replace(Placeholders.GENERIC_TIME, TimeUtil.formatTimeLeft(expireIn))
            );

            // Mewcraft start
            meta.displayName(ComponentUtil.asComponent(this.rewardName));
            meta.lore(ComponentUtil.asComponent(lore));

            ItemUtil.replaceNameAndLore(meta, reward.replacePlaceholders());
            ItemUtil.replaceNameAndLore(meta, this.crate.replacePlaceholders());
            // Mewcraft end
        });
        return item;
    }

    @Override
    @NotNull
    public ItemClick getObjectClick(@NotNull CrateReward reward) {
        return (viewer, event) -> {

        };
    }
}
