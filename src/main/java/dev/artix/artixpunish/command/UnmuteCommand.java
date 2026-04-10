package dev.artix.artixpunish.command;

import dev.artix.artixpunish.manager.MuteManager;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class UnmuteCommand implements CommandHandler {

    private final MuteManager muteManager;

    public UnmuteCommand(MuteManager muteManager) {
        this.muteManager = muteManager;
    }

    @Override
    public boolean handle(CommandArgs a) {
        if (!a.hasPermission("artixpunish.unmute")) {
            Messages.send(a.getSender(), "error-no-permission");
            return true;
        }
        if (a.length() < 1) {
            Messages.send(a.getSender(), "usage-unmute");
            return true;
        }
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(a.getArg(0));
        if (muteManager.removeMute(target.getUniqueId())) {
            Messages.send(a.getSender(), "unmute-ok", "player", a.getArg(0));
        } else {
            Messages.send(a.getSender(), "unmute-not-found", "player", a.getArg(0));
        }
        return true;
    }
}
