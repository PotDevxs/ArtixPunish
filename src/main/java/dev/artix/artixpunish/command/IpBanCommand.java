package dev.artix.artixpunish.command;

import dev.artix.artixpunish.manager.BanManager;
import dev.artix.artixpunish.manager.HistoryManager;
import dev.artix.artixpunish.model.PunishmentRecord;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public final class IpBanCommand implements CommandHandler {

    private final BanManager banManager;
    private final HistoryManager historyManager;

    public IpBanCommand(BanManager banManager, HistoryManager historyManager) {
        this.banManager = banManager;
        this.historyManager = historyManager;
    }

    @Override
    public boolean handle(CommandArgs a) {
        if (!a.hasPermission("artixpunish.ipban")) {
            Messages.send(a.getSender(), "error-no-permission");
            return true;
        }
        if (a.length() < 1) {
            Messages.send(a.getSender(), "usage-ipban");
            return true;
        }
        String rawIp = a.getArg(0);
        String ip = BanManager.normalizeIp(rawIp);
        if (ip.isEmpty()) {
            Messages.send(a.getSender(), "error-ip-invalid");
            return true;
        }
        String reason = a.defaultReason(1);
        String staff = a.getSender().getName();
        UUID id = UUID.nameUUIDFromBytes(("ipban:" + ip).getBytes(StandardCharsets.UTF_8));
        String labelName = "IP:" + ip;
        PunishmentRecord record = new PunishmentRecord(id, labelName, reason, staff, System.currentTimeMillis(), -1L);
        banManager.setIpBan(ip, record);
        historyManager.log("IPBAN", labelName, id, reason, staff, null);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getAddress() != null && BanManager.normalizeIp(p.getAddress().getAddress().getHostAddress()).equals(ip)) {
                p.kickPlayer(Messages.format("kick-ipban", "reason", reason, "staff", staff));
            }
        }
        Messages.send(a.getSender(), "ipban-applied", "ip", ip, "reason", reason);
        return true;
    }
}
