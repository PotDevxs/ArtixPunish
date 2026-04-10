package dev.artix.artixpunish.command;

import dev.artix.artixpunish.manager.HistoryManager;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.entity.Player;

public final class KickCommand implements CommandHandler {

    private final HistoryManager historyManager;

    public KickCommand(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public boolean handle(CommandArgs a) {
        if (!a.hasPermission("artixpunish.kick")) {
            Messages.send(a.getSender(), "error-no-permission");
            return true;
        }
        if (a.length() < 1) {
            Messages.send(a.getSender(), "usage-kick");
            return true;
        }
        Player target = a.getPlayerExact(0);
        if (target == null) {
            Messages.send(a.getSender(), "error-not-online", "player", a.getArg(0));
            return true;
        }
        if (target.equals(a.getSender())) {
            Messages.send(a.getSender(), "error-self");
            return true;
        }
        String reason = a.defaultReason(1);
        String staff = a.getSender().getName();
        historyManager.log("KICK", target.getName(), target.getUniqueId(), reason, staff, null);
        target.kickPlayer(Messages.format("kick-generic", "reason", reason, "staff", staff));
        Messages.send(a.getSender(), "kick-applied", "player", target.getName(), "reason", reason);
        return true;
    }
}
