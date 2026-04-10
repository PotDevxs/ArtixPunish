package dev.artix.artixpunish.gui;

import dev.artix.artixpunish.gui.ArtixMenuHolder.Type;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Constrói e abre os inventários de punição.
 */
public final class MenuFactory {

    public static final String TITLE_MAIN = ChatColor.BLACK + "" + ChatColor.BOLD + "Punições";
    public static final String TITLE_ONLINE = ChatColor.DARK_GRAY + "Jogadores online";
    public static final String TITLE_CONFIRM_P = ChatColor.DARK_RED + "Confirmar limpar bans";
    public static final String TITLE_CONFIRM_I = ChatColor.GOLD + "Confirmar limpar IP";
    public static final String TITLE_PICK_TB = ChatColor.DARK_AQUA + "Duração do ban";
    public static final String TITLE_PICK_TM = ChatColor.DARK_AQUA + "Duração do mute";

    private MenuFactory() {
    }

    public static void openMain(JavaPlugin plugin, Player p, boolean canResetPlayers, boolean canResetIp) {
        ArtixMenuHolder h = new ArtixMenuHolder(Type.MAIN, "");
        Inventory inv = Bukkit.createInventory(h, 27, TITLE_MAIN);
        h.setInventory(inv);
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, MenuItems.filler());
        }
        inv.setItem(11, MenuItems.named(Material.COMPASS, (short) 0, "&a&lPunir jogador",
                "&7Ver lista de jogadores online", "&7e aplicar punições."));
        if (canResetPlayers) {
            inv.setItem(13, MenuItems.named(Material.ANVIL, (short) 0, "&c&lLimpar todos os bans",
                    "&7Remove &fTODOS &7os banimentos", "&7por conta (UUID).", "&cAção irreversível!"));
        }
        if (canResetIp) {
            inv.setItem(15, MenuItems.named(Material.LAVA_BUCKET, (short) 0, "&6&lLimpar bans de IP",
                    "&7Remove todos os banimentos", "&7registados por endereço IP."));
        }
        inv.setItem(22, MenuItems.named(Material.BARRIER, (short) 0, "&cFechar", "&7Clique para sair."));
        p.openInventory(inv);
    }

    public static void openOnlinePlayers(JavaPlugin plugin, Player viewer) {
        ArtixMenuHolder h = new ArtixMenuHolder(Type.ONLINE_PLAYERS, "");
        Inventory inv = Bukkit.createInventory(h, 54, TITLE_ONLINE);
        h.setInventory(inv);
        for (int i = 0; i < 54; i++) {
            inv.setItem(i, MenuItems.filler());
        }
        int[] slots = {
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
        };
        int idx = 0;
        for (Player op : Bukkit.getOnlinePlayers()) {
            if (op.equals(viewer)) {
                continue;
            }
            if (idx >= slots.length) {
                break;
            }
            inv.setItem(slots[idx++], MenuItems.playerHead(op.getName()));
        }
        inv.setItem(49, MenuItems.named(Material.ARROW, (short) 0, "&7Voltar", "&7Ao menu principal"));
        viewer.openInventory(inv);
    }

    public static void openPunishTarget(JavaPlugin plugin, Player staff, String targetName) {
        ArtixMenuHolder h = new ArtixMenuHolder(Type.PUNISH_TARGET, targetName);
        Inventory inv = Bukkit.createInventory(h, 27, ChatColor.DARK_RED + "Punir: " + truncate(targetName, 18));
        h.setInventory(inv);
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, MenuItems.filler());
        }
        inv.setItem(10, MenuItems.named(Material.WOOL, (short) 14, "&cBan permanente", "&7Motivo: menu"));
        inv.setItem(11, MenuItems.named(Material.WOOL, (short) 1, "&6Ban temporário", "&7Escolher duração"));
        inv.setItem(12, MenuItems.named(Material.WOOL, (short) 4, "&eExpulsar (kick)", "&7Só se estiver online"));
        inv.setItem(13, MenuItems.named(Material.WOOL, (short) 11, "&9Mute permanente", ""));
        inv.setItem(14, MenuItems.named(Material.WOOL, (short) 3, "&bMute temporário", "&7Escolher duração"));
        inv.setItem(15, MenuItems.named(Material.PAPER, (short) 0, "&eAvisar (warn)", ""));
        inv.setItem(16, MenuItems.named(Material.WOOL, (short) 5, "&aRemover mute", ""));
        inv.setItem(18, MenuItems.named(Material.ARROW, (short) 0, "&7Voltar", "&7Lista de jogadores"));
        inv.setItem(22, MenuItems.named(Material.BARRIER, (short) 0, "&cFechar", ""));
        staff.openInventory(inv);
    }

    public static void openConfirmResetPlayers(JavaPlugin plugin, Player p) {
        ArtixMenuHolder h = new ArtixMenuHolder(Type.CONFIRM_RESET_PLAYERS, "");
        Inventory inv = Bukkit.createInventory(h, 27, TITLE_CONFIRM_P);
        h.setInventory(inv);
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, MenuItems.filler());
        }
        inv.setItem(11, MenuItems.named(Material.WOOL, (short) 5, "&a&lCONFIRMAR",
                "&7Remove todos os bans de jogadores."));
        inv.setItem(15, MenuItems.named(Material.WOOL, (short) 14, "&c&lCANCELAR", ""));
        inv.setItem(22, MenuItems.named(Material.ARROW, (short) 0, "&7Voltar", ""));
        p.openInventory(inv);
    }

    public static void openConfirmResetIp(JavaPlugin plugin, Player p) {
        ArtixMenuHolder h = new ArtixMenuHolder(Type.CONFIRM_RESET_IP, "");
        Inventory inv = Bukkit.createInventory(h, 27, TITLE_CONFIRM_I);
        h.setInventory(inv);
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, MenuItems.filler());
        }
        inv.setItem(11, MenuItems.named(Material.WOOL, (short) 5, "&a&lCONFIRMAR",
                "&7Remove todos os bans de IP."));
        inv.setItem(15, MenuItems.named(Material.WOOL, (short) 14, "&c&lCANCELAR", ""));
        inv.setItem(22, MenuItems.named(Material.ARROW, (short) 0, "&7Voltar", ""));
        p.openInventory(inv);
    }

    public static void openPickTempBan(JavaPlugin plugin, Player p, String targetName) {
        ArtixMenuHolder h = new ArtixMenuHolder(Type.PICK_TEMP_BAN, targetName);
        Inventory inv = Bukkit.createInventory(h, 27, TITLE_PICK_TB);
        h.setInventory(inv);
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, MenuItems.filler());
        }
        inv.setItem(10, MenuItems.named(Material.WATCH, (short) 0, "&a1 hora", ""));
        inv.setItem(11, MenuItems.named(Material.WATCH, (short) 0, "&a6 horas", ""));
        inv.setItem(12, MenuItems.named(Material.WATCH, (short) 0, "&e1 dia", ""));
        inv.setItem(13, MenuItems.named(Material.WATCH, (short) 0, "&e7 dias", ""));
        inv.setItem(14, MenuItems.named(Material.WATCH, (short) 0, "&630 dias", ""));
        inv.setItem(15, MenuItems.named(Material.WOOL, (short) 14, "&cPermanente", "&7Usa ban permanente"));
        inv.setItem(18, MenuItems.named(Material.ARROW, (short) 0, "&7Voltar", "&7Punir " + targetName));
        p.openInventory(inv);
    }

    public static void openPickTempMute(JavaPlugin plugin, Player p, String targetName) {
        ArtixMenuHolder h = new ArtixMenuHolder(Type.PICK_TEMP_MUTE, targetName);
        Inventory inv = Bukkit.createInventory(h, 27, TITLE_PICK_TM);
        h.setInventory(inv);
        for (int i = 0; i < 27; i++) {
            inv.setItem(i, MenuItems.filler());
        }
        inv.setItem(10, MenuItems.named(Material.WATCH, (short) 0, "&a1 hora", ""));
        inv.setItem(11, MenuItems.named(Material.WATCH, (short) 0, "&a6 horas", ""));
        inv.setItem(12, MenuItems.named(Material.WATCH, (short) 0, "&e1 dia", ""));
        inv.setItem(13, MenuItems.named(Material.WATCH, (short) 0, "&e7 dias", ""));
        inv.setItem(14, MenuItems.named(Material.WATCH, (short) 0, "&630 dias", ""));
        inv.setItem(15, MenuItems.named(Material.WOOL, (short) 14, "&cPermanente", "&7Mute permanente"));
        inv.setItem(18, MenuItems.named(Material.ARROW, (short) 0, "&7Voltar", ""));
        p.openInventory(inv);
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        return s.length() <= max ? s : s.substring(0, max - 2) + "..";
    }
}
