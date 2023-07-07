package su.nightexpress.excellentcrates.key;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nexmedia.engine.api.config.JYML;
import su.nexmedia.engine.api.manager.AbstractManager;
import su.nexmedia.engine.utils.ComponentUtil;
import su.nexmedia.engine.utils.PDCUtil;
import su.nexmedia.engine.utils.PlayerUtil;
import su.nexmedia.engine.utils.StringUtil;
import su.nightexpress.excellentcrates.ExcellentCrates;
import su.nightexpress.excellentcrates.Keys;
import su.nightexpress.excellentcrates.config.Config;
import su.nightexpress.excellentcrates.config.Lang;
import su.nightexpress.excellentcrates.crate.impl.Crate;
import su.nightexpress.excellentcrates.data.impl.CrateUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class KeyManager extends AbstractManager<ExcellentCrates> {

    private final Map<String, CrateKey> keysMap;

    public KeyManager(@NotNull ExcellentCrates plugin) {
        super(plugin);
        this.keysMap = new HashMap<>();
    }

    @Override
    public void onLoad() {
        this.plugin.getConfigManager().extractResources(Config.DIR_KEYS);

        for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + Config.DIR_KEYS, true)) {
            CrateKey key = new CrateKey(this.plugin, cfg);
            if (key.load()) {
                this.keysMap.put(key.getId(), key);
            } else this.plugin.error("Key not loaded: '" + cfg.getFile().getName() + "'.");
        }
        this.plugin.info("Loaded " + this.getKeysMap().size() + " crate keys.");

        this.addListener(new KeyListener(this));
    }

    @Override
    public void onShutdown() {
        this.getKeys().forEach(CrateKey::clear);
        this.getKeysMap().clear();
    }

    public boolean create(@NotNull String id) {
        id = StringUtil.lowerCaseUnderscore(id);
        if (this.getKeyById(id) != null) {
            return false;
        }

        JYML cfg = new JYML(this.plugin.getDataFolder() + Config.DIR_KEYS, id + ".yml");
        CrateKey key = new CrateKey(this.plugin, cfg);
        key.setName(Lang.LIME + StringUtil.capitalizeFully(id) + " Key");
        key.setVirtual(false);

        ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK);
        item.editMeta(meta -> meta.displayName(ComponentUtil.asComponent(key.getName()))); // Mewcraft

        key.setItem(item);
        key.save();
        key.load();

        this.getKeysMap().put(key.getId(), key);
        return true;
    }

    public boolean delete(@NotNull CrateKey crateKey) {
        if (crateKey.getFile().delete()) {
            crateKey.clear();
            this.getKeysMap().remove(crateKey.getId());
            return true;
        }
        return false;
    }

    @NotNull
    public Map<String, CrateKey> getKeysMap() {
        return this.keysMap;
    }

    @NotNull
    public Collection<CrateKey> getKeys() {
        return this.getKeysMap().values();
    }

    @NotNull
    public List<String> getKeyIds() {
        return new ArrayList<>(this.getKeysMap().keySet());
    }

    @Nullable
    public CrateKey getKeyById(@NotNull String id) {
        return this.getKeysMap().get(id.toLowerCase());
    }

    @Nullable
    public CrateKey getKeyByItem(@NotNull ItemStack item) {
        String id = PDCUtil.getString(item, Keys.CRATE_KEY_ID).orElse(null);
        return id == null ? null : this.getKeyById(id);
    }

    @NotNull
    public Set<CrateKey> getKeys(@NotNull Crate crate) {
        return crate.getKeyIds().stream().map(this::getKeyById).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @NotNull
    public Set<CrateKey> getKeys(@NotNull Player player, @NotNull Crate crate) {
        return this.getKeys(crate).stream().filter(key -> this.getKeysAmount(player, key) > 0).collect(Collectors.toSet());
    }

    @Nullable
    public ItemStack getFirstKeyStack(@NotNull Player player, @NotNull CrateKey crateKey) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null || item.getType().isAir()) continue;

            CrateKey crateKey2 = this.getKeyByItem(item);
            if (crateKey2 != null && crateKey2.equals(crateKey)) {
                return item;
            }
        }
        return null;
    }

    public boolean isKey(@NotNull ItemStack item) {
        return this.getKeyByItem(item) != null;
    }

    public boolean isKey(@NotNull ItemStack item, @NotNull CrateKey other) {
        CrateKey key = this.getKeyByItem(item);
        return key != null && key.getId().equalsIgnoreCase(other.getId());
    }

    public int getKeysAmount(@NotNull Player player, @NotNull Crate crate) {
        return this.getKeys(player, crate).stream().mapToInt(key -> this.getKeysAmount(player, key)).sum();
    }

    public int getKeysAmount(@NotNull Player player, @NotNull CrateKey crateKey) {
        if (crateKey.isVirtual()) {
            CrateUser user = plugin.getUserManager().getUserData(player);
            return user.getKeys(crateKey.getId());
        }
        return PlayerUtil.countItem(player, itemHas -> {
            CrateKey itemKey = this.getKeyByItem(itemHas);
            return itemKey != null && itemKey.getId().equalsIgnoreCase(crateKey.getId());
        });
    }

    public boolean hasKey(@NotNull Player player, @NotNull Crate crate) {
        return !this.getKeys(player, crate).isEmpty();
    }

    public boolean hasKey(@NotNull Player player, @NotNull CrateKey crateKey) {
        return this.getKeysAmount(player, crateKey) > 0;
    }

    public void giveKeysOnHold(@NotNull Player player) {
        CrateUser user = plugin.getUserManager().getUserData(player);
        user.getKeysOnHold().forEach((keyId, amount) -> {
            CrateKey crateKey = this.getKeyById(keyId);
            if (crateKey == null) return;

            this.giveKey(player, crateKey, amount);
        });
        user.cleanKeysOnHold();
        user.saveData(this.plugin);
    }

    public void setKey(@NotNull CrateUser user, @NotNull CrateKey key, int amount) {
        Player player = user.getPlayer();
        if (player != null) {
            this.setKey(player, key, amount);
            return;
        }

        if (key.isVirtual()) {
            user.setKeys(key.getId(), amount);
        }
    }

    public void setKey(@NotNull Player player, @NotNull CrateKey key, int amount) {
        if (key.isVirtual()) {
            CrateUser user = plugin.getUserManager().getUserData(player);
            user.setKeys(key.getId(), amount);
        } else {
            ItemStack keyItem = key.getItem();
            int has = PlayerUtil.countItem(player, keyItem);
            if (has > amount) {
                PlayerUtil.takeItem(player, keyItem, has - amount);
            } else if (has < amount) {
                PlayerUtil.addItem(player, keyItem, amount - has);
            }
        }
    }

    public void giveKey(@NotNull CrateUser user, @NotNull CrateKey key, int amount) {
        Player player = user.getPlayer();
        if (player != null) {
            this.giveKey(player, key, amount);
            return;
        }

        if (key.isVirtual()) {
            user.addKeys(key.getId(), amount);
        } else {
            user.addKeysOnHold(key.getId(), amount);
        }
    }

    public void giveKey(@NotNull Player player, @NotNull CrateKey key, int amount) {
        if (key.isVirtual()) {
            CrateUser user = plugin.getUserManager().getUserData(player);
            user.addKeys(key.getId(), amount);
        } else {
            ItemStack keyItem = key.getItem();
            keyItem.setAmount(amount < 0 ? Math.abs(amount) : amount);
            PlayerUtil.addItem(player, keyItem);
        }
    }

    public void takeKey(@NotNull CrateUser user, @NotNull CrateKey key, int amount) {
        Player player = user.getPlayer();
        if (player != null) {
            this.takeKey(player, key, amount);
            return;
        }

        if (key.isVirtual()) {
            user.takeKeys(key.getId(), amount);
        }
    }

    public void takeKey(@NotNull Player player, @NotNull CrateKey key, int amount) {
        if (key.isVirtual()) {
            CrateUser user = plugin.getUserManager().getUserData(player);
            user.takeKeys(key.getId(), amount);
        } else {
            Predicate<ItemStack> predicate = itemHas -> {
                CrateKey itemKey = this.getKeyByItem(itemHas);
                return itemKey != null && itemKey.getId().equalsIgnoreCase(key.getId());
            };
            int has = PlayerUtil.countItem(player, predicate);
            if (has < amount) amount = has;

            PlayerUtil.takeItem(player, predicate, amount);
        }
    }
}
