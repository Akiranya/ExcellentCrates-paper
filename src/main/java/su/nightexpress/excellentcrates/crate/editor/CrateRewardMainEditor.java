package su.nightexpress.excellentcrates.crate.editor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.Menu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.ComponentUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nightexpress.excellentcrates.ExcellentCrates;
import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.config.Lang;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.crate.impl.CrateReward;
import su.nightexpress.excellentcrates.crate.impl.Rarity;
import su.nightexpress.excellentcrates.editor.EditorLocales;

import java.util.List;
import java.util.stream.Stream;

public class CrateRewardMainEditor extends EditorMenu<ExcellentCrates, CrateReward> {

    public CrateRewardMainEditor(@NotNull CrateReward reward) {
        super(reward.plugin(), reward, Config.EDITOR_TITLE_CRATE.get(), 45);
        Crate crate = reward.getCrate();

        this.addReturn(40).setClick((viewer, event) -> {
            this.plugin.runTask(task -> crate.getEditor().getEditorRewards().open(viewer.getPlayer(), 1)); // Mewcraft - fix typo
        });

        this.addItem(Material.ITEM_FRAME, EditorLocales.REWARD_PREVIEW, 4).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                PlayerUtil.addItem(viewer.getPlayer(), reward.getPreview());
                return;
            }

