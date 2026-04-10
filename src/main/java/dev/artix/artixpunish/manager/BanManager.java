package dev.artix.artixpunish.manager;

import dev.artix.artixpunish.model.PunishmentRecord;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BanManager {

    private final JavaPlugin plugin;
    private final File file;
    private final Map<UUID, PunishmentRecord> bans = new HashMap<UUID, PunishmentRecord>();
    private final Map<String, PunishmentRecord> ipBans = new HashMap<String, PunishmentRecord>();

    public BanManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data/bans.yml");
    }

    public void load() {
        bans.clear();
        ipBans.clear();
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Não foi possível criar bans.yml: " + e.getMessage());
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
                    PunishmentRecord r = readRecord(uuid, e);
                    if (r != null && !r.isExpired()) {
                        bans.put(uuid, r);
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        ConfigurationSection ipSec = yaml.getConfigurationSection("ips");
        if (ipSec != null) {
            for (String ip : ipSec.getKeys(false)) {
                ConfigurationSection e = ipSec.getConfigurationSection(ip);
                if (e == null) {
                    continue;
                }
                PunishmentRecord r = readRecordForIp(ip, e);
                if (r != null && !r.isExpired()) {
                    ipBans.put(normalizeIp(ip), r);
                }
            }
        }
    }

    private static PunishmentRecord readRecord(UUID uuid, ConfigurationSection e) {
        String name = e.getString("name", "");
        String reason = e.getString("reason", "");
        String staff = e.getString("staff", "");
        long created = e.getLong("created", System.currentTimeMillis());
        long expires = e.getLong("expires", -1);
        return new PunishmentRecord(uuid, name, reason, staff, created, expires);
    }

    private static PunishmentRecord readRecordForIp(String ipKey, ConfigurationSection e) {
        String name = e.getString("name", "");
        String reason = e.getString("reason", "");
        String staff = e.getString("staff", "");
        long created = e.getLong("created", System.currentTimeMillis());
        long expires = e.getLong("expires", -1);
        UUID u = parseUuid(e.getString("uuid"));
        if (u == null) {
            String key = normalizeIp(ipKey);
            u = UUID.nameUUIDFromBytes(("ipban:" + key).getBytes(StandardCharsets.UTF_8));
        }
        return new PunishmentRecord(u, name, reason, staff, created, expires);
    }

    private static UUID parseUuid(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<UUID, PunishmentRecord> en : bans.entrySet()) {
            PunishmentRecord r = en.getValue();
            String path = "players." + en.getKey().toString();
            yaml.set(path + ".name", r.getLastKnownName());
            yaml.set(path + ".reason", r.getReason());
            yaml.set(path + ".staff", r.getStaff());
            yaml.set(path + ".created", r.getCreated());
            yaml.set(path + ".expires", r.getExpiresAt());
        }
        for (Map.Entry<String, PunishmentRecord> en : ipBans.entrySet()) {
            PunishmentRecord r = en.getValue();
            String path = "ips." + en.getKey();
            yaml.set(path + ".uuid", r.getUuid().toString());
            yaml.set(path + ".name", r.getLastKnownName());
            yaml.set(path + ".reason", r.getReason());
            yaml.set(path + ".staff", r.getStaff());
            yaml.set(path + ".created", r.getCreated());
            yaml.set(path + ".expires", r.getExpiresAt());
        }
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Erro ao salvar bans.yml: " + e.getMessage());
        }
    }

    public PunishmentRecord getBan(UUID uuid) {
        PunishmentRecord r = bans.get(uuid);
        if (r != null && r.isExpired()) {
            bans.remove(uuid);
            save();
            return null;
        }
        return r;
    }

    public PunishmentRecord getIpBan(String address) {
        if (address == null) {
            return null;
        }
        PunishmentRecord r = ipBans.get(normalizeIp(address));
        if (r != null && r.isExpired()) {
            ipBans.remove(normalizeIp(address));
            save();
            return null;
        }
        return r;
    }

    public void setBan(PunishmentRecord record) {
        bans.put(record.getUuid(), record);
        save();
    }

    public void setIpBan(String ip, PunishmentRecord record) {
        ipBans.put(normalizeIp(ip), record);
        save();
    }

    public boolean removeBan(UUID uuid) {
        if (bans.remove(uuid) != null) {
            save();
            return true;
        }
        return false;
    }

    public boolean removeIpBan(String ip) {
        if (ipBans.remove(normalizeIp(ip)) != null) {
            save();
            return true;
        }
        return false;
    }

    public static String normalizeIp(String address) {
        if (address == null) {
            return "";
        }
        String s = address;
        if (s.startsWith("/")) {
            s = s.substring(1);
        }
        int slash = s.indexOf('/');
        if (slash >= 0) {
            s = s.substring(0, slash);
        }
        return s.trim();
    }

    public Map<UUID, PunishmentRecord> getBansSnapshot() {
        return new HashMap<UUID, PunishmentRecord>(bans);
    }

    public Map<String, PunishmentRecord> getIpBansSnapshot() {
        return new HashMap<String, PunishmentRecord>(ipBans);
    }

    /** Remove todos os bans por UUID. Retorna quantidade removida. */
    public int clearAllPlayerBans() {
        int n = bans.size();
        bans.clear();
        save();
        return n;
    }

    /** Remove todos os bans por IP. Retorna quantidade removida. */
    public int clearAllIpBans() {
        int n = ipBans.size();
        ipBans.clear();
        save();
        return n;
    }
}
