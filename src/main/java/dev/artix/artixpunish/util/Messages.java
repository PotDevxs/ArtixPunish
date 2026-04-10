package dev.artix.artixpunish.util;

import dev.artix.artixpunish.model.PunishmentRecord;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public final class Messages {

    private static FileConfiguration cfg;
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-BR"));

    static {
        DATE_FMT.setTimeZone(TimeZone.getDefault());
    }

    private Messages() {
    }

    public static void init(JavaPlugin plugin) {
        cfg = plugin.getConfig();
    }

    public static void reload(JavaPlugin plugin) {
        plugin.reloadConfig();
        cfg = plugin.getConfig();
    }

    public static String raw(String key) {
        String s = cfg.getString(key, "&cMensagem ausente: " + key);
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String format(String key, String... replacements) {
        String out = raw(key);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            out = out.replace("%" + replacements[i] + "%", replacements[i + 1]);
        }
        return out;
    }

    public static void send(CommandSender sender, String key, String... replacements) {
        sender.sendMessage(format(key, replacements));
    }

    public static String formatDate(long epochMillis) {
        return DATE_FMT.format(new Date(epochMillis));
    }

    /**
     * Mensagem de kick no login (estilo redes grandes: várias linhas, separadores).
     * Usa {@code ban-screen-lines} no config; se vazio, cai no formato antigo {@code ban-screen}.
     */
    public static String formatBanScreen(PunishmentRecord r, boolean ipBan) {
        List<String> lines = cfg.getStringList("ban-screen-lines");
        if (lines == null || lines.isEmpty()) {
            return legacyBanScreen(r);
        }
        String discord = cfg.getString("ban-screen-discord", "discord.gg/artix");
        String banTypeLine;
        if (ipBan) {
            banTypeLine = cfg.getString("ban-screen-kind-ip", "&cBanimento por IP");
        } else if (r.isPermanent()) {
            banTypeLine = cfg.getString("ban-screen-kind-permanent", "&cBanimento permanente");
        } else {
            banTypeLine = cfg.getString("ban-screen-kind-temp", "&eBanimento temporário");
        }
        String expiresLine;
        if (r.isPermanent()) {
            expiresLine = cfg.getString("ban-screen-line-expires-permanent", "&7Duração: &fPermanente");
        } else {
            expiresLine = cfg.getString("ban-screen-line-expires-temp", "&7Expira em: &f%time%")
                    .replace("%time%", formatDate(r.getExpiresAt()));
        }
        String player = r.getLastKnownName() != null ? r.getLastKnownName() : "";
        String reason = r.getReason() != null ? r.getReason() : "";
        String staff = r.getStaff() != null ? r.getStaff() : "";
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            String s = line;
            s = s.replace("%player%", player);
            s = s.replace("%reason%", reason);
            s = s.replace("%staff%", staff);
            s = s.replace("%discord%", discord);
            s = s.replace("%banTypeLine%", banTypeLine);
            s = s.replace("%expiresLine%", expiresLine);
            sb.append(ChatColor.translateAlternateColorCodes('&', s)).append('\n');
        }
        return sb.toString().trim();
    }

    private static String legacyBanScreen(PunishmentRecord r) {
        String exp;
        if (r.isPermanent()) {
            exp = raw("ban-screen-permanent");
        } else {
            exp = format("ban-screen-expires", "time", formatDate(r.getExpiresAt()));
        }
        return format("ban-screen",
                "reason", r.getReason(),
                "staff", r.getStaff(),
                "expiresLine", exp);
    }
}
