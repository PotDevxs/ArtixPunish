package dev.artix.artixpunish.command;

import dev.artix.artixpunish.manager.HistoryManager;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class HistoricoCommand implements CommandHandler {

    private final HistoryManager historyManager;

    public HistoricoCommand(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public boolean handle(CommandArgs a) {
        if (!a.hasPermission("artixpunish.history")) {
            Messages.send(a.getSender(), "error-no-permission");
            return true;
        }
        if (a.length() < 1) {
            Messages.send(a.getSender(), "usage-historico");
            return true;
        }
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(a.getArg(0));
        UUID uuid = target.getUniqueId();
        List<Map<String, Object>> rows = historyManager.getForPlayer(uuid);
        if (rows.isEmpty()) {
            Messages.send(a.getSender(), "historico-empty", "player", a.getArg(0));
            return true;
        }
        Messages.send(a.getSender(), "historico-header", "player", a.getArg(0));
        int n = Math.min(15, rows.size());
        for (int i = 0; i < n; i++) {
            Map<String, Object> e = rows.get(i);
            String type = String.valueOf(e.get("type"));
            String reason = String.valueOf(e.get("reason"));
            String staff = String.valueOf(e.get("staff"));
            Object c = e.get("created");
            long created = c instanceof Number ? ((Number) c).longValue() : 0L;
            String line = Messages.format("historico-line",
                    "type", type,
                    "reason", reason,
                    "staff", staff,
                    "date", Messages.formatDate(created));
            a.getSender().sendMessage(line);
        }
        if (rows.size() > n) {
            Messages.send(a.getSender(), "historico-more", "count", String.valueOf(rows.size() - n));
        }
        return true;
    }
}
