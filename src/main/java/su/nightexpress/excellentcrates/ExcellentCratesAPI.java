package su.nightexpress.excellentcrates;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentcrates.crate.CrateManager;
import su.nightexpress.excellentcrates.data.impl.CrateUser;
import su.nightexpress.excellentcrates.data.UserManager;
import su.nightexpress.excellentcrates.key.KeyManager;
import su.nightexpress.excellentcrates.menu.MenuManager;

public class ExcellentCratesAPI {

    public static final ExcellentCrates PLUGIN = ExcellentCrates.getPlugin(ExcellentCrates.class);

    public static @NotNull CrateUser getUserData(@NotNull Player player) {
        return PLUGIN.getUserManager().getUserData(player);
    }

    public static @NotNull UserManager getUserManager() {
        return PLUGIN.getUserManager();
    }

    public static @NotNull CrateManager getCrateManager() {
        return PLUGIN.getCrateManager();
    }

    public static @NotNull KeyManager getKeyManager() {
        return PLUGIN.getKeyManager();
    }

    public static @NotNull MenuManager getMenuManager() {
        return PLUGIN.getMenuManager();
    }
}
