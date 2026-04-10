package dev.artix.artixpunish.command;

import dev.artix.artixpunish.manager.BanManager;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class UnbanCommand implements CommandHandler {

    private final BanManager banManager;

    public UnbanCommand(BanManager banManager) {
        this.banManager = banManager;
    }

    @Override
    public boolean handle(CommandArgs a) {
        if (!a.hasPermission("artixpunish.unban")) {
            Messages.send(a.getSender(), "error-no-permission");
            return true;
        }
        if (a.length() < 1) {
            Messages.send(a.getSender(), "usage-unban");
            return true;
        }
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(a.getArg(0));
        if (banManager.removeBan(target.getUniqueId())) {
            Messages.send(a.getSender(), "unban-ok", "player", a.getArg(0));
        } else {
            Messages.send(a.getSender(), "unban-not-found", "player", a.getArg(0));
        }
        return true;
    }
}
