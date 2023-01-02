package su.nightexpress.excellentcrates.crate;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.menu.AbstractMenuAuto;
import su.nexmedia.engine.api.menu.MenuClick;
import su.nexmedia.engine.api.menu.MenuItem;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.utils.ComponentUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.excellentcrates.ExcellentCrates;
import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.excellentcrates.data.CrateUser;
import su.nightexpress.excellentcrates.data.UserRewardWinLimit;

import java.util.ArrayList;
import java.util.List;

public class CratePreview extends AbstractMenuAuto<ExcellentCrates, CrateReward> {

    private static final String PLACEHOLDER_WIN_LIMIT_AMOUNT   = "%win_limit_amount%";
    private static final String PLACEHOLDER_WIN_LIMIT_COOLDOWN = "%win_limit_cooldown%";
    private static final String PLACEHOLDER_WIN_LIMIT_DRAINED  = "%win_limit_drained%";

    private final Crate crate;
    private final int[]        rewardSlots;
    private final String       rewardName;
    private final List<String> rewardLore;
    private final List<String> rewardLoreLimitAmount;
    private final List<String> rewardLoreLimitCoolown;
    private final List<String> rewardLoreLimitDrained;
    private final boolean      hideDrainedRewards;

    public CratePreview(@NotNull Crate crate, @NotNull JYML cfg) {
        super(crate.plugin(), cfg, "");
        this.crate = crate;
        this.title = ComponentUtil.replace(this.title, crate.replacePlaceholders());

        this.hideDrainedRewards = cfg.getBoolean("Reward.Hide_Drained_Rewards");
        this.rewardSlots = cfg.getIntArray("Reward.Slots");
        this.rewardName = cfg.getString("Reward.Name", Placeholders.REWARD_PREVIEW_NAME);
        this.rewardLore = cfg.getStringList("Reward.Lore.Default");
        this.rewardLoreLimitAmount = cfg.getStringList("Reward.Lore.Win_Limit.Amount");
        this.rewardLoreLimitCoolown = cfg.getStringList("Reward.Lore.Win_Limit.Cooldown");
        this.rewardLoreLimitDrained = cfg.getStringList("Reward.Lore.Win_Limit.Drained");

        MenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.CLOSE) {
                    player.closeInventory();
                }
                else this.onItemClickDefault(player, type2);
            }
        };

        for (String id : cfg.getSection("Content")) {
            MenuItem menuItem = cfg.getMenuItem("Content." + id, MenuItemType.class);

            if (menuItem.getType() != null) {
                menuItem.setClickHandler(click);
            }
            this.addItem(menuItem);
        }
    }

    @Override
    protected int[] getObjectSlots() {
        return this.rewardSlots;
    }

    @Override
    @NotNull
    protected List<CrateReward> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.hideDrainedRewards ? crate.getRewards(player) : crate.getRewards());
    }

    @Override
    @NotNull
    protected ItemStack getObjectStack(@NotNull Player player, @NotNull CrateReward reward) {
        ItemStack item = reward.getPreview();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        CrateUser crateUser = plugin.getUserManager().getUserData(player);
        UserRewardWinLimit rewardLimit = crateUser.getRewardWinLimit(reward);

        List<String> lore = new ArrayList<>(this.rewardLore);
        if (rewardLimit == null || rewardLimit.isDrained(reward) || !reward.isWinLimitedAmount())
            lore.remove(PLACEHOLDER_WIN_LIMIT_AMOUNT);
        if (rewardLimit == null || rewardLimit.isDrained(reward) || rewardLimit.isExpired())
            lore.remove(PLACEHOLDER_WIN_LIMIT_COOLDOWN);
        if (rewardLimit == null || !rewardLimit.isDrained(reward)) lore.remove(PLACEHOLDER_WIN_LIMIT_DRAINED);

        lore = StringUtil.replace(lore, PLACEHOLDER_WIN_LIMIT_AMOUNT, false, this.rewardLoreLimitAmount);
        lore = StringUtil.replace(lore, PLACEHOLDER_WIN_LIMIT_COOLDOWN, false, this.rewardLoreLimitCoolown);
        lore = StringUtil.replace(lore, PLACEHOLDER_WIN_LIMIT_DRAINED, false, this.rewardLoreLimitDrained);
        lore.replaceAll(crateUser.replacePlaceholers(reward));

        meta.displayName(ComponentUtil.asComponent(this.rewardName));
        meta.lore(ComponentUtil.asComponent(lore));
        item.setItemMeta(meta);

        ItemUtil.replace(item, reward.replacePlaceholders());
        ItemUtil.replace(item, this.crate.replacePlaceholders());
        return item;
    }

    @Override
    @NotNull
    protected MenuClick getObjectClick(@NotNull Player player, @NotNull CrateReward reward) {
        return (player1, type, e) -> {

        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent e, @NotNull SlotType slotType) {
        return true;
    }
}
