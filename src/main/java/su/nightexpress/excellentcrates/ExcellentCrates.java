package su.nightexpress.excellentcrates;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.NexPlugin;
import su.nexmedia.engine.api.command.GeneralCommand;
import su.nexmedia.engine.api.data.UserDataHolder;
import su.nexmedia.engine.command.list.ReloadSubCommand;
import su.nexmedia.engine.hooks.Hooks;
import su.nightexpress.excellentcrates.api.hologram.HologramHandler;
import su.nightexpress.excellentcrates.command.*;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.config.Lang;
import su.nightexpress.excellentcrates.crate.CrateManager;
import su.nightexpress.excellentcrates.data.DataHandler;
import su.nightexpress.excellentcrates.data.UserManager;
import su.nightexpress.excellentcrates.data.impl.CrateUser;
import su.nightexpress.excellentcrates.editor.EditorLocales;
import su.nightexpress.excellentcrates.editor.EditorMainMenu;
import su.nightexpress.excellentcrates.hooks.HookId;
import su.nightexpress.excellentcrates.hooks.hologram.HologramHandlerDecent;
import su.nightexpress.excellentcrates.hooks.hologram.HologramHandlerHD;
import su.nightexpress.excellentcrates.hooks.impl.PlaceholderHook;
import su.nightexpress.excellentcrates.key.KeyManager;
import su.nightexpress.excellentcrates.menu.MenuManager;

import java.sql.SQLException;

public class ExcellentCrates extends NexPlugin<ExcellentCrates> implements UserDataHolder<ExcellentCrates, CrateUser> {

    private DataHandler dataHandler;
    private UserManager userManager;

    private EditorMainMenu editor;
    private KeyManager keyManager;
    private CrateManager crateManager;
    private MenuManager menuManager;

    private HologramHandler hologramHandler;

    @Override
    protected @NotNull ExcellentCrates getSelf() {
        return this;
    }

    @Override
    public void enable() {
        this.keyManager = new KeyManager(this);
        this.keyManager.setup();

        this.crateManager = new CrateManager(this);
        this.crateManager.setup();

        this.menuManager = new MenuManager(this);
        this.menuManager.setup();
    }

    @Override
    public void disable() {
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        if (this.keyManager != null) {
            this.keyManager.shutdown();
            this.keyManager = null;
        }
        if (this.crateManager != null) {
            this.crateManager.shutdown();
            this.crateManager = null;
        }
        if (this.menuManager != null) {
            this.menuManager.shutdown();
            this.menuManager = null;
        }
        if (this.hologramHandler != null) {
            this.hologramHandler.shutdown();
            this.hologramHandler = null;
        }
        if (Hooks.hasPlaceholderAPI()) {
            PlaceholderHook.shutdown();
        }
    }

    @Override
    public void loadConfig() {
        this.getConfig().initializeOptions(Config.class);
    }

    @Override
    public void loadLang() {
        this.getLangManager().loadMissing(Lang.class);
        this.getLangManager().loadEditor(EditorLocales.class);
        this.getLang().saveChanges();
    }

    @Override
    public void registerHooks() {
        if (Hooks.hasPlugin(HookId.HOLOGRAPHIC_DISPLAYS)) {
            this.hologramHandler = new HologramHandlerHD(this);
        } else if (Hooks.hasPlugin(HookId.DECENT_HOLOGRAMS)) {
            this.hologramHandler = new HologramHandlerDecent(this);
        }

        if (Hooks.hasPlaceholderAPI()) {
            PlaceholderHook.setup();
        }
    }

    @Override
    public void registerCommands(@NotNull GeneralCommand<ExcellentCrates> mainCommand) {
        mainCommand.addChildren(new EditorCommand(this));
        mainCommand.addChildren(new DropCommand(this));
        mainCommand.addChildren(new ForceOpenCommand(this));
        mainCommand.addChildren(new GiveCommand(this));
        mainCommand.addChildren(new KeyCommand(this));
        mainCommand.addChildren(new MenuCommand(this));
        mainCommand.addChildren(new PreviewCommand(this));
        mainCommand.addChildren(new ResetCooldownCommand(this));
        mainCommand.addChildren(new ResetLimitCommand(this));
        mainCommand.addChildren(new ReloadSubCommand<>(this, Perms.COMMAND_RELOAD));
        //mainCommand.addChildren(new EditorSubCommand<>(this, this, Perms.COMMAND_EDITOR));
    }

    @Override
    public void registerPermissions() {
        this.registerPermissions(Perms.class);
    }

    @Override
    public boolean setupDataHandlers() {
        try {
            this.dataHandler = DataHandler.getInstance(this);
            this.dataHandler.setup();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }

        this.userManager = new UserManager(this);
        this.userManager.setup();

        return true;
    }

    @Override
    public @NotNull DataHandler getData() {
        return this.dataHandler;
    }

    @Override
    public @NotNull UserManager getUserManager() {
        return userManager;
    }

    public @NotNull KeyManager getKeyManager() {
        return keyManager;
    }

    public @NotNull CrateManager getCrateManager() {
        return this.crateManager;
    }

    public @NotNull MenuManager getMenuManager() {
        return menuManager;
    }

    public @Nullable HologramHandler getHologramHandler() {
        return hologramHandler;
    }

    public @NotNull EditorMainMenu getEditor() {
        if (this.editor == null) {
            this.editor = new EditorMainMenu(this);
        }
        return this.editor;
    }
}
