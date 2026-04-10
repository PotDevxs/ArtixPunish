package dev.artix.artixpunish.command;

import dev.artix.artixpunish.manager.BanManager;
import dev.artix.artixpunish.util.Messages;

public final class UnbanIpCommand implements CommandHandler {

    private final BanManager banManager;

    public UnbanIpCommand(BanManager banManager) {
        this.banManager = banManager;
    }

    @Override
    public boolean handle(CommandArgs a) {
        if (!a.hasPermission("artixpunish.unbanip")) {
            Messages.send(a.getSender(), "error-no-permission");
            return true;
        }
        if (a.length() < 1) {
            Messages.send(a.getSender(), "usage-unbanip");
            return true;
        }
        String ip = BanManager.normalizeIp(a.getArg(0));
        if (banManager.removeIpBan(ip)) {
            Messages.send(a.getSender(), "unbanip-ok", "ip", ip);
        } else {
            Messages.send(a.getSender(), "unbanip-not-found", "ip", ip);
        }
        return true;
    }
}
