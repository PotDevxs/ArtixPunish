package dev.artix.artixpunish.command;

import dev.artix.artixpunish.manager.HistoryManager;
import dev.artix.artixpunish.manager.MuteManager;
import dev.artix.artixpunish.model.PunishmentRecord;
import dev.artix.artixpunish.util.DurationParser;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.OfflinePlayer;
public final class TempMuteCommand implements CommandHandler {

    private final MuteManager muteManager;
    private final HistoryManager historyManager;

    public TempMuteCommand(MuteManager muteManager, HistoryManager historyManager) {
        this.muteManager = muteManager;
        this.historyManager = historyManager;
    }

    @Override
    public boolean handle(CommandArgs a) {
        if (!a.hasPermission("artixpunish.tempmute")) {
            Messages.send(a.getSender(), "error-no-permission");
            return true;
        }
        if (a.length() < 2) {
            Messages.send(a.getSender(), "usage-tempmute");
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
        muteManager.setMute(record);
        historyManager.log("TEMPMUTE", record.getLastKnownName(), target.getUniqueId(), reason, staff, Long.valueOf(expires));
        Messages.send(a.getSender(), "tempmute-applied",
                "player", record.getLastKnownName(),
                "reason", reason,
                "duration", DurationParser.formatMillis(millis));
        return true;
    }
}
