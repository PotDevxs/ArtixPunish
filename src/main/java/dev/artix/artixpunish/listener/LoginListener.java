package dev.artix.artixpunish.listener;

import dev.artix.artixpunish.manager.BanManager;
import dev.artix.artixpunish.model.PunishmentRecord;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public final class LoginListener implements Listener {

    private final BanManager banManager;

    public LoginListener(BanManager banManager) {
        this.banManager = banManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        String ip = BanManager.normalizeIp(event.getAddress() != null ? event.getAddress().getHostAddress() : "");

        PunishmentRecord ipBan = banManager.getIpBan(ip);
        if (ipBan != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Messages.formatBanScreen(ipBan, true));
            return;
        }

        PunishmentRecord ban = banManager.getBan(event.getUniqueId());
        if (ban != null) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, Messages.formatBanScreen(ban, false));
        }
    }
}
