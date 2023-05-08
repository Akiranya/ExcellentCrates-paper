package su.nightexpress.excellentcrates.data;

import org.jetbrains.annotations.NotNull;
import su.nexmedia.engine.api.data.AbstractUserManager;
import su.nightexpress.excellentcrates.ExcellentCrates;
import su.nightexpress.excellentcrates.data.impl.CrateUser;

import java.util.UUID;

public class UserManager extends AbstractUserManager<ExcellentCrates, CrateUser> {

    public UserManager(@NotNull ExcellentCrates plugin) {
        super(plugin, plugin);
    }

    @Override
    protected @NotNull CrateUser createData(@NotNull UUID uuid, @NotNull String name) {
        return new CrateUser(plugin, uuid, name);
    }
}