            ItemStack cursor = event.getCursor();
            if (cursor != null && !cursor.getType().isAir()) {
                reward.setPreview(cursor);
                event.getView().setCursor(null);
                this.save(viewer);
            }
        }).getOptions().setDisplayModifier(((viewer, item) -> {
            item.setType(reward.getPreview().getType());
            item.setItemMeta(reward.getPreview().getItemMeta());
            // Mewcraft start
            item.editMeta(meta -> {
                meta.displayName(ComponentUtil.asComponent(EditorLocales.REWARD_PREVIEW.getLocalizedName()));
                meta.lore(ComponentUtil.asComponent(EditorLocales.REWARD_PREVIEW.getLocalizedLore()));
                meta.addItemFlags(ItemFlag.values());
            });
            // Mewcraft end
        }));

        this.addItem(Material.NAME_TAG, EditorLocales.REWARD_NAME, 19).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                reward.setName(ComponentUtil.asMiniMessage(ItemUtil.getName(reward.getPreview()))); // Mewcraft
                this.save(viewer);
                return;
            }
            if (event.isShiftClick() && event.isLeftClick()) {
                ItemStack preview = reward.getPreview();
                preview.editMeta(meta -> meta.displayName(ComponentUtil.asComponent(reward.getName()))); // Mewcraft
                reward.setPreview(preview);
                this.save(viewer);
                return;
            }

            this.handleInput(viewer, Lang.EDITOR_ENTER_DISPLAY_NAME, wrapper -> {
                reward.setName(wrapper.getText());
                crate.save();
                return true;
            });
        });

        this.addItem(Material.ENDER_EYE, EditorLocales.REWARD_BROADCAST, 20).setClick((viewer, event) -> {
            reward.setBroadcast(!reward.isBroadcast());
            this.save(viewer);
        });

        this.addItem(Material.COMPARATOR, EditorLocales.REWARD_CHANCE, 21).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_CHANCE, wrapper -> {
                reward.setChance(wrapper.asDouble());
                crate.save();
                return true;
            });
        });

        this.addItem(Material.FISHING_ROD, EditorLocales.REWARD_RARITY, 13).setClick((viewer, event) -> {
            EditorManager.suggestValues(viewer.getPlayer(), plugin.getCrateManager().getRarityMap().keySet(), true);
            this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_RARITY, wrapper -> {
                Rarity rarity = this.plugin.getCrateManager().getRarity(wrapper.getTextRaw());
                if (rarity == null) return true;

                reward.setRarity(rarity);
                reward.getCrate().save();
                return true;
            });
        });

        this.addItem(Material.REPEATER, EditorLocales.REWARD_WIN_LIMITS, 22).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isLeftClick()) {
                    reward.setWinLimitAmount(1);
                    reward.setWinLimitCooldown(-1);
                } else {
                    reward.setWinLimitAmount(-1);
                    reward.setWinLimitCooldown(0);
                }
                this.save(viewer);
                return;
            }

            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_WIN_LIMIT_AMOUNT, wrapper -> {
                    reward.setWinLimitAmount(wrapper.asAnyInt(-1));
                    crate.save();
                    return true;
                });
            } else {
                this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_WIN_LIMIT_COOLDOWN, wrapper -> {
                    reward.setWinLimitCooldown(wrapper.asAnyInt(0));
                    crate.save();
                    return true;
                });
            }
        });

        this.addItem(Material.COMMAND_BLOCK, EditorLocales.REWARD_COMMANDS, 23).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                reward.getCommands().clear();
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_COMMAND, wrapper -> {
                reward.getCommands().add(wrapper.getText());
                crate.save();
                return true;
            });
            // Mewcraft start - fix placeholder list
        }).getOptions().setDisplayModifier((viewer, item) -> item.editMeta(meta -> {
            List<Component> commands = this.object.getCommands().stream().map(line -> Component.text(line).color(NamedTextColor.WHITE).asComponent()).toList();
            ItemUtil.replacePlaceholderListComponent(meta, Placeholders.REWARD_COMMANDS, commands, true);
        }));
        // Mewcraft end

        this.addItem(Material.CHEST_MINECART, EditorLocales.REWARD_ITEMS, 24).setClick((viewer, event) -> {
            new ContentEditor(reward).open(viewer.getPlayer(), 1);
        });

        this.addItem(Material.DAYLIGHT_DETECTOR, EditorLocales.REWARD_IGNORED_PERMISSIONS, 25).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                reward.getIgnoredForPermissions().clear();
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, Lang.EDITOR_REWARD_ENTER_COMMAND, wrapper -> {
                reward.getIgnoredForPermissions().add(wrapper.getTextRaw());
                crate.save();
                return true;
            });
            // Mewcraft start - fix placeholder list
        }).getOptions().setDisplayModifier((viewer, item) -> item.editMeta(meta -> {
            List<Component> permissions = this.object.getIgnoredForPermissions().stream().map(line -> Component.text(line).color(NamedTextColor.WHITE).asComponent()).toList();
            ItemUtil.replacePlaceholderListComponent(meta, Placeholders.REWARD_IGNORED_FOR_PERMISSIONS, permissions, true);
        }));
        // Mewcraft end

        this.getItems().forEach(menuItem -> {
            if (menuItem.getOptions().getDisplayModifier() == null) {
                menuItem.getOptions().setDisplayModifier(((viewer, item) -> ItemUtil.replaceNameAndLore(item, reward.replacePlaceholders()))); // Mewcraft
            }
        });
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.getCrate().save();
        this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }

    private static class ContentEditor extends Menu<ExcellentCrates> {

        private final CrateReward reward;

        public ContentEditor(@NotNull CrateReward reward) {
            super(reward.getCrate().plugin(), reward.getName(), 27);
            this.reward = reward;
        }

        @Override
        public boolean isPersistent() {
            return false;
        }

        @Override
        public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
            super.onClick(viewer, item, slotType, slot, event);
            event.setCancelled(false);
        }

        @Override
        public void onReady(@NotNull MenuViewer viewer, @NotNull Inventory inventory) {
            super.onReady(viewer, inventory);
            inventory.setContents(this.reward.getItems().stream().map(ItemStack::clone).toList().toArray(ItemStack[]::new));
        }

        @Override
        public void onClose(@NotNull MenuViewer viewer, @NotNull InventoryCloseEvent event) {
            Inventory inventory = event.getInventory();
            this.reward.setItems(Stream.of(inventory.getContents()).toList());
            this.reward.getCrate().save();
            this.plugin.runTask(task -> this.reward.getEditor().open(viewer.getPlayer(), 1));
            super.onClose(viewer, event);
        }
    }
}
