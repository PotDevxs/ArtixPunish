package dev.artix.artixpunish.manager;

import dev.artix.artixpunish.model.PunishmentRecord;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class MuteManager {

    private final JavaPlugin plugin;
    private final File file;
    private final Map<UUID, PunishmentRecord> mutes = new HashMap<UUID, PunishmentRecord>();

    public MuteManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data/mutes.yml");
    }

    public void load() {
        mutes.clear();
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Não foi possível criar mutes.yml: " + e.getMessage());
            }
        }
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection sec = yaml.getConfigurationSection("players");
        if (sec != null) {
            for (String key : sec.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    ConfigurationSection e = sec.getConfigurationSection(key);
                    if (e == null) {
                        continue;
                    }
                    String name = e.getString("name", "");
                    String reason = e.getString("reason", "");
                    String staff = e.getString("staff", "");
                    long created = e.getLong("created", System.currentTimeMillis());
                    long expires = e.getLong("expires", -1);
                    PunishmentRecord r = new PunishmentRecord(uuid, name, reason, staff, created, expires);
                    if (!r.isExpired()) {
                        mutes.put(uuid, r);
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<UUID, PunishmentRecord> en : mutes.entrySet()) {
            PunishmentRecord r = en.getValue();
            String path = "players." + en.getKey().toString();
            yaml.set(path + ".name", r.getLastKnownName());
            yaml.set(path + ".reason", r.getReason());
            yaml.set(path + ".staff", r.getStaff());
            yaml.set(path + ".created", r.getCreated());
            yaml.set(path + ".expires", r.getExpiresAt());
        }
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Erro ao salvar mutes.yml: " + e.getMessage());
        }
    }

    public PunishmentRecord getMute(UUID uuid) {
        PunishmentRecord r = mutes.get(uuid);
        if (r != null && r.isExpired()) {
            mutes.remove(uuid);
            save();
            return null;
        }
        return r;
    }

    public void setMute(PunishmentRecord record) {
        mutes.put(record.getUuid(), record);
        save();
    }

    public boolean removeMute(UUID uuid) {
        if (mutes.remove(uuid) != null) {
            save();
            return true;
        }
        return false;
    }

    public Map<UUID, PunishmentRecord> getMutesSnapshot() {
        return new HashMap<UUID, PunishmentRecord>(mutes);
    }
}
