package su.nightexpress.excellentcrates.key.editor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.AutoPaged;
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
import su.nightexpress.excellentcrates.editor.EditorLocales;
import su.nightexpress.excellentcrates.key.CrateKey;
import su.nightexpress.excellentcrates.key.KeyManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class KeyListEditor extends EditorMenu<ExcellentCrates, KeyManager> implements AutoPaged<CrateKey> {

    public KeyListEditor(@NotNull KeyManager keyManager) {
        super(keyManager.plugin(), keyManager, Config.EDITOR_TITLE_KEY.get(), 45);

        this.addReturn(39).setClick((viewer, event) -> {
            this.plugin.runTask(task -> this.plugin.getEditor().open(viewer.getPlayer(), 1));
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.KEY_CREATE, 41).setClick((viewer, event) -> {
            this.startEdit(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_CRATE_ENTER_ID), chat -> {
                if (!keyManager.create(StringUtil.lowerCaseUnderscore(chat.getMessage()))) {
                    EditorManager.error(viewer.getPlayer(), plugin.getMessage(Lang.EDITOR_KEY_ERROR_CREATE_EXIST).getLocalized());
                    return false;
                }
                return true;
            });
        });
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
    public @NotNull List<CrateKey> getObjects(@NotNull Player player) {
        return new ArrayList<>(plugin.getKeyManager().getKeys());
    }

    @Override
    public @NotNull Comparator<CrateKey> getObjectSorter() {
        return Comparator.comparing(CrateKey::getId);
    }

    @Override
    public @NotNull ItemStack getObjectStack(@NotNull Player player, @NotNull CrateKey key) {
        ItemStack item = new ItemStack(key.getItem());
        item.editMeta(meta -> {
            meta.addItemFlags(ItemFlag.values());
            meta.displayName(ComponentUtil.asComponent(EditorLocales.KEY_OBJECT.getLocalizedName()));
            meta.lore(ComponentUtil.asComponent(EditorLocales.KEY_OBJECT.getLocalizedLore()));
            ItemUtil.replaceNameAndLore(meta, key.replacePlaceholders());
        });
        return item;
    }

    @Override
    public @NotNull ItemClick getObjectClick(@NotNull CrateKey key) {
        return (viewer, event) -> {
            Player player = viewer.getPlayer();
            if (event.isRightClick() && event.isShiftClick()) {
                if (this.plugin.getKeyManager().delete(key)) {
                    this.plugin.runTask(task -> this.open(player, viewer.getPage()));
                }
                return;
            }
            key.getEditor().open(player, 1);
        };
    }
}
