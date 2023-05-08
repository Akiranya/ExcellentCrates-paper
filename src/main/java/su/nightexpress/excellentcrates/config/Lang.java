package su.nightexpress.excellentcrates.config;

import su.nexmedia.engine.api.lang.LangKey;
import su.nexmedia.engine.lang.EngineLang;
import su.nightexpress.excellentcrates.Placeholders;


public class Lang extends EngineLang {

    public static final LangKey COMMAND_DROP_USAGE = new LangKey("Command.Drop.Usage", "<crateId> <world> <x> <y> <z>");
    public static final LangKey COMMAND_DROP_DESC = new LangKey("Command.Drop.Desc", "Drop crate at specified location in the world.");
    public static final LangKey COMMAND_DROP_DONE = new LangKey("Command.Drop.Done", "Dropped <gold>" + Placeholders.CRATE_NAME + "</gold> at <gold>" + Placeholders.Location.X + "</gold>, <gold>" + Placeholders.Location.Y + "</gold>, <gold>" + Placeholders.Location.Z + "</gold> in <gold>" + Placeholders.Location.WORLD + "</gold>.");

    public static final LangKey COMMAND_FORCE_OPEN_DESC = new LangKey("Command.ForceOpen.Desc", "Force open a crate for a player.");
    public static final LangKey COMMAND_FORCE_OPEN_USAGE = new LangKey("Command.ForceOpen.Usage", "<crateId> [player]");
    public static final LangKey COMMAND_FORCE_OPEN_DONE = new LangKey("Command.ForceOpen.Done", "Force opened <gold>" + Placeholders.CRATE_NAME + "</gold> for <gold>" + Placeholders.Player.NAME + "</gold>.");
    public static final LangKey COMMAND_FORCE_OPEN_NOTIFY = new LangKey("Command.ForceOpen.Notify", "You have been forced to open <gold>" + Placeholders.CRATE_NAME + "</gold>.");

    public static final LangKey COMMAND_GIVE_USAGE = new LangKey("Command.Give.Usage", "<player | *> <crateId> [amount]");
    public static final LangKey COMMAND_GIVE_DESC = new LangKey("Command.Give.Desc", "Gives crate(s) to a player.");
    public static final LangKey COMMAND_GIVE_DONE = new LangKey("Command.Give.Done", "Given <gold>x" + Placeholders.GENERIC_AMOUNT + "</gold> of <gold>" + Placeholders.CRATE_NAME + "</gold> crate(s) to <gold>" + Placeholders.Player.NAME + "</gold>.");
    public static final LangKey COMMAND_GIVE_NOTIFY = new LangKey("Command.Give.Notify", "You recieved <gold>x" + Placeholders.GENERIC_AMOUNT + "</gold> of <gold>" + Placeholders.CRATE_NAME + "</gold>!");

    public static final LangKey COMMAND_KEY_DESC = new LangKey("Command.Key.Desc", "Manage or view player's crate keys.");
    public static final LangKey COMMAND_KEY_ERROR_PLAYER = new LangKey("Command.Key.Error.Player", "<red>Could not proccess operation for offline/invalid player <yellow>" + Placeholders.Player.NAME + "</yellow>!");

    public static final LangKey COMMAND_KEY_GIVE_USAGE = new LangKey("Command.Key.Give.Usage", "<player | *> <keyId> <amount>");
    public static final LangKey COMMAND_KEY_GIVE_DESC = new LangKey("Command.Key.Give.Desc", "Give crate key(s) to a player.");
    public static final LangKey COMMAND_KEY_GIVE_DONE = new LangKey("Command.Key.Give.Done", "Given <green>x" + Placeholders.GENERIC_AMOUNT + "</green> of <green>" + Placeholders.KEY_NAME + "</green> key(s) to <green>" + Placeholders.Player.NAME + "</green>.");
    public static final LangKey COMMAND_KEY_GIVE_NOTIFY = new LangKey("Command.Key.Give.Notify", "You recieved <green>x" + Placeholders.GENERIC_AMOUNT + "</green> of <green>" + Placeholders.KEY_NAME + "</green>!");

