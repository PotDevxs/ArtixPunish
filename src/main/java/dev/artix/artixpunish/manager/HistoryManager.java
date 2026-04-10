package dev.artix.artixpunish.manager;

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

/**
 * Log simples das últimas punições (para /historico).
 */
public final class HistoryManager {

    private static final int MAX_ENTRIES = 500;

    private final JavaPlugin plugin;
    private final File file;
    private final List<Map<String, Object>> entries = new ArrayList<Map<String, Object>>();

    public HistoryManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data/history.yml");
    }

    public void load() {
        entries.clear();
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Não foi possível criar history.yml: " + e.getMessage());
            }
        }
        FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        @SuppressWarnings("unchecked")
        List<Map<?, ?>> list = (List<Map<?, ?>>) yaml.getList("entries");
        if (list != null) {
            for (Map<?, ?> m : list) {
                Map<String, Object> copy = new HashMap<String, Object>();
                for (Map.Entry<?, ?> e : m.entrySet()) {
                    copy.put(String.valueOf(e.getKey()), e.getValue());
                }
                entries.add(copy);
            }
        }
    }

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("entries", new ArrayList<Map<String, Object>>(entries));
        try {
            yaml.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Erro ao salvar history.yml: " + e.getMessage());
        }
    }

    public void log(String type, String targetName, UUID targetUuid, String reason, String staff, Long expiresAt) {
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("type", type);
        row.put("targetName", targetName != null ? targetName : "");
        row.put("targetUuid", targetUuid != null ? targetUuid.toString() : "");
        row.put("reason", reason != null ? reason : "");
        row.put("staff", staff != null ? staff : "");
        row.put("created", System.currentTimeMillis());
        if (expiresAt != null) {
            row.put("expires", expiresAt.longValue());
        }
        entries.add(0, row);
        while (entries.size() > MAX_ENTRIES) {
            entries.remove(entries.size() - 1);
        }
        save();
    }

    public List<Map<String, Object>> getForPlayer(UUID uuid) {
        if (uuid == null) {
            return Collections.emptyList();
        }
        String id = uuid.toString();
        List<Map<String, Object>> out = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> e : entries) {
            Object u = e.get("targetUuid");
            if (id.equals(u != null ? u.toString() : "")) {
                out.add(e);
            }
        }
        return out;
    }
}
