package su.nightexpress.excellentcrates.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.excellentcrates.ExcellentCrates;
import su.nightexpress.excellentcrates.Perms;
import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.excellentcrates.config.Lang;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.data.impl.CrateUser;

import java.util.List;
import java.util.Map;

public class ResetCooldownCommand extends AbstractCommand<ExcellentCrates> {

    public ResetCooldownCommand(@NotNull ExcellentCrates plugin) {
        super(plugin, new String[]{"resetcooldown"}, Perms.COMMAND_RESETCOOLDOWN);
    }

    @Override
    public @NotNull String getUsage() {
        return plugin.getMessage(Lang.COMMAND_RESET_COOLDOWN_USAGE).getLocalized();
    }

    @Override
    public @NotNull String getDescription() {
        return plugin.getMessage(Lang.COMMAND_RESET_COOLDOWN_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public @NotNull List<@NotNull String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg == 2) {
            return plugin.getCrateManager().getCrateIds(false);
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        if (args.length != 3) {
            this.printUsage(sender);
            return;
        }

        CrateUser user = plugin.getUserManager().getUserData(args[1]);
        if (user == null) {
            this.errorPlayer(sender);
            return;
        }

        Crate crate = plugin.getCrateManager().getCrateById(args[2]);
        if (crate == null) {
            plugin.getMessage(Lang.CRATE_ERROR_INVALID).send(sender);
            return;
        }

        user.setCrateCooldown(crate, 0L);
        plugin.getMessage(Lang.COMMAND_RESET_COOLDOWN_DONE)
            .replace(Placeholders.Player.NAME, user.getName())
            .replace(Placeholders.CRATE_NAME, crate.getName())
            .replace(Placeholders.CRATE_ID, crate.getId())
            .send(sender);
    }
}