    public static final LangKey COMMAND_KEY_TAKE_USAGE = new LangKey("Command.Key.Take.Usage", "<player | *> <keyId> <amount>");
    public static final LangKey COMMAND_KEY_TAKE_DESC = new LangKey("Command.Key.Take.Desc", "Take crate key(s) from a player.");
    public static final LangKey COMMAND_KEY_TAKE_DONE = new LangKey("Command.Key.Take.Done", "Taken <red>x" + Placeholders.GENERIC_AMOUNT + "</red> <red>" + Placeholders.KEY_NAME + "</red> key(s) from <red>" + Placeholders.Player.NAME + "/red>");
    public static final LangKey COMMAND_KEY_TAKE_NOTIFY = new LangKey("Command.Key.Take.Notify", "You lost <red>x" + Placeholders.GENERIC_AMOUNT + "</red> <red>" + Placeholders.KEY_NAME + "</red>!");
    // public static final LangKey Command_Key_Take_Error  = new LangKey(this, "<red>Could not take keys: <yellow>Player does not exist or do not have such amount of keys.");

    public static final LangKey COMMAND_KEY_SET_USAGE = new LangKey("Command.Key.Set.Usage", "<player | *> <keyId> <amount>");
    public static final LangKey COMMAND_KEY_SET_DESC = new LangKey("Command.Key.Set.Desc", "Set crate key(s) amount for a player.");
    public static final LangKey COMMAND_KEY_SET_DONE = new LangKey("Command.Key.Set.Done", "Set <yellow>x" + Placeholders.GENERIC_AMOUNT + "</yellow> of <yellow>" + Placeholders.KEY_NAME + "</yellow> key(s) for <yellow>" + Placeholders.Player.NAME + "</yellow>.");
    public static final LangKey COMMAND_KEY_SET_NOTIFY = new LangKey("Command.Key.Set.Notify", "Your <yellow>" + Placeholders.KEY_NAME + "</yellow> amount has been changed to <yellow>x" + Placeholders.GENERIC_AMOUNT + "</yellow>!");

    public static final LangKey COMMAND_KEY_SHOW_DESC = new LangKey("Command.Key.Show.Desc", "Show amount of your or other player keys.");
    public static final LangKey COMMAND_KEY_SHOW_USAGE = new LangKey("Command.Key.Show.Usage", "[player]");
    public static final LangKey COMMAND_KEY_SHOW_FORMAT_LIST = new LangKey("Command.Key.Show.Format.List",
        """
        <!prefix:"false"!>
        <gold><st>              </st><b>[</b></gold> <green>%player_name%</green> <yellow><b>Crate Keys</b></yellow> <gold><b>]</b><st>              </st>
        <gray>
        <gold>▸ <yellow>%key_name%: <gold>%amount%
        """);

    public static final LangKey COMMAND_PREVIEW_DESC = new LangKey("Command.Preview.Desc", "Open crate preview.");
    public static final LangKey COMMAND_PREVIEW_USAGE = new LangKey("Command.Preview.Usage", "<crateId> [player]");
    public static final LangKey COMMAND_PREVIEW_DONE_OTHERS = new LangKey("Command.Preview.Done.Others", "Opened <gold>" + Placeholders.CRATE_NAME + "</gold> preview for <gold>" + Placeholders.Player.DISPLAY_NAME + "</gold>.");

    public static final LangKey COMMAND_RESET_LIMIT_DESC = new LangKey("Command.ResetLimit.Desc", "Reset reward win limit for specified crate and reward.");
    public static final LangKey COMMAND_RESET_LIMIT_USAGE = new LangKey("Command.ResetLimit.Usage", "<player> <crateId> [rewardId]");
    public static final LangKey COMMAND_RESET_LIMIT_DONE_CRATE = new LangKey("Command.ResetLimit.Done.Crate", "Reset <gold>" + Placeholders.Player.NAME + "</gold> win limit for all rewards of <gold>" + Placeholders.CRATE_NAME + "</gold>.");
    public static final LangKey COMMAND_RESET_LIMIT_DONE_REWARD = new LangKey("Command.ResetLimit.Done.Reward", "Reset <gold>" + Placeholders.Player.NAME + "</gold> win limit for <gold>" + Placeholders.REWARD_NAME + "</gold> reward of <gold>" + Placeholders.CRATE_NAME + "</gold>.");

