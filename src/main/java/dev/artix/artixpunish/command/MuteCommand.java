package dev.artix.artixpunish.command;

import dev.artix.artixpunish.manager.HistoryManager;
import dev.artix.artixpunish.manager.MuteManager;
import dev.artix.artixpunish.model.PunishmentRecord;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.OfflinePlayer;
public final class MuteCommand implements CommandHandler {

    private final MuteManager muteManager;
    private final HistoryManager historyManager;

    public MuteCommand(MuteManager muteManager, HistoryManager historyManager) {
        this.muteManager = muteManager;
        this.historyManager = historyManager;
    }

    @Override
    public boolean handle(CommandArgs a) {
        if (!a.hasPermission("artixpunish.mute")) {
            Messages.send(a.getSender(), "error-no-permission");
            return true;
        }
        if (a.length() < 1) {
            Messages.send(a.getSender(), "usage-mute");
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
        muteManager.setMute(record);
        historyManager.log("MUTE", record.getLastKnownName(), target.getUniqueId(), reason, staff, null);
        Messages.send(a.getSender(), "mute-applied", "player", record.getLastKnownName(), "reason", reason);
        return true;
    }
}
