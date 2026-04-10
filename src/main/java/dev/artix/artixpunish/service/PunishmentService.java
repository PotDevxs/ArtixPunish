package dev.artix.artixpunish.service;

import dev.artix.artixpunish.manager.BanManager;
import dev.artix.artixpunish.manager.HistoryManager;
import dev.artix.artixpunish.manager.MuteManager;
import dev.artix.artixpunish.manager.WarnManager;
import dev.artix.artixpunish.model.PunishmentRecord;
import dev.artix.artixpunish.model.WarnEntry;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Lógica central de punições (comandos e menus).
 */
public final class PunishmentService {

    private PunishmentService() {
    }

    public static String defaultReason(JavaPlugin plugin, String key) {
        String r = plugin.getConfig().getString(key, null);
        if (r != null && !r.trim().isEmpty()) {
            return r.trim();
        }
        return plugin.getConfig().getString("defaults.reason", "Sem motivo");
    }

    public static boolean banPermanent(JavaPlugin plugin, BanManager bans, HistoryManager hist,
                                       CommandSender staff, String targetName, String reason) {
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            Messages.send(staff, "error-player-unknown", "player", targetName);
            return false;
        }
        String staffName = staff.getName();
        PunishmentRecord record = new PunishmentRecord(
                target.getUniqueId(),
                target.getName() != null ? target.getName() : targetName,
                reason,
                staffName,
                System.currentTimeMillis(),
                -1L);
        bans.setBan(record);
        hist.log("BAN", record.getLastKnownName(), target.getUniqueId(), reason, staffName, null);
        Messages.send(staff, "ban-applied", "player", record.getLastKnownName(), "reason", reason);
        Player online = target.getPlayer();
        if (online != null) {
            online.kickPlayer(Messages.format("kick-ban", "reason", reason, "staff", staffName));
        }
        return true;
    }

    public static boolean tempBan(JavaPlugin plugin, BanManager bans, HistoryManager hist,
                                  CommandSender staff, String targetName, long millis, String reason) {
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            Messages.send(staff, "error-player-unknown", "player", targetName);
            return false;
        }
        long expires = System.currentTimeMillis() + millis;
        String staffName = staff.getName();
        PunishmentRecord record = new PunishmentRecord(
                target.getUniqueId(),
                target.getName() != null ? target.getName() : targetName,
                reason,
                staffName,
                System.currentTimeMillis(),
                expires);
        bans.setBan(record);
        hist.log("TEMPBAN", record.getLastKnownName(), target.getUniqueId(), reason, staffName, Long.valueOf(expires));
        Messages.send(staff, "tempban-applied", "player", record.getLastKnownName(), "reason", reason,
                "duration", dev.artix.artixpunish.util.DurationParser.formatMillis(millis));
        Player online = target.getPlayer();
        if (online != null) {
            online.kickPlayer(Messages.format("kick-tempban", "reason", reason, "staff", staffName,
                    "expires", Messages.formatDate(expires)));
        }
        return true;
    }

    public static boolean kick(JavaPlugin plugin, HistoryManager hist, CommandSender staff, String targetName, String reason) {
        Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) {
            Messages.send(staff, "error-not-online", "player", targetName);
            return false;
        }
        if (target.equals(staff)) {
            Messages.send(staff, "error-self");
            return false;
        }
        String staffName = staff.getName();
        hist.log("KICK", target.getName(), target.getUniqueId(), reason, staffName, null);
        target.kickPlayer(Messages.format("kick-generic", "reason", reason, "staff", staffName));
        Messages.send(staff, "kick-applied", "player", target.getName(), "reason", reason);
        return true;
    }

    public static boolean mutePermanent(JavaPlugin plugin, MuteManager mutes, HistoryManager hist,
                                         CommandSender staff, String targetName, String reason) {
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            Messages.send(staff, "error-player-unknown", "player", targetName);
            return false;
        }
        String staffName = staff.getName();
        PunishmentRecord record = new PunishmentRecord(
                target.getUniqueId(),
                target.getName() != null ? target.getName() : targetName,
                reason,
                staffName,
                System.currentTimeMillis(),
                -1L);
        mutes.setMute(record);
        hist.log("MUTE", record.getLastKnownName(), target.getUniqueId(), reason, staffName, null);
        Messages.send(staff, "mute-applied", "player", record.getLastKnownName(), "reason", reason);
        return true;
    }

    public static boolean tempMute(JavaPlugin plugin, MuteManager mutes, HistoryManager hist,
                                    CommandSender staff, String targetName, long millis, String reason) {
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            Messages.send(staff, "error-player-unknown", "player", targetName);
            return false;
        }
        long expires = System.currentTimeMillis() + millis;
        String staffName = staff.getName();
        PunishmentRecord record = new PunishmentRecord(
                target.getUniqueId(),
                target.getName() != null ? target.getName() : targetName,
                reason,
                staffName,
                System.currentTimeMillis(),
                expires);
        mutes.setMute(record);
        hist.log("TEMPMUTE", record.getLastKnownName(), target.getUniqueId(), reason, staffName, Long.valueOf(expires));
        Messages.send(staff, "tempmute-applied", "player", record.getLastKnownName(), "reason", reason,
                "duration", dev.artix.artixpunish.util.DurationParser.formatMillis(millis));
        return true;
    }

    public static boolean warn(JavaPlugin plugin, WarnManager warns, HistoryManager hist,
                                CommandSender staff, String targetName, String reason) {
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            Messages.send(staff, "error-player-unknown", "player", targetName);
            return false;
        }
        String staffName = staff.getName();
        String name = target.getName() != null ? target.getName() : targetName;
        WarnEntry entry = new WarnEntry(reason, staffName, System.currentTimeMillis());
        warns.addWarn(target.getUniqueId(), name, entry);
        hist.log("WARN", name, target.getUniqueId(), reason, staffName, null);
        int total = warns.getWarnCount(target.getUniqueId());
        Messages.send(staff, "warn-applied", "player", name, "reason", reason, "total", String.valueOf(total));
        Player online = target.getPlayer();
        if (online != null) {
            Messages.send(online, "warn-notify", "reason", reason, "staff", staffName, "total", String.valueOf(total));
        }
        return true;
    }

    public static boolean unmute(MuteManager mutes, CommandSender staff, String targetName) {
        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (mutes.removeMute(target.getUniqueId())) {
            Messages.send(staff, "unmute-ok", "player", targetName);
            return true;
        }
        Messages.send(staff, "unmute-not-found", "player", targetName);
        return false;
    }
}
