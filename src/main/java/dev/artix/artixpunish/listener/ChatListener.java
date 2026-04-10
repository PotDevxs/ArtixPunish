package dev.artix.artixpunish.listener;

import dev.artix.artixpunish.manager.MuteManager;
import dev.artix.artixpunish.model.PunishmentRecord;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public final class ChatListener implements Listener {

    private final MuteManager muteManager;

    public ChatListener(MuteManager muteManager) {
        this.muteManager = muteManager;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(PlayerChatEvent event) {
        PunishmentRecord m = muteManager.getMute(event.getPlayer().getUniqueId());
        if (m == null) {
            return;
        }
        event.setCancelled(true);
        if (m.isPermanent()) {
            Messages.send(event.getPlayer(), "mute-chat-denied-permanent",
                    "reason", m.getReason(),
                    "staff", m.getStaff());
        } else {
            Messages.send(event.getPlayer(), "mute-chat-denied-temp",
                    "reason", m.getReason(),
                    "staff", m.getStaff(),
                    "expires", Messages.formatDate(m.getExpiresAt()));
        }
    }
}
