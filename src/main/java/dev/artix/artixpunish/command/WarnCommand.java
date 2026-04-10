package dev.artix.artixpunish.command;

import dev.artix.artixpunish.manager.HistoryManager;
import dev.artix.artixpunish.manager.WarnManager;
import dev.artix.artixpunish.model.WarnEntry;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class WarnCommand implements CommandHandler {

    private final WarnManager warnManager;
    private final HistoryManager historyManager;

    public WarnCommand(WarnManager warnManager, HistoryManager historyManager) {
        this.warnManager = warnManager;
        this.historyManager = historyManager;
    }

    @Override
    public boolean handle(CommandArgs a) {
        if (!a.hasPermission("artixpunish.warn")) {
            Messages.send(a.getSender(), "error-no-permission");
            return true;
        }
        if (a.length() < 1) {
            Messages.send(a.getSender(), "usage-warn");
            return true;
        }
        OfflinePlayer target = a.getOfflinePlayer(0);
        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            Messages.send(a.getSender(), "error-player-unknown", "player", a.getArg(0));
            return true;
        }
        String reason = a.defaultReason(1);
        String staff = a.getSender().getName();
        String name = target.getName() != null ? target.getName() : a.getArg(0);
        WarnEntry entry = new WarnEntry(reason, staff, System.currentTimeMillis());
        warnManager.addWarn(target.getUniqueId(), name, entry);
        historyManager.log("WARN", name, target.getUniqueId(), reason, staff, null);

        int total = warnManager.getWarnCount(target.getUniqueId());
        Messages.send(a.getSender(), "warn-applied", "player", name, "reason", reason, "total", String.valueOf(total));

        Player online = target.getPlayer();
        if (online != null) {
            Messages.send(online, "warn-notify", "reason", reason, "staff", staff, "total", String.valueOf(total));
        }
        return true;
    }
}
