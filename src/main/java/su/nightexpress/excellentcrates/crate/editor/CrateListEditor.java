package su.nightexpress.excellentcrates.crate.editor;

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
import su.nightexpress.excellentcrates.crate.CrateManager;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.editor.EditorLocales;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public class CrateListEditor extends EditorMenu<ExcellentCrates, CrateManager> implements AutoPaged<Crate> {

    public CrateListEditor(@NotNull CrateManager crateManager) {
        super(crateManager.plugin(), crateManager, Config.EDITOR_TITLE_CRATE.get(), 45);

        this.addReturn(39).setClick((viewer2, event) -> {
            this.plugin.runTask(task -> this.plugin.getEditor().open(viewer2.getPlayer(), 1));
        });
        this.addNextPage(44);
        this.addPreviousPage(36);

        this.addCreation(EditorLocales.CRATE_CREATE, 41).setClick((viewer2, event) -> {
            Player player = viewer2.getPlayer();
            this.startEdit(player, plugin.getMessage(Lang.EDITOR_CRATE_ENTER_ID), chat -> {
                if (!this.object.create(StringUtil.lowerCaseUnderscore(chat.getMessage()))) {
                    EditorManager.error(player, plugin.getMessage(Lang.EDITOR_CRATE_ERROR_CREATE_EXISTS).getLocalized());
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
    public @NotNull List<Crate> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.object.getCrateMap());
    }

    @Override
    public @NotNull Comparator<Crate> getObjectSorter() {
        return Comparator.comparing(Crate::getId);
    }

    @Override
    public @NotNull ItemStack getObjectStack(@NotNull Player player, @NotNull Crate crate) {
        ItemStack item = new ItemStack(crate.getItem());
        item.editMeta(meta -> {
            meta.addItemFlags(ItemFlag.values());
            meta.displayName(ComponentUtil.asComponent(EditorLocales.CRATE_OBJECT.getLocalizedName()));
            meta.lore(ComponentUtil.asComponent(EditorLocales.CRATE_OBJECT.getLocalizedLore()));
            ItemUtil.replaceNameAndLore(meta, crate.replacePlaceholders());
        });
        return item;
    }

    @Override
    public @NotNull ItemClick getObjectClick(@NotNull Crate crate) {
        return (viewer, event) -> {
            if (event.isShiftClick() && event.isRightClick()) {
                this.object.delete(crate);
                this.plugin.runTask(task -> this.open(viewer.getPlayer(), viewer.getPage()));
                return;
            }
            this.plugin.runTask(task -> crate.getEditor().open(viewer.getPlayer(), 1));
        };
    }
}
