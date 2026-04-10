package dev.artix.artixpunish.manager;

import dev.artix.artixpunish.model.WarnEntry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class WarnManager {

    private final JavaPlugin plugin;
    private final File file;
    private final Map<UUID, List<WarnEntry>> warns = new HashMap<UUID, List<WarnEntry>>();
    private final Map<UUID, String> names = new HashMap<UUID, String>();

    public WarnManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data/warns.yml");
    }

    public void load() {
        warns.clear();
        names.clear();
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Não foi possível criar warns.yml: " + e.getMessage());
            }
        }
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection sec = yaml.getConfigurationSection("players");
        if (sec != null) {
            for (String key : sec.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    ConfigurationSection ps = sec.getConfigurationSection(key);
                    if (ps == null) {
                        continue;
                    }
                    names.put(uuid, ps.getString("name", ""));
                    List<Map<?, ?>> list = ps.getMapList("entries");
                    List<WarnEntry> entries = new ArrayList<WarnEntry>();
                    for (Map<?, ?> m : list) {
                        Object reason = m.get("reason");
                        Object staff = m.get("staff");
                        Object created = m.get("created");
                        long c = created instanceof Number ? ((Number) created).longValue() : System.currentTimeMillis();
                        entries.add(new WarnEntry(
                                reason != null ? reason.toString() : "",
                                staff != null ? staff.toString() : "",
                                c));
                    }
                    warns.put(uuid, entries);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<UUID, List<WarnEntry>> en : warns.entrySet()) {
            String path = "players." + en.getKey().toString();
            yaml.set(path + ".name", names.getOrDefault(en.getKey(), ""));
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            for (WarnEntry w : en.getValue()) {
                Map<String, Object> m = new HashMap<String, Object>();
                m.put("reason", w.getReason());
                m.put("staff", w.getStaff());
                m.put("created", w.getCreated());
                list.add(m);
            }
            yaml.set(path + ".entries", list);
        }
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Erro ao salvar warns.yml: " + e.getMessage());
        }
    }

    public List<WarnEntry> getWarns(UUID uuid) {
        List<WarnEntry> list = warns.get(uuid);
        if (list == null) {
            return Collections.emptyList();
        }
        return new ArrayList<WarnEntry>(list);
    }

    public int getWarnCount(UUID uuid) {
        List<WarnEntry> list = warns.get(uuid);
        return list != null ? list.size() : 0;
    }

    public void addWarn(UUID uuid, String lastKnownName, WarnEntry entry) {
        List<WarnEntry> list = warns.get(uuid);
        if (list == null) {
            list = new ArrayList<WarnEntry>();
            warns.put(uuid, list);
        }
        list.add(entry);
        if (lastKnownName != null) {
            names.put(uuid, lastKnownName);
        }
        save();
    }

    public boolean clearWarns(UUID uuid) {
        if (warns.remove(uuid) != null) {
            names.remove(uuid);
            save();
            return true;
        }
        return false;
    }

    public String getStoredName(UUID uuid) {
        return names.getOrDefault(uuid, "");
    }
}
