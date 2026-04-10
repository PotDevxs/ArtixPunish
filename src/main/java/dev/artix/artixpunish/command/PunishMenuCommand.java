package dev.artix.artixpunish.command;

import dev.artix.artixpunish.gui.MenuFactory;
import dev.artix.artixpunish.util.Messages;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class PunishMenuCommand implements CommandHandler {

    private final JavaPlugin plugin;

    public PunishMenuCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean handle(CommandArgs a) {
        if (!a.hasPermission("artixpunish.menu")) {
            Messages.send(a.getSender(), "error-no-permission");
            return true;
        }
        if (!a.requirePlayerSender()) {
            return true;
        }
        Player p = a.getPlayerSender();
        MenuFactory.openMain(plugin, p, p.hasPermission("artixpunish.menu.reset.players"),
                p.hasPermission("artixpunish.menu.reset.ip"));
        return true;
    }
}
