package dev.artix.artixpunish.command;

import dev.artix.artixpunish.manager.WarnManager;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class ClearWarnsCommand implements CommandHandler {

    private final WarnManager warnManager;

    public ClearWarnsCommand(WarnManager warnManager) {
        this.warnManager = warnManager;
    }

    @Override
    public boolean handle(CommandArgs a) {
        if (!a.hasPermission("artixpunish.clearwarns")) {
            Messages.send(a.getSender(), "error-no-permission");
            return true;
        }
        if (a.length() < 1) {
            Messages.send(a.getSender(), "usage-clearwarns");
            return true;
        }
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(a.getArg(0));
        if (warnManager.clearWarns(target.getUniqueId())) {
            Messages.send(a.getSender(), "clearwarns-ok", "player", a.getArg(0));
        } else {
            Messages.send(a.getSender(), "clearwarns-none", "player", a.getArg(0));
        }
        return true;
    }
}
