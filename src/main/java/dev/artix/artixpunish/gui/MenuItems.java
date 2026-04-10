package dev.artix.artixpunish.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class MenuItems {

    private MenuItems() {
    }

    static ItemStack filler() {
        ItemStack it = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName(" ");
        it.setItemMeta(m);
        return it;
    }

    static ItemStack named(Material mat, short data, String name, String... lore) {
        ItemStack it = new ItemStack(mat, 1, data);
        ItemMeta m = it.getItemMeta();
        m.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        if (lore.length > 0) {
            List<String> lines = new ArrayList<String>();
            for (String line : lore) {
                lines.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            m.setLore(lines);
        }
        it.setItemMeta(m);
        return it;
    }

    static ItemStack playerHead(String playerName) {
        ItemStack it = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta m = (SkullMeta) it.getItemMeta();
        m.setOwner(playerName);
        m.setDisplayName(ChatColor.GREEN + playerName);
        m.setLore(Arrays.asList(ChatColor.GRAY + "Clique para punir"));
        it.setItemMeta(m);
        return it;
    }
}
