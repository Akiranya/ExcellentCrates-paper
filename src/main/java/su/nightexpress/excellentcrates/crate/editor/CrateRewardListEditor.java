package su.nightexpress.excellentcrates.crate.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.menu.AutoPaged;
import su.nexmedia.engine.api.menu.click.ClickType;
import su.nexmedia.engine.api.menu.click.ItemClick;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuOptions;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ComponentUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.excellentcrates.ExcellentCrates;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.config.Lang;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.crate.impl.CrateReward;
import su.nightexpress.excellentcrates.editor.EditorLocales;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class CrateRewardListEditor extends EditorMenu<ExcellentCrates, Crate> implements AutoPaged<CrateReward> {

    public CrateRewardListEditor(@NotNull Crate crate) {
        super(crate.plugin(), crate, Config.EDITOR_TITLE_CRATE.get(), 45);

        this.addReturn(40).setClick((viewer, event) -> {
            this.plugin.runTask(task -> crate.getEditor().open(viewer.getPlayer(), 1));
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.REWARD_CREATE, 42).setClick((viewer, event) -> {
            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir()) {
                String id = StringUtil.lowerCaseUnderscore(ComponentUtil.asMiniMessage(ItemUtil.getName(cursor)));
                int count = 0;
                while (crate.getReward(count == 0 ? id : id + count) != null) {
                    count++;
                }
                CrateReward reward = new CrateReward(crate, count == 0 ? id : id + count);
                reward.setName(ComponentUtil.asMiniMessage(ItemUtil.getName(cursor)));
                reward.getItems().add(new ItemStack(cursor));
                reward.setPreview(cursor);
                crate.addReward(reward);
                event.getView().setCursor(null);
                this.save(viewer);
                return;
            }

            this.startEdit(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_REWARD_ENTER_ID), chat -> {
                String id = StringUtil.lowerCaseUnderscore(chat.getMessage());
                if (crate.getReward(id) != null) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_REWARD_ERROR_CREATE_EXIST).getLocalized());
                    return false;
                }
                CrateReward reward = new CrateReward(crate, id);
                crate.addReward(reward);
                return true;
            });
        });

        this.addItem(Material.HOPPER, EditorLocales.REWARD_SORT, 38).setClick((viewer, event) -> {
            Comparator<CrateReward> comparator;
            ClickType type = ClickType.from(event);
            if (type == ClickType.NUMBER_1) {
                comparator = Comparator.comparingDouble(CrateReward::getChance).reversed();
            } else if (type == ClickType.NUMBER_2) {
                comparator = Comparator.comparing(r -> r.getPreview().getType().name());
            } else if (type == ClickType.NUMBER_3) {
                comparator = Comparator.comparing(r -> ComponentUtil.asPlainText(ItemUtil.getName(r.getPreview())));
            } else if (type == ClickType.NUMBER_4) {
                comparator = Comparator.comparingDouble((CrateReward r) -> r.getRarity().getChance()).reversed();
            } else return;
            crate.setRewards(crate.getRewards().stream().sorted(comparator).toList());
            this.save(viewer);
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
    }

    @Override
    public void onPrepare(@NotNull MenuViewer viewer, @NotNull MenuOptions options) {
        super.onPrepare(viewer, options);
        this.getItemsForPage(viewer).forEach(this::addItem);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    public @NotNull List<CrateReward> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getRewards());
    }

    @Override
    public @NotNull Comparator<CrateReward> getObjectSorter() {
        return ((o1, o2) -> 0);
    }

    @Override
    public @NotNull ItemStack getObjectStack(@NotNull Player player, @NotNull CrateReward reward) {
        ItemStack item = new ItemStack(reward.getPreview());
        item.editMeta(meta -> {
            meta.addItemFlags(ItemFlag.values());
            meta.displayName(ComponentUtil.asComponent(EditorLocales.REWARD_OBJECT.getLocalizedName()));
            meta.lore(ComponentUtil.asComponent(EditorLocales.REWARD_OBJECT.getLocalizedLore()));
            ItemUtil.replaceNameAndLore(meta, reward.replacePlaceholders());
        });
        return item;
    }

    @Override
    public @NotNull ItemClick getObjectClick(@NotNull CrateReward reward) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();
            if (event.getClick() == org.bukkit.event.inventory.ClickType.DROP) {
                this.object.removeReward(reward);
                this.save(viewer);
                return;
            }

            if (event.isShiftClick()) {
                // Reward position move.
                List<CrateReward> all = new ArrayList<>(this.object.getRewards());
                int index = all.indexOf(reward);
                int allSize = all.size();

                if (event.isLeftClick()) {
                    if (index + 1 >= allSize) return;

                    all.remove(index);
                    all.add(index + 1, reward);
                } else if (event.isRightClick()) {
                    if (index == 0) return;

                    all.remove(index);
                    all.add(index - 1, reward);
                }
                this.object.setRewards(all);
                this.save(viewer);
                return;
            }

            if (event.isLeftClick()) {
                this.plugin.runTask(task -> reward.getEditor().open(player, 1));
            }
        };
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }
}
