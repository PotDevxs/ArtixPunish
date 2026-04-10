package dev.artix.artixpunish.command;

import dev.artix.artixpunish.manager.BanManager;
import dev.artix.artixpunish.manager.HistoryManager;
import dev.artix.artixpunish.model.PunishmentRecord;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
public final class BanCommand implements CommandHandler {

    private final BanManager banManager;
    private final HistoryManager historyManager;

    public BanCommand(BanManager banManager, HistoryManager historyManager) {
        this.banManager = banManager;
        this.historyManager = historyManager;
    }

    @Override
    public boolean handle(CommandArgs a) {
        if (!a.hasPermission("artixpunish.ban")) {
            Messages.send(a.getSender(), "error-no-permission");
            return true;
        }
        if (a.length() < 1) {
            Messages.send(a.getSender(), "usage-ban");
            return true;
        }
        OfflinePlayer target = a.getOfflinePlayer(0);
        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            Messages.send(a.getSender(), "error-player-unknown", "player", a.getArg(0));
            return true;
        }
        String reason = a.defaultReason(1);
        String staff = a.getSender().getName();
        PunishmentRecord record = new PunishmentRecord(
                target.getUniqueId(),
                target.getName() != null ? target.getName() : a.getArg(0),
                reason,
                staff,
                System.currentTimeMillis(),
                -1L);
        banManager.setBan(record);
        historyManager.log("BAN", record.getLastKnownName(), target.getUniqueId(), reason, staff, null);

        Messages.send(a.getSender(), "ban-applied", "player", record.getLastKnownName(), "reason", reason);

        Player online = target.getPlayer();
        if (online != null) {
            online.kickPlayer(Messages.format("kick-ban", "reason", reason, "staff", staff));
        }
        return true;
    }
}
