package dev.artix.artixpunish.command;

import dev.artix.artixpunish.manager.BanManager;
import dev.artix.artixpunish.manager.HistoryManager;
import dev.artix.artixpunish.model.PunishmentRecord;
import dev.artix.artixpunish.util.DurationParser;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
public final class TempBanCommand implements CommandHandler {

    private final BanManager banManager;
    private final HistoryManager historyManager;

    public TempBanCommand(BanManager banManager, HistoryManager historyManager) {
        this.banManager = banManager;
        this.historyManager = historyManager;
    }

    @Override
    public boolean handle(CommandArgs a) {
        if (!a.hasPermission("artixpunish.tempban")) {
            Messages.send(a.getSender(), "error-no-permission");
            return true;
        }
        if (a.length() < 2) {
            Messages.send(a.getSender(), "usage-tempban");
            return true;
        }
        OfflinePlayer target = a.getOfflinePlayer(0);
        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            Messages.send(a.getSender(), "error-player-unknown", "player", a.getArg(0));
            return true;
        }
        long millis = DurationParser.parse(a.getArg(1));
        if (millis < 0) {
            Messages.send(a.getSender(), "error-duration");
            return true;
        }
        long expires = System.currentTimeMillis() + millis;
        String reason = a.defaultReason(2);
        String staff = a.getSender().getName();
        PunishmentRecord record = new PunishmentRecord(
                target.getUniqueId(),
                target.getName() != null ? target.getName() : a.getArg(0),
                reason,
                staff,
                System.currentTimeMillis(),
                expires);
        banManager.setBan(record);
        historyManager.log("TEMPBAN", record.getLastKnownName(), target.getUniqueId(), reason, staff, Long.valueOf(expires));

        Messages.send(a.getSender(), "tempban-applied",
                "player", record.getLastKnownName(),
                "reason", reason,
                "duration", DurationParser.formatMillis(millis));

        Player online = target.getPlayer();
        if (online != null) {
            online.kickPlayer(Messages.format("kick-tempban",
                    "reason", reason,
                    "staff", staff,
                    "expires", Messages.formatDate(expires)));
        }
        return true;
    }
}