    public static final LangKey COMMAND_RESET_COOLDOWN_DESC = new LangKey("Command.ResetCooldown.Desc", "Reset player cooldown for specified crate.");
    public static final LangKey COMMAND_RESET_COOLDOWN_USAGE = new LangKey("Command.ResetCooldown.Usage", "<player> <crateId>");
    public static final LangKey COMMAND_RESET_COOLDOWN_DONE = new LangKey("Command.ResetCooldown.Done", "Reset <gold>" + Placeholders.Player.NAME + "</gold> cooldown for <gold>" + Placeholders.CRATE_NAME + "</gold>.");

    public static final LangKey COMMAND_MENU_USAGE = new LangKey("Command.Menu.Usage", "[menuId]");
    public static final LangKey COMMAND_MENU_DESC = new LangKey("Command.Menu.Desc", "Open crate menu.");
    public static final LangKey COMMAND_MENU_DONE_OTHERS = new LangKey("Command.Menu.Done.Others", "Opened <gold>" + Placeholders.MENU_ID + "</gold> crate menu for <gold>" + Placeholders.Player.DISPLAY_NAME + "</gold>.");

    public static final LangKey CRATE_ERROR_INVALID = new LangKey("Crate.Error.Invalid", "<red>Invalid crate!");
    public static final LangKey CRATE_OPEN_ERROR_INVENTORY_SPACE = new LangKey("Crate.Open.Error.InventorySpace", "<red>Please clean up your inventory to open the crate!");
    public static final LangKey CRATE_OPEN_ERROR_COOLDOWN_TEMPORARY = new LangKey("Crate.Open.Error.Cooldown.Temporary", "<red>You have to wait <gold>" + Placeholders.GENERIC_TIME + "</gold> before you can open <gold>" + Placeholders.CRATE_NAME + "</gold> again!");
    public static final LangKey CRATE_OPEN_ERROR_COOLDOWN_ONE_TIMED = new LangKey("Crate.Open.Error.Cooldown.OneTimed", "<red>You already have opened this crate, you can not open it more!");
    public static final LangKey CRATE_OPEN_ERROR_NO_KEY = new LangKey("Crate.Open.Error.NoKey", "<red>You don't have a key for this crate!");
    public static final LangKey CRATE_OPEN_ERROR_NO_HOLD_KEY = new LangKey("Crate.Open.Error.NoHoldKey", "<red>You must hold a key to open this crate!");
    public static final LangKey CRATE_OPEN_ERROR_NO_REWARDS = new LangKey("Crate.Open.Error.NoRewards", "<red>This crate does not contains any rewards for you!");
    public static final LangKey CRATE_OPEN_ERROR_COST_MONEY = new LangKey("Crate.Open.Error.Cost.Money", "<red>You don't have enough money to open this crate!");
    public static final LangKey CRATE_OPEN_ERROR_COST_EXP = new LangKey("Crate.Open.Error.Cost.Exp", "<red>You don't have enough exp levels to open this crate!");
    public static final LangKey CRATE_OPEN_REWARD_INFO = new LangKey("Crate.Open.Reward.Info", "You got the <gold>" + Placeholders.REWARD_NAME + "</gold> reward from the <gold>" + Placeholders.CRATE_NAME + "</gold>!");
    public static final LangKey CRATE_OPEN_REWARD_BROADCAST = new LangKey("Crate.Open.Reward.Broadcast", "<gray>Player <green>" + Placeholders.Player.DISPLAY_NAME + "</green> just got the <gold>" + Placeholders.REWARD_NAME + "</gold> reward from the <gold>" + Placeholders.CRATE_NAME + "</gold>!");
    public static final LangKey CRATE_KEY_ERROR_INVALID = new LangKey("Crate.Key.Error.Invalid", "<red>Invalid key!");

    public static final LangKey MENU_INVALID = new LangKey("Menu.Invalid", "<red>Menu does not exist!");

