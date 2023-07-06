package su.nightexpress.excellentcrates.menu.impl;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.menu.MenuItemType;
import su.nexmedia.engine.api.menu.impl.ConfigMenu;
import su.nexmedia.engine.api.menu.item.MenuItem;
import su.nexmedia.engine.api.type.ClickType;
import su.nexmedia.engine.utils.ComponentUtil;
import su.nexmedia.engine.utils.ItemUtil;
import su.nexmedia.engine.utils.NumberUtil;
import su.nightexpress.excellentcrates.ExcellentCrates;
import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.excellentcrates.api.CrateClickAction;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.crate.impl.Crate;

import java.util.List;

public class MenuView extends ConfigMenu<ExcellentCrates> {

    private static final String PLACEHOLDER_KEYS = "%keys%";

    private final String crateName;
    private final List<String> crateLore;

    //private final CrateMenu crateMenu;

    MenuView(@NotNull MenuConfig menuConfig) {
        super(menuConfig.plugin(), menuConfig.getConfig());
        //this.crateMenu = crateMenu;

        this.crateName = cfg.getString("Crate.Name", Placeholders.CRATE_NAME); // Mewcraft
        this.crateLore = cfg.getStringList("Crate.Lore"); // Mewcraft

        this.registerHandler(MenuItemType.class)
            .addClick(MenuItemType.CLOSE, (viewer, event) -> this.plugin.runTask(task -> viewer.getPlayer().closeInventory()));

        this.load();

        for (String id : cfg.getSection("Crate.Slots")) {
            Crate crate = plugin.getCrateManager().getCrateById(id);
            if (crate == null) {
                plugin.error("Invalid crate '" + id + "' in '" + menuConfig.getId() + "' menu!");
                continue;
            }

            int slot = cfg.getInt("Crate.Slots." + id);

            ItemStack item = crate.getRawItem();
            // Mewcraft start
            item.editMeta(meta -> {
                meta.displayName(ComponentUtil.asComponent(this.crateName));
                meta.lore(ComponentUtil.asComponent(this.crateLore));
                ItemUtil.replaceNameAndLore(meta, crate.replacePlaceholders());
            });
            // Mewcraft

            MenuItem menuItem = new MenuItem(item);
            menuItem.setSlots(slot);
            menuItem.setClick((viewer, event) -> {
                ClickType clickType = ClickType.from(event);
                CrateClickAction clickAction = Config.getCrateClickAction(clickType);
                if (clickAction == null) return;

                this.plugin.runTask(task -> {
                    viewer.getPlayer().closeInventory();
                    plugin.getCrateManager().interactCrate(viewer.getPlayer(), crate, clickAction, null, null);
                });
            });
            menuItem.getOptions().addDisplayModifier((viewer, item2) -> {
                ItemUtil.replaceNameAndLore(item2, str -> str.replace(PLACEHOLDER_KEYS, NumberUtil.format(plugin.getKeyManager().getKeysAmount(viewer.getPlayer(), crate)))); // Mewcraft
            });

            this.addItem(menuItem);
        }
    }
}
