package su.nightexpress.excellentcrates.crate.editor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.editor.InputHandler;
import su.nexmedia.engine.api.manager.IListener;
import su.nexmedia.engine.api.menu.impl.EditorMenu;
import su.nexmedia.engine.api.menu.impl.MenuViewer;
import su.nexmedia.engine.api.particle.SimpleParticle;
import su.nexmedia.engine.editor.EditorManager;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nexmedia.engine.utils.ComponentUtil;
import su.nexmedia.engine.utils.FileUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.excellentcrates.ExcellentCrates;
import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.excellentcrates.api.OpenCostType;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.config.Lang;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.editor.EditorLocales;

import java.util.ArrayList;
import java.util.List;

public class CrateMainEditor extends EditorMenu<ExcellentCrates, Crate> implements IListener {

    private CrateRewardListEditor editorRewards;
    private boolean isReadyForBlock = false;

    public CrateMainEditor(@NotNull Crate crate) {
        super(crate.plugin(), crate, Config.EDITOR_TITLE_CRATE.get(), 45);

        this.addReturn(40).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.plugin.getEditor().getCratesEditor().open(viewer.getPlayer(), 1));
        });

        this.addItem(Material.NAME_TAG, EditorLocales.CRATE_NAME, 2).setClick((viewer, event) -> {
            this.handleInput(viewer, Lang.EDITOR_ENTER_DISPLAY_NAME, wrapper -> {
                crate.setName(wrapper.getText());
                crate.save();
                return true;
            });
        });

        this.addItem(Material.ITEM_FRAME, EditorLocales.CRATE_ITEM, 4).setClick((viewer, event) -> {
            if (event.isRightClick()) {
                PlayerUtil.addItem(viewer.getPlayer(), crate.getRawItem());
                return;
            }

            ItemStack cursor = event.getCursor();
            if (cursor == null || cursor.getType().isAir()) return;

            crate.setItem(cursor);
            event.getView().setCursor(null);
            this.save(viewer);
        }).getOptions().setDisplayModifier(((viewer, item) -> {
            item.setType(crate.getItem().getType());
            item.setItemMeta(crate.getItem().getItemMeta());
            // Mewcraft start
            item.editMeta(meta -> {
                meta.displayName(ComponentUtil.asComponent(EditorLocales.CRATE_ITEM.getLocalizedName()));
                meta.lore(ComponentUtil.asComponent(EditorLocales.CRATE_ITEM.getLocalizedLore()));
            });
            // Mewcraft end
        }));

        this.addItem(Material.REDSTONE_TORCH, EditorLocales.CRATE_PERMISSION, 6).setClick((viewer, event) -> {
            crate.setPermissionRequired(!crate.isPermissionRequired());
            this.save(viewer);
        });

        this.addItem(Material.TRIPWIRE_HOOK, EditorLocales.CRATE_KEYS, 10).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_CRATE_ENTER_KEY_ID, wrapper -> {
                    crate.getKeyIds().add(wrapper.getTextRaw());
                    crate.save();
                    return true;
                });
                EditorManager.suggestValues(viewer.getPlayer(), plugin.getKeyManager().getKeyIds(), true);
            } else if (event.isRightClick()) {
                crate.getKeyIds().clear();
                this.save(viewer);
            }
        });

        this.addItem(Material.PAINTING, EditorLocales.CRATE_CONFIG, 11).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isRightClick()) {
                    crate.setPreviewConfig(null);
                    this.save(viewer);
                    return;
                }
                List<String> previews = FileUtil.getFiles(plugin.getDataFolder() + Config.DIR_PREVIEWS, true)
                    .stream().map(f -> f.getName().replace(".yml", "")).toList();

                this.handleInput(viewer, Lang.EDITOR_CRATE_ENTER_PREVIEW_CONFIG, wrapper -> {
                    crate.setPreviewConfig(wrapper.getTextRaw());
                    crate.createPreview();
                    crate.save();
                    return true;
                });
                EditorManager.suggestValues(viewer.getPlayer(), previews, true);
            } else {
                if (event.isRightClick()) {
                    crate.setOpeningConfig(null);
                    this.save(viewer);
                    return;
                }
                this.handleInput(viewer, Lang.EDITOR_CRATE_ENTER_ANIMATION_CONFIG, wrapper -> {
                    crate.setOpeningConfig(wrapper.getTextRaw());
                    crate.save();
                    return true;
                });
                EditorManager.suggestValues(viewer.getPlayer(), plugin.getCrateManager().getOpeningsMap().keySet(), true);
            }
        });

        this.addItem(Material.EMERALD, EditorLocales.CRATE_REWARDS, 13).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.getEditorRewards().open(viewer.getPlayer(), 1));
        });

        this.addItem(Material.CLOCK, EditorLocales.CRATE_OPEN_COOLDOWN, 15).setClick((viewer, event) -> {
            if (event.getClick() == ClickType.DROP) {
                crate.setOpenCooldown(-1);
                this.save(viewer);
                return;
            }
            if (event.isRightClick()) {
                crate.setOpenCooldown(0);
                this.save(viewer);
                return;
            }
            this.handleInput(viewer, Lang.EDITOR_CRATE_ENTER_COOLDOWN, wrapper -> {
                crate.setOpenCooldown(wrapper.asAnyInt(0));
                crate.save();
                return true;
            });
        });

        this.addItem(Material.GOLD_NUGGET, EditorLocales.CRATE_OPEN_COST, 16).setClick((viewer, event) -> {
            if (event.getClick() == ClickType.DROP) {
                crate.setOpenCost(OpenCostType.MONEY, 0D);
                crate.setOpenCost(OpenCostType.EXP, 0);
                this.save(viewer);
                return;
            }
            if (event.isLeftClick()) {
                this.handleInput(viewer, Lang.EDITOR_CRATE_ENTER_OPEN_COST_MONEY, wrapper -> {
                    crate.setOpenCost(OpenCostType.MONEY, wrapper.asDouble());
                    crate.save();
                    return true;
                });
            } else if (event.isRightClick()) {
                this.handleInput(viewer, Lang.EDITOR_CRATE_ENTER_OPEN_COST_EXP, wrapper -> {
                    crate.setOpenCost(OpenCostType.EXP, wrapper.asInt());
                    crate.save();
                    return true;
                });
            }
        });

        this.addItem(Material.CHEST, EditorLocales.CRATE_BLOCK_LOCATIONS, 22).setClick((viewer, event) -> {
            if (event.isLeftClick()) {
                this.isReadyForBlock = true;
                this.handleInput(viewer, Lang.EDITOR_CRATE_ENTER_BLOCK_LOCATION, wrapper -> false);
            } else {
                crate.getBlockLocations().clear();
                crate.updateHologram();
                this.save(viewer);
            }
        }).getOptions().setDisplayModifier((viewer, item) -> item.editMeta(meta -> {
            // Mewcraft - fix placeholder list
            List<Component> blockLocations = crate.getBlockLocations().stream().map(location -> {
                String x = NumberUtil.format(location.getX());
                String y = NumberUtil.format(location.getY());
                String z = NumberUtil.format(location.getZ());
                String world = location.getWorld() == null ? "null" : location.getWorld().getName();
                return Component.text(x + ", " + y + ", " + z + " in " + world).color(NamedTextColor.WHITE).asComponent();
            }).toList();
            ItemUtil.replacePlaceholderListComponent(meta, Placeholders.CRATE_BLOCK_LOCATIONS, blockLocations, true);
        }));

        this.addItem(Material.SLIME_BLOCK, EditorLocales.CRATE_BLOCK_PUSHBACK, 21).setClick((viewer, event) -> {
            crate.setBlockPushbackEnabled(!crate.isBlockPushbackEnabled());
            this.save(viewer);
        });

        this.addItem(Material.ARMOR_STAND, EditorLocales.CRATE_BLOCK_HOLOGRAM, 31).setClick((viewer, event) -> {
            if (event.isShiftClick()) {
                if (event.isLeftClick()) {
                    crate.setBlockHologramEnabled(!crate.isBlockHologramEnabled());
                    crate.updateHologram();
                    this.save(viewer);
                } else if (event.isRightClick()) {
                    this.handleInput(viewer, Lang.EDITOR_CRATE_ENTER_BLOCK_HOLOGRAM_OFFSET, wrapper -> {
                        crate.setBlockHologramOffsetY(wrapper.asAnyDouble(0));
                        crate.updateHologram();
                        crate.save();
                        return true;
                    });
                }
            } else {
                if (event.isLeftClick()) {
                    this.handleInput(viewer, Lang.EDITOR_CRATE_ENTER_BLOCK_HOLOGRAM_TEXT, wrapper -> {
                        List<String> list = crate.getBlockHologramText();
                        list.add(wrapper.getText());
                        crate.setBlockHologramText(list);
                        crate.updateHologram();
                        crate.save();
                        return true;
                    });
                } else if (event.isRightClick()) {
                    crate.setBlockHologramText(new ArrayList<>());
                    crate.updateHologram();
                    this.save(viewer);
                }
            }
        }).getOptions().setDisplayModifier((viewer, item) -> item.editMeta(meta -> {
            // Mewcraft - fix placeholder list
            List<Component> holoLines = crate.getBlockHologramText().stream().map(line -> Component.text(line).color(NamedTextColor.WHITE).asComponent()).toList();
            ItemUtil.replacePlaceholderListComponent(meta, Placeholders.CRATE_BLOCK_HOLOGRAM_TEXT, holoLines, true);
        }));

        this.addItem(Material.BLAZE_POWDER, EditorLocales.CRATE_BLOCK_EFFECT, 23).setClick((viewer, event) -> {
            if (event.getClick() == ClickType.DROP) {
                crate.setBlockEffectModel(CollectionsUtil.next(crate.getBlockEffectModel()));
                this.save(viewer);
            } else {
                if (event.isRightClick()) {
                    this.handleInput(viewer, Lang.EDITOR_CRATE_ENTER_PARTICLE_DATA, wrapper -> {
                        String data = wrapper.getText();
                        crate.setBlockEffectParticle(crate.getBlockEffectParticle().parseData(data));
                        crate.save();
                        return true;
                    });
                } else if (event.isLeftClick()) {
                    this.handleInput(viewer, Lang.EDITOR_CRATE_ENTER_PARTICLE_NAME, wrapper -> {
                        Particle particle = StringUtil.getEnum(wrapper.getTextRaw(), Particle.class).orElse(Particle.REDSTONE);
                        crate.setBlockEffectParticle(SimpleParticle.of(particle).parseData(""));
                        crate.save();
                        return true;
                    });
                    EditorManager.suggestValues(viewer.getPlayer(), CollectionsUtil.getEnumsList(Particle.class), true);
                }
            }
        });

        this.getItems().forEach(menuItem -> {
            menuItem.getOptions().addDisplayModifier((viewer, item) -> ItemUtil.replaceNameAndLore(item, crate.replacePlaceholders()));
        });

        this.registerListeners();
    }

    @Override
    public void registerListeners() {
        this.plugin.getPluginManager().registerEvents(this, this.plugin);
    }

    @Override
    public void clear() {
        super.clear();
        this.unregisterListeners();
        if (this.editorRewards != null) {
            this.editorRewards.clear();
            this.editorRewards = null;
        }
    }

    @NotNull
    public CrateRewardListEditor getEditorRewards() {
        if (this.editorRewards == null) {
            this.editorRewards = new CrateRewardListEditor(this.object);
        }
        return this.editorRewards;
    }

    private void save(@NotNull MenuViewer viewer) {
        this.object.save();
        this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
    }

    @Override
    public void onClick(@NotNull MenuViewer viewer, @Nullable ItemStack item, @NotNull SlotType slotType, int slot, @NotNull InventoryClickEvent event) {
        super.onClick(viewer, item, slotType, slot, event);
        if (slotType == SlotType.PLAYER || slotType == SlotType.PLAYER_EMPTY) {
            event.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onCrateBlockClick(PlayerInteractEvent e) {
        if (!this.isReadyForBlock) return;

        Player player = e.getPlayer();

        InputHandler editor = EditorManager.getInputHandler(player);
        if (editor == null) return;

        Block block = e.getClickedBlock();
        if (block == null) return;

        e.setUseInteractedBlock(Event.Result.DENY);
        e.setUseItemInHand(Event.Result.DENY);

        if (plugin.getCrateManager().getCrateByBlock(block) != null) return;

        Crate crate = this.object;
        crate.getBlockLocations().add(block.getLocation());
        crate.updateHologram();
        crate.save();
        EditorManager.endEdit(player);
        this.isReadyForBlock = false;
    }
}