    public static final LangKey EDITOR_CRATE_ENTER_ID = new LangKey("Editor.Crate.Enter.Id", "<gray>Enter <green>unique</green> crate <green>identifier</green>...");
    public static final LangKey EDITOR_CRATE_ENTER_DISPLAY_NAME = new LangKey("Editor.Crate.Enter.DisplayName", "<gray>Enter crate <green>display name</green>...");
    public static final LangKey EDITOR_CRATE_ENTER_PARTICLE_NAME = new LangKey("Editor.Crate.Enter.Particle.Name", "<gray>Enter <green>particle</green> name...");
    public static final LangKey EDITOR_CRATE_ENTER_PARTICLE_DATA = new LangKey("Editor.Crate.Enter.Particle.Data", "<gray>Enter <green>particle</green> data...");
    public static final LangKey EDITOR_CRATE_ENTER_KEY_ID = new LangKey("Editor.Crate.Enter.KeyId", "<gray>Enter <green>key</green> identifier...");
    public static final LangKey EDITOR_CRATE_ENTER_BLOCK_LOCATION = new LangKey("Editor.Crate.Enter.Block.Location", "<gray>Click a <green>block</green> to assign crate...");
    public static final LangKey EDITOR_CRATE_ENTER_BLOCK_HOLOGRAM_TEXT = new LangKey("Editor.Crate.Enter.Block.Hologram.Text", "<gray>Enter <green>text</green> line...");
    public static final LangKey EDITOR_CRATE_ENTER_BLOCK_HOLOGRAM_OFFSET = new LangKey("Editor.Crate.Enter.Block.Hologram.Offset", "<gray>Enter <green>offset</green> value...");
    public static final LangKey EDITOR_CRATE_ENTER_COOLDOWN = new LangKey("Editor.Crate.Enter.Cooldown", "<gray>Enter <green>cooldown</green> in seconds...");
    public static final LangKey EDITOR_CRATE_ENTER_CITIZENS = new LangKey("Editor.Crate.Enter.Citizens", "<gray>Enter <green>Citizens NPC</green> ID...");
    public static final LangKey EDITOR_CRATE_ENTER_ANIMATION_CONFIG = new LangKey("Editor.Crate.Enter.AnimationConfig", "<gray>Enter <green>animation config</green> name...");
    public static final LangKey EDITOR_CRATE_ENTER_PREVIEW_CONFIG = new LangKey("Editor.Crate.Enter.PreviewConfig", "<gray>Enter <green>preview config</green> name...");
    public static final LangKey EDITOR_CRATE_ENTER_OPEN_COST_MONEY = new LangKey("Editor.Crate.Enter.OpenCost.Money", "<gray>Enter <green>money</green> cost...");
    public static final LangKey EDITOR_CRATE_ENTER_OPEN_COST_EXP = new LangKey("Editor.Crate.Enter.OpenCost.Exp", "<gray>Enter <green>exp levels</green> cost...");
    public static final LangKey EDITOR_CRATE_ERROR_CREATE_EXISTS = new LangKey("Editor.Crate.Error.Create.Exists", "<red>Crate with such id is already exists!");

    public static final LangKey EDITOR_REWARD_ENTER_ID = new LangKey("Editor.Reward.Enter.Id", "<gray>Enter <green>unique</green> reward <green>identifier</green>...");
    public static final LangKey EDITOR_REWARD_ENTER_DISPLAY_NAME = new LangKey("Editor.Reward.Enter.DisplayName", "<gray>Enter reward <green>display name</green>...");
    public static final LangKey EDITOR_REWARD_ENTER_CHANCE = new LangKey("Editor.Reward.Enter.Chance", "<gray>Enter win <green>chance</green>...");
    public static final LangKey EDITOR_REWARD_ENTER_RARITY = new LangKey("Editor.Reward.Enter.Rarity", "<gray>Enter <green>[Rarity]");
    public static final LangKey EDITOR_REWARD_ENTER_COMMAND = new LangKey("Editor.Reward.Enter.Command", "<gray>Enter a <green>command</green>...");
    public static final LangKey EDITOR_REWARD_ENTER_PERMISSION = new LangKey("Editor.Reward.Enter.Permissions", "<gray>Enter a <green>permission</green>...");
    public static final LangKey EDITOR_REWARD_ENTER_WIN_LIMIT_AMOUNT = new LangKey("Editor.Reward.Enter.WinLimit.Amount", "<gray>Enter win limit <green>amount</green>...");
    public static final LangKey EDITOR_REWARD_ENTER_WIN_LIMIT_COOLDOWN = new LangKey("Editor.Reward.Enter.WinLimit.Cooldown", "<gray>Enter win limit <green>cooldown</green>...");
    public static final LangKey EDITOR_REWARD_ERROR_CREATE_EXIST = new LangKey("Editor.Reward.Error.Create.Exist", "<red>Reward with such id is already exists!");

    public static final LangKey EDITOR_KEY_ERROR_CREATE_EXIST = new LangKey("Editor.Key.Error.Create.Exist", "Key with such id is already exists!");
}
