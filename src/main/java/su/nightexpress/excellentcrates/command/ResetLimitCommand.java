package su.nightexpress.excellentcrates.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.command.AbstractCommand;
import su.nexmedia.engine.api.lang.LangMessage;
import su.nexmedia.engine.utils.CollectionsUtil;
import su.nightexpress.excellentcrates.ExcellentCrates;
import su.nightexpress.excellentcrates.Perms;
import su.nightexpress.excellentcrates.Placeholders;
import su.nightexpress.excellentcrates.config.Lang;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.crate.impl.CrateReward;
import su.nightexpress.excellentcrates.data.impl.CrateUser;

import java.util.List;
import java.util.Map;

public class ResetLimitCommand extends AbstractCommand<ExcellentCrates> {

    public ResetLimitCommand(@NotNull ExcellentCrates plugin) {
        super(plugin, new String[]{"resetlimit"}, Perms.COMMAND_RESETLIMIT);
    }

    @Override
    public @NotNull String getUsage() {
        return plugin.getMessage(Lang.COMMAND_RESET_LIMIT_USAGE).getLocalized();
    }

    @Override
    public @NotNull String getDescription() {
        return plugin.getMessage(Lang.COMMAND_RESET_LIMIT_DESC).getLocalized();
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public @NotNull List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 1) {
            return CollectionsUtil.playerNames(player);
        }
        if (arg == 2) {
            return plugin.getCrateManager().getCrateIds(false);
        }
        if (arg == 3) {
            Crate crate = plugin.getCrateManager().getCrateById(args[2]);
            if (crate != null) {
                return crate.getRewards().stream().map(CrateReward::getId).toList();
            }
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args, @NotNull Map<String, String> flags) {
        if (args.length < 3) {
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

        CrateReward reward = args.length >= 4 ? crate.getReward(args[3]) : null;
        LangMessage message;
        if (reward == null) {
            user.removeRewardWinLimit(crate.getId());
            message = plugin.getMessage(Lang.COMMAND_RESET_LIMIT_DONE_CRATE);
        } else {
            user.removeRewardWinLimit(crate.getId(), reward.getId());
            message = plugin.getMessage(Lang.COMMAND_RESET_LIMIT_DONE_REWARD).replace(Placeholders.REWARD_NAME, reward.getName());
        }

        message
            .replace(Placeholders.Player.NAME, user.getName())
            .replace(Placeholders.CRATE_NAME, crate.getName())
            .send(sender);
    }
}
