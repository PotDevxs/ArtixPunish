package dev.artix.artixpunish.gui;

import dev.artix.artixpunish.manager.BanManager;
import dev.artix.artixpunish.manager.HistoryManager;
import dev.artix.artixpunish.manager.MuteManager;
import dev.artix.artixpunish.manager.WarnManager;
import dev.artix.artixpunish.service.PunishmentService;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class MenuListener implements Listener {

    private static final long MS_H = 3600000L;
    private static final long MS_D = 24L * MS_H;

    private final JavaPlugin plugin;
    private final BanManager banManager;
    private final MuteManager muteManager;
    private final WarnManager warnManager;
    private final HistoryManager historyManager;

    public MenuListener(JavaPlugin plugin, BanManager banManager, MuteManager muteManager,
                        WarnManager warnManager, HistoryManager historyManager) {
        this.plugin = plugin;
        this.banManager = banManager;
        this.muteManager = muteManager;
        this.warnManager = warnManager;
        this.historyManager = historyManager;
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        InventoryHolder h = event.getInventory().getHolder();
        if (h instanceof ArtixMenuHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (!(holder instanceof ArtixMenuHolder)) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player) event.getWhoClicked();
        if (event.getClickedInventory() == null || event.getClickedInventory() != event.getView().getTopInventory()) {
            return;
        }
        int slot = event.getRawSlot();
        ArtixMenuHolder menu = (ArtixMenuHolder) holder;
        ItemStack cur = event.getCurrentItem();
        if (cur == null || cur.getType() == Material.AIR) {
            return;
        }

        String reason = PunishmentService.defaultReason(plugin, "menu.default-reason");

        switch (menu.getMenuType()) {
            case MAIN:
                handleMain(p, slot);
                break;
            case ONLINE_PLAYERS:
                handleOnline(p, slot, cur);
                break;
            case PUNISH_TARGET:
                handlePunish(p, slot, menu.getTargetName(), reason);
                break;
            case CONFIRM_RESET_PLAYERS:
                handleConfirmPlayers(p, slot);
                break;
            case CONFIRM_RESET_IP:
                handleConfirmIp(p, slot);
                break;
            case PICK_TEMP_BAN:
                handlePickTempBan(p, slot, menu.getTargetName(), reason);
                break;
            case PICK_TEMP_MUTE:
                handlePickTempMute(p, slot, menu.getTargetName(), reason);
                break;
            default:
                break;
        }
    }

    private void handleMain(Player p, int slot) {
        if (slot == 22) {
            p.closeInventory();
            return;
        }
        if (slot == 11) {
            MenuFactory.openOnlinePlayers(plugin, p);
            return;
        }
        if (slot == 13 && p.hasPermission("artixpunish.menu.reset.players")) {
            MenuFactory.openConfirmResetPlayers(plugin, p);
            return;
        }
        if (slot == 15 && p.hasPermission("artixpunish.menu.reset.ip")) {
            MenuFactory.openConfirmResetIp(plugin, p);
            return;
        }
    }

    private void handleOnline(Player p, int slot, ItemStack cur) {
        if (slot == 49) {
            MenuFactory.openMain(plugin, p, p.hasPermission("artixpunish.menu.reset.players"),
                    p.hasPermission("artixpunish.menu.reset.ip"));
            return;
        }
        if (cur.getType() != Material.SKULL_ITEM) {
            return;
        }
        if (!(cur.getItemMeta() instanceof SkullMeta)) {
            return;
        }
        SkullMeta sm = (SkullMeta) cur.getItemMeta();
        @SuppressWarnings("deprecation")
        String name = sm.getOwner();
        if (name == null || name.isEmpty()) {
            return;
        }
        MenuFactory.openPunishTarget(plugin, p, name);
    }

    private void handlePunish(Player p, int slot, String target, String reason) {
        if (target == null || target.isEmpty()) {
            return;
        }
        if (slot == 18) {
            MenuFactory.openOnlinePlayers(plugin, p);
            return;
        }
        if (slot == 22) {
            p.closeInventory();
            return;
        }
        switch (slot) {
            case 10:
                PunishmentService.banPermanent(plugin, banManager, historyManager, p, target, reason);
                p.closeInventory();
                break;
            case 11:
                MenuFactory.openPickTempBan(plugin, p, target);
                break;
            case 12:
                PunishmentService.kick(plugin, historyManager, p, target, reason);
                p.closeInventory();
                break;
            case 13:
                PunishmentService.mutePermanent(plugin, muteManager, historyManager, p, target, reason);
                p.closeInventory();
                break;
            case 14:
                MenuFactory.openPickTempMute(plugin, p, target);
                break;
            case 15:
                PunishmentService.warn(plugin, warnManager, historyManager, p, target, reason);
                p.closeInventory();
                break;
            case 16:
                PunishmentService.unmute(muteManager, p, target);
                p.closeInventory();
                break;
            default:
                break;
        }
    }

    private void handleConfirmPlayers(Player p, int slot) {
        if (slot == 22) {
            MenuFactory.openMain(plugin, p, p.hasPermission("artixpunish.menu.reset.players"),
                    p.hasPermission("artixpunish.menu.reset.ip"));
            return;
        }
        if (slot == 15) {
            p.closeInventory();
            return;
        }
        if (slot == 11) {
            if (!p.hasPermission("artixpunish.menu.reset.players")) {
                Messages.send(p, "error-no-permission");
                return;
            }
            int n = banManager.clearAllPlayerBans();
            Messages.send(p, "menu-reset-players", "count", String.valueOf(n));
            p.closeInventory();
        }
    }

    private void handleConfirmIp(Player p, int slot) {
        if (slot == 22) {
            MenuFactory.openMain(plugin, p, p.hasPermission("artixpunish.menu.reset.players"),
                    p.hasPermission("artixpunish.menu.reset.ip"));
            return;
        }
        if (slot == 15) {
            p.closeInventory();
            return;
        }
        if (slot == 11) {
            if (!p.hasPermission("artixpunish.menu.reset.ip")) {
                Messages.send(p, "error-no-permission");
                return;
            }
            int n = banManager.clearAllIpBans();
            Messages.send(p, "menu-reset-ip", "count", String.valueOf(n));
            p.closeInventory();
        }
    }

    private void handlePickTempBan(Player p, int slot, String target, String reason) {
        if (slot == 18) {
            MenuFactory.openPunishTarget(plugin, p, target);
            return;
        }
        long ms;
        switch (slot) {
            case 10:
                ms = MS_H;
                break;
            case 11:
                ms = 6 * MS_H;
                break;
            case 12:
                ms = MS_D;
                break;
            case 13:
                ms = 7 * MS_D;
                break;
            case 14:
                ms = 30 * MS_D;
                break;
            case 15:
                PunishmentService.banPermanent(plugin, banManager, historyManager, p, target, reason);
                p.closeInventory();
                return;
            default:
                return;
        }
        PunishmentService.tempBan(plugin, banManager, historyManager, p, target, ms, reason);
        p.closeInventory();
    }

    private void handlePickTempMute(Player p, int slot, String target, String reason) {
        if (slot == 18) {
            MenuFactory.openPunishTarget(plugin, p, target);
            return;
        }
        long ms;
        switch (slot) {
            case 10:
                ms = MS_H;
                break;
            case 11:
                ms = 6 * MS_H;
                break;
            case 12:
                ms = MS_D;
                break;
            case 13:
                ms = 7 * MS_D;
                break;
            case 14:
                ms = 30 * MS_D;
                break;
            case 15:
                PunishmentService.mutePermanent(plugin, muteManager, historyManager, p, target, reason);
                p.closeInventory();
                return;
            default:
                return;
        }
        PunishmentService.tempMute(plugin, muteManager, historyManager, p, target, ms, reason);
        p.closeInventory();
    }
}
